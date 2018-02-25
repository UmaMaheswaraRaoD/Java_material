package com.java.railway;

public class Ticket {
	private static int ticketId;
	private String ticketClass;
	private int ticketticketCount;
	
	
	public int getTicketticketCount() {
		return ticketticketCount;
	}
	public void setTicketticketCount(int ticketticketCount) {
		this.ticketticketCount = ticketticketCount;
	}
	public static int getTicketId() {
		return ticketId;
	}
	public static void setTicketId(int ticketId) {
		Ticket.ticketId = ticketId;
	}
	public String getTicketClass() {
		return ticketClass;
	}
	public void setTicketClass(String ticketClass) {
		this.ticketClass = ticketClass;
	}
	

}
