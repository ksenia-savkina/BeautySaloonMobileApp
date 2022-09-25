package com.example.BeautySaloonBusinessLogic.ViewModels;

import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DistributionViewModel {
    public int id;

    public int employeeId;

    public Date issueDate;

    public Map<Integer, Pair<String, Integer>> distributionCosmetics;

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        return "Номер выдачи: " + id + " Дата: " + formatter.format(issueDate);
    }
}
