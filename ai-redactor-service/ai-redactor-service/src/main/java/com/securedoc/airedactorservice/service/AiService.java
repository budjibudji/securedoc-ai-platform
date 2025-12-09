package com.securedoc.airedactorservice.service;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.nlp.translator.NamedEntity;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import jakarta.annotation.PostConstruct;
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

    private ZooModel<String, NamedEntity[]> model;

    // Regex pour détecter les numéros de téléphone (Formats internationaux +1 ..., locaux 06..., avec espaces ou tirets)
    // Exemples capturés : "+1 555 0199", "06 12 34 56 78", "123-456-7890"
    private static final String PHONE_REGEX = "(\\+|00)?\\d{1,3}[-\\s.]?(\\d{1,4}[-\\s.]?){2,5}";

    // Regex simple pour les emails (Bonus : c'est toujours utile dans un CV)
    private static final String EMAIL_REGEX = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";

    @PostConstruct
    public void initModel() {
        String modelUrl = "djl://ai.djl.huggingface.pytorch/dslim/bert-base-NER";

        Criteria<String, NamedEntity[]> criteria = Criteria.builder()
                .setTypes(String.class, NamedEntity[].class)
                .optApplication(Application.NLP.TOKEN_CLASSIFICATION)
                .optEngine("PyTorch")
                .optModelUrls(modelUrl)
                .optProgress(new ProgressBar())
                .build();
        try {
            System.out.println("🤖 [IA] Chargement du modèle depuis : " + modelUrl);
            this.model = criteria.loadModel();
            System.out.println("✅ [IA] Modèle NER chargé avec succès !");
        } catch (IOException | ModelException e) {
            System.err.println("❌ CRITICAL ERROR : Impossible de charger le modèle.");
            throw new RuntimeException("Echec initialisation IA", e);
        }
    }

    public String extractText(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    /**
     * Approche HYBRIDE : Deep Learning + Regex
     */
    public List<String> detectSensitiveInfo(String text) {
        List<String> sensitiveData = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) return sensitiveData;

        // ---------------------------------------------------------
        // 1. DÉTECTION PAR IA (Noms, Lieux, Organisations)
        // ---------------------------------------------------------
        try (Predictor<String, NamedEntity[]> predictor = model.newPredictor()) {

            // Attention : Si le texte est très long (>512 tokens), pense à utiliser le découpage (chunking)
            // vu précédemment. Pour l'instant, on garde simple pour ton CV court.
            NamedEntity[] entities = predictor.predict(text);

            for (NamedEntity entity : entities) {
                String type = entity.getEntity();
                String word = entity.getWord();

                // On garde PER (Personnes), LOC (Lieux), ORG (Entreprises)
                if (type.contains("PER") || type.contains("LOC") || type.contains("ORG")) {
                    // On filtre les tout petits mots (<2 lettres) pour éviter les faux positifs
                    if (!sensitiveData.contains(word) && word.length() > 2) {
                        System.out.println("   🔍 [IA] Détecté : " + word + " [" + type + "]");
                        sensitiveData.add(word);
                    }
                }
            }

        } catch (TranslateException e) {
            System.err.println("❌ Erreur lors de la prédiction IA : " + e.getMessage());
        }

        // ---------------------------------------------------------
        // 2. DÉTECTION PAR REGEX (Téléphones, Emails)
        // ---------------------------------------------------------

        // A. Détection Téléphones
        Pattern phonePattern = Pattern.compile(PHONE_REGEX);
        Matcher phoneMatcher = phonePattern.matcher(text);
        while (phoneMatcher.find()) {
            String phone = phoneMatcher.group().trim();
            // On vérifie que c'est bien un numéro assez long (ex: > 6 chiffres) pour éviter les dates ou codes postaux
            if (!sensitiveData.contains(phone) && phone.replaceAll("\\D", "").length() > 6) {
                System.out.println("   📞 [REGEX] Téléphone détecté : " + phone);
                sensitiveData.add(phone);
            }
        }

        // B. Détection Emails (Bonus)
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        Matcher emailMatcher = emailPattern.matcher(text);
        while (emailMatcher.find()) {
            String email = emailMatcher.group();
            if (!sensitiveData.contains(email)) {
                System.out.println("   📧 [REGEX] Email détecté : " + email);
                sensitiveData.add(email);
            }
        }

        return sensitiveData;
    }
}