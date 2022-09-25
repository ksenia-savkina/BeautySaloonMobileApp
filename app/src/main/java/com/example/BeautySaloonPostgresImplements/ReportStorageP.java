package com.example.BeautySaloonPostgresImplements;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReportBindingModelEmployee;
import com.example.BeautySaloonBusinessLogic.Interfaces.IReportStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportCosmeticsViewModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportPurchaseCosmeticViewModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportStorageP implements IReportStorage {

    private final Connection connection;

    public ReportStorageP(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<ReportPurchaseCosmeticViewModel> getPurchaseList(int cosmeticId) {
        List<ReportPurchaseCosmeticViewModel> purchaseCosmeticViewModels = new ArrayList<>();
        String sql = "SELECT DISTINCT C.cosmeticName, C.price, P.date, RC.count, P.clientId" +
                " FROM cosmetics C JOIN receiptCosmetics RC" +
                " ON C.id = RC.cosmeticId" +
                " JOIN receipts R ON RC.receiptId = R.id" +
                " JOIN purchases P ON R.id = P.receiptId" +
                " WHERE C.id = ?;";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, cosmeticId);
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    ReportPurchaseCosmeticViewModel purchaseCosmeticViewModel = new ReportPurchaseCosmeticViewModel();
                    purchaseCosmeticViewModel.cosmeticName = set.getString("cosmeticName");
                    purchaseCosmeticViewModel.price = set.getDouble("price");
                    purchaseCosmeticViewModel.date = set.getDate("date");
                    purchaseCosmeticViewModel.count = set.getInt("count");
                    purchaseCosmeticViewModel.clientId = set.getInt("clientId");
                    purchaseCosmeticViewModels.add(purchaseCosmeticViewModel);
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return purchaseCosmeticViewModels;
    }

    @Override
    public List<ReportCosmeticsViewModel> getCosmetics(ReportBindingModelEmployee model) {
        List<ReportCosmeticsViewModel> reportCosmeticsViewModels = new ArrayList<>();
        String sql1 = "SELECT DISTINCT R.purchaseDate, C.cosmeticName, RC.count" +
                " FROM cosmetics C JOIN receiptCosmetics RC" +
                " ON C.id = RC.cosmeticId" +
                " JOIN receipts R ON RC.receiptId = R.id" +
                " WHERE R.employeeId = ?" +
                " AND R.purchaseDate >= ?" +
                " AND R.purchaseDate <= ?;";

        String sql2 = "SELECT DISTINCT D.issueDate, C.cosmeticName, DC.count" +
                " FROM cosmetics C JOIN distributionCosmetics DC" +
                " ON C.id = DC.cosmeticId" +
                " JOIN distributions D ON DC.distributionId = D.id" +
                " WHERE D.employeeId = ?" +
                " AND D.issueDate >= ?" +
                " AND D.issueDate <= ?;";

        Thread thread = new Thread(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement(sql1);
                statement.setInt(1, model.employeeId);
                statement.setDate(2, new java.sql.Date(model.dateFrom.getTime()));
                statement.setDate(3, new java.sql.Date(model.dateTo.getTime()));
                ResultSet set = statement.executeQuery();
                ReportCosmeticsViewModel reportCosmeticsViewModel;
                while (set.next()) {
                    reportCosmeticsViewModel = new ReportCosmeticsViewModel();
                    reportCosmeticsViewModel.typeOfService = "Чек";
                    reportCosmeticsViewModel.dateOfService = set.getDate("purchaseDate");
                    reportCosmeticsViewModel.cosmeticName = set.getString("cosmeticName");
                    reportCosmeticsViewModel.count = set.getInt("count");
                    reportCosmeticsViewModels.add(reportCosmeticsViewModel);
                }
                statement = connection.prepareStatement(sql2);
                statement.setInt(1, model.employeeId);
                statement.setDate(2, new java.sql.Date(model.dateFrom.getTime()));
                statement.setDate(3, new java.sql.Date(model.dateTo.getTime()));
                set = statement.executeQuery();
                while (set.next()) {
                    reportCosmeticsViewModel = new ReportCosmeticsViewModel();
                    reportCosmeticsViewModel.typeOfService = "Выдача";
                    reportCosmeticsViewModel.dateOfService = set.getDate("issueDate");
                    reportCosmeticsViewModel.cosmeticName = set.getString("cosmeticName");
                    reportCosmeticsViewModel.count = set.getInt("count");
                    reportCosmeticsViewModels.add(reportCosmeticsViewModel);
                }
            } catch (Exception e) {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reportCosmeticsViewModels;
    }
}