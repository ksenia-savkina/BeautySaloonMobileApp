package com.example.BeautySaloonBusinessLogic.BusinessLogics;

import android.content.Context;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReportBindingModelEmployee;
import com.example.BeautySaloonBusinessLogic.HelperModels.ExcelInfoEmployee;
import com.example.BeautySaloonBusinessLogic.HelperModels.PdfInfoEmployee;
import com.example.BeautySaloonBusinessLogic.HelperModels.WordInfoEmployee;
import com.example.BeautySaloonBusinessLogic.Interfaces.IReportStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportCosmeticsViewModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportPurchaseCosmeticViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReportLogicEmployee {

    private final IReportStorage _reportStorage;

    public ReportLogicEmployee(IReportStorage reportStorage) {
        _reportStorage = reportStorage;
    }

    public List<ReportPurchaseCosmeticViewModel> getPurchaseList(ReportBindingModelEmployee model) {
        List<ReportPurchaseCosmeticViewModel> list = new ArrayList<>();

        double totalCost = 0;

        for (int cosmetic : model.purchaseCosmetics) {
            list.addAll(_reportStorage.getPurchaseList(cosmetic));
        }

        for (ReportPurchaseCosmeticViewModel reportPurchaseCosmetic : list) {
            totalCost += reportPurchaseCosmetic.price * reportPurchaseCosmetic.count;
        }

        list.get(0).totalCost = totalCost;

        return list;
    }

    public List<ReportCosmeticsViewModel> getCosmetics(ReportBindingModelEmployee model) {
        return _reportStorage.getCosmetics(model);
    }

    public void savePurchaseListToWordFile(ReportBindingModelEmployee model, Context context) {
        WordInfoEmployee wordInfoEmployee = new WordInfoEmployee();
        wordInfoEmployee.fileName = model.fileName;
        wordInfoEmployee.title = "Сведения по покупкам";
        wordInfoEmployee.purchases = getPurchaseList(model);
        SaveToWordEmployee.createDoc(wordInfoEmployee, context);
    }

    public void savePurchaseListToExcelFile(ReportBindingModelEmployee model, Context context) {
        ExcelInfoEmployee excelInfoEmployee = new ExcelInfoEmployee();
        excelInfoEmployee.fileName = model.fileName;
        excelInfoEmployee.title = "Сведения по покупкам";
        excelInfoEmployee.purchases = getPurchaseList(model);
        SaveToExcelEmployee.createDoc(excelInfoEmployee, context);
    }

    public void saveCosmeticsToPdfFile(ReportBindingModelEmployee model, Context context) {
        PdfInfoEmployee pdfInfoEmployee = new PdfInfoEmployee();
        pdfInfoEmployee.fileName = model.fileName;
        pdfInfoEmployee.title = "Список косметики";
        pdfInfoEmployee.dateFrom = model.dateFrom;
        pdfInfoEmployee.dateTo = model.dateTo;
        pdfInfoEmployee.employeeId = model.employeeId;
        pdfInfoEmployee.cosmetics = getCosmetics(model);
        SaveToPdfEmployee.createDoc(pdfInfoEmployee, context);
    }
}