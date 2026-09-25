package utility;

import application.VelyaLifeApplication;
import model.AbstractUser;
import model.Doctor;
import model.Patient;
import repository.DoctorRepository;
import repository.PatientRepository;
import repository.UserRepository;

public class UserService {
    private final UserRepository userRepo = VelyaLifeApplication.getUserRepository();
    private final DoctorRepository doctorRepo = VelyaLifeApplication.getDoctorRepository();
    private final PatientRepository patientRepo = VelyaLifeApplication.getPatientRepository();

    public void registerUserToDb(AbstractUser u) {
        if (u == null || u.getType() == null) {
            System.err.println("Cannot register a null user or user without a type.");
            return;
        }

        userRepo.save(u);
        if (u.getType().equalsIgnoreCase("Doctor") && u instanceof Doctor) {
            doctorRepo.save((Doctor) u);
        } else if (u.getType().equalsIgnoreCase("Patient") && u instanceof Patient) {
            patientRepo.save((Patient) u);
        }
    }

    public void deleteUserFromDb(AbstractUser u) {
        if (u == null || u.getUsername() == null) {
            System.err.println("Cannot delete a null user or user without a username.");
            return;
        }

        userRepo.delete(u.getUsername());
    }
}