package test;

import model.Doctor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

    Doctor d1 = new Doctor(
            "Charles",
                    "Dixon",
            LocalDate.of(1985, 9, 11),
            "London",
                    "UK",
                    "M",
                    "SHFCSHFCS0",
                    "913-301-0149",
                    "CharlesDixon@jourrapide.com"
                    );
    @Test
    void getName(){
        assertEquals("Charles", d1.getName());
    }

    @Test
    void getLastName(){
        assertEquals("Dixon", d1.getLastName());
    }

    @Test
    void getDateOfBirth(){
        assertEquals(LocalDate.of(1985, 9, 11), d1.getDateBirth());
    }

    @Test
    void getPlaceBirth() {
        assertEquals("Trento", d1.getPlaceBirth());
    }

    @Test
    void getNationality() {
        assertEquals("Italia", d1.getNationality());
    }

    @Test
    void getSex() {
        assertEquals("M", d1.getSex());
    }

    @Test
    void getCodiceFiscale() {
        assertEquals("DXNCRL85P10L378O", d1.getCodiceFiscale());
    }

    @Test
    void getTelNumber() {
        assertEquals("913-301-0149", d1.getTelNumber());
    }

    @Test
    void getUsername() {
        assertEquals("DXNCRL85P10L378O", d1.getUsername());
    }

    @Test
    void getEmail() {
        assertEquals("CharlesDixon@jourrapide.com", d1.getEmail());
    }

    @Test
    void getType() {
        assertEquals("Doctor", d1.getType());
    }

    @Test
    void setName() {
        d1.setName("Violetta");
    }

    @Test
    void setLastName() {
        d1.setLastName("Castiglione");
    }

    @Test
    void setDateBirth() {
        d1.setDateBirth(LocalDate.of(1996, 7, 18));
    }

    @Test
    void setPlaceBirth() {
        d1.setPlaceBirth("Milano");
    }

    @Test
    void setNationality() {
        d1.setNationality("Italia");
    }

    @Test
    void setSex() {
        d1.setSex("F");
    }

    @Test
    void setCodiceFiscale() {
        d1.setCodiceFiscale("CSTVTT96L58F205N");
    }

    @Test
    void setTelNumber() {
        d1.setTelNumber("0319 0223676");
    }

    @Test
    void setUsername() {
        d1.setUsername("CSTVTT96L58F205N");
    }

    @Test
    void setEmail() {
        d1.setEmail("ViolettaCastiglione@armyspy.com");
    }

    @Test
    void checkUsername() {
        d1.checkUsername("CSTVTT96L58F205N");
    }

    @Test
    void DoctortoString() {
        assertNotNull(d1.toString());
    }

}