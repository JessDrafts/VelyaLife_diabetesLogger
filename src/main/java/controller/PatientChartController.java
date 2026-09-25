package controller;

import application.VelyaLifeApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.DailyLog;
import model.Doctor;
import model.Patient;
import repository.DailyLogRepository;
import repository.DoctorRepository;
import repository.PatientRepository;
import utility.Help_DailyLog;
import utility.PDFReportExporter;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static utility.Help_functions.formattedDate;

public class PatientChartController {

    public AnchorPane sidebar;
    @FXML private PatientSidebarController sidebarController;

    // Patient Info Labels
    @FXML private Label nameLabel;
    @FXML private Label cfLabel;
    @FXML private Label ageLabel;
    @FXML private Label weightLabel;
    @FXML private Label sexLabel;
    @FXML private Label smokeLabel;
    @FXML private Label drinkLabel;
    @FXML private Label riskFactorLabel;
    @FXML private Label doctorNoteLabel;

    // Doctor Info Labels
    @FXML private Label docNameLabel;
    @FXML private Label docCfLabel;

    // Chart & Controls
    @FXML private ChoiceBox<String> periodChoiceBox;
    @FXML private LineChart<String, Number> glycemiaChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    // Table & Columns
    @FXML private TableView<DailyLog> measurementsTable;
    @FXML private TableColumn<DailyLog, String> dateColumn;
    @FXML private TableColumn<DailyLog, Void> detailColumn;

    private PatientRepository patientRepo;
    private DoctorRepository doctorRepo;
    private DailyLogRepository dailyLogRepo;
    private String currentUsername;

