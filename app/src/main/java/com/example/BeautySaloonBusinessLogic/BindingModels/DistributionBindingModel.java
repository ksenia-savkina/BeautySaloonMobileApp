package com.example.BeautySaloonBusinessLogic.BindingModels;

import android.util.Pair;

import java.util.Date;
import java.util.Map;

public class DistributionBindingModel {

    public int id;

    public int employeeId;

    public Date issueDate;

    public Map<Integer, Pair<String, Integer>> distributionCosmetics;
}