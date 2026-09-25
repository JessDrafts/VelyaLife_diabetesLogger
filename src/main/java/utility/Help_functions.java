package utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class Help_functions {
	/**
	 * Checks if the codice fiscale is correct
	 * @return true if the codice fiscale is correct else return false
	 */
	public static boolean isValideCodicefiscale(String codicefiscale, String cognome, String nome, LocalDate nascita, char sesso, String comune_nascita, String paese_nascita) {
		if (codicefiscale.length() != 16)
			return false;
		
		return checkCognome(codicefiscale,cognome) && checkNome(codicefiscale, nome) && checkNascita(codicefiscale, nascita, sesso) && 
			   checkLuogoNascita(codicefiscale, comune_nascita, paese_nascita) && checkCharControllo(codicefiscale);
	}
	
	/**
	 * Checks if email is composed correctly
	 * @return true if it is correct, false otherwise
	 */
	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
	            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern p = Pattern.compile(emailRegex);
		
		return email != null && p.matcher(email).matches();
	}

	/**
	 * Check if password follows the correct structure
	 * @param psw input given by user
	 * @return true if all conditions are checked
	 */
	public static boolean isValidPassword(String psw) {
		if (psw == null) {
			return false;
		}
		Pattern special = Pattern.compile(".*[@#&_-].*");

		return psw.length() >= 6
				&& special.matcher(psw).matches()
				&& psw.matches(".*[0-9].*")
				&& psw.matches(".*[a-z].*")
				&& psw.matches(".*[A-Z].*");
	}

	public static String formattedDate(LocalDate Date) {
		if (Date == null) {
			return "dd/MM/yyyy";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return Date.format(formatter);
	}

	public static String formattedDateTime(LocalDateTime Date_Time) {
		if (Date_Time == null) {
			return "dd/MM/yyyy";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return Date_Time.format(formatter);
	}

	private static boolean checkCognome(String codicefiscale, String cognome) {
		codicefiscale = codicefiscale.toUpperCase();
		cognome = cognome.toUpperCase().replaceAll("\\s+","");
		
		int[] letters_count = countConsonants_Vowels(cognome);
		String[] letters = getSubString(cognome);
		
		if (cognome.length() < 3) {
			cognome = cognome.concat("X");
			return cognome.equals(codicefiscale.substring(0, 3));
		}
		
		if (letters_count[1] >= 3) {
			return codicefiscale.substring(0, 3).equals(letters[1].substring(0, 3));
		}
		else {
			return codicefiscale.substring(0, 3).equals(letters[1].concat(letters[0].substring(0, 1)));
		}
	}
	
	private static boolean checkNome(String codicefiscale, String nome) {
		codicefiscale = codicefiscale.toUpperCase();
		nome = nome.toUpperCase().replaceAll("\\s+","");
		
		int[] letters_count = countConsonants_Vowels(nome);
		String[] letters = getSubString(nome);
		
		if (nome.length() < 3) {
			nome = nome.concat("X");
			return nome.equals(codicefiscale.substring(3, 6));
		}
		
		if (letters_count[1] == 3) {
			return codicefiscale.substring(3, 6).equals(letters[1]);
		}
		else {
			if (letters_count[1] > 3) {
				return codicefiscale.substring(3, 6).equals(letters[1].substring(0, 1).concat(letters[1].substring(2,3)).concat(letters[1].substring(3,4)));
			}
			return codicefiscale.substring(3, 6).equals(letters[1].concat(letters[0].substring(0, 1)));
		}
	}
	
	private static boolean checkNascita(String codicefiscale, LocalDate nascita, char sesso) {
		codicefiscale = codicefiscale.toUpperCase();
		sesso = Character.toUpperCase(sesso);
		String[] months = new String[] {"A", "B", "C", "D", "E", "H", "L", "M", "P", "R", "S", "T"};
		String dataCF;
		
		dataCF = String.valueOf(nascita.getYear()).substring(2) + months[nascita.getMonthValue() - 1];
		if (sesso == 'F') {
			return codicefiscale.substring(6, 11).equals(dataCF + String.format("%02d", nascita.getDayOfMonth() + 40));
		}
		else if(sesso == 'M') {
			return codicefiscale.substring(6, 11).equals(dataCF + String.format("%02d", nascita.getDayOfMonth()));
		}
		else {
			return false;
		}
	}
	
	private static boolean checkLuogoNascita(String codicefiscale, String comune_nascita, String paese_nascita) {

	    codicefiscale = codicefiscale.toUpperCase();

	    String codiceLuogo = codicefiscale.substring(11, 15);

	    // Nato in Italia
	    if (comune_nascita != null && !comune_nascita.isBlank()) {

	    	String comune = CodiciCatastali.getComune(codiceLuogo);

	    	return comune != null &&
	    	       comune.equalsIgnoreCase(comune_nascita.trim());
	    }

	    // Nato all'estero
	    if (paese_nascita != null && !paese_nascita.isBlank() && !paese_nascita.equalsIgnoreCase("ITALIA")) {

	        String stato = CodiciCatastali.getStato(codiceLuogo);

	        return stato != null &&
	               stato.equalsIgnoreCase(paese_nascita.trim().toUpperCase());
	    }

	    return false;
	}
	
	private static boolean checkCharControllo(String codicefiscale) {
		codicefiscale = codicefiscale.toUpperCase();
	    if (codicefiscale.length() != 16) {
	        return false;
	    }

	    int somma = 0;

	    for (int i = 0; i < 15; i++) {

	        char c = codicefiscale.charAt(i);

	        if (i % 2 == 0) {
	            somma += getValoreDispari(c);
	        } else {
	            somma += getValorePari(c);
	        }
	    }

	    int resto = somma % 26;

	    char carattereCalcolato = (char) ('A' + resto);

	    return carattereCalcolato == codicefiscale.charAt(15);
	}
	
	private static int[] countConsonants_Vowels(String input) {
	    Set<Character> vowelsSet = new HashSet<>();
	    for (char ch : "aeiouAEIOU".toCharArray()) {
	        vowelsSet.add(ch);
	    }

	    int[] count = new int[2];

	    for (char ch : input.toCharArray()) {
	        if (Character.isLetter(ch)) {
	            if (vowelsSet.contains(ch)) {
	                count[0]++; //vowels
	            } else {
	                count[1]++; //consonants
	            }
	        }
	    }
	    return count;
	}

	private static String[] getSubString(String input) {
	    Set<Character> vowelsSet = new HashSet<>();
	    for (char ch : "aeiouAEIOU".toCharArray()) {
	        vowelsSet.add(ch);
	    }
	    
	    String[] letters = new String[] {"",""};
	
	    for (char ch : input.toCharArray()) {
	        if (Character.isLetter(ch)) {
	            if (vowelsSet.contains(ch)) {
	            	letters[0] = letters[0].concat(String.valueOf(ch));
	            }
	            else {
	                letters[1] = letters[1].concat(String.valueOf(ch));
	            }
	        }
	    }
	    return letters;
	}
	
	private static int getValorePari(char c) {

	    if (Character.isDigit(c)) {
	        return c - '0';
	    }

	    return c - 'A';
	}
	
	private static int getValoreDispari(char c) {

        return switch (c) {
            case '0', 'A' -> 1;
            case '1', 'B' -> 0;
            case '2', 'C' -> 5;
            case '3', 'D' -> 7;
            case '4', 'E' -> 9;
            case '5', 'F' -> 13;
            case '6', 'G' -> 15;
            case '7', 'H' -> 17;
            case '8', 'I' -> 19;
            case '9', 'J' -> 21;
            case 'K' -> 2;
            case 'L' -> 4;
            case 'M' -> 18;
            case 'N' -> 20;
            case 'O' -> 11;
            case 'P' -> 3;
            case 'Q' -> 6;
            case 'R' -> 8;
            case 'S' -> 12;
            case 'T' -> 14;
            case 'U' -> 16;
            case 'V' -> 10;
            case 'W' -> 22;
            case 'X' -> 25;
            case 'Y' -> 24;
            case 'Z' -> 23;
            default -> -1;
        };
	}
}
