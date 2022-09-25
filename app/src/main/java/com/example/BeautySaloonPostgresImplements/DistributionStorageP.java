package com.example.BeautySaloonPostgresImplements;

import android.util.Pair;

import com.example.BeautySaloonBusinessLogic.BindingModels.DistributionBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IDistributionStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.DistributionViewModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributionStorageP implements IDistributionStorage {

    private final Connection connection;

    public DistributionStorageP(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<DistributionViewModel> getFullList() {
        List<DistributionViewModel> distributionViewModels = new ArrayList<>();
        String sql = "SELECT D.id, D.issueDate, D.employeeId, DC.cosmeticId, C.cosmeticName, DC.count" +
                " FROM DISTRIBUTIONS D JOIN DistributionCosmetics DC" +
                " ON D.id = DC.DistributionId" +
                " JOIN COSMETICS C on DC.cosmeticId = C.id" +
                " ORDER BY D.id";
        Thread thread = new Thread(() -> {
            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                int id = -1;
                ResultSet set = statement.executeQuery(sql);
                DistributionViewModel distributionViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        distributionViewModel = new DistributionViewModel();
                        distributionViewModel.distributionCosmetics = new HashMap<>();
                        distributionViewModel.id = newId;
                        distributionViewModel.issueDate = set.getDate("issueDate");
                        distributionViewModel.employeeId = set.getInt("employeeId");
                    }

                    distributionViewModel.distributionCosmetics.put(set.getInt("cosmeticId"),
                            new Pair<>(set.getString("cosmeticName"), set.getInt("count")));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            distributionViewModels.add(distributionViewModel);
                        set.previous();
                    } else {
                        distributionViewModels.add(distributionViewModel);
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
        return distributionViewModels;
    }

    @Override
    public List<DistributionViewModel> getFilteredList(DistributionBindingModel model) {
        if (model == null) {
            return null;
        }
        List<DistributionViewModel> distributionViewModels = new ArrayList<>();
        String sql;
        if (model.issueDate != null)
            sql = "SELECT D.id, D.issueDate, D.employeeId, DC.cosmeticId, C.cosmeticName, DC.count" +
                    " FROM DISTRIBUTIONS D JOIN DistributionCosmetics DC" +
                    " ON D.id = DC.DistributionId" +
                    " JOIN COSMETICS C on DC.cosmeticId = C.id" +
                    " where D.issueDate = ?" +
                    " ORDER BY D.id";
        else
            sql = "SELECT D.id, D.issueDate, D.employeeId, DC.cosmeticId, C.cosmeticName, DC.count" +
                    " FROM DISTRIBUTIONS D JOIN DistributionCosmetics DC" +
                    " ON D.id = DC.DistributionId" +
                    " JOIN COSMETICS C on DC.cosmeticId = C.id" +
                    " where D.employeeId = ?" +
                    " ORDER BY D.id";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                if (model.issueDate != null)
                    statement.setDate(1, new java.sql.Date(model.issueDate.getTime()));
                else
                    statement.setInt(1, model.employeeId);
                int id = -1;
                ResultSet set = statement.executeQuery();
                DistributionViewModel distributionViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");

                    if (newId != id) {
                        distributionViewModel = new DistributionViewModel();
                        distributionViewModel.distributionCosmetics = new HashMap<>();
                        distributionViewModel.id = newId;
                        distributionViewModel.issueDate = set.getDate("issueDate");
                        distributionViewModel.employeeId = set.getInt("employeeId");
                    }

                    distributionViewModel.distributionCosmetics.put(set.getInt("cosmeticId"),
                            new Pair<>(set.getString("cosmeticName"), set.getInt("count")));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            distributionViewModels.add(distributionViewModel);
                        set.previous();
                    } else {
                        distributionViewModels.add(distributionViewModel);
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
        return distributionViewModels;
    }

    @Override
    public DistributionViewModel getElement(DistributionBindingModel model) {
        if (model == null) {
            return null;
        }
        List<DistributionViewModel> distributionViewModels = new ArrayList<>();
        Thread thread = new Thread(() -> {
            String sql = "";
            if (model.id > -1)
                sql = "SELECT D.id, D.issueDate, D.employeeId, DC.cosmeticId, C.cosmeticName, DC.count" +
                        " FROM DISTRIBUTIONS D JOIN DistributionCosmetics DC" +
                        " ON D.id = DC.DistributionId" +
                        " JOIN COSMETICS C on DC.cosmeticId = C.id" +
                        " where D.id = ?";
            else if (model.issueDate != null)
                sql = "SELECT D.id, D.issueDate, D.employeeId, DC.cosmeticId, C.cosmeticName, DC.count" +
                        " FROM DISTRIBUTIONS D JOIN DistributionCosmetics DC" +
                        " ON D.id = DC.DistributionId" +
                        " JOIN COSMETICS C on DC.cosmeticId = C.id" +
                        " where D.issueDate = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                if (model.id > -1)
                    statement.setInt(1, model.id);
                else if (model.issueDate != null)
                    statement.setDate(1, new java.sql.Date(model.issueDate.getTime()));
                int id = -1;
                ResultSet set = statement.executeQuery();
                DistributionViewModel distributionViewModel = null;
                while (set.next()) {
                    int newId = set.getInt("id");
                    if (newId != id) {
                        distributionViewModel = new DistributionViewModel();
                        distributionViewModel.distributionCosmetics = new HashMap<>();
                        distributionViewModel.id = newId;
                        distributionViewModel.issueDate = set.getDate("issueDate");
                        distributionViewModel.employeeId = set.getInt("employeeId");
                    }

                    distributionViewModel.distributionCosmetics.put(set.getInt("cosmeticId"),
                            new Pair<>(set.getString("cosmeticName"), set.getInt("count")));

                    if (set.next()) {
                        if (newId != set.getInt("id"))
                            distributionViewModels.add(distributionViewModel);
                        set.previous();
                    } else {
                        distributionViewModels.add(distributionViewModel);
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
        if (distributionViewModels.size() > 0)
            return distributionViewModels.get(0);
        else
            return null;
    }

    @Override
    public void insert(DistributionBindingModel model) {
        try {
            List<Integer> listId = new ArrayList<>();
            Thread thread = new Thread(() -> {
                String sql1 = "INSERT INTO DISTRIBUTIONS VALUES (nextval('distributions_id_seq'), (?), (?)) RETURNING ID";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql1);
                    statement.setDate(1, new java.sql.Date(new Date().getTime()));
                    statement.setInt(2, model.employeeId);
                    ResultSet set = statement.executeQuery();
                    if (set.next())
                        listId.add(set.getInt(1));
                    set.close();
                    for (Map.Entry<Integer, Pair<String, Integer>> entry : model.distributionCosmetics.entrySet()) {
                        String sql2 = "INSERT INTO DistributionCosmetics VALUES ((?), (?), (?))";
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
    public void update(DistributionBindingModel model) throws Exception {
        DistributionViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        try {
            Thread thread = new Thread(() -> {
                try {
                    String sql = "SELECT * FROM DistributionCosmetics WHERE DistributionId = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, model.id);
                    ResultSet set = statement.executeQuery();
                    while (set.next()) {
                        int cosmeticId = set.getInt("cosmeticId");
                        if (!model.distributionCosmetics.containsKey(cosmeticId)) {
                            sql = "DELETE FROM DistributionCosmetics WHERE CosmeticId = ? and DistributionId = ?";
                            statement = connection.prepareStatement(sql);
                            statement.setInt(1, cosmeticId);
                            statement.setInt(2, model.id);
                            statement.executeUpdate();
                        } else {
                            Map<Integer, Pair<String, Integer>> map = new HashMap<>();
                            map.put(cosmeticId, model.distributionCosmetics.get(cosmeticId));
                            for (Map.Entry<Integer, Pair<String, Integer>> entry : map.entrySet()) {
                                sql = "UPDATE DistributionCosmetics SET Count = (?) WHERE CosmeticId = ? and DistributionId = ?";
                                statement = connection.prepareStatement(sql);
                                statement.setInt(1, entry.getValue().second);
                                statement.setInt(2, cosmeticId);
                                statement.setInt(3, model.id);
                                statement.executeUpdate();
                            }
                            model.distributionCosmetics.remove(cosmeticId);
                        }
                    }
                    sql = "UPDATE DISTRIBUTIONS SET IssueDate = (?), EmployeeId = (?) WHERE Id = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setDate(1, new java.sql.Date(new Date().getTime()));
                    statement.setInt(2, model.employeeId);
                    statement.setInt(3, model.id);
                    statement.executeUpdate();

                    for (Map.Entry<Integer, Pair<String, Integer>> entry : model.distributionCosmetics.entrySet()) {
                        String sql2 = "INSERT INTO DistributionCosmetics VALUES ((?), (?), (?))";
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
    public void delete(DistributionBindingModel model) throws Exception {
        DistributionViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        Thread thread = new Thread(() -> {
            try {
                String sql1 = "DELETE FROM DistributionCosmetics WHERE DistributionId = ?";
                PreparedStatement statement = connection.prepareStatement(sql1);
                statement.setInt(1, model.id);
                statement.executeUpdate();
                String sql2 = "DELETE FROM DISTRIBUTIONS WHERE Id = ?";
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