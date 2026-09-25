package model;

import java.time.LocalDate;

import utility.Help_functions;

public class Patient extends AbstractUser implements Comparable<Patient> {

    private String codiceFiscale;
	private double weight;
	private boolean smoker;
	private boolean drinker;
	private String riskFactor;
	private Doctor refDoctor;
	private String doctorNotes;

	// Create patient for the first time
	public Patient(String name, String lastName, LocalDate dateOfBirth, String placeOfBirth, String nationality, String sex, String codiceFiscale, double weight, boolean smoker, boolean drinker, String riskFactor, Doctor refDoctor, String telephone, String email) {
		super(name, lastName, dateOfBirth, placeOfBirth, nationality, sex, telephone, codiceFiscale, email, "Patient");
		this.codiceFiscale = codiceFiscale;
		this.weight = weight;
		this.smoker = smoker;
		this.drinker = drinker;
		this.riskFactor = riskFactor;
		this.refDoctor = refDoctor;
    }
	
	// Retrieve patient from database
	public Patient(String name, String lastName, LocalDate dateOfBirth, String placeOfBirth, String nationality, String sex, double weight, boolean smoker, boolean drinker, String riskFactor, Doctor refDoctor, String doctorNotes, String telephone, String codiceFiscale, String hashedPassword, String email, String type) {
		super(name, lastName, dateOfBirth, placeOfBirth, nationality, sex, telephone, codiceFiscale, hashedPassword, email, type);
		this.codiceFiscale = codiceFiscale;
		this.weight = weight;
		this.smoker = smoker;
		this.drinker = drinker;
		this.refDoctor = refDoctor;
		this.riskFactor = riskFactor;
		this.doctorNotes = doctorNotes;
	}
	
	//GETTERS
	public String getCodiceFiscale() {
    	return codiceFiscale;
    }
	
	public double getWeight() {
		return weight;
	}
	
	public boolean getIsSmoker() {
		return smoker;
	}
	
	public boolean getIsDrinker() {
		return drinker;
	}
	
	public String getRiskFactor() {
		return riskFactor;
	}
	
	public Doctor getRefDoctor() {
		return refDoctor;
	}
	
	public String getDoctorNotes() {
		return doctorNotes;
	}
	
	//SETTERS
    public void setCodiceFiscale(String codiceFiscale) {
    	this.codiceFiscale = codiceFiscale;
    }
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void setSmoker(boolean smoker) {
		this.smoker = smoker;
	}
	
	public void setDrinker(boolean drinker) {
		this.drinker = drinker;
	}
	
	public void setRiskFactor(String riskFactor) {
		this.riskFactor = riskFactor;
	}
	
	public void setRefDoctor(Doctor refDoctor) {
		this.refDoctor = refDoctor;
	}
	
	public void setDoctorNotes(String doctorNotes) {
		this.doctorNotes = doctorNotes;
	}

	public String toString() {
    	return "Patient's Information: " + getUsername() + "\n" +
	           "Name: " + getLastName() + " " + getName() + "\n" +
    		   "Date of birth: " + Help_functions.formattedDate(getDateBirth()) + "\n" +
    		   "Codice fiscale: " + codiceFiscale + "\n" + 
    		   "Phone number: " + getTelNumber() + " - Email: " + this.getEmail() + "\n" +
    		   "Sex: " + getSex() + ", Weight: " + weight + " kg\n" +
    		   "Do they smoke?: " + (getIsSmoker() ? "Yes" : "No") + ", Do they drink?: " + (getIsDrinker() ? "Yes" : "No") + "\n" +
    		   "Risk factors: " + riskFactor + "\n" +
    		   "Doctor in charge: " + (refDoctor.getName() + " " + refDoctor.getLastName())
    		   + "\n" +
    		   "Doctor's note: " + doctorNotes;
    }

	public int compareTo(Patient o) {
		return this.getCodiceFiscale().compareTo(o.getCodiceFiscale());
	}
}
