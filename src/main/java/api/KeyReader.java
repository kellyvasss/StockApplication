package api;

import java.io.FileInputStream;
import java.util.Properties;

public class KeyReader {
    Properties properties;

    public KeyReader(String textfile) {

        properties = new Properties();
        String user = System.getProperty("user.home");

        try {
            FileInputStream input = new FileInputStream(
                    user + "/OneDrive/Dokument/Keys/" + textfile + ".txt");
            properties.load(input);
        } catch (Exception e) {
            // Sökvägen är fel
        }
    }
    public String getAPIKey() {
        return properties.getProperty("apiKey");
    }

}
