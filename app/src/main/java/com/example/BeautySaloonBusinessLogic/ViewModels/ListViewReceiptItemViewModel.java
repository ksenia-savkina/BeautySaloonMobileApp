package com.example.BeautySaloonBusinessLogic.ViewModels;

public class ListViewReceiptItemViewModel {
    public int id;

    public String cosmeticName;

    public double price;

    public int count;

    @Override
    public String toString() {
        return "Косметика: " + cosmeticName + " Цена: " + price + " Количество: " + count;
    }
}