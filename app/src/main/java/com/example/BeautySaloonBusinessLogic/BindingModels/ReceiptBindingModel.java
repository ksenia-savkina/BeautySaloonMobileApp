package com.example.BeautySaloonBusinessLogic.BindingModels;

import android.util.Pair;

import java.util.Date;
import java.util.Map;

public class ReceiptBindingModel {

    public int id;

    public int employeeId;

    public double totalCost;

    public Date purchaseDate;

    public Map<Integer, Pair<String, Integer>> receiptCosmetics;
}