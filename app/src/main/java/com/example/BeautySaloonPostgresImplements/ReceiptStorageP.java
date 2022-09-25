package com.example.BeautySaloonPostgresImplements;

import android.util.Pair;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReceiptBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IReceiptStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReceiptViewModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiptStorageP implements IReceiptStorage {

    private final Connection connection;

    public ReceiptStorageP(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<ReceiptViewModel> getFullList() {
        List<ReceiptViewModel> receiptViewModels = new ArrayList<>();
        String sql = "SELECT R.id, R.totalCost, R.purchaseDate, R.employeeId, RC.cosmeticId, C.cosmeticName, RC.count" +
                " FROM RECEIPTS R JOIN ReceiptCosmetics RC" +
                " ON R.id = RC.ReceiptId" +
                " JOIN COSMETICS C on RC.cosmeticId = C.id" +
                " ORDER BY R.id";
        Thread thread = new Thread(() -> {
            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                int id = -1;
                ResultSet set = statement.executeQuery(sql);
                ReceiptViewModel receiptViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        receiptViewModel = new ReceiptViewModel();
                        receiptViewModel.receiptCosmetics = new HashMap<>();
                        receiptViewModel.id = newId;
                        receiptViewModel.totalCost = set.getDouble("totalCost");
                        receiptViewModel.purchaseDate = set.getDate("purchaseDate");
                        receiptViewModel.employeeId = set.getInt("employeeId");
                    }

                    receiptViewModel.receiptCosmetics.put(set.getInt("cosmeticId"),
                            new Pair<>(set.getString("cosmeticName"), set.getInt("count")));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            receiptViewModels.add(receiptViewModel);
                        set.previous();
                    } else {
                        receiptViewModels.add(receiptViewModel);
                        break;
                    }
                    id = set.getInt("id");
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
        return receiptViewModels;
    }

    @Override
    public List<ReceiptViewModel> getFilteredList(ReceiptBindingModel model) {
        if (model == null) {
            return null;
        }
        List<ReceiptViewModel> receiptViewModels = new ArrayList<>();
        String sql;
        if (model.purchaseDate != null)
            sql = "SELECT R.id, R.totalCost, R.purchaseDate, R.employeeId, RC.cosmeticId, C.cosmeticName, RC.count" +
                    " FROM RECEIPTS R JOIN ReceiptCosmetics RC" +
                    " ON R.id = RC.ReceiptId" +
                    " JOIN COSMETICS C on RC.cosmeticId = C.id" +
                    " where R.purchaseDate = ?" +
                    " ORDER BY R.id";
        else
            sql = "SELECT R.id, R.totalCost, R.purchaseDate, R.employeeId, RC.cosmeticId, C.cosmeticName, RC.count" +
                    " FROM RECEIPTS R JOIN ReceiptCosmetics RC" +
                    " ON R.id = RC.ReceiptId" +
                    " JOIN COSMETICS C on RC.cosmeticId = C.id" +
                    " where R.employeeId = ?" +
                    " ORDER BY R.id";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                if (model.purchaseDate != null)
                    statement.setDate(1, new java.sql.Date(model.purchaseDate.getTime()));
                else
                    statement.setInt(1, model.employeeId);
                int id = -1;
                ResultSet set = statement.executeQuery();
                ReceiptViewModel receiptViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        receiptViewModel = new ReceiptViewModel();
                        receiptViewModel.receiptCosmetics = new HashMap<>();
                        receiptViewModel.id = newId;
                        receiptViewModel.totalCost = set.getDouble("totalCost");
                        receiptViewModel.purchaseDate = set.getDate("purchaseDate");
                        receiptViewModel.employeeId = set.getInt("employeeId");
                    }

                    receiptViewModel.receiptCosmetics.put(set.getInt("cosmeticId"),
                            new Pair<>(set.getString("cosmeticName"), set.getInt("count")));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            receiptViewModels.add(receiptViewModel);
                        set.previous();
                    } else {
                        receiptViewModels.add(receiptViewModel);
                        break;
                    }
                    id = set.getInt("id");
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
        return receiptViewModels;
    }

    @Override
    public ReceiptViewModel getElement(ReceiptBindingModel model) {
        if (model == null) {
            return null;
        }
        List<ReceiptViewModel> receiptViewModels = new ArrayList<>();
        Thread thread = new Thread(() -> {
            String sql = "";
            if (model.id > -1)
                sql = "SELECT R.id, R.totalCost, R.purchaseDate, R.employeeId, RC.cosmeticId, C.cosmeticName, RC.count" +
                        " FROM RECEIPTS R JOIN ReceiptCosmetics RC" +
                        " ON R.id = RC.ReceiptId" +
                        " JOIN COSMETICS C on RC.cosmeticId = C.id" +
                        " where R.id = ?";
            else if (model.purchaseDate != null)
                sql = "SELECT R.id, R.totalCost, R.purchaseDate, R.employeeId, RC.cosmeticId, C.cosmeticName, RC.count" +
                        " FROM RECEIPTS R JOIN ReceiptCosmetics RC" +
                        " ON R.id = RC.ReceiptId" +
                        " JOIN COSMETICS C on RC.cosmeticId = C.id" +
                        " where R.purchaseDate = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                if (model.id > -1)
                    statement.setInt(1, model.id);
                else if (model.purchaseDate != null)
                    statement.setDate(1, new java.sql.Date(model.purchaseDate.getTime()));
                int id = -1;
                ResultSet set = statement.executeQuery();
                ReceiptViewModel receiptViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");
                    if (newId != id) {
                        receiptViewModel = new ReceiptViewModel();
                        receiptViewModel.receiptCosmetics = new HashMap<>();
                        receiptViewModel.id = newId;
                        receiptViewModel.totalCost = set.getDouble("totalCost");
                        receiptViewModel.purchaseDate = set.getDate("purchaseDate");
                        receiptViewModel.employeeId = set.getInt("employeeId");
                    }

                    receiptViewModel.receiptCosmetics.put(set.getInt("cosmeticId"),
                            new Pair<>(set.getString("cosmeticName"), set.getInt("count")));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            receiptViewModels.add(receiptViewModel);
                        set.previous();
                    } else {
                        receiptViewModels.add(receiptViewModel);
                        break;
                    }
                    id = set.getInt("id");
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
        if (receiptViewModels.size() > 0)
            return receiptViewModels.get(0);
        else
            return null;
    }

    @Override
    public void insert(ReceiptBindingModel model) {
        try {
            List<Integer> listId = new ArrayList<>();
            Thread thread = new Thread(() -> {
                String sql1 = "INSERT INTO RECEIPTS VALUES (nextval('receipts_id_seq'), (?), (?), (?)) RETURNING ID";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql1);
                    statement.setDouble(1, model.totalCost);
                    statement.setDate(2, new java.sql.Date(new Date().getTime()));
                    statement.setInt(3, model.employeeId);
                    ResultSet set = statement.executeQuery();
                    if (set.next())
                        listId.add(set.getInt(1));
                    set.close();
                    for (Map.Entry<Integer, Pair<String, Integer>> entry : model.receiptCosmetics.entrySet()) {
                        String sql2 = "INSERT INTO ReceiptCosmetics VALUES ((?), (?), (?))";
                        statement = connection.prepareStatement(sql2);
                        statement.setInt(1, entry.getKey());
                        statement.setInt(2, listId.get(0));
                        statement.setInt(3, entry.getValue().second);
                        statement.executeUpdate();
                    }
                    connection.commit();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(ReceiptBindingModel model) throws Exception {
        ReceiptViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        try {
            Thread thread = new Thread(() -> {
                try {
                    String sql = "SELECT * FROM ReceiptCosmetics WHERE ReceiptId = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, model.id);
                    ResultSet set = statement.executeQuery();
                    while (set.next()) {
                        int cosmeticId = set.getInt("cosmeticId");
                        if (!model.receiptCosmetics.containsKey(cosmeticId)) {
                            sql = "DELETE FROM ReceiptCosmetics WHERE CosmeticId = ? and ReceiptId = ?";
                            statement = connection.prepareStatement(sql);
                            statement.setInt(1, cosmeticId);
                            statement.setInt(2, model.id);
                            statement.executeUpdate();
                        } else {
                            Map<Integer, Pair<String, Integer>> map = new HashMap<>();
                            map.put(cosmeticId, model.receiptCosmetics.get(cosmeticId));
                            for (Map.Entry<Integer, Pair<String, Integer>> entry : map.entrySet()) {
                                sql = "UPDATE ReceiptCosmetics SET Count = (?) WHERE CosmeticId = ? and ReceiptId = ?";
                                statement = connection.prepareStatement(sql);
                                statement.setInt(1, entry.getValue().second);
                                statement.setInt(2, cosmeticId);
                                statement.setInt(3, model.id);
                                statement.executeUpdate();
                            }
                            model.receiptCosmetics.remove(cosmeticId);
                        }
                    }
                    sql = "UPDATE RECEIPTS SET TotalCost = (?), PurchaseDate = (?), EmployeeId = (?) WHERE Id = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setDouble(1, model.totalCost);
                    statement.setDate(2, new java.sql.Date(new Date().getTime()));
                    statement.setInt(3, model.employeeId);
                    statement.setInt(4, model.id);
                    statement.executeUpdate();

                    for (Map.Entry<Integer, Pair<String, Integer>> entry : model.receiptCosmetics.entrySet()) {
                        String sql2 = "INSERT INTO ReceiptCosmetics VALUES ((?), (?), (?))";
                        statement = connection.prepareStatement(sql2);
                        statement.setInt(1, entry.getKey());
                        statement.setInt(2, model.id);
                        statement.setInt(3, entry.getValue().second);
                        statement.executeUpdate();
                    }
                    connection.commit();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(ReceiptBindingModel model) throws Exception {
        ReceiptViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        Thread thread = new Thread(() -> {
            try {
                String sql1 = "DELETE FROM ReceiptCosmetics WHERE ReceiptId = ?";
                PreparedStatement statement = connection.prepareStatement(sql1);
                statement.setInt(1, model.id);
                statement.executeUpdate();
                String sql2 = "DELETE FROM RECEIPTS WHERE Id = ?";
                statement = connection.prepareStatement(sql2);
                statement.setInt(1, model.id);
                statement.executeUpdate();
                connection.commit();
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
    }
}