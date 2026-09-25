package utility;

import model.AbstractUser;
import model.Doctor;
import model.Patient;
import java.time.LocalDate;

public class UserFactory {

    // first time register user
    public static Patient createPatient(
            String name, String lastName, LocalDate dateOfBirth, String placeOfBirth,
            String nationality, String sex, String codiceFiscale, double weight,
            boolean smoker, boolean drinker, String riskFactor, Doctor refDoctor,
            String telephone, String email) {

        return new Patient(
                name, lastName, dateOfBirth, placeOfBirth, nationality, sex,
                codiceFiscale, weight, smoker, drinker, riskFactor, refDoctor,
                telephone, email
        );
    }

    public static Doctor createDoctor(
            String name, String lastName, LocalDate dateOfBirth, String placeOfBirth,
            String nationality, String sex, String codiceFiscale, String telephone,
            String email) {

        return new Doctor(
                name, lastName, dateOfBirth, placeOfBirth, nationality, sex,
                codiceFiscale, telephone, email
        );
    }

    // 2. Retrieve from DB
    public static AbstractUser fromDatabase(
            String role, String name, String lastName, LocalDate dateOfBirth,
            String placeOfBirth, String nationality, String sex, String telephone,
            String codiceFiscale, String hashedPassword, String email, String type,
            double weight, boolean smoker, boolean drinker, String riskFactor,
            Doctor refDoctor, String doctorNotes) {

        if (role == null) {
            return null;
        }

        if (role.equalsIgnoreCase("Doctor")) {
            return new Doctor(
                    name, lastName, dateOfBirth, placeOfBirth, nationality, sex,
                    telephone, codiceFiscale, hashedPassword, email, type
            );
        } else if (role.equalsIgnoreCase("Patient")) {
            return new Patient(
                    name, lastName, dateOfBirth, placeOfBirth, nationality, sex,
                    weight, smoker, drinker, riskFactor, refDoctor, doctorNotes,
                    telephone, codiceFiscale, hashedPassword, email, type
            );
        }
        return null;
    }
}