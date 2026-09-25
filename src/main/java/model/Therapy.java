package model;

public class Therapy {

	private int id;
	private String prescription;
	private double dailyDose;
	private double amountIntaken;
	private String instructions;
	private Patient patient;
	private Doctor doctor;
	private String startDate;
	private String endDate;
	
	public Therapy(String prescription, double dailyDose, double amountIntaken, String instructions, Patient patient, Doctor doctor, String startDate, String endDate) {
		this.prescription = prescription;
		this.dailyDose = dailyDose;
		this.amountIntaken = amountIntaken;
		this.instructions = instructions;
		this.patient = patient;
		this.doctor = doctor;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Therapy(int id, String prescription, double dailyDose, double amountIntaken, String instructions, Patient patient, Doctor doctor, String startDate, String endDate) {
		this(prescription, dailyDose, amountIntaken, instructions, patient, doctor, startDate, endDate);
		this.id = id;
	}
	
	//GETTERS
	public int getId() {
		return id;
	}
	
	public String getPrescription() {
		return prescription;
	}
	
	public double getDailyDose() {
		return dailyDose;
	}
	
	public double getAmountIntaken() {
		return amountIntaken;
	}
	
	public String getInstructions() {
		return instructions;
	}
	
	public Patient getPatient() {
		return patient;
	}

	public Doctor getDoctor() { return doctor; }

	public String getStartDate() { return startDate; }

	public String getEndDate() { return endDate; }
	
	//SETTERS
	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}
	
	public void setDailyDose(double dailyDose) {
		this.dailyDose = dailyDose;
	}
	
	public void setAmountIntaken(double amountIntaken) {
		this.amountIntaken = amountIntaken;
	}
	
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public void setDoctor(Doctor doctor) { this.doctor = doctor; }
	public void setStartDate(String startDate) { this.startDate = startDate; }
	public void setEndDate(String endDate) { this.endDate = endDate; }
	
	@Override
	public String toString() {
		return "Prescription N.: " + id + "\n" +
			   "Patient: " + patient.getCodiceFiscale() + "\n" +
			   "Name medicine: " + prescription + "\n" +
			   "Amount : " + amountIntaken + "\n" +
			   "Daily dose: " + dailyDose + "\n" +
			   "Instructions: " + instructions + "\n" +
	     		"From: " + startDate  + (endDate == null? "": "To: " + endDate);
	}
}
