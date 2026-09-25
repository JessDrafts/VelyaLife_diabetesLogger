package utility;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import model.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDFReportExporter {

    private static final Logger LOGGER = Logger.getLogger(PDFReportExporter.class.getName());

    // --- Method for patient ---
    public static void generateReport(Patient patient, Doctor doctor, Node chartNode, String timeframe, List<DailyLog> logs) {
        generateDoctorReport(patient, doctor, chartNode, timeframe, logs, null, null);
    }

    // --- New method for doctor ---
    public static void generateDoctorReport(
            Patient patient,
            Doctor doctor,
            Node chartNode,
            String timeframe,
            List<DailyLog> logs,
            List<MedicalCondition> conditions,
            List<Therapy> therapies
    ) {
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        String fileName = "Full_Report_" + patient.getCodiceFiscale() + "_" + timeframe.replace(" ", "_") + ".pdf";
        File pdfDestination = new File(desktopDir, fileName);

        File tempImageFile = null;

        try (PDDocument document = new PDDocument()) {
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            // ==================== PAGE 1: DASHBOARD & GRAPH ====================
            PDPage page1 = new PDPage(PDRectangle.A4);
            document.addPage(page1);

            try (PDPageContentStream cs = new PDPageContentStream(document, page1)) {
                float startX = 40;
                float startY = 800;
                float pageWidth = page1.getMediaBox().getWidth() - (startX * 2);

                // Patient Info
                float yLeft = startY;
                drawText(cs, fontBold, 13, startX, yLeft, "Patient Information");
                yLeft -= 18;
                yLeft = drawTextLine(cs, fontRegular, startX, yLeft, "Name: " + safeStr(patient.getName()) + " " + safeStr(patient.getLastName()));
                yLeft = drawTextLine(cs, fontRegular, startX, yLeft, "Codice Fiscale: " + safeStr(patient.getCodiceFiscale()));
                yLeft = drawTextLine(cs, fontRegular, startX, yLeft, "Date of Birth: " + (patient.getDateBirth() != null ? patient.getDateBirth().toString() : "-"));
                yLeft = drawTextLine(cs, fontRegular, startX, yLeft, "Weight: " + patient.getWeight() + " kg   |   Sex: " + safeStr(patient.getSex()));

                // Doctor Info
                float rightColX = 320;
                float yRight = startY;
                drawText(cs, fontBold, 13, rightColX, yRight, "Doctor Information");
                yRight -= 18;
                if (doctor != null) {
                    yRight = drawTextLine(cs, fontRegular, rightColX, yRight, "Name: " + safeStr(doctor.getName()) + " " + safeStr(doctor.getLastName()));
                    yRight = drawTextLine(cs, fontRegular, rightColX, yRight, "Codice Fiscale: " + safeStr(doctor.getCodiceFiscale()));
                } else {
                    yRight = drawTextLine(cs, fontRegular, rightColX, yRight, "Name: -");
                    yRight = drawTextLine(cs, fontRegular, rightColX, yRight, "Codice Fiscale: -");
                }

                float currentY = Math.min(yLeft, yRight) - 20;

                // Medical Information
                drawText(cs, fontBold, 13, startX, currentY, "Medical Information");
                currentY -= 18;

                String smokerDrinker = "Smoker: " + (patient.getIsSmoker() ? "yes" : "no") + "         Drinker: " + (patient.getIsDrinker() ? "yes" : "no");
                currentY = drawTextLine(cs, fontRegular, startX, currentY, smokerDrinker);

                drawText(cs, fontBold, 10, startX, currentY, "Risk factors: ");
                float riskLabelWidth = fontBold.getStringWidth("Risk factors: ") / 1000 * 10;
                currentY = drawWrappedText(cs, fontRegular, startX + riskLabelWidth, currentY, pageWidth - riskLabelWidth, safeStr(patient.getRiskFactor()));
                currentY -= 5;

                drawText(cs, fontBold, 10, startX, currentY, "Doctor's note: ");
                float noteLabelWidth = fontBold.getStringWidth("Doctor's note: ") / 1000 * 10;
                currentY = drawWrappedText(cs, fontRegular, startX + noteLabelWidth, currentY, pageWidth - noteLabelWidth, safeStr(patient.getDoctorNotes()));

                // Graph Snapshot
                if (chartNode != null) {
                    WritableImage fxImage = chartNode.snapshot(null, null);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
                    tempImageFile = File.createTempFile("chart_snapshot_", ".png");
                    ImageIO.write(bufferedImage, "png", tempImageFile);
                    PDImageXObject pdImage = PDImageXObject.createFromFileByContent(tempImageFile, document);

                    currentY -= 20;
                    drawText(cs, fontBold, 13, startX, currentY, "Glycemia Trend");
                    drawText(cs, fontRegular, 10, startX + 110, currentY, "[" + timeframe + "]");
                    currentY -= 15;

                    float maxImgHeight = currentY - 40;
                    float imgWidth = pageWidth;
                    float scale = imgWidth / pdImage.getWidth();
                    float imgHeight = pdImage.getHeight() * scale;

                    if (imgHeight > maxImgHeight) {
                        imgHeight = maxImgHeight;
                        imgWidth = pdImage.getWidth() * (imgHeight / pdImage.getHeight());
                    }

                    cs.drawImage(pdImage, startX, currentY - imgHeight, imgWidth, imgHeight);
                }
            }

            // ==================== PAGE 2: THERAPY AND PATHOLOGY (for doctor) ====================
            if ((therapies != null && !therapies.isEmpty()) || (conditions != null && !conditions.isEmpty())) {
                PDPage pageMedical = new PDPage(PDRectangle.A4);
                document.addPage(pageMedical);

                try (PDPageContentStream cs = new PDPageContentStream(document, pageMedical)) {
                    float startX = 40;
                    float currentY = 800;

                    // Therapy table
                    if (therapies != null && !therapies.isEmpty()) {
                        drawText(cs, fontBold, 14, startX, currentY, "Active Therapies");
                        currentY -= 20;

                        for (Therapy t : therapies) {
                            if (currentY < 100) break;
                            String line = "• " + safeStr(t.getPrescription()) + " - Dose: " + t.getDailyDose() + " (Start: " + safeStr(t.getStartDate()) + " - End: " + safeStr(t.getEndDate()) + ")";
                            drawText(cs, fontRegular, 10, startX + 10, currentY, line);
                            currentY -= 15;
                        }
                        currentY -= 20;
                    }

                    // Med condition table
                    if (conditions != null && !conditions.isEmpty()) {
                        drawText(cs, fontBold, 14, startX, currentY, "Medical Conditions");
                        currentY -= 20;

                        for (MedicalCondition mc : conditions) {
                            if (currentY < 100) break;
                            String line = "• " + safeStr(mc.getType()) + ": " + safeStr(mc.getDescription()) + " (" + safeStr(mc.getStart()) + " to " + safeStr(mc.getEnd()) + ")";
                            drawText(cs, fontRegular, 10, startX + 10, currentY, line);
                            currentY -= 15;
                        }
                    }
                }
            }

            // ==================== PAGE 3: DAILY LOGS ====================
            if (logs != null && !logs.isEmpty()) {
                PDPage pageLogs = new PDPage(PDRectangle.A4);
                document.addPage(pageLogs);

                try (PDPageContentStream cs = new PDPageContentStream(document, pageLogs)) {
                    float startX = 40;
                    float currentY = 800;

                    drawText(cs, fontBold, 15, startX, currentY, "Measurements - " + timeframe);
                    currentY -= 25;

                    float col2X = startX + 180;
                    float col3X = startX + 340;
                    float rowHeight = 22;

                    cs.setNonStrokingColor(230 / 255.0f, 230 / 255.0f, 230 / 255.0f);
                    cs.addRect(startX, currentY - 5, 515, rowHeight);
                    cs.fill();
                    cs.setNonStrokingColor(0.0f, 0.0f, 0.0f);

                    drawText(cs, fontBold, 10, startX + 5, currentY, "Date / Time");
                    drawText(cs, fontBold, 10, col2X + 5, currentY, "Glycemia (mg/dL)");
                    drawText(cs, fontBold, 10, col3X + 5, currentY, "Meal Status");

                    currentY -= rowHeight;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                    for (DailyLog log : logs) {
                        if (currentY < 50) break;

                        cs.setStrokingColor(220 / 255.0f, 220 / 255.0f, 220 / 255.0f);
                        cs.moveTo(startX, currentY - 5);
                        cs.lineTo(startX + 515, currentY - 5);
                        cs.stroke();

                        String dateStr = (log.getCreatedAt() != null) ? log.getCreatedAt().format(formatter) : "-";
                        String glycemiaStr = String.valueOf(log.getBloodSugarLevel());
                        String mealStr = log.getBeforeMeal() ? "Before meal" : "After meal";

                        drawText(cs, fontRegular, 10, startX + 5, currentY, dateStr);
                        drawText(cs, fontRegular, 10, col2X + 5, currentY, glycemiaStr);
                        drawText(cs, fontRegular, 10, col3X + 5, currentY, mealStr);

                        currentY -= rowHeight;
                    }
                }
            }

            document.save(pdfDestination);
            LOGGER.info(() -> "PDF report successfully saved: " + pdfDestination.getAbsolutePath());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error generating PDF report", e);
        } finally {
            if (tempImageFile != null && tempImageFile.exists()) {
                tempImageFile.delete();
            }
        }
    }

    // --- HELPER METHOD---
    private static void drawText(PDPageContentStream cs, PDType1Font font, float fontSize, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "-");
        cs.endText();
    }

    private static float drawTextLine(PDPageContentStream cs, PDType1Font font, float x, float y, String text) throws IOException {
        drawText(cs, font, 10f, x, y, text);
        return y - (10f * 1.4f);
    }

    private static float drawWrappedText(PDPageContentStream cs, PDType1Font font, float x, float y, float maxWidth, String text) throws IOException {
        if (text == null || text.isBlank()) text = "-";

        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            float lineWidth = font.getStringWidth(testLine) / 1000 * 10f;

            if (lineWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        float leading = 10f * 1.3f;
        for (int i = 0; i < lines.size(); i++) {
            float lineX = (i == 0) ? x : x - (font.getStringWidth("Risk factors: ") / 1000 * 10f);
            cs.beginText();
            cs.setFont(font, 10f);
            cs.newLineAtOffset(lineX, y);
            cs.showText(lines.get(i));
            cs.endText();
            y -= leading;
        }

        return y;
    }

    private static String safeStr(String input) {
        return (input == null || input.isBlank()) ? "-" : input;
    }
}