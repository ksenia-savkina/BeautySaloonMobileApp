package com.example.BeautySaloonBusinessLogic.ViewModels;

public class ListViewDistributionItemViewModel {
    public int id;

    public String cosmeticName;

    public int count;

    @Override
    public String toString() {
        return "Косметика: " + cosmeticName + " Количество: " + count;
    }
}