package com.example.BeautySaloonDatabaseImplement.Implements;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.BeautySaloonBusinessLogic.BindingModels.EmployeeBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IEmployeeStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.EmployeeViewModel;
import com.example.BeautySaloonViewEmployee.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class EmployeeStorage implements IEmployeeStorage {
    @Override
    public List<EmployeeViewModel> getFullList() {
        Cursor c = MainActivity.db.query("employees", null, null, null, null, null, null);
        return getList(c);
    }

    @Override
    public List<EmployeeViewModel> getFilteredList(EmployeeBindingModel model) {
        if (model == null) {
            return null;
        }
        Cursor c = MainActivity.db.query("employees", null, "login = ? and password = ?", new String[]{model.login, model.password}, null, null, null);
        return getList(c);
    }

    @Override
    public EmployeeViewModel getElement(EmployeeBindingModel model) {
        if (model == null) {
            return null;
        }
        Cursor c;
        if (model.login != null)
            c = MainActivity.db.query("employees", null, "id = ? or login = ? or email = ? or phoneNumber = ?", new String[]{String.valueOf(model.id), model.login, model.eMail, model.phoneNumber}, null, null, null);
        else
            c = MainActivity.db.query("employees", null, "id = ?", new String[]{String.valueOf(model.id)}, null, null, null);
        if (getList(c).size() > 0)
            return getList(c).get(0);
        else
            return null;
    }

    @Override
    public void insert(EmployeeBindingModel model) {
        MainActivity.db.insert("employees", null, getCV(model));
    }

    @Override
    public void update(EmployeeBindingModel model) throws Exception {
        EmployeeViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        MainActivity.db.update("employees", getCV(model), "id = ?", new String[]{String.valueOf(model.id)});
    }

    @Override
    public void delete(EmployeeBindingModel model) throws Exception {
        EmployeeViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        MainActivity.db.delete("employees", "id = ?", new String[]{String.valueOf(model.id)});
    }

    private List<EmployeeViewModel> getList(Cursor c) {
        List<EmployeeViewModel> employeeViewModels = new ArrayList<>();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    EmployeeViewModel employeeViewModel = new EmployeeViewModel();
                    employeeViewModel.id = c.getInt(c.getColumnIndex("id"));
                    employeeViewModel.f_Name = c.getString(c.getColumnIndex("f_name"));
                    employeeViewModel.l_Name = c.getString(c.getColumnIndex("l_name"));
                    employeeViewModel.login = c.getString(c.getColumnIndex("login"));
                    employeeViewModel.password = c.getString(c.getColumnIndex("password"));
                    employeeViewModel.eMail = c.getString(c.getColumnIndex("email"));
                    employeeViewModel.phoneNumber = c.getString(c.getColumnIndex("phonenumber"));
                    employeeViewModels.add(employeeViewModel);
                } while (c.moveToNext());
            }
        }
        return employeeViewModels;
    }

    private ContentValues getCV(EmployeeBindingModel model) {
        ContentValues cv = new ContentValues();
        cv.put("f_name", model.f_Name);
        cv.put("l_name", model.l_Name);
        cv.put("login", model.login);
        cv.put("password", model.password);
        cv.put("email", model.eMail);
        cv.put("phonenumber", model.phoneNumber);
        return cv;
    }
}