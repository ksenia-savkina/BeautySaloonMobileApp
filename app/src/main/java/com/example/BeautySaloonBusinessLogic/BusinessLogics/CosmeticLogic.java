package com.example.BeautySaloonBusinessLogic.BusinessLogics;

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.ICosmeticStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.CosmeticStorage;
import com.example.BeautySaloonPostgresImplements.CosmeticStorageP;
import com.example.BeautySaloonViewEmployee.MainActivity;
import com.example.BeautySaloonViewEmployee.PostgresDB;

import java.util.Arrays;
import java.util.List;

public class CosmeticLogic {

    private final ICosmeticStorage _cosmeticStorage;

    public CosmeticLogic(ICosmeticStorage cosmeticStorage) {
        _cosmeticStorage = cosmeticStorage;
    }

    public List<CosmeticViewModel> read(CosmeticBindingModel model) {
        if (model == null) {
            return _cosmeticStorage.getFullList();
        }
        if (model.id != -1) {
            return Arrays.asList(_cosmeticStorage.getElement(model));
        }
        return _cosmeticStorage.getFilteredList(model);
    }

    public void createOrUpdate(CosmeticBindingModel model) throws Exception {
        CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
        cosmeticBindingModel.id = -1;
        cosmeticBindingModel.cosmeticName = model.cosmeticName;
        CosmeticViewModel element = _cosmeticStorage.getElement(cosmeticBindingModel);
        if (element != null && element.id != model.id) {
            throw new Exception("Уже есть косметика с таким названием");
        }
        if (model.id != -1) {
            _cosmeticStorage.update(model);
        } else {
            _cosmeticStorage.insert(model);
        }
    }

    public void delete(CosmeticBindingModel model) throws Exception {
        CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
        cosmeticBindingModel.id = model.id;
        CosmeticViewModel element = _cosmeticStorage.getElement(cosmeticBindingModel);
        if (element == null) {
            throw new Exception("Косметика не найдена");
        }
        _cosmeticStorage.delete(model);
    }

    public void sync(int employeeId) {
        ICosmeticStorage cosmeticStorageFrom;
        ICosmeticStorage cosmeticStorageTo;
        if (MainActivity.storage.equals("client-server")) {
            cosmeticStorageFrom = new CosmeticStorage();
            cosmeticStorageTo = new CosmeticStorageP(PostgresDB.connection);
        } else {
            cosmeticStorageFrom = new CosmeticStorageP(PostgresDB.connection);
            cosmeticStorageTo = new CosmeticStorage();
        }

        List<CosmeticViewModel> listFrom = cosmeticStorageFrom.getFullList();

        for (CosmeticViewModel cosmeticViewModel : listFrom) {
            CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
            cosmeticBindingModel.id = -1;
            cosmeticBindingModel.cosmeticName = cosmeticViewModel.cosmeticName;
            CosmeticViewModel element = cosmeticStorageTo.getElement(cosmeticBindingModel);
            if (element == null) {
                cosmeticBindingModel.employeeId = employeeId;
                cosmeticBindingModel.price = cosmeticViewModel.price;
                cosmeticStorageTo.insert(cosmeticBindingModel);
            }
        }
    }
}