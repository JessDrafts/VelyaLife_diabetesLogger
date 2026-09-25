package model;

import java.time.LocalDate;

public interface IUser {

	String getName();
	String getLastName();
	LocalDate getDateBirth();
	String getPlaceBirth();
	String getNationality();
	String getSex();
	String getTelNumber();
	String getUsername();
    String getPlainPassword();
    String getPassword();
    String getEmail();
    String getType();

	void setName(String name);
	void setLastName(String lastName);
	void setDateBirth(LocalDate date);
	void setPlaceBirth(String dateOfBirth);
	void setNationality(String nationality);
	void setSex(String sex);
	void setTelNumber(String telNumber);
	void setUsername(String username);
	void setPassword(String password);
	void setEmail(String email);
    boolean checkUsername(String username);
    boolean checkPassword(String password);
}
