package utility;

import model.DailyLog;
import model.Therapy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Help_DailyLog {

    // Calculate Blood sugar avg of a given list
    public static double calculateAverageBloodSugar(List<DailyLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return 0.0;
        }
        return logs.stream()
                .mapToDouble(DailyLog::getBloodSugarLevel)
                .average()
                .orElse(0.0);
    }

    // Return all the logs of a given date
    public static List<DailyLog> filterByDate(List<DailyLog> logs, LocalDate date) {
        if (logs == null) {
            return List.of();
        }
        return logs.stream()
                .filter(log -> log.getCreatedAt().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    // Check sugar level severity
    public static String evaluateGlucoseSeverity(double bloodSugar, boolean beforeMeal) {
        if (beforeMeal) {
            if (bloodSugar < 55) return "CRITICAL LOW";
            if (bloodSugar < 70) return "LOW";
            if (bloodSugar > 200) return "CRITICAL HIGH";
            if (bloodSugar > 130) return "HIGH";
        } else {
            if (bloodSugar < 55) return "CRITICAL LOW";
            if (bloodSugar < 70) return "LOW";
            if (bloodSugar > 250) return "CRITICAL HIGH";
            if (bloodSugar > 180) return "HIGH";
        }
        return "NORMAL";
    }

    // Check if the therapy is active
    public static boolean isTherapyActiveOnDate(Therapy therapy, LocalDate date) {
        if (therapy == null || date == null) return false;
        try {
            LocalDate start = LocalDate.parse(therapy.getStartDate());
            if (date.isBefore(start)) return false;

            if (therapy.getEndDate() != null && !therapy.getEndDate().isBlank() && !therapy.getEndDate().equals("-")) {
                LocalDate end = LocalDate.parse(therapy.getEndDate());
                return !date.isAfter(end);
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Check if the therapy is followed for a specific day.
     * Return false if:
     * 1. No medication was taken.
     * 2. Total amount is not equal to the prescribed one.
     * 3. Single dose does not match the given one.
     */
    public static boolean checkTherapyAdherenceForDay(List<Therapy> activeTherapies, List<DailyLog> dayLogs) {
        if (activeTherapies == null || activeTherapies.isEmpty()) {
            return true; // No active therapy
        }

        if (dayLogs == null || dayLogs.isEmpty()) {
            return false; // No report was logged
        }

        // 1. Calculate the total amount
        double totalPrescribed = 0.0;
        for (Therapy t : activeTherapies) {
            if (t != null) {
                totalPrescribed += t.getDailyDose();
            }
        }

        // 2. Calculate the intaken amount
        double totalIntaken = 0.0;
        for (DailyLog log : dayLogs) {
            if (log != null) {
                totalIntaken += log.getAmountIntaken();
            }
        }

        if (Double.compare(totalPrescribed, totalIntaken) != 0) {
            return false; // Quantity mismatch
        }

        // 3. Control for single therapy
        for (Therapy therapy : activeTherapies) {
            if (therapy == null) continue;

            double intakenForDrug = 0.0;
            for (DailyLog log : dayLogs) {
                if (log != null && log.getDrugs() != null && log.getDrugs().equalsIgnoreCase(therapy.getPrescription())) {
                    intakenForDrug += log.getAmountIntaken();
                }
            }

            if (Double.compare(therapy.getDailyDose(), intakenForDrug) != 0) {
                return false; // quantity mismatch
            }
        }

        return true;
    }

    // check if the therapy was not followed for three consecutive days
    public static boolean hasExactlyThreeConsecutiveNonCompliantDays(List<Therapy> therapies, List<DailyLog> allLogs) {
        if (therapies == null || therapies.isEmpty()) return false;

        LocalDate today = LocalDate.now();

        // Controlla se negli ultimi 3 giorni (oggi, ieri, 2 giorni fa) la terapia NON è stata seguita
        for (int i = 0; i < 3; i++) {
            LocalDate date = today.minusDays(i);

            List<Therapy> activeOnDate = new ArrayList<>();
            for (Object obj : therapies) {
                if (obj instanceof Therapy t) {
                    if (isTherapyActiveOnDate(t, date)) {
                        activeOnDate.add(t);
                    }
                }
            }

            // Se non ci sono terapie attive in questo giorno, non possiamo considerare il giorno come "mancato"
            if (activeOnDate.isEmpty()) {
                return false;
            }

            List<DailyLog> dayLogs = filterByDate(allLogs, date);

            // Se in anche solo uno dei 3 giorni il paziente è stato conforme, non sono 3 giorni consecutivi
            if (checkTherapyAdherenceForDay(activeOnDate, dayLogs)) {
                return false;
            }
        }

        // Se siamo arrivati fin qui, significa che per tutti e 3 i giorni la terapia NON è stata seguita
        return true;
    }
}