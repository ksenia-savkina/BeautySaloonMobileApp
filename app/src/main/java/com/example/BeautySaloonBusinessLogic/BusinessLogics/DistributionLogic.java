package com.example.BeautySaloonBusinessLogic.BusinessLogics;

import com.example.BeautySaloonBusinessLogic.BindingModels.DistributionBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IDistributionStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.DistributionViewModel;

import java.util.Arrays;
import java.util.List;

public class DistributionLogic {

    private final IDistributionStorage _distributionStorage;

    public DistributionLogic(IDistributionStorage distributionStorage) {
        _distributionStorage = distributionStorage;
    }

    public List<DistributionViewModel> read(DistributionBindingModel model) {
        if (model == null) {
            return _distributionStorage.getFullList();
        }
        if (model.id != -1) {
            return Arrays.asList(_distributionStorage.getElement(model));
        }
        return _distributionStorage.getFilteredList(model);
    }

    public void createOrUpdate(DistributionBindingModel model) throws Exception {
        DistributionBindingModel distributionBindingModel = new DistributionBindingModel();
        distributionBindingModel.id = -1;
        distributionBindingModel.issueDate = model.issueDate;
        DistributionViewModel element = _distributionStorage.getElement(distributionBindingModel);
        if (element != null && element.id != model.id) {
            throw new Exception("Уже произведена выдача в данное время");
        }
        if (model.id != -1) {
            _distributionStorage.update(model);
        } else {
            _distributionStorage.insert(model);
        }
    }

    public void delete(DistributionBindingModel model) throws Exception {
        DistributionBindingModel distributionBindingModel = new DistributionBindingModel();
        distributionBindingModel.id = model.id;
        DistributionViewModel element = _distributionStorage.getElement(distributionBindingModel);
        if (element == null) {
            throw new Exception("Выдача не найдена");
        }
        _distributionStorage.delete(model);
    }
}