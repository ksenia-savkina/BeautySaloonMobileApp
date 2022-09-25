package com.example.BeautySaloonBusinessLogic.Interfaces;

import com.example.BeautySaloonBusinessLogic.BindingModels.EmployeeBindingModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.EmployeeViewModel;

import java.util.List;

public interface IEmployeeStorage {

    List<EmployeeViewModel> getFullList();

    List<EmployeeViewModel> getFilteredList(EmployeeBindingModel model);

    EmployeeViewModel getElement(EmployeeBindingModel model);

    void insert(EmployeeBindingModel model);

    void update(EmployeeBindingModel model) throws Exception;

    void delete(EmployeeBindingModel model) throws Exception;
}
