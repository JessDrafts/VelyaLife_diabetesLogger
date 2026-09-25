package model;

import utility.Help_functions;

import java.time.LocalDateTime;

public class DailyLog {

    private int id;
    private Patient patient;
    private LocalDateTime createdAt;
    private boolean beforeMeal;
    private double bloodSugarLevel;
    private String drugs;
    private int amountIntaken;

    // create log
    public DailyLog(double bloodSugarLevel, Patient patient, LocalDateTime createdAt, boolean beforeMeal, String drugs, int amountIntaken) {
        this.bloodSugarLevel = bloodSugarLevel;
        this.patient = patient;
        this.createdAt = createdAt;
        this.beforeMeal = beforeMeal;
        this.drugs = drugs;
        this.amountIntaken = amountIntaken;
    }

    // retrieve from database
    public DailyLog(int id, double bloodSugarLevel, Patient patient, LocalDateTime createdAt, boolean beforeMeal, String drugs, int amountIntaken) {
        this(bloodSugarLevel, patient, createdAt, beforeMeal, drugs, amountIntaken);
        this.id = id;
    }

    // GETTERS
    public int getId() { return id; }
    public double getBloodSugarLevel() { return bloodSugarLevel; }
    public Patient getPatient() { return patient; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean getBeforeMeal() { return beforeMeal; }
    public String getDrugs() { return drugs; }
    public int getAmountIntaken() { return amountIntaken; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setBloodSugarLevel(double bloodSugarLevel) { this.bloodSugarLevel = bloodSugarLevel; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setBeforeMeal(boolean beforeMeal) { this.beforeMeal = beforeMeal; }
    public void setDrugs(String drugs) { this.drugs = drugs; }
    public void setAmountIntaken(int amountIntaken) { this.amountIntaken = amountIntaken; }

    @Override
    public String toString() {
        return "Report N° " + id + "\n" +
                "Date and time: " + Help_functions.formattedDateTime(createdAt) + "\n" +
                "Patient: " + patient.getName() + " " + patient.getLastName() + "\n" +
                "Sugar lever: " + bloodSugarLevel + "\n" +
                "Measured before meal: " + (beforeMeal ? "Sì" : "No") + "\n" +
                "medicine intaken: " + drugs + "quantity: " + amountIntaken + "\n";
    }
}