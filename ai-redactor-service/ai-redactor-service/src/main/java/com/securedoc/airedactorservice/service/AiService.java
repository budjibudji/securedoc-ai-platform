package com.securedoc.airedactorservice.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiService {

    /**
     * Étape 1 : Extraire tout le texte du PDF
     */
    public String extractText(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);

        document.close();
        return text;
    }

    /**
     * Étape 2 : Détecter les données sensibles (Version simple : Regex + mots clés)
     */
    public List<String> detectSensitiveInfo(String text) {
        List<String> sensitiveData = new ArrayList<>();

        // 1. Emails
        Matcher emailMatcher = Pattern.compile(
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}"
        ).matcher(text);

        while (emailMatcher.find()) {
            sensitiveData.add(emailMatcher.group());
        }

        // 2. Numéros de téléphone marocains
        Matcher phoneMatcher = Pattern.compile(
                "0[5-7][0-9]{8}"
        ).matcher(text);

        while (phoneMatcher.find()) {
            sensitiveData.add(phoneMatcher.group());
        }

        // 3. Détection très simple de noms (mock)
        if (text.contains("Ikram")) sensitiveData.add("Ikram");
        if (text.contains("Khadija")) sensitiveData.add("Khadija");

        return sensitiveData;
    }
}
