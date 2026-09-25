package controller;

import application.VelyaLifeApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.*;
import repository.*;
import utility.Help_DailyLog;
import utility.NotificationService;
import utility.PDFReportExporter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DetailPatientController {
    private static final Logger LOGGER = Logger.getLogger(DetailPatientController.class.getName());

    // Info Patient & Doctor
    @FXML private Label cfLabel;
    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label sexLabel;
    @FXML private Label weightLabel;
    @FXML private Label smokeLabel;
    @FXML private Label drinkLabel;
    @FXML private Label docCfLabel;
    @FXML private Label docNameLabel;

    // Risk Factors
    @FXML private TextArea riskFactorArea;
    @FXML private Button riskFactorButton;

    // Doctor's note
    @FXML private Label doctorNoteAuthorLabel;
    @FXML private TextArea doctorNoteArea;
    @FXML private Button doctorNoteButton;

    // Graph
    @FXML private ChoiceBox<String> periodChoiceBox;
    @FXML private LineChart<String, Number> glycemiaChart;

    // DailyLog table
    @FXML private TableView<DailyLog> measurementsTable;
    @FXML private TableColumn<DailyLog, String> dateColumn;
    @FXML private TableColumn<DailyLog, Void> detailColumn;

    // Medical conditions
    @FXML private TableView<MedicalCondition> medConditionTable;
    @FXML private TableColumn<MedicalCondition, String> typeColumn;
    @FXML private TableColumn<MedicalCondition, String> nameColumn1;
    @FXML private TableColumn<MedicalCondition, String> startDateColumn;
    @FXML private TableColumn<MedicalCondition, String> endDateColumn;
    @FXML private TableColumn<MedicalCondition, String> medDetailColumn;

    // Therapy
    @FXML private Button addTherapyButton;
    @FXML private TableView<Therapy> therapyTable;
    @FXML private TableColumn<Therapy, String> therapyColumn;
    @FXML private TableColumn<Therapy, String> terDateColumn;
    @FXML private TableColumn<Therapy, String> statusColumn;
    @FXML private TableColumn<Therapy, Void> terActionColumn;

    // Other Doctor's Interventions
    @FXML private TableView<MedLogHistory> docLogTable;
    @FXML private TableColumn<MedLogHistory, String> doctorColumn;
    @FXML private TableColumn<MedLogHistory, String> docDateColumn;
    @FXML private TableColumn<MedLogHistory, String> changeColumn;
    @FXML private TableColumn<MedLogHistory, Void> docActionColumn;

    private Patient currentPatient;
    private Doctor loggedDoctor;

    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private MedicalConditionRepository medicalConditionRepository;
    private TherapyRepository therapyRepository;
    private MedLogHistoryRepository medLogHistoryRepository;
    private DailyLogRepository dailyReportRepository;

    @FXML
    public void initialize() {
        patientRepository = VelyaLifeApplication.getPatientRepository();
        doctorRepository = VelyaLifeApplication.getDoctorRepository();
        medicalConditionRepository = VelyaLifeApplication.getMedicalConditionRepository();
        therapyRepository = VelyaLifeApplication.getTherapyRepository();
        medLogHistoryRepository = VelyaLifeApplication.getMedLogHistoryRepository();
        dailyReportRepository = VelyaLifeApplication.getDailyLogRepository();

        setupMeasurementsTable();
        setupMedConditionTable();
        setupTherapyTable();
        setupDocLogTable();

        periodChoiceBox.setItems(FXCollections.observableArrayList("Last 7 days", "Last 30 days", "All time"));
        periodChoiceBox.getSelectionModel().selectFirst();
        periodChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (currentPatient != null && newValue != null) {
                loadChartData(newValue);
            }
        });

        if (riskFactorArea != null) {
            riskFactorArea.setEditable(false);
        }
        if (riskFactorButton != null) {
            riskFactorButton.setText("Edit");
        }

        if (doctorNoteArea != null) {
            doctorNoteArea.setEditable(false);
        }
        if (doctorNoteButton != null) {
            doctorNoteButton.setText("Edit");
        }
    }

    public void setLoggedDoctorByUsername(String doctorUsername) {
        if (doctorUsername != null && doctorRepository != null) {
            this.loggedDoctor = doctorRepository.getById(doctorUsername);
        }
        updatePermissions();
    }

    public void setPatient(Patient patient) {
        this.currentPatient = patient;
        if (patient == null) return;

        cfLabel.setText("Codice Fiscale: " + defaultIfNull(patient.getCodiceFiscale()));
        nameLabel.setText("Name: " + defaultIfNull(patient.getName()) + " " + defaultIfNull(patient.getLastName()));
        ageLabel.setText("Date of Birth: " + (patient.getDateBirth() != null ? patient.getDateBirth().toString() : "-"));
        sexLabel.setText("Sex: " + defaultIfNull(patient.getSex()));
        weightLabel.setText("Weight: " + (patient.getWeight() > 0 ? patient.getWeight() + " kg" : "-"));
        smokeLabel.setText("Smoker: " + (patient.getIsSmoker() ? "Yes" : "No"));
        drinkLabel.setText("Drinker: " + (patient.getIsDrinker() ? "Yes" : "No"));

        // Risk Factors
        if (riskFactorArea != null) {
            riskFactorArea.setText(defaultIfNull(patient.getRiskFactor()));
            riskFactorArea.setEditable(false);
        }
        if (riskFactorButton != null) {
            riskFactorButton.setText("Edit");
        }

        // Doctor Notes
        if (doctorNoteArea != null) {
            doctorNoteArea.setText(defaultIfNull(patient.getDoctorNotes()));
            doctorNoteArea.setEditable(false);
        }
        if (doctorNoteButton != null) {
            doctorNoteButton.setText("Edit");
        }

        if (doctorNoteAuthorLabel != null) {
            doctorNoteAuthorLabel.setText("Doctor's Note (" + loggedDoctor.getName() + " " + loggedDoctor.getLastName() +"):");
        }

        if (patient.getRefDoctor() != null) {
            docCfLabel.setText("Codice Fiscale: " + defaultIfNull(patient.getRefDoctor().getCodiceFiscale()));
            docNameLabel.setText("Name: " + defaultIfNull(patient.getRefDoctor().getName()) + " " + defaultIfNull(patient.getRefDoctor().getLastName()));
        }

        loadPatientData();
        loadChartData(periodChoiceBox.getValue());
        updatePermissions();
    }

    private boolean isMyPatient() {
        if (currentPatient == null || loggedDoctor == null) return false;
        Doctor refDoc = currentPatient.getRefDoctor();
        if (refDoc == null || refDoc.getCodiceFiscale() == null) return false;

        return refDoc.getCodiceFiscale().equalsIgnoreCase(loggedDoctor.getCodiceFiscale());
    }

    private void updatePermissions() {
        boolean myPatient = isMyPatient();
        boolean isAnyDoctor = (loggedDoctor != null);

        if (addTherapyButton != null) {
            addTherapyButton.setVisible(myPatient);
            addTherapyButton.setManaged(myPatient);
        }

        if (riskFactorButton != null) {
            riskFactorButton.setVisible(isAnyDoctor);
            riskFactorButton.setManaged(isAnyDoctor);
            riskFactorButton.setText("Edit");
        }
        if (riskFactorArea != null) {
            riskFactorArea.setEditable(false);
        }

        if (doctorNoteButton != null) {
            doctorNoteButton.setVisible(isAnyDoctor);
            doctorNoteButton.setManaged(isAnyDoctor);
            doctorNoteButton.setText("Edit");
        }
        if (doctorNoteArea != null) {
            doctorNoteArea.setEditable(false);
        }

        therapyTable.refresh();
    }

    // ==================== RISK FACTORS & DOCTOR NOTES ====================
    @FXML
    private void handleSaveRiskFactor(ActionEvent event) {
        if (loggedDoctor == null) {
            showAlert("Permission Denied", "You must be logged in as a doctor to edit risk factors.");
            return;
        }

        Button btn = (riskFactorButton != null) ? riskFactorButton : (Button) event.getSource();

        if (!riskFactorArea.isEditable()) {
            riskFactorArea.setEditable(true);
            riskFactorArea.requestFocus();
            btn.setText("Save");
        } else {
            if (currentPatient != null) {
                String updatedRisk = riskFactorArea.getText();
                currentPatient.setRiskFactor(updatedRisk);

                if (patientRepository != null) {
                    patientRepository.update(currentPatient);

                    if (medLogHistoryRepository != null) {
                        MedLogHistory log = new MedLogHistory(
                                currentPatient,
                                loggedDoctor,
                                java.time.LocalDateTime.now(),
                                "Updated Risk Factors"
                        );
                        medLogHistoryRepository.save(log);
                    }

                    showAlert("Success", "Risk factors updated successfully.");
                    loadPatientData();
                }
            }
            riskFactorArea.setEditable(false);
            btn.setText("Edit");
        }
    }

    @FXML
    private void handleSaveDoctorNote(ActionEvent event) {
        if (loggedDoctor == null) {
            showAlert("Permission Denied", "You must be logged in as a doctor to edit doctor notes.");
            return;
        }

        Button btn = (doctorNoteButton != null) ? doctorNoteButton : (Button) event.getSource();

        if (!doctorNoteArea.isEditable()) {
            doctorNoteArea.setEditable(true);
            doctorNoteArea.requestFocus();
            btn.setText("Save");
        } else {
            if (currentPatient != null) {
                String updatedNote = doctorNoteArea.getText();
                currentPatient.setDoctorNotes(updatedNote);

                if (patientRepository != null) {
                    patientRepository.update(currentPatient);

                    if (medLogHistoryRepository != null) {
                        MedLogHistory log = new MedLogHistory(
                                currentPatient,
                                loggedDoctor,
                                java.time.LocalDateTime.now(),
                                "Updated Doctor Note"
                        );
                        medLogHistoryRepository.save(log);
                    }

                    showAlert("Success", "Doctor notes saved successfully.");
                    loadPatientData();
                }
            }
            doctorNoteArea.setEditable(false);
            btn.setText("Edit");
        }
    }

    // ==================== GRAPH ====================
    private void loadChartData(String period) {
        if (currentPatient == null || glycemiaChart == null) return;

        List<DailyLog> logs = dailyReportRepository != null ? dailyReportRepository.getByPatient(currentPatient.getCodiceFiscale()) : List.of();
        LocalDate now = LocalDate.now();

        if (glycemiaChart.getXAxis() != null) {
            glycemiaChart.getXAxis().setLabel("Time Period");
        }
        if (glycemiaChart.getYAxis() instanceof NumberAxis yAxis) {
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(350);
            yAxis.setTickUnit(50);
            yAxis.setLabel("Glycemia (mg/dL)");
        }

        glycemiaChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Glycemia (" + period + ")");

        if (logs == null || logs.isEmpty()) {
            glycemiaChart.getData().add(series);
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

        if ("Last 7 days".equals(period)) {
            LocalDate start = now.minusDays(6);
            Map<LocalDate, List<DailyLog>> grouped = logs.stream()
                    .filter(l -> l.getCreatedAt() != null && !l.getCreatedAt().toLocalDate().isBefore(start) && !l.getCreatedAt().toLocalDate().isAfter(now))
                    .collect(Collectors.groupingBy(l -> l.getCreatedAt().toLocalDate()));

            for (int i = 0; i < 7; i++) {
                LocalDate date = start.plusDays(i);
                List<DailyLog> dayLogs = grouped.get(date);
                String label = date.format(dateFormatter);

                if (dayLogs != null && !dayLogs.isEmpty()) {
                    double avg = Help_DailyLog.calculateAverageBloodSugar(dayLogs);
                    series.getData().add(new XYChart.Data<>(label, avg));
                } else {
                    series.getData().add(new XYChart.Data<>(label, 0));
                }
            }
        } else if ("Last 30 days".equals(period)) {
            LocalDate start = now.minusDays(29);
            Map<LocalDate, List<DailyLog>> grouped = logs.stream()
                    .filter(l -> l.getCreatedAt() != null && !l.getCreatedAt().toLocalDate().isBefore(start) && !l.getCreatedAt().toLocalDate().isAfter(now))
                    .collect(Collectors.groupingBy(l -> l.getCreatedAt().toLocalDate()));

            LocalDate currentDay = start;
            while (!currentDay.isAfter(now)) {
                List<DailyLog> dayLogs = grouped.get(currentDay);
                String label = currentDay.format(dateFormatter);

                if (dayLogs != null && !dayLogs.isEmpty()) {
                    double avg = Help_DailyLog.calculateAverageBloodSugar(dayLogs);
                    series.getData().add(new XYChart.Data<>(label, avg));
                } else {
                    series.getData().add(new XYChart.Data<>(label, 0));
                }
                currentDay = currentDay.plusDays(1);
            }

        } else { // "All time"
            LocalDate startOfYear = now.withDayOfYear(1);
            Map<Integer, List<DailyLog>> groupedByMonth = logs.stream()
                    .filter(l -> l.getCreatedAt() != null
                            && !l.getCreatedAt().toLocalDate().isBefore(startOfYear)
                            && !l.getCreatedAt().toLocalDate().isAfter(now))
                    .collect(Collectors.groupingBy(l -> l.getCreatedAt().getMonthValue()));

            for (int monthValue = 1; monthValue <= 12; monthValue++) {
                if (monthValue > now.getMonthValue()) break;

                List<DailyLog> monthLogs = groupedByMonth.get(monthValue);
                String monthName = Month.of(monthValue).name().substring(0, 3);

                if (monthLogs != null && !monthLogs.isEmpty()) {
                    double avg = Help_DailyLog.calculateAverageBloodSugar(monthLogs);
                    series.getData().add(new XYChart.Data<>(monthName, avg));
                } else {
                    series.getData().add(new XYChart.Data<>(monthName, 0));
                }
            }
        }
        glycemiaChart.getData().add(series);
    }

    private void loadPatientData() {
        if (currentPatient == null) return;

        String patientCf = currentPatient.getCodiceFiscale();

        if (medicalConditionRepository != null) {
            List<MedicalCondition> conditions = medicalConditionRepository.getByPatient(patientCf);
            if (conditions != null) {
                medConditionTable.setItems(FXCollections.observableArrayList(conditions));
            }
        }

        loadTherapies();

        if (medLogHistoryRepository != null) {
            List<MedLogHistory> interventions = medLogHistoryRepository.getByUserCodiceFiscale(patientCf);
            if (interventions != null) {
                docLogTable.setItems(FXCollections.observableArrayList(interventions));
            }
        }

        if (dailyReportRepository != null) {
            List<DailyLog> reports = dailyReportRepository.getByPatient(patientCf);
            if (reports != null) {
                measurementsTable.setItems(FXCollections.observableArrayList(reports));
            }
        }
    }

    private void loadTherapies() {
        if (therapyRepository != null && currentPatient != null) {
            List<Therapy> therapies = therapyRepository.getByPatient(currentPatient.getCodiceFiscale());
            if (therapies != null) {
                therapyTable.setItems(FXCollections.observableArrayList(therapies));
            }
        }
    }

    // ==================== DAILY LOG TABLE ====================
    private void setupMeasurementsTable() {
        dateColumn.setCellValueFactory(cellData -> {
            DailyLog log = cellData.getValue();
            if (log != null && log.getCreatedAt() != null) {
                return new SimpleStringProperty(log.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("-");
        });

        detailColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            {
                viewBtn.getStyleClass().add("buttons");
                viewBtn.setOnAction(_ -> {
                    DailyLog report = getTableView().getItems().get(getIndex());
                    if (report != null) {
                        showDailyLogDetails(report);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
    }

    private void showDailyLogDetails(DailyLog log) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Measurement Details");
        alert.setHeaderText("Daily Log - " + (log.getCreatedAt() != null ? log.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"));

        alert.setContentText("Blood Sugar Level (Glycemia): " + log.getBloodSugarLevel() + " mg/dL\n");
        alert.showAndWait();
    }

    // ==================== MEDICAL CONDITION ====================
    private void setupMedConditionTable() {
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        if (nameColumn1 != null) {
            nameColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        medDetailColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    // ==================== THERAPY ====================
    private void setupTherapyTable() {
        therapyColumn.setCellValueFactory(new PropertyValueFactory<>("prescription"));
        terDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        statusColumn.setCellValueFactory(cellData -> {
            Therapy therapy = cellData.getValue();
            if (therapy == null) return new SimpleStringProperty("");

            String endStr = therapy.getEndDate();
            if (endStr == null || endStr.isBlank() || endStr.equalsIgnoreCase("-")) {
                return new SimpleStringProperty("Active");
            }

            try {
                LocalDate endDate = LocalDate.parse(endStr);
                if (endDate.isBefore(LocalDate.now())) {
                    return new SimpleStringProperty("Expired");
                } else {
                    return new SimpleStringProperty("Active");
                }
            } catch (Exception e) {
                return new SimpleStringProperty("Active");
            }
        });

        terActionColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button detailsBtn = new Button("Details");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(8, detailsBtn, editBtn, deleteBtn);

            {
                detailsBtn.getStyleClass().add("buttons");
                editBtn.getStyleClass().add("buttons");
                deleteBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");

                detailsBtn.setOnAction(_ -> {
                    Therapy therapy = getTableView().getItems().get(getIndex());
                    if (therapy != null) {
                        handleShowTherapy(therapy);
                    }
                });

                editBtn.setOnAction(_ -> {
                    Therapy therapy = getTableView().getItems().get(getIndex());
                    if (therapy != null) {
                        handleEditTherapy(therapy);
                    }
                });

                deleteBtn.setOnAction(_ -> {
                    Therapy therapy = getTableView().getItems().get(getIndex());
                    if (therapy != null) {
                        handleDeleteTherapy(therapy);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    boolean myPatient = isMyPatient();
                    boolean isDoctor = (loggedDoctor != null);

                    editBtn.setVisible(isDoctor);
                    editBtn.setManaged(isDoctor);

                    deleteBtn.setVisible(myPatient);
                    deleteBtn.setManaged(myPatient);

                    setGraphic(container);
                }
            }
        });
    }

    private void handleEditTherapy(Therapy therapy) {
        if (loggedDoctor == null) {
            showAlert("Permission Denied", "You must be logged in as a doctor to edit therapies.");
            return;
        }

        Dialog<Object> dialog = new Dialog<>();
        dialog.setTitle("Edit Therapy");
        dialog.setHeaderText("Modify therapy details for " + currentPatient.getName() + " " + currentPatient.getLastName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField prescriptionField = new TextField(therapy.getPrescription());
        TextField dailyDoseField = new TextField(String.valueOf(therapy.getDailyDose()));
        TextField amountField = new TextField(String.valueOf(therapy.getAmountIntaken()));
        TextField instructionsField = new TextField(therapy.getInstructions());

        DatePicker startDatePicker = new DatePicker();
        try {
            if (therapy.getStartDate() != null && !therapy.getStartDate().isBlank()) {
                startDatePicker.setValue(LocalDate.parse(therapy.getStartDate()));
            }
        } catch (Exception ignored) {}

        DatePicker endDatePicker = new DatePicker();
        try {
            if (therapy.getEndDate() != null && !therapy.getEndDate().isBlank()) {
                endDatePicker.setValue(LocalDate.parse(therapy.getEndDate()));
            }
        } catch (Exception ignored) {}

        grid.add(new Label("Prescription:"), 0, 0);
        grid.add(prescriptionField, 1, 0);
        grid.add(new Label("Daily Dose:"), 0, 1);
        grid.add(dailyDoseField, 1, 1);
        grid.add(new Label("Amount Intaken:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Instructions:"), 0, 3);
        grid.add(instructionsField, 1, 3);
        grid.add(new Label("Start Date:"), 0, 4);
        grid.add(startDatePicker, 1, 4);
        grid.add(new Label("End Date:"), 0, 5);
        grid.add(endDatePicker, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    therapy.setPrescription(prescriptionField.getText());
                    therapy.setDailyDose(Double.parseDouble(dailyDoseField.getText().trim()));
                    therapy.setAmountIntaken(Double.parseDouble(amountField.getText().trim()));
                    therapy.setInstructions(instructionsField.getText());
                    therapy.setStartDate(startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : "");
                    therapy.setEndDate(endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : "");

                    return therapy;
                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Please enter valid numbers for daily dose and amount.");
                }
            }
            return null;
        });

        Optional<Object> result = dialog.showAndWait();
        if (result.isPresent() && result.get() instanceof Therapy updatedTherapy) {

            therapyRepository.update(updatedTherapy);

            NotificationService.getInstance().notifyTherapyModified(updatedTherapy, loggedDoctor);

            if (!isMyPatient() && medLogHistoryRepository != null) {
                MedLogHistory log = new MedLogHistory(
                        currentPatient,
                        loggedDoctor,
                        java.time.LocalDateTime.now(),
                        "Modified therapy: " + updatedTherapy.getPrescription()
                );
                medLogHistoryRepository.save(log);
            }

            loadPatientData();
            showAlert("Success", "Therapy updated successfully and notification sent.");
        }
    }

    // ==================== DOCTOR'S LOG ====================
    private void setupDocLogTable() {
        doctorColumn.setCellValueFactory(cellData -> {
            var doctor = cellData.getValue().getDoctor();
            return new javafx.beans.property.SimpleStringProperty(
                    doctor != null ? doctor.getCodiceFiscale() : "-"
            );
        });
        docDateColumn.setCellValueFactory(new PropertyValueFactory<>("modifiedAt"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

        docActionColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            {
                viewBtn.getStyleClass().add("buttons");
                viewBtn.setOnAction(_ -> {
                    MedLogHistory log = getTableView().getItems().get(getIndex());
                    if (log != null) {
                        showMedLogDetails(log);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
    }

    private void showMedLogDetails(MedLogHistory log) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Intervention Details");

        String doctorInfo = (log.getDoctor() != null) ? log.getDoctor().getName() + " " + log.getDoctor().getLastName() : defaultIfNull(log.getDoctor().getName());
        alert.setHeaderText("Intervention details by Dr. " + doctorInfo);

        StringBuilder details = new StringBuilder();
        details.append("Date: ").append(log.getModifiedAt() != null ? log.getModifiedAt().toString() : defaultIfNull(log.getModifiedAt().toString())).append("\n");
        details.append("Summary: ").append(defaultIfNull(log.getContent())).append("\n\n");

        if (log.getContent() != null && !log.getContent().isBlank()) {
            details.append("Details:\n").append(log.getContent());
        }

        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleDownloadPdf() {
        if (currentPatient == null) return;

        String patientCf = currentPatient.getCodiceFiscale();

        List<DailyLog> logs = dailyReportRepository != null ? dailyReportRepository.getByPatient(patientCf) : List.of();
        List<MedicalCondition> conditions = medicalConditionRepository != null ? medicalConditionRepository.getByPatient(patientCf) : List.of();
        List<Therapy> therapies = therapyRepository != null ? therapyRepository.getByPatient(patientCf) : List.of();

        String period = (periodChoiceBox != null && periodChoiceBox.getValue() != null) ? periodChoiceBox.getValue() : "All time";

        PDFReportExporter.generateDoctorReport(
                currentPatient,
                currentPatient.getRefDoctor(),
                glycemiaChart,
                period,
                logs,
                conditions,
                therapies
        );

        showAlert("PDF Export", "Full PDF report for patient has been exported to Desktop.");
    }

    private void handleShowTherapy(Therapy therapy) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TherapyDetailView.fxml"));
            Parent root = loader.load();

            TherapyDetailController controller = loader.getController();
            controller.setTherapy(therapy);

            Stage stage = new Stage();
            stage.setTitle("Therapy Detail");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading TherapyDetailView.fxml", e);
        }
    }

    private void handleDeleteTherapy(Therapy therapy) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the therapy: " + therapy.getPrescription() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            therapyRepository.delete(String.valueOf(therapy.getId()));

            NotificationService.getInstance().notifyTherapyDeleted(therapy, loggedDoctor);

            loadTherapies();
            showAlert("Success", "Therapy deleted successfully and notification sent.");
        }
    }

    @FXML
    public void handleAddTherapy() {
        if (!isMyPatient()) {
            showAlert("Permission Denied", "You can only add therapies to your assigned patients.");
            return;
        }

        Dialog<Object> dialog = new Dialog<>();
        dialog.setTitle("Add New Therapy");
        dialog.setHeaderText("Insert therapy details for " + currentPatient.getName() + " " + currentPatient.getLastName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField prescriptionField = new TextField();
        prescriptionField.setPromptText("Drug / Medication");
        TextField dailyDoseField = new TextField();
        dailyDoseField.setPromptText("Daily Dose (e.g. 2.0)");
        TextField amountField = new TextField();
        amountField.setPromptText("Amount Intaken (e.g. 1.0)");
        TextField instructionsField = new TextField();
        instructionsField.setPromptText("Instructions");
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker();

        grid.add(new Label("Prescription:"), 0, 0);
        grid.add(prescriptionField, 1, 0);
        grid.add(new Label("Daily Dose:"), 0, 1);
        grid.add(dailyDoseField, 1, 1);
        grid.add(new Label("Amount Intaken:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Instructions:"), 0, 3);
        grid.add(instructionsField, 1, 3);
        grid.add(new Label("Start Date:"), 0, 4);
        grid.add(startDatePicker, 1, 4);
        grid.add(new Label("End Date:"), 0, 5);
        grid.add(endDatePicker, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String prescription = prescriptionField.getText();
                    double dailyDose = Double.parseDouble(dailyDoseField.getText().trim());
                    double amount = Double.parseDouble(amountField.getText().trim());
                    String instructions = instructionsField.getText();
                    String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : LocalDate.now().toString();
                    String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : "";

                    return new Therapy(
                            prescription,
                            dailyDose,
                            amount,
                            instructions,
                            currentPatient,
                            loggedDoctor,
                            startDate,
                            endDate
                    );
                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Please enter valid numbers for daily dose and amount.");
                }
            }
            return null;
        });

        Optional<Object> result = dialog.showAndWait();
        if (result.isPresent() && result.get() instanceof Therapy newTherapy) {
            therapyRepository.save(newTherapy);

            NotificationService.getInstance().notifyNewTherapy(currentPatient, newTherapy, loggedDoctor);

            loadTherapies();
            showAlert("Success", "Therapy created and notification sent successfully.");
        }
    }

    private String defaultIfNull(String val) {
        return (val != null && !val.isBlank()) ? val : "-";
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}