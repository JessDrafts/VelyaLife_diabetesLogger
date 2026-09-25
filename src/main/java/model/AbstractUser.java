package model;

import java.security.SecureRandom;
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

public abstract class AbstractUser implements IUser{

	private String name;
	private String lastName;
	private LocalDate dateOfBirth;
	private String placeOfBirth;
	private String nationality;
	private String sex;
	private String telephone;
	private String username;
    private String plainPassword;
    private String password;
    private String email;
    private final String type;

	// Create user for the first time
    public AbstractUser(String name, String lastName, LocalDate dateOfBirth, String placeOfBirth, String nationality, String sex, String telephone, String username, String email, String type) {
        this.name = name;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.placeOfBirth = placeOfBirth;
		this.nationality = nationality;
		this.sex = sex;
		this.telephone = telephone;
		this.username = username;
        this.plainPassword = generatePassword();
        this.password = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        this.email = email;
        this.type = type;
    }

	// Retrieve user from database
    public AbstractUser(String name, String lastName, LocalDate dateOfBirth, String placeOfBirth, String nationality, String sex, String telephone, String username, String hashedPassword, String email, String type) {
		this.name = name;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.placeOfBirth = placeOfBirth;
		this.nationality = nationality;
		this.sex = sex;
		this.telephone = telephone;
		this.username = username;
        this.password = hashedPassword;
        this.email = email;
        this.type = type;
    }

	public String getName() { return name; }

	public String getLastName(){ return lastName; }

	public LocalDate getDateBirth(){ return dateOfBirth; }

	public String getPlaceBirth() { return placeOfBirth; }

	public String getNationality() { return nationality; }

	public String getSex() { return sex; }

	public String getTelNumber() { return telephone; }

	public String getUsername() { return username; }

	public String getPlainPassword(){ return plainPassword; }

	public String getPassword() { return password; }

	public String getEmail() { return email; }

	public String getType() { return type; }

	public void setName(String name) { this.name = name; }

	public void setLastName(String lastName) { this.lastName = lastName; }

	public void setDateBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

	public void setPlaceBirth(String placeOfBirth) { this.placeOfBirth = placeOfBirth; }

	public void setNationality(String nationality) { this.nationality = nationality; }

	public void setSex(String sex) {this.sex = sex; }

	public void setTelNumber(String telNumber) { this.telephone = telNumber; }

	public void setUsername(String username) { this.username = username; }

	public void setPasswordGen() {
		this.plainPassword = generatePassword();
		this.password = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
	}

	@Override
	public void setPassword(String password) {
		this.plainPassword = password;
		this.password = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
	}

	public void setEmail(String email) { this.email = email; }

	public boolean checkUsername(String username) { return this.username.equals(username); }

	public boolean checkPassword(String password) {
		return BCrypt.checkpw(password, this.password);
	}
	
	protected String generatePassword() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#&_-";
        final int PASSWORD_LENGTH = 6;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
