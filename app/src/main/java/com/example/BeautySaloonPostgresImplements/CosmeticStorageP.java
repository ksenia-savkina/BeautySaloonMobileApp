package com.example.BeautySaloonPostgresImplements;

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.ICosmeticStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CosmeticStorageP implements ICosmeticStorage {

    private final Connection connection;

    public CosmeticStorageP(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<CosmeticViewModel> getFullList() {
        List<CosmeticViewModel> cosmeticViewModels = new ArrayList<>();
        String sql = "SELECT * FROM COSMETICS ORDER BY ID";
        Thread thread = new Thread(() -> {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(sql);
                while (set.next()) {
                    CosmeticViewModel cosmeticViewModel = new CosmeticViewModel();
                    cosmeticViewModel.id = set.getInt(1);
                    cosmeticViewModel.cosmeticName = set.getString(2);
                    cosmeticViewModel.price = set.getDouble(3);
                    cosmeticViewModel.employeeId = set.getInt(4);
                    cosmeticViewModels.add(cosmeticViewModel);
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
        return cosmeticViewModels;
    }

    @Override
    public List<CosmeticViewModel> getFilteredList(CosmeticBindingModel model) {
        if (model == null) {
            return null;
        }
        String sql;
        List<CosmeticViewModel> cosmeticViewModels = new ArrayList<>();
        if (model.cosmeticName != null) {
            sql = "SELECT * FROM COSMETICS WHERE cosmeticName = ? ORDER BY ID";
        } else {
            sql = "SELECT * FROM COSMETICS WHERE employeeId = ? ORDER BY ID";
        }
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (model.cosmeticName != null)
                    statement.setString(1, model.cosmeticName);
                else
                    statement.setInt(1, model.employeeId);
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    CosmeticViewModel cosmeticViewModel = new CosmeticViewModel();
                    cosmeticViewModel.id = set.getInt(1);
                    cosmeticViewModel.cosmeticName = set.getString(2);
                    cosmeticViewModel.price = set.getDouble(3);
                    cosmeticViewModel.employeeId = set.getInt(4);
                    cosmeticViewModels.add(cosmeticViewModel);
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
        return cosmeticViewModels;
    }

    @Override
    public CosmeticViewModel getElement(CosmeticBindingModel model) {
        if (model == null) {
            return null;
        }
        List<CosmeticViewModel> cosmeticViewModels = new ArrayList<>();
        Thread thread = new Thread(() -> {
            String sql = "";
            if (model.id > -1)
                sql = "SELECT * FROM COSMETICS WHERE id = ?";
            else if (model.cosmeticName != null)
                sql = "SELECT * FROM COSMETICS WHERE cosmeticName = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (model.id > -1)
                    statement.setInt(1, model.id);
                else if (model.cosmeticName != null)
                    statement.setString(1, model.cosmeticName);
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    CosmeticViewModel cosmeticViewModel = new CosmeticViewModel();
                    cosmeticViewModel.id = set.getInt(1);
                    cosmeticViewModel.cosmeticName = set.getString(2);
                    cosmeticViewModel.price = set.getDouble(3);
                    cosmeticViewModel.employeeId = set.getInt(4);
                    cosmeticViewModels.add(cosmeticViewModel);
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
        if (cosmeticViewModels.size() > 0)
            return cosmeticViewModels.get(0);
        else
            return null;
    }

    @Override
    public void insert(CosmeticBindingModel model) {
        String sql = "INSERT INTO COSMETICS VALUES (nextval('cosmetics_id_seq'), (?), (?), (?))";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, model.cosmeticName);
                statement.setDouble(2, model.price);
                statement.setInt(3, model.employeeId);
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

    @Override
    public void update(CosmeticBindingModel model) throws Exception {
//        CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
//        cosmeticBindingModel.id = model.id;
        CosmeticViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        String sql = "UPDATE COSMETICS SET CosmeticName = (?), Price = (?), EmployeeId = (?) WHERE Id = ?";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, model.cosmeticName);
                statement.setDouble(2, model.price);
                statement.setInt(3, model.employeeId);
                statement.setInt(4, model.id);
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

    @Override
    public void delete(CosmeticBindingModel model) throws Exception {
        CosmeticViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        String sql = "DELETE FROM COSMETICS WHERE Id = ?";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

//    private List<CosmeticViewModel> getList(ResultSet set) {
//        List<CosmeticViewModel> cosmeticViewModels = new ArrayList<>();
//        try {
//            while (set.next()) {
//                CosmeticViewModel cosmeticViewModel = new CosmeticViewModel();
//                cosmeticViewModel.id = set.getInt(1);
//                cosmeticViewModel.cosmeticName = set.getString(2);
//                cosmeticViewModel.price = set.getDouble(3);
//                cosmeticViewModel.employeeId = set.getInt(4);
//                cosmeticViewModels.add(cosmeticViewModel);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return cosmeticViewModels;
//    }
}