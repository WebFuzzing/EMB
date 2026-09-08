package it.unipi.LoveMining.service.utility;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class InterestExtractorService {

    private final Map<String, String> interestsMap = new HashMap<>();

    @PostConstruct
    public void loadInterestsFromCsv() {
        try {
            ClassPathResource resource = new ClassPathResource("interessi.csv");
            BufferedReader reader = new BufferedReader( new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String line;
            // skip first line if present: (Display_Name;Original_Variants)
            // reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // 1. Split Display_Name from Original_Variants by ;
                String[] parts = line.split(";");
                if (parts.length < 2) continue;

                String displayName = parts[0].trim();
                String variantsString = parts[1].trim();

                // 2. Slit variants by ,
                String[] variants = variantsString.split(",");

                for (String variant : variants) {
                    String cleanVariant = variant.trim().toLowerCase();
                    if (!cleanVariant.isEmpty()) {
                        // map variant->display_name
                        interestsMap.put(cleanVariant, displayName);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR loading file interessi.csv: " + e.getMessage());
        }
    }

    public Set<String> extractInterestsFromText(String text) {
        Set<String> foundInterests = new HashSet<>();

        if (text == null || text.isEmpty()) {
            return foundInterests;
        }

        String lowerCaseText = text.toLowerCase();

        for (Map.Entry<String, String> entry : interestsMap.entrySet()) {
            String variant = entry.getKey();
            String displayName = entry.getValue();

            // "\\b" for "Word Boundary"
            String regex = "\\b" + Pattern.quote(variant) + "\\b";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(lowerCaseText);

            if (matcher.find()) {
                foundInterests.add(displayName);
            }
        }
        return foundInterests;
    }
}