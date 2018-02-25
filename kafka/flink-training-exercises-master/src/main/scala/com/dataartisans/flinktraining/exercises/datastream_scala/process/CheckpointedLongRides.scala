/*
 * Copyright 2017 data Artisans GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dataartisans.flinktraining.exercises.datastream_scala.process

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.{TimeCharacteristic, TimerService}
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
import com.dataartisans.flinktraining.exercises.datastream_java.sources.CheckpointedTaxiRideSource
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import java.util.concurrent.TimeUnit

import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils
import org.apache.flink.api.common.time.Time
import org.apache.flink.runtime.state.filesystem.FsStateBackend


/**
  * Scala reference implementation for the "Long Ride Alerts" exercise of the Flink training
  * (http://training.data-artisans.com).
  *
  * The goal for this exercise is to emit START events for taxi rides that have not been matched
  * by an END event during the first 2 hours of the ride.
  *
  * This version is setup for checkpointing and fault recovery.
  *
  * Parameters:
  * -input path-to-input-file
  *
  */
object CheckpointedLongRides {
  def main(args: Array[String]) {

    // parse parameters
    val params = ParameterTool.fromArgs(args)
    val input = params.getRequired("input")
    val speed = 1800       // events of 30 minutes are served every second

    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // operate in Event-time
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    // set up checkpointing
    env.setStateBackend(new FsStateBackend("file:///tmp/checkpoints"))
    env.enableCheckpointing(1000)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(60, Time.of(10, TimeUnit.SECONDS)))

    val rides = env.addSource(new CheckpointedTaxiRideSource(input, speed))

    val longRides = rides
      // remove all rides which are not within NYC
      .filter { r => GeoUtils.isInNYC(r.startLon, r.startLat) && GeoUtils.isInNYC(r.endLon, r.endLat) }
      .keyBy(_.rideId)
      .process(new MatchFunction())

    longRides.print()

    env.execute("Long Taxi Rides (checkpointed)")
  }

  class MatchFunction extends ProcessFunction[TaxiRide, TaxiRide] {
    // keyed, managed state
    // holds an END event if the ride has ended, otherwise a START event
    lazy val rideState: ValueState[TaxiRide] = getRuntimeContext.getState(
      new ValueStateDescriptor[TaxiRide]("saved ride", classOf[TaxiRide]))

    override def processElement(ride: TaxiRide,
                                context: ProcessFunction[TaxiRide, TaxiRide]#Context,
                                out: Collector[TaxiRide]): Unit = {
      val timerService = context.timerService

      if (ride.isStart) {
        // the matching END might have arrived first (out of order); don't overwrite it
        if (rideState.value() == null) {
          rideState.update(ride)
        }
      }
      else {
        rideState.update(ride)
      }

      timerService.registerEventTimeTimer(ride.getEventTime + 120 * 60 * 1000)
    }

    override def onTimer(timestamp: Long,
                         ctx: ProcessFunction[TaxiRide, TaxiRide]#OnTimerContext,
                         out: Collector[TaxiRide]): Unit = {
      val savedRide = rideState.value

      if (savedRide != null && savedRide.isStart) {
        out.collect(savedRide)
      }

      rideState.clear()
    }
  }

}
