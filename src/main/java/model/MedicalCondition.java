package model;

import java.time.LocalDateTime;

public class MedicalCondition {
    private int id;
    private String name;
    private String description;
    private String type;
    private String start;
    private String end;
    private LocalDateTime createdAt;
    private Patient patient;

    // 🔹 create condition log
    public MedicalCondition(String name, String description, String type, String start, String end, LocalDateTime createdAt, Patient patient) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createdAt = createdAt;
        this.patient = patient;
    }

    // 🔹 from DB
    public MedicalCondition(int id, String name, String description, String type, String start, String end, LocalDateTime createdAt, Patient patient) {
        this( name, description, type, start, end, createdAt, patient);
        this.id = id;
    }

    // GETTER
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
    public Patient getPatient() { return patient; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // SETTER
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setStart(String start) { this.start = start; }
    public void setEnd(String end) { this.end = end; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return   (type_check(type) == null ? "" : "logged at " +  createdAt + "\n" + type_check(type) + ": " +
                                                  description + "\n" + "started: " + start +
                                                  (end == null ? "." : "\nended: " + end));
    }

    private static String type_check(String t) {
        if (t.equalsIgnoreCase("Symptom")) {return "Symptoms: ";}
        else if (t.equalsIgnoreCase("pathologies")) {return "Pathologies: ";}
        else if (t.equalsIgnoreCase("other_therapies")) {return "Other therapies: ";}
        return null;
    }
}
