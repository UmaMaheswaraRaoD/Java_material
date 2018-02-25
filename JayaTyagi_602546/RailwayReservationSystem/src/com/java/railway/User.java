package com.java.railway;

public class User {
	Passenger[] passengers = new Passenger[200];
	int[] ticketAllotted = new int[200]; //contains ticket Ids of each ticket allotted to a user
	private int userTransactionId;
	private int userTotalCost;
	private int ticketCancelledFlag = 0;
	public int userTicketCount;

	public int getTicketCancelledFlag() {
		return ticketCancelledFlag;
	}
	public void setTicketCancelledFlag(int ticketCancelledFlag) {
		this.ticketCancelledFlag = ticketCancelledFlag;
	}
	public int getUserTotalCost() {
		return userTotalCost;
	}
	public void setUserTotalCost(int userTotalCost) {
		this.userTotalCost = userTotalCost;
	}
	public int getUserTransactionId() {
		return userTransactionId;
	}
	public void setUserTransactionId(int userTransactionId) {
		this.userTransactionId = userTransactionId;
	}
	
	
}
