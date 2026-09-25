package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CodiciCatastali {

    private static final Map<String, String> comuni = new HashMap<>();
    private static final Map<String, String> stati = new HashMap<>();

    static {
        caricaCSV("/data/comuni.csv", comuni);
        caricaCSV("/data/stati_esteri.csv", stati);
    }

    private static void caricaCSV(String file, Map<String, String> mappa) {

        try (InputStream is = CodiciCatastali.class.getResourceAsStream(file)) {
            assert is != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                String riga;

                while ((riga = br.readLine()) != null) {

                    String[] parti = riga.split(";");

                    if (parti.length == 2) {
                        mappa.put(parti[0].toUpperCase(),
                                  parti[1].toUpperCase());
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Error upload " + file);
        }
    }

    public static String getComune(String codice) {
        return comuni.get(codice.toUpperCase());
    }

    public static String getStato(String codice) {
        return stati.get(codice.toUpperCase());
    }

    public static boolean contieneComune(String codice) {
        return comuni.containsKey(codice.toUpperCase());
    }

    public static boolean contieneStato(String codice) {
        return stati.containsKey(codice.toUpperCase());
    }
}