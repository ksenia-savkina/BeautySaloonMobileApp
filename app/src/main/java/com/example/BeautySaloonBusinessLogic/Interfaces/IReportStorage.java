package com.example.BeautySaloonBusinessLogic.Interfaces;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReportBindingModelEmployee;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportCosmeticsViewModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReportPurchaseCosmeticViewModel;

import java.util.List;

public interface IReportStorage {
    List<ReportPurchaseCosmeticViewModel> getPurchaseList(int cosmeticId);

    List<ReportCosmeticsViewModel> getCosmetics(ReportBindingModelEmployee model);
}