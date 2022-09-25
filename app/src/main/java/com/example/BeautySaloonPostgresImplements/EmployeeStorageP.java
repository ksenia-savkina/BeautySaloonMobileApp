package com.example.BeautySaloonPostgresImplements;

import com.example.BeautySaloonBusinessLogic.BindingModels.EmployeeBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IEmployeeStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.EmployeeViewModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeeStorageP implements IEmployeeStorage {

    private final Connection connection;

    public EmployeeStorageP(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<EmployeeViewModel> getFullList() {
        List<EmployeeViewModel> employeeViewModels = new ArrayList<>();
        String sql = "SELECT * FROM EMPLOYEES ORDER BY ID";
        Thread thread = new Thread(() -> {
            try (Statement statement = connection.createStatement()) {
                ResultSet set = statement.executeQuery(sql);
                while (set.next()) {
                    EmployeeViewModel employeeViewModel = new EmployeeViewModel();
                    employeeViewModel.id = set.getInt(1);
                    employeeViewModel.f_Name = set.getString(2);
                    employeeViewModel.l_Name = set.getString(3);
                    employeeViewModel.login = set.getString(4);
                    employeeViewModel.password = set.getString(5);
                    employeeViewModel.eMail = set.getString(6);
                    employeeViewModel.phoneNumber = set.getString(7);
                    employeeViewModels.add(employeeViewModel);
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
        return employeeViewModels;
    }

    @Override
    public List<EmployeeViewModel> getFilteredList(EmployeeBindingModel model) {
        if (model == null) {
            return null;
        }
        List<EmployeeViewModel> employeeViewModels = new ArrayList<>();
        String sql = "SELECT * FROM EMPLOYEES WHERE login = ? and password = ?";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, model.login);
                statement.setString(2, model.password);
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    EmployeeViewModel employeeViewModel = new EmployeeViewModel();
                    employeeViewModel.id = set.getInt(1);
                    employeeViewModel.f_Name = set.getString(2);
                    employeeViewModel.l_Name = set.getString(3);
                    employeeViewModel.login = set.getString(4);
                    employeeViewModel.password = set.getString(5);
                    employeeViewModel.eMail = set.getString(6);
                    employeeViewModel.phoneNumber = set.getString(7);
                    employeeViewModels.add(employeeViewModel);
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
        return employeeViewModels;
    }

    @Override
    public EmployeeViewModel getElement(EmployeeBindingModel model) {
        if (model == null) {
            return null;
        }
        List<EmployeeViewModel> employeeViewModels = new ArrayList<>();
        Thread thread = new Thread(() -> {
            String sql = "";
            if (model.id > -1)
                sql = "SELECT * FROM EMPLOYEES WHERE id = ?";
            else if (model.login != null)
                sql = "SELECT * FROM EMPLOYEES WHERE id = ? or login = ? or email = ? or phoneNumber = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, model.id);
                if (model.login != null) {
                    statement.setString(2, model.login);
                    statement.setString(3, model.eMail);
                    statement.setString(4, model.phoneNumber);
                }
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    EmployeeViewModel employeeViewModel = new EmployeeViewModel();
                    employeeViewModel.id = set.getInt(1);
                    employeeViewModel.f_Name = set.getString(2);
                    employeeViewModel.l_Name = set.getString(3);
                    employeeViewModel.login = set.getString(4);
                    employeeViewModel.password = set.getString(5);
                    employeeViewModel.eMail = set.getString(6);
                    employeeViewModel.phoneNumber = set.getString(7);
                    employeeViewModels.add(employeeViewModel);
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
        if (employeeViewModels.size() > 0)
            return employeeViewModels.get(0);
        return null;
    }

    @Override
    public void insert(EmployeeBindingModel model) {
        String sql = "INSERT INTO EMPLOYEES VALUES (nextval('employees_id_seq'), (?), (?), (?), (?), (?), (?))";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, model.f_Name);
                statement.setString(2, model.l_Name);
                statement.setString(3, model.login);
                statement.setString(4, model.password);
                statement.setString(5, model.eMail);
                statement.setString(6, model.phoneNumber);
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
    public void update(EmployeeBindingModel model) throws Exception {
        EmployeeViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        String sql = "UPDATE EMPLOYEES SET f_Name = (?), l_Name = (?), login = (?), password = (?), eMail = (?), phoneNumber = (?) WHERE Id = ?";
        Thread thread = new Thread(() -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, model.f_Name);
                statement.setString(2, model.l_Name);
                statement.setString(3, model.login);
                statement.setString(4, model.password);
                statement.setString(5, model.eMail);
                statement.setString(6, model.phoneNumber);
                statement.setInt(7, model.id);
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
    public void delete(EmployeeBindingModel model) throws Exception {
        EmployeeViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }

        String sql = "DELETE FROM EMPLOYEES WHERE Id = ?";
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
}