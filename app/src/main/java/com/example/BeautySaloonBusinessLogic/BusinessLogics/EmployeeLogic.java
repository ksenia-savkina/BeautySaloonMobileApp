package com.example.BeautySaloonBusinessLogic.BusinessLogics;

import com.example.BeautySaloonBusinessLogic.BindingModels.EmployeeBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IEmployeeStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.EmployeeViewModel;

import java.util.Arrays;
import java.util.List;

public class EmployeeLogic {

    private final IEmployeeStorage _employeeStorage;

    public EmployeeLogic(IEmployeeStorage employeeStorage) {
        _employeeStorage = employeeStorage;
    }

    public List<EmployeeViewModel> read(EmployeeBindingModel model) {
        if (model == null) {
            return _employeeStorage.getFullList();
        }
        if (model.id != -1) {
            return Arrays.asList(_employeeStorage.getElement(model));
        }
        return _employeeStorage.getFilteredList(model);
    }

    public void createOrUpdate(EmployeeBindingModel model) throws Exception {
        EmployeeBindingModel employeeBindingModel = new EmployeeBindingModel();
        employeeBindingModel.id = -1;
        employeeBindingModel.login = model.login;
        employeeBindingModel.eMail = model.eMail;
        employeeBindingModel.phoneNumber = model.phoneNumber;
        EmployeeViewModel element = _employeeStorage.getElement(employeeBindingModel);
        if (element != null && element.id != model.id) {
            throw new Exception("Уже есть пользователь с такими данными");
        }
        if (model.id != -1) {
            _employeeStorage.update(model);
        } else {
            _employeeStorage.insert(model);
        }
    }

    public void delete(EmployeeBindingModel model) throws Exception {
        EmployeeBindingModel employeeBindingModel = new EmployeeBindingModel();
        employeeBindingModel.id = model.id;
        EmployeeViewModel element = _employeeStorage.getElement(employeeBindingModel);
        if (element == null) {
            throw new Exception("Пользователь не найден");
        }
        _employeeStorage.delete(model);
    }
}