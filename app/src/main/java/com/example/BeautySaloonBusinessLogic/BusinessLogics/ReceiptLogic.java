package com.example.BeautySaloonBusinessLogic.BusinessLogics;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReceiptBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IReceiptStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReceiptViewModel;

import java.util.Arrays;
import java.util.List;

public class ReceiptLogic {

    private final IReceiptStorage _receiptStorage;

    public ReceiptLogic(IReceiptStorage receiptStorage) {
        _receiptStorage = receiptStorage;
    }

    public List<ReceiptViewModel> read(ReceiptBindingModel model) {
        if (model == null) {
            return _receiptStorage.getFullList();
        }
        if (model.id != -1) {
            return Arrays.asList(_receiptStorage.getElement(model));
        }
        return _receiptStorage.getFilteredList(model);
    }

    public void createOrUpdate(ReceiptBindingModel model) throws Exception {
        ReceiptBindingModel receiptBindingModel = new ReceiptBindingModel();
        receiptBindingModel.id = -1;
        receiptBindingModel.purchaseDate = model.purchaseDate;
        ReceiptViewModel element = _receiptStorage.getElement(receiptBindingModel);
        if (element != null && element.id != model.id) {
            throw new Exception("Уже пробит чек в данное время");
        }
        if (model.id != -1) {
            _receiptStorage.update(model);
        } else {
            _receiptStorage.insert(model);
        }
    }

    public void delete(ReceiptBindingModel model) throws Exception {
        ReceiptBindingModel receiptBindingModel = new ReceiptBindingModel();
        receiptBindingModel.id = model.id;
        ReceiptViewModel element = _receiptStorage.getElement(receiptBindingModel);
        if (element == null) {
            throw new Exception("Чек не найден");
        }
        _receiptStorage.delete(model);
    }
}