package com.example.BeautySaloonBusinessLogic.HelperModels;

import com.example.BeautySaloonBusinessLogic.ViewModels.ReportCosmeticsViewModel;

import java.util.Date;
import java.util.List;

public class PdfInfoEmployee {
    public String fileName;

    public String title;

    public Date dateFrom;

    public Date dateTo;

    public int employeeId;

    public List<ReportCosmeticsViewModel> cosmetics;
}