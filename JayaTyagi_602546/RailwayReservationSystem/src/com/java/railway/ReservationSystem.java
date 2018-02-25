package com.java.railway;

import java.util.Scanner;



public class ReservationSystem {
//	private static int ticketId;
//	private int[] tickets = new int[200];
//	private String ticketClass;
	
	static Object lock = new Object();  // used to synchronize
	static int transactionId = 345646;
	
	User[] users = new User[20];
	static Ticket[] tickets = new Ticket[200];
	
	static int userCount = 0;
	static int ticketCount = 0;
	static int firstClassCount = 0;
	static int secondClassCount = 0;
	static int thirdClassCount = 0;
	
	
	
	
	public static void main(String[] args) throws InterruptedException {
		
//		Threading
		User1 user1 = new User1();
		Thread u1 = new Thread(user1);

		User2 user2 = new User2();
		Thread u2 = new Thread(user2);
		
		u1.start();
		u2.start();
		u1.join();
		u2.join();
		
//		Admin();
		
		
		
	}
	
	public static void Admin() {
		Scanner sc = new Scanner(System.in);
		int choice;
		String ticketClass1;
		int costPerTicket = 0;
		User[] users = new User[20];
		int ticketC = 0;
		String name;
		int age, phoneNumber, totalCost = 0, paid = 0, checkTransactionId;
		do {
			System.out.println("***********!! Railway Reservation System !!************");
			System.out.println("1.Book Tickets\n2.Cancel Tickets\n3.Get Ticket Details\n4.Reservation Chart\n5.Display Unbooked Tickets\n6.Exit\n");
			choice = sc.nextInt();
			
			//choice 1
			if(choice == 1) {
				users[userCount] = new User();
				System.out.println("Please enter the class of the ticket: \n1.First Class\n2.Second Class\n3.Third Class\n");
				
				ticketClass1 = sc.next();
				
			
				
				System.out.println("Please enter number of tickets");
				ticketC = sc.nextInt();
				
				if(ticketClass1.equals("1")) {
					costPerTicket = 2000;
					firstClassCount += ticketC;
				}
				else if(ticketClass1.equals("2")) {
					costPerTicket = 1500;
					secondClassCount += ticketC;
				}
				else if(ticketClass1.equals("3")) {
					costPerTicket = 1000;
					thirdClassCount += ticketC;
				}
				
				users[userCount].userTicketCount = ticketC;
				for(int i = 0; i < ticketC; i++) {
					tickets[ticketCount] = new Ticket();
					users[userCount].ticketAllotted[i] = ticketCount;
					tickets[ticketCount].setTicketticketCount(ticketCount);
					tickets[ticketCount].setTicketId(ticketCount);
					tickets[ticketCount].setTicketClass(ticketClass1);
					
					users[userCount].passengers[i] = new Passenger();
					
					
					System.out.println("Please enter details for passenger " + i);
					System.out.println("Please enter your name");
					
					
					
					do {
						name = sc.next();
						if(!validateName(name))
							System.out.println("Invalid Name Format");
					}while(!validateName(name));
					
					
					users[userCount].passengers[i].setName(name);
					
					
					System.out.println("Please enter your age");
					
					
					do {
						age = sc.nextInt();
						if(!validateAge(age))
							System.out.println("Invalid Age Format");
					}while(!validateAge(age));
					
					users[userCount].passengers[i].setAge(age);
					
					System.out.println("Please enter your mobile number");
					
					
					do {
						phoneNumber = sc.nextInt();
						if(!validatePhone(phoneNumber))
							System.out.println("Invalid Phone Number Format");
					}while(!validatePhone(phoneNumber));
					
					users[userCount].passengers[i].setPhone(phoneNumber);
					
					ticketCount++;
				
				}
				System.out.println("Tickets Successfully booked!!");
				System.out.println("Transaction ID: " + transactionId);
				
				totalCost = ticketC * costPerTicket;
				do {
					System.out.println("Please pay Rs." + totalCost);
					paid = sc.nextInt();
					if(paid == totalCost) {
						System.out.println("Thanks !!");
					}
				}while(paid != totalCost);
				
				users[userCount].setUserTransactionId(transactionId);
				users[userCount].setUserTotalCost(totalCost);
				
				userCount++;
				transactionId++;
				
				
			}
			
			//choice 2
			if(choice == 2) {
				int flag = 0;
				System.out.println("Please Enter your transaction ID");
				checkTransactionId = sc.nextInt();
				for(int k = 0; k < userCount; k++) {
					if(users[k].getUserTransactionId() == checkTransactionId) {
						System.out.println(users[k].getUserTotalCost() + " Rs. would be refunded to your account within 2 working days");
						flag = 1;
						users[k].setTicketCancelledFlag(1);
						
						for(int i = 0; i < userCount; i++) {
							for(int j = 0; j < ticketCount; j++) {
								if(users[i].ticketAllotted[j] == tickets[i].getTicketticketCount() && tickets[i].getTicketClass().equals("1")) {
									synchronized(lock) {
									firstClassCount -= users[i].userTicketCount;
									}
								}
								else if(users[i].ticketAllotted[j] == tickets[i].getTicketticketCount() && tickets[i].getTicketClass().equals("2")) {
									synchronized(lock) {
									secondClassCount -= users[i].userTicketCount;
									}
								}
								else if(users[i].ticketAllotted[j] == tickets[i].getTicketticketCount() && tickets[i].getTicketClass().equals("3")) {
									synchronized(lock) {
									thirdClassCount -= users[i].userTicketCount;
									}
								}
							}
						}
						
						
						
						break;
					}
				}
				if(flag != 1) {
					System.out.println("Invalid ID");
				}
			}
			
			//choice3 
			if(choice == 3) {
				
				System.out.println("Please Enter your transaction ID");
				checkTransactionId = sc.nextInt();
				for(int i = 0; i < userCount; i++) {
					
					if(users[i].getUserTransactionId() == checkTransactionId) {
						
						
						System.out.println("Number of tickets booked: " + (users[i].userTicketCount) + "\n"); 
						for(int j = 0; j < users[i].userTicketCount; j++) {
							System.out.println("Passenger " + (j + 1) + ":\n");
							System.out.println("Passenger Name: " + users[i].passengers[j].getName() + "\n");
							System.out.println("Passenger Age: " + users[i].passengers[j].getAge() + "\n");
							System.out.println("Passenger Mobile Number: " + users[i].passengers[j].getPhone() + "\n");
							
						}
						if(users[i].getTicketCancelledFlag() == 1) {
							System.out.println("Tickets Cancelled !!");
							break;
						}
						else {
							System.out.println("Tickets Confirmed ! Thanks !");
							break;
						}
					}
				}
				
			}
			
			
			//choice 4
			if(choice == 4) {
				int userInFirstClassCount = 0, userInSecondClassCount = 0, userInThirdClassCount = 0;
				User[] usersInFirstClass = new User[100];
				User[] usersInSecondClass = new User[100];
				User[] usersInThirdClass = new User[100];
				for(int i = 0; i < userCount; i++) {
					for(int j = 0; j < ticketCount; j++) {
						if(users[i].ticketAllotted[j] == tickets[i].getTicketticketCount() && tickets[i].getTicketClass().equals("1")) {
							usersInFirstClass[i] = new User();
							usersInFirstClass[i] = users[i];
							userInFirstClassCount++;
						}
						else if(users[i].ticketAllotted[j] == tickets[i].getTicketticketCount() && tickets[i].getTicketClass().equals("2")) {
							usersInSecondClass[i] = new User();
							usersInSecondClass[i] = users[i];
							userInSecondClassCount++;
						}
						else if(users[i].ticketAllotted[j] == tickets[i].getTicketticketCount() && tickets[i].getTicketClass().equals("3")) {
							usersInThirdClass[i] = new User();
							usersInThirdClass[i] = users[i];
							userInThirdClassCount++;
						}
					}
				}
				
				System.out.println("Passenger List in First Class\n");
				
				if(userInFirstClassCount == 0) {
					System.out.println("No Passenger");
				}
				else {
					for(int i = 0; i < userInFirstClassCount; i++) {
						for(int j = 0; j < usersInFirstClass[i].userTicketCount; j++) {
							System.out.println(usersInFirstClass[i].passengers[j].getName() + "\t" + usersInFirstClass[i].passengers[j].getAge() + "\t" + usersInFirstClass[i].passengers[j].getPhone() + "\n");
						}
						System.out.println("\n");
					}
				}
				
				System.out.println("Passenger List in Second Class\n");
				
				if(userInSecondClassCount == 0) {
					System.out.println("No Passenger");
				}
				else {
					for(int i = 0; i < userInSecondClassCount; i++) {
						for(int j = 0; j < usersInSecondClass[i].userTicketCount; j++) {
							System.out.println(usersInSecondClass[i].passengers[j].getName() + "\t" + usersInSecondClass[i].passengers[j].getAge() + "\t" + usersInSecondClass[i].passengers[j].getPhone() + "\n");
						}
						System.out.println("\n");
					}
				}
				System.out.println("Passenger List in Third Class\n");
				
				if(userInThirdClassCount == 0) {
					System.out.println("No Passenger");
				}
				else {
					for(int i = 0; i < userInThirdClassCount; i++) {
						for(int j = 0; j < usersInThirdClass[i].userTicketCount; j++) {
							System.out.println(usersInThirdClass[i].passengers[j].getName() + "\t" + usersInThirdClass[i].passengers[j].getAge() + "\t" + usersInThirdClass[i].passengers[j].getPhone() + "\n");
						}
						System.out.println("\n");
					}
				}
				
			}
			
			
			//choice 5
			if(choice == 5) {
				int availableFirstClass = 5 - firstClassCount;
				int availableSecondClass = 10 - secondClassCount;
				int availableThirdClass = 50 - thirdClassCount;
				System.out.println("Seats available in First Class: " + (availableFirstClass) + "\n");
				System.out.println("Seats available in Second Class: " + (availableSecondClass) + "\n");
				System.out.println("Seats available in Third Class: " + (availableThirdClass) + "\n");
			}
			
			
		} while(choice != 6);
		
	}
	
	
	// validation functions
	public static boolean validateName(String str) {
		if(str.matches(".*\\d+.*")) {
			return false;
		}
		return true;
	}
	
	public static boolean validateAge(int num) {
		if(num > 150) {
			return false;
		}
		return true;
	}
	
	public static boolean validatePhone(int num) {
		if(num >  2147483647) { //upper limit of integer
			return false;
		}
		return true;
	}
	
	
	
	
	
}


class User1 implements Runnable {
	
	
	@Override 
	public void run() {
		
		ReservationSystem.Admin();
	}
	
}

class User2 implements Runnable {
	
	
	@Override 
	public void run() {
		ReservationSystem.Admin();
	}
	
}
