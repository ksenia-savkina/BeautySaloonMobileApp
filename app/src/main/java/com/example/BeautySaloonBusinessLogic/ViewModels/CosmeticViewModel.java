package com.example.BeautySaloonBusinessLogic.ViewModels;

public class CosmeticViewModel {

    public int id;

    public int employeeId;

    public String cosmeticName;

    public double price;

    @Override
    public String toString() {
        return "Название: " + cosmeticName + " Стоимость: " + price;
    }
}
