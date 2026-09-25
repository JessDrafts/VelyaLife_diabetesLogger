package controller;

import application.VelyaLifeApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.Doctor;
import model.Patient;
import repository.DoctorRepository;
import repository.PatientRepository;

public class InfoPageController {

    public AnchorPane sidebar;

	@FXML
	public Label nameField;
	
	@FXML
	private Label codiceFiscaleField;
	
	@FXML
	private Label emailField;
	
	@FXML
	private Label telField;
	
    @FXML
    private PatientSidebarController sidebarController;
    
    private PatientRepository patientRepo;
    private DoctorRepository doctorRepo;

    @FXML
    public void initialize() {
    	patientRepo = VelyaLifeApplication.getPatientRepository();
    	doctorRepo = VelyaLifeApplication.getDoctorRepository();
    }
    
    public void setUser(String username) {
        sidebarController.setUser(username);
    }
    
    public void initData(String authenticatedUser) {
        Patient patient = patientRepo.getById(authenticatedUser);
        Doctor refDoctor = doctorRepo.getById(patient.getRefDoctor().getCodiceFiscale());

		nameField.setText(refDoctor.getName() + " " + refDoctor.getLastName());
    	codiceFiscaleField.setText(refDoctor.getCodiceFiscale());
    	emailField.setText(refDoctor.getEmail());
    	telField.setText(refDoctor.getTelNumber());
	}
}
