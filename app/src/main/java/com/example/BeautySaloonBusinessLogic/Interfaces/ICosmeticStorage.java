package com.example.BeautySaloonBusinessLogic.Interfaces;

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;

import java.util.List;

public interface ICosmeticStorage {
    List<CosmeticViewModel> getFullList();

    List<CosmeticViewModel> getFilteredList(CosmeticBindingModel model);

    CosmeticViewModel getElement(CosmeticBindingModel model);

    void insert(CosmeticBindingModel model);

    void update(CosmeticBindingModel model) throws Exception;

    void delete(CosmeticBindingModel model) throws Exception;
}
