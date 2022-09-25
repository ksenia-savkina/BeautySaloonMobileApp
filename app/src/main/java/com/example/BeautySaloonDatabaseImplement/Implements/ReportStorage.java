package com.example.BeautySaloonDatabaseImplement.Implements;

import android.database.Cursor;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReportBindingModelEmployee;
import com.example.BeautySaloonBusinessLogic.Interfaces.IReportStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportCosmeticsViewModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportPurchaseCosmeticViewModel;
import com.example.BeautySaloonViewEmployee.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportStorage implements IReportStorage {
    @Override
    public List<ReportPurchaseCosmeticViewModel> getPurchaseList(int cosmeticId) {
        Cursor c = MainActivity.db.rawQuery("select distinct C.cosmeticName, C.price, P.date, RC.count, P.clientId" +
                " from cosmetics C join receiptCosmetics RC" +
                " on C.id = RC.cosmeticId" +
                " join receipts R on RC.receiptId = R.id" +
                " join purchases P on R.id = P.receiptId" +
                " where C.id = ?;", new String[]{String.valueOf(cosmeticId)});
        return getList(c);
    }

    @Override
    public List<ReportCosmeticsViewModel> getCosmetics(ReportBindingModelEmployee model) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
//        List<String> strings = new ArrayList<>();
//        strings.add(String.valueOf(model.employeeId));
//        strings.add(formatter.format(model.dateFrom));
//        strings.add(formatter.format(model.dateTo));
//        strings.addAll(strings);

//        Cursor c = MainActivity.db.rawQuery("select R.purchasedate, C.cosmeticname, RC.count" +
//                " from cosmetics C join receiptCosmetics RC" +
//                " on C.id = RC.cosmeticId" +
//                " join receipts R on RC.receiptId = R.id" +
//                " where R.employeeid = ?" +
//                " and R.purchasedate >= ?" +
//                " and R.purchasedate <= ?" +
//                " union" +
//                " select D.issuedate, C.cosmeticname, DC.count" +
//                " from cosmetics C join distributionCosmetics DC" +
//                " on C.id = DC.cosmeticId" +
//                " join distributions D on DC.distributionId = D.id" +
//                " where D.employeeid = ?" +
//                " and D.issuedate >= ?" +
//                " and D.issuedate <= ?;", strings.toArray(new String[strings.size()]));

        String[] strings = new String[]{String.valueOf(model.employeeId), formatter.format(model.dateFrom), formatter.format(model.dateTo)};

        Cursor c1 = MainActivity.db.rawQuery("select R.purchasedate, C.cosmeticname, RC.count" +
                " from cosmetics C join receiptCosmetics RC" +
                " on C.id = RC.cosmeticId" +
                " join receipts R on RC.receiptId = R.id" +
                " where R.employeeid = ?" +
                " and R.purchasedate >= ?" +
                " and R.purchasedate <= ?;", strings);

        Cursor c2 = MainActivity.db.rawQuery("select D.issuedate, C.cosmeticname, DC.count" +
                " from cosmetics C join distributionCosmetics DC" +
                " on C.id = DC.cosmeticId" +
                " join distributions D on DC.distributionId = D.id" +
                " where D.employeeid = ?" +
                " and D.issuedate >= ?" +
                " and D.issuedate <= ?;", strings);
        return getCosmeticList(c1, c2);
    }

    private List<ReportPurchaseCosmeticViewModel> getList(Cursor c) {
        List<ReportPurchaseCosmeticViewModel> purchaseCosmeticViewModels = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    ReportPurchaseCosmeticViewModel purchaseCosmeticViewModel = new ReportPurchaseCosmeticViewModel();
                    purchaseCosmeticViewModel.cosmeticName = c.getString(c.getColumnIndex("cosmeticName"));
                    purchaseCosmeticViewModel.price = c.getDouble(c.getColumnIndex("price"));
                    try {
                        purchaseCosmeticViewModel.date = formatter.parse(c.getString(c.getColumnIndex("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    purchaseCosmeticViewModel.count = c.getInt(c.getColumnIndex("count"));
                    purchaseCosmeticViewModel.clientId = c.getInt(c.getColumnIndex("clientId"));
                    purchaseCosmeticViewModels.add(purchaseCosmeticViewModel);
                } while (c.moveToNext());
            }
        }
        return purchaseCosmeticViewModels;
    }

    private List<ReportCosmeticsViewModel> getCosmeticList(Cursor c1, Cursor c2) {
        List<ReportCosmeticsViewModel> reportCosmeticsViewModels = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        ReportCosmeticsViewModel reportCosmeticsViewModel;
        if (c1 != null) {
            if (c1.moveToFirst()) {
                do {
                    reportCosmeticsViewModel = new ReportCosmeticsViewModel();
                    reportCosmeticsViewModel.typeOfService = "Чек";
                    try {
                        reportCosmeticsViewModel.dateOfService = formatter.parse(c1.getString(c1.getColumnIndex("purchaseDate")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    reportCosmeticsViewModel.cosmeticName = c1.getString(c1.getColumnIndex("cosmeticName"));
                    reportCosmeticsViewModel.count = c1.getInt(c1.getColumnIndex("count"));
                    reportCosmeticsViewModels.add(reportCosmeticsViewModel);
                }
                while (c1.moveToNext());
            }
        }

        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {
                    reportCosmeticsViewModel = new ReportCosmeticsViewModel();
                    reportCosmeticsViewModel.typeOfService = "Выдача";
                    try {
                        reportCosmeticsViewModel.dateOfService = formatter.parse(c2.getString(c2.getColumnIndex("issueDate")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    reportCosmeticsViewModel.cosmeticName = c2.getString(c2.getColumnIndex("cosmeticName"));
                    reportCosmeticsViewModel.count = c2.getInt(c2.getColumnIndex("count"));
                    reportCosmeticsViewModels.add(reportCosmeticsViewModel);
                } while (c2.moveToNext());
            }
        }
        return reportCosmeticsViewModels;
    }
}