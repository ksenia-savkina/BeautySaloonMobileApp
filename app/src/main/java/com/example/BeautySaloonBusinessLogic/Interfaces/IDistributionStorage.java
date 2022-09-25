package com.example.BeautySaloonBusinessLogic.Interfaces;

import com.example.BeautySaloonBusinessLogic.BindingModels.DistributionBindingModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.DistributionViewModel;

import java.util.List;

public interface IDistributionStorage {

    List<DistributionViewModel> getFullList();

    List<DistributionViewModel> getFilteredList(DistributionBindingModel model);

    DistributionViewModel getElement(DistributionBindingModel model);

    void insert(DistributionBindingModel model);

    void update(DistributionBindingModel model) throws Exception;

    void delete(DistributionBindingModel model) throws Exception;
}