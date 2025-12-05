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
     * Trouver les coordonnées des mots sensibles dans la page
     */
    private List<PDRectangle> findWordCoordinates(PDDocument document, PDPage page, List<String> wordsToFind) throws IOException {

        final List<PDRectangle> rectangles = new ArrayList<>();

        PDFTextStripper stripper = new PDFTextStripper() {

            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {

                String cleanText = text.trim();

                // Vérifier si la zone contient un des mots sensibles
                for (String sensitive : wordsToFind) {
                    if (cleanText.contains(sensitive)) {

                        float x = textPositions.get(0).getXDirAdj();
                        float y = textPositions.get(0).getPageHeight()
                                - textPositions.get(0).getYDirAdj()
                                - textPositions.get(0).getHeightDir();
                        float width = textPositions.get(textPositions.size() - 1).getXDirAdj()
                                + textPositions.get(textPositions.size() - 1).getWidthDirAdj() - x;
                        float height = textPositions.get(0).getHeightDir();

                        rectangles.add(new PDRectangle(x, y, width, height));
                    }
                }

                super.writeString(text, textPositions);
            }
        };

        stripper.setSortByPosition(true);
        stripper.setStartPage(document.getPages().indexOf(page) + 1);
        stripper.setEndPage(document.getPages().indexOf(page) + 1);

        // On exécute le strip (n'écrit rien, juste "écoute" le texte)
        stripper.writeText(document, new OutputStreamWriter(new ByteArrayOutputStream()));

        return rectangles;
    }
}
