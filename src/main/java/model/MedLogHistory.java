package model;

import java.time.LocalDateTime;
import static utility.Help_functions.formattedDateTime;

public class MedLogHistory {

	private int id;
	private Patient patient;
	private Doctor doctor;
	private LocalDateTime modifiedAt;
	private String content;
	
	public MedLogHistory (Patient patient, Doctor doctor, LocalDateTime modifiedAt, String content) {
		this.patient = patient;
		this.doctor = doctor;
		this.modifiedAt = modifiedAt;
		this.content = content;
	}
	
	public MedLogHistory (int id, Patient patient, Doctor doctor, LocalDateTime modifiedAt, String modification) {
		this(patient,doctor,modifiedAt,modification);
		this.id = id;
	}
	
	//GETTERS
	public int getId() {
		return id;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public Doctor getDoctor() {
		return doctor;
	}
	
	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}
	
	public String getContent() {
		return content;
	}
	
	//SETTERS
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	
	public void setModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {

        return "Report N.: " + id + "\n" +
                "Date & Time: " + formattedDateTime(modifiedAt) + "\n" +
                "Patient: " + patient.getName() + patient.getLastName() + "\n" +
                "Modified by Dr. " + doctor.getName() + " " + doctor.getLastName() + "\n" +
                content;
	}

}
