package com.securedoc.airedactorservice.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RedactionService {

    /**
     * Méthode principale : prend un PDF + liste des mots sensibles
     * Retourne un PDF modifié (byte[])
     */
    public byte[] redactDocument(InputStream inputStream, List<String> sensitiveWords) throws IOException {

        PDDocument document = PDDocument.load(inputStream);

        // Parcourir toutes les pages du document
        for (PDPage page : document.getPages()) {

            // Trouver les coordonnées des mots à flouter
            List<PDRectangle> rectsToRedact = findWordCoordinates(document, page, sensitiveWords);

            // Dessiner les rectangles noirs
            if (!rectsToRedact.isEmpty()) {
                try (PDPageContentStream contentStream = new PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true,
                        true
                )) {
                    contentStream.setNonStrokingColor(0, 0, 0); // Couleur noire

                    for (PDRectangle rect : rectsToRedact) {
                        contentStream.addRect(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getWidth(), rect.getHeight());
                        contentStream.fill();
                    }
                }
            }
        }

        // Transformer le document PDF modifié en tableau d'octets
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();

        return out.toByteArray();
    }

    /**
     * Trouver les coordonnées précises des mots sensibles dans la page
     */
    private List<PDRectangle> findWordCoordinates(PDDocument document, PDPage page, List<String> wordsToFind) throws IOException {

        final List<PDRectangle> rectangles = new ArrayList<>();

        PDFTextStripper stripper = new PDFTextStripper() {

            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {

                String content = text; // Le texte de la ligne

                // Pour chaque mot sensible à trouver
                for (String word : wordsToFind) {
                    if (word == null || word.isEmpty()) continue;

                    // On cherche toutes les occurrences du mot dans la ligne
                    int index = 0;
                    while ((index = content.indexOf(word, index)) != -1) {

                        // Si le mot est trouvé, on récupère ses indices de début et de fin
                        int endIndex = index + word.length();

                        // SÉCURITÉ : Vérifier que les indices existent dans textPositions
                        // (Parfois PDFBox a des décalages entre String et TextPosition)
                        if (index < textPositions.size() && endIndex <= textPositions.size()) {

                            TextPosition firstChar = textPositions.get(index);
                            TextPosition lastChar = textPositions.get(endIndex - 1);

                            // --- CALCUL DES COORDONNÉES PRÉCISES DU MOT ---

                            // X : Position gauche du premier caractère
                            float x = firstChar.getXDirAdj();

                            // Largeur : (Position X du dernier char + sa largeur) - Position X du premier char
                            float width = (lastChar.getXDirAdj() + lastChar.getWidthDirAdj()) - x;

                            // Hauteur et Y : On prend la hauteur max du mot
                            float pageHeight = firstChar.getPageHeight();
                            float fontSize = firstChar.getHeightDir();

                            // Y : PDFBox compte Y depuis le haut, PDRectangle depuis le bas.
                            // On se place à la ligne de base (baseline) et on descend un peu (-2) pour couvrir les lettres comme "g" ou "y"
                            float y = pageHeight - firstChar.getYDirAdj() - 2;

                            // Hauteur : On ajoute un peu de marge (+4) pour que ce soit joli
                            float height = fontSize + 4;

                            rectangles.add(new PDRectangle(x, y, width, height));
                        }

                        // On continue de chercher plus loin dans la ligne
                        index += word.length();
                    }
                }
                super.writeString(text, textPositions);
            }
        };

        stripper.setSortByPosition(true);
        stripper.setStartPage(document.getPages().indexOf(page) + 1);
        stripper.setEndPage(document.getPages().indexOf(page) + 1);

        // On lance l'analyse (écrit dans le vide, mais déclenche writeString)
        stripper.writeText(document, new OutputStreamWriter(new ByteArrayOutputStream()));

        return rectangles;
    }
}