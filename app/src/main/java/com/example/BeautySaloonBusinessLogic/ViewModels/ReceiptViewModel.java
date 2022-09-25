package com.example.BeautySaloonBusinessLogic.ViewModels;

import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ReceiptViewModel {
    public int id;

    public int employeeId;

    public double totalCost;

    public Date purchaseDate;

    public Map<Integer, Pair<String, Integer>> receiptCosmetics;

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        return "Номер чека: " + id + " Общая стоимость: " + totalCost + " Дата: " + formatter.format(purchaseDate);
    }
}