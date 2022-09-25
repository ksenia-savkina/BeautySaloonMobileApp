package com.example.BeautySaloonBusinessLogic.BusinessLogics;

import android.content.Context;
import android.util.Pair;

import com.example.BeautySaloonBusinessLogic.HelperModels.ExcelInfoEmployee;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportPurchaseCosmeticViewModel;

import org.apache.poi.ss.usermodel.BorderExtent;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SaveToExcelEmployee {

    private static File filePath = null;

    public static void createDoc(ExcelInfoEmployee info, Context context) {
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
            Workbook book = new XSSFWorkbook();
            Sheet sheet = book.createSheet("Лист");

            Row row = sheet.createRow(0);
            Cell name = row.createCell(0);
            name.setCellValue(info.title);

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

            int rowIndex = 1;

            for (Pair<String, List<Double>> cosmetic : listDictionary) {
                rowIndex++;
                row = sheet.createRow(rowIndex);
                row.createCell(0).setCellValue("Наименование");
                row.createCell(1).setCellValue(cosmetic.first);

                rowIndex++;
                row = sheet.createRow(rowIndex);
                row.createCell(0).setCellValue("Стоимость");
                row.createCell(1).setCellValue(cosmetic.second.get(0));

                rowIndex += 2;
                row = sheet.createRow(rowIndex);
                row.createCell(1).setCellValue("Дата");
                row.createCell(2).setCellValue("Количество");
                row.createCell(3).setCellValue("ID клиента");

                int firstRow = rowIndex;
                rowIndex++;

                for (int i = 0; i < cosmetic.second.size(); i++) {
                    row = sheet.createRow(rowIndex);
                    row.createCell(1).setCellValue(formatter.format(info.purchases.get(j).date));
                    row.createCell(2).setCellValue(String.valueOf(info.purchases.get(j).count));
                    row.createCell(3).setCellValue(String.valueOf(info.purchases.get(j).clientId));
                    rowIndex++;
                    j++;
                }

                PropertyTemplate propertyTemplate = new PropertyTemplate();
                propertyTemplate.drawBorders(new CellRangeAddress(firstRow, rowIndex - 1, 1, 3),
                        BorderStyle.MEDIUM, BorderExtent.ALL);
                propertyTemplate.applyBorders(sheet);
            }

            rowIndex++;
            row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue("Общая сумма покупок");
            row.createCell(1).setCellValue(info.purchases.get(0).totalCost);

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            book.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}