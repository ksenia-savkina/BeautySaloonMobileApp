package com.example.BeautySaloonBusinessLogic.BusinessLogics;

import android.content.Context;
import android.util.Pair;

import com.example.BeautySaloonBusinessLogic.HelperModels.WordInfoEmployee;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportPurchaseCosmeticViewModel;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SaveToWordEmployee {

    private static File filePath = null;

    public static void createDoc(WordInfoEmployee info, Context context) {
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
            XWPFDocument xwpfDocument = new XWPFDocument();
            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();
            XWPFRun xwpfRun = xwpfParagraph.createRun();

            xwpfParagraph.setAlignment(ParagraphAlignment.CENTER);

            xwpfRun.setText(info.title);
            xwpfRun.setFontSize(14);

            List<Pair<String, List<Double>>> listDictionary = new ArrayList<>();
            List<Double> pairList = new ArrayList<>();
            for (int i = 0; i < info.purchases.size(); i++) {
                ReportPurchaseCosmeticViewModel purchase = info.purchases.get(i);
                pairList.add(purchase.price);
                if (i + 1 == info.purchases.size() || !purchase.cosmeticName.equals(info.purchases.get(i + 1).cosmeticName)) {
                    listDictionary.add(new Pair<>(purchase.cosmeticName, pairList));
                    pairList = new ArrayList<>();
                }
            }

            int j = 0;

            for (Pair<String, List<Double>> cosmetic : listDictionary) {
                XWPFParagraph xwpfParagraphCosmetic = xwpfDocument.createParagraph();
                XWPFRun xwpfRunCosmetic = xwpfParagraphCosmetic.createRun();
                xwpfRunCosmetic.setText("Наименование: " + cosmetic.first);
                xwpfRunCosmetic.setFontSize(12);
                xwpfParagraphCosmetic = xwpfDocument.createParagraph();
                xwpfRunCosmetic = xwpfParagraphCosmetic.createRun();
                xwpfRunCosmetic.setText("Стоимость: " + cosmetic.second.get(0));
                xwpfRunCosmetic.setFontSize(12);
                XWPFTable table = xwpfDocument.createTable();
                XWPFTableRow firstRow = table.getRows().get(0);
                firstRow.getCell(0).setText("Дата");
                firstRow.addNewTableCell().setText("Количество");
                firstRow.addNewTableCell().setText("ID клиента");


                for (int i = 0; i < cosmetic.second.size(); i++) {
                    XWPFTableRow secondRow = table.createRow();
                    secondRow.getCell(0).setText(formatter.format(info.purchases.get(j).date));
                    secondRow.getCell(1).setText(String.valueOf(info.purchases.get(j).count));
                    secondRow.getCell(2).setText(String.valueOf(info.purchases.get(j).clientId));
                    j++;
                }
            }

            xwpfParagraph = xwpfDocument.createParagraph();
            xwpfRun = xwpfParagraph.createRun();
            xwpfRun.setText("Общая сумма покупок: " + info.purchases.get(0).totalCost);
            xwpfRun.setFontSize(12);

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            xwpfDocument.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            xwpfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}