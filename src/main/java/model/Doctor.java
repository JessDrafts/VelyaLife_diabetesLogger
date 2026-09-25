package model;

import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;

public class Doctor extends AbstractUser {

    private String codiceFiscale;
    private final SortedSet<Patient> patients = new TreeSet<>();

    // Create user for the first time
    public Doctor(String name, String lastName, LocalDate dateOfBirth, String placeOfBirth, String nationality, String sex, String codiceFiscale, String telephone, String email) {
        super(name, lastName, dateOfBirth, placeOfBirth, nationality, sex, telephone, codiceFiscale, email, "Doctor");
		this.codiceFiscale = codiceFiscale;
    }

    // Retrieve user from database
    public Doctor(String name, String lastName, LocalDate dateOfBirth, String placeOfBirth, String nationality, String sex, String telephone, String codiceFiscale, String hashedPassword, String email, String type) {
    	super(name, lastName, dateOfBirth, placeOfBirth, nationality, sex, telephone, codiceFiscale, hashedPassword, email, type);
		this.codiceFiscale = codiceFiscale;
	}

    public String getCodiceFiscale() { return codiceFiscale; }

    public void setCodiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }

    public SortedSet<Patient> getAllPatient() {
    	return patients;
    }
    
    public void addPatient(Patient p) { this.patients.add(p); }

    public String toString() {
    	return  "Doctor's Information: " + getCodiceFiscale() + "\n" +
                "Name: " + getLastName() + " " + getName() + "\n" +
                "Codice fiscale: " + codiceFiscale + "\n" +
                "Phone number: " + getTelNumber() + " - Email: " + getEmail();
    }
}
