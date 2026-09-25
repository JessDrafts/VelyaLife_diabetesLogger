package utility;

import application.VelyaLifeApplication;
import org.mindrot.jbcrypt.BCrypt;

import model.AbstractUser;
import repository.UserRepository;

import java.time.LocalDate;

public class AdminCreator extends AbstractUser{
	
	public AdminCreator(String username, String hashedPassword, String email) {
		super("Admin", "",LocalDate.of(1900, 1, 1), "", "", "", "",  username, hashedPassword, email, "ADMIN");
	}
	
	public String toString() {
		return "Username: " + this.getUsername() + "\n" +
				"Email: " + this.getEmail() + "\n" +
				"Role: " + this.getType();
	}

    public static void launch() {
        
        UserRepository userRepo = VelyaLifeApplication.getUserRepository();
        UserService userService = new UserService();
        if (!userRepo.usernameExists("admin")) {
            AdminCreator admin = new AdminCreator("admin", BCrypt.hashpw("@admin123", BCrypt.gensalt()), "info.velyalifeteam@gmail.com");
            userService.registerUserToDb(admin);
        }
    }
}