    @FXML
    public void initialize() {
        patientRepo = VelyaLifeApplication.getPatientRepository();
        doctorRepo = VelyaLifeApplication.getDoctorRepository();
        dailyLogRepo = VelyaLifeApplication.getDailyLogRepository();

        periodChoiceBox.getItems().addAll("Current Day", "Current Week", "Current Month", "Current Year");
        periodChoiceBox.setValue("Current Day");

        periodChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (currentUsername != null && newValue != null) {
                loadChartData(currentUsername, newValue);
            }
        });

        setupTable();
    }

    public void setUser(String username) {
        this.currentUsername = username;
        if (sidebarController != null) {
            sidebarController.setUser(username);
        }
        loadPatientInfo(username);
        loadTableData(username);
        loadChartData(username, periodChoiceBox.getValue());
    }

    public void loadPatientInfo(String authenticatedUser) {
        Patient patient = patientRepo.getById(authenticatedUser);
        if (patient == null) return;

        Doctor refDoctor;
        if (patient.getRefDoctor() != null && patient.getRefDoctor().getCodiceFiscale() != null) {
            refDoctor = doctorRepo.getById(patient.getRefDoctor().getCodiceFiscale());
        }
        else { refDoctor = null; }

        // Personal Info
        cfLabel.setText("Codice Fiscale: " + (patient.getCodiceFiscale() != null ? patient.getCodiceFiscale() : "-"));
        nameLabel.setText("Name: " + patient.getName() + " " + patient.getLastName());
        ageLabel.setText("Date of Birth: " + (patient.getDateBirth() != null ? formattedDate(patient.getDateBirth()) : "-"));
        weightLabel.setText("Weight: " + patient.getWeight() + " kg");
        sexLabel.setText("Sex: " + (patient.getSex() != null ? patient.getSex() : "-"));

        // Medical Info
        smokeLabel.setText("Smoker: " + (patient.getIsSmoker() ? "yes" : "no"));
        drinkLabel.setText("Drinker: " + (patient.getIsDrinker() ? "yes" : "no"));
        riskFactorLabel.setText("Risk factors: " + (patient.getRiskFactor() != null ? patient.getRiskFactor() : "-"));
        doctorNoteLabel.setText("Doctor's note: " + (patient.getDoctorNotes() != null ? patient.getDoctorNotes() : "-"));

        // Doctor Info
        if (refDoctor != null) {
            docNameLabel.setText("Name: " + refDoctor.getName() + " " + refDoctor.getLastName());
            docCfLabel.setText("Codice Fiscale: " + refDoctor.getCodiceFiscale());
        } else {
            docNameLabel.setText("Name: -");
            docCfLabel.setText("Codice Fiscale: -");
        }
    }

    private void setupTable() {
        dateColumn.setCellValueFactory(cellData -> {
            DailyLog log = cellData.getValue();
            String formatted = (log.getCreatedAt() != null) ? log.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
            return new SimpleStringProperty(formatted);
        });

        detailColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button detailBtn = new Button("View");
            {
                detailBtn.getStyleClass().add("buttons");
                detailBtn.setOnAction(_ -> {
                    DailyLog measurement = getTableView().getItems().get(getIndex());
                    openDetailWindow(measurement);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : detailBtn);
            }
        });
    }

    private void loadTableData(String username) {
        List<DailyLog> logs = dailyLogRepo.getByPatient(username);
        ObservableList<DailyLog> observableLogs = FXCollections.observableArrayList(logs);
        measurementsTable.setItems(observableLogs);
    }

    private void loadChartData(String username, String period) {
        List<DailyLog> logs = dailyLogRepo.getByPatient(username);
        LocalDate now = LocalDate.now();

        CategoryAxis activeXAxis = (xAxis != null) ? xAxis : (CategoryAxis) glycemiaChart.getXAxis();
        NumberAxis activeYAxis = (yAxis != null) ? yAxis : (NumberAxis) glycemiaChart.getYAxis();

        if (activeXAxis != null) {
            activeXAxis.setLabel("Time Period");
        }

        if (activeYAxis != null) {
            activeYAxis.setAutoRanging(false);
            activeYAxis.setLowerBound(0);
            activeYAxis.setUpperBound(350);
            activeYAxis.setTickUnit(50);
            activeYAxis.setLabel("Glycemia (mg/dL)");
        }

        glycemiaChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Glycemia (" + period + ")");

        if ("Current Day".equals(period)) {
            List<DailyLog> dayLogs = logs.stream()
                    .filter(l -> l.getCreatedAt() != null && l.getCreatedAt().toLocalDate().equals(now))
                    .sorted(Comparator.comparing(DailyLog::getCreatedAt))
                    .toList();

            for (DailyLog log : dayLogs) {
                String timeLabel = log.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
                series.getData().add(new XYChart.Data<>(timeLabel, log.getBloodSugarLevel()));
            }

        } else if ("Current Week".equals(period)) {
            LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
            Map<LocalDate, List<DailyLog>> groupedByDate = logs.stream()
                    .filter(l -> l.getCreatedAt() != null && !l.getCreatedAt().toLocalDate().isBefore(startOfWeek) && !l.getCreatedAt().toLocalDate().isAfter(now))
                    .collect(Collectors.groupingBy(l -> l.getCreatedAt().toLocalDate()));

            for (int i = 0; i < 7; i++) {
                LocalDate date = startOfWeek.plusDays(i);
                if (date.isAfter(now)) break;
                List<DailyLog> dayLogs = groupedByDate.get(date);
                String dayName = date.getDayOfWeek().name().substring(0, 3);

                if (dayLogs != null && !dayLogs.isEmpty()) {
                    double avg = Help_DailyLog.calculateAverageBloodSugar(dayLogs);
                    series.getData().add(new XYChart.Data<>(dayName, avg));
                } else {
                    series.getData().add(new XYChart.Data<>(dayName, 0));
                }
            }

        } else if ("Current Month".equals(period)) {
            LocalDate startOfMonth = now.withDayOfMonth(1);
            Map<LocalDate, List<DailyLog>> groupedByDate = logs.stream()
                    .filter(l -> l.getCreatedAt() != null && !l.getCreatedAt().toLocalDate().isBefore(startOfMonth) && !l.getCreatedAt().toLocalDate().isAfter(now))
                    .collect(Collectors.groupingBy(l -> l.getCreatedAt().toLocalDate()));

            LocalDate currentDay = startOfMonth;
            while (!currentDay.isAfter(now)) {
                List<DailyLog> dayLogs = groupedByDate.get(currentDay);
                String dateStr = currentDay.format(DateTimeFormatter.ofPattern("dd/MM"));

                if (dayLogs != null && !dayLogs.isEmpty()) {
                    double avg = Help_DailyLog.calculateAverageBloodSugar(dayLogs);
                    series.getData().add(new XYChart.Data<>(dateStr, avg));
                } else {
                    series.getData().add(new XYChart.Data<>(dateStr, 0));
                }
                currentDay = currentDay.plusDays(1);
            }
        } else if ("Current Year".equals(period)) {
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

    private void openDetailWindow(DailyLog measurement) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Measurement Detail");
        alert.setHeaderText("Log ID: " + measurement.getId());
        alert.setContentText("Date: " + measurement.getCreatedAt() +
                "\nGlycemia: " + measurement.getBloodSugarLevel() + " mg/dL" +
                "\nBefore meal: " + (measurement.getBeforeMeal() ? "Yes" : "No") +
                "\nDrugs: " + measurement.getDrugs() + " (Amount: " + measurement.getAmountIntaken() + ")");
        alert.showAndWait();
    }

    @FXML
    private void handleDownloadPdf() {
        if (currentUsername == null || currentUsername.isBlank()) {
            return;
        }

        Patient patient = patientRepo.getById(currentUsername);
        if (patient == null) return;

        Doctor refDoctor = null;
        if (patient.getRefDoctor() != null && patient.getRefDoctor().getCodiceFiscale() != null) {
            refDoctor = doctorRepo.getById(patient.getRefDoctor().getCodiceFiscale());
        }

        String period = periodChoiceBox.getValue() != null ? periodChoiceBox.getValue() : "Current Day";
        List<DailyLog> logs = dailyLogRepo.getByPatient(currentUsername);

        // create pdf
        PDFReportExporter.generateReport(patient, refDoctor, glycemiaChart, period, logs);
    }
}