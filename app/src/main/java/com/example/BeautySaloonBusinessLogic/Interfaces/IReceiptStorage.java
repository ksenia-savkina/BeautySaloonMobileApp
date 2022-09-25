package com.example.BeautySaloonBusinessLogic.Interfaces;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReceiptBindingModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReceiptViewModel;

import java.util.List;

public interface IReceiptStorage {

    List<ReceiptViewModel> getFullList();

    List<ReceiptViewModel> getFilteredList(ReceiptBindingModel model);

    ReceiptViewModel getElement(ReceiptBindingModel model);

    void insert(ReceiptBindingModel model);

    void update(ReceiptBindingModel model) throws Exception;

    void delete(ReceiptBindingModel model) throws Exception;
}