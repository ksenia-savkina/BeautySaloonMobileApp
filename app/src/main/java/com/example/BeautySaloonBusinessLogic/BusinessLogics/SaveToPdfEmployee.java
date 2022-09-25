package com.example.BeautySaloonBusinessLogic.BusinessLogics;

import android.content.Context;

import com.example.BeautySaloonBusinessLogic.HelperModels.PdfInfoEmployee;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportCosmeticsViewModel;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SaveToPdfEmployee {

    private static File filePath = null;

    public static void createDoc(PdfInfoEmployee info, Context context) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        filePath = new File(context.getExternalFilesDir(null), info.fileName);
        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();
            Document document = new Document(pdfDoc);

            String FONT_FILENAME = "/assets/arial.ttf";
            PdfFont font = PdfFontFactory.createFont(FONT_FILENAME, PdfEncodings.IDENTITY_H);
            document.setFont(font);

            Paragraph paragraph = new Paragraph(info.title);
            paragraph.setFontSize(16);
            paragraph.setTextAlignment(TextAlignment.CENTER);
            document.add(paragraph);

            paragraph = new Paragraph("С " + formatter.format(info.dateFrom) + " по " + formatter.format(info.dateTo));
            paragraph.setFontSize(14);
            paragraph.setTextAlignment(TextAlignment.CENTER);
            document.add(paragraph);

            float[] pointColumnWidths = {150F, 150F, 150F, 150F};
            Table table = new Table(pointColumnWidths);
            table.addCell(new Cell().add(new Paragraph("Вид услуги")));
            table.addCell(new Cell().add(new Paragraph("Дата оказания услуги")));
            table.addCell(new Cell().add(new Paragraph("Косметика")));
            table.addCell(new Cell().add(new Paragraph("Количество")));

            for (ReportCosmeticsViewModel cosmetic : info.cosmetics) {
                table.addCell(new Cell().add(new Paragraph(cosmetic.typeOfService)));
                table.addCell(new Cell().add(new Paragraph(formatter.format(cosmetic.dateOfService))));
                table.addCell(new Cell().add(new Paragraph(cosmetic.cosmeticName)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(cosmetic.count))));
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}