package model;

import utility.Help_functions;

import java.time.LocalDateTime;

public class Notification {
	
	private int id;
	private String message;
	private AbstractUser sender;
	private AbstractUser receiver;
	private LocalDateTime  createdAt;
	private boolean seen;

	// create notification
	public Notification(String message, AbstractUser sender, AbstractUser receiver, LocalDateTime createdAt, boolean seen) {
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.createdAt = createdAt;
		this.seen = seen;
	}

	// retrieve from database
	public Notification(Integer id, String message, AbstractUser sender, AbstractUser receiver, LocalDateTime createdAt, boolean seen) {
		this(message, sender, receiver, createdAt, seen);
		this.id = id;
	}
	
	//GETTERS
	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public boolean getSeen() {
		return seen;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public AbstractUser getSender() {
		return sender;
	}

	public AbstractUser getReceiver() {
		return receiver;
	}

	//SETTERS
	public void setSender(AbstractUser sender) {
	    this.sender = sender;
	}

	public void setReceiver(AbstractUser receiver) {
		this.receiver = receiver;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Notification: " + id + "\n" +
			   "From: " + sender + " 	To: " + receiver +"\n" +
	            message + "\n" + 
			   "Date and time: " + Help_functions.formattedDateTime(getCreatedAt());
	}
}
