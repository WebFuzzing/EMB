package io.github.proxyprint.kitchen.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by daniel on 01-06-2016.
 */
public class NgrokConfig {
    private static String PATH_TO_URL = "/tmp/externalURL";
    private static String EXTERNAL_URL = "NOTDEF";

    public static String getExternalUrl() throws IOException {
        if(EXTERNAL_URL.equals("NOTDEF")) {
            BufferedReader reader = null;

            try {
                File file = new File(PATH_TO_URL);
                reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    if(line!=null && line.contains("http")) {
                        line = line.trim();
                        EXTERNAL_URL = line;
                        break;
                    }
                }
                return EXTERNAL_URL;

            } catch (IOException e) {
                return null;
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    return null;
                }
            }
        }
        else {
            return EXTERNAL_URL;
        }
    }
}
