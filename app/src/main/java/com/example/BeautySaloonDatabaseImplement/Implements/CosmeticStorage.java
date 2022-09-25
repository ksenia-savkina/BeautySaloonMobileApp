package com.example.BeautySaloonDatabaseImplement.Implements;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.ICosmeticStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;
import com.example.BeautySaloonViewEmployee.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class CosmeticStorage implements ICosmeticStorage {

    @Override
    public List<CosmeticViewModel> getFullList() {
        Cursor c = MainActivity.db.query("cosmetics", null, null, null, null, null, "id");
        return getList(c);
    }

    @Override
    public List<CosmeticViewModel> getFilteredList(CosmeticBindingModel model) {
        if (model == null) {
            return null;
        }
        Cursor c;
        if (model.cosmeticName != null)
            c = MainActivity.db.query("cosmetics", null, "cosmeticName = ?", new String[]{model.cosmeticName}, null, null, "id");
        else
            c = MainActivity.db.query("cosmetics", null, "employeeId = ?", new String[]{String.valueOf(model.employeeId)}, null, null, "id");
        return getList(c);
    }

    @Override
    public CosmeticViewModel getElement(CosmeticBindingModel model) {
        if (model == null) {
            return null;
        }
        Cursor c = null;
        if (model.id > -1)
            c = MainActivity.db.query("cosmetics", null, "id = ?", new String[]{String.valueOf(model.id)}, null, null, null);
        else if (model.cosmeticName != null)
            c = MainActivity.db.query("cosmetics", null, "cosmeticName = ?", new String[]{model.cosmeticName}, null, null, null);
        if (getList(c).size() > 0)
            return getList(c).get(0);
        return null;
    }

    @Override
    public void insert(CosmeticBindingModel model) {
        MainActivity.db.insert("cosmetics", null, getCV(model));
    }

    @Override
    public void update(CosmeticBindingModel model) throws Exception {
//        CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
//        cosmeticBindingModel.id = model.id;
        CosmeticViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        MainActivity.db.update("cosmetics", getCV(model), "id = ?", new String[]{String.valueOf(model.id)});
    }

    @Override
    public void delete(CosmeticBindingModel model) throws Exception {
        CosmeticViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        MainActivity.db.delete("cosmetics", "id = ?", new String[]{String.valueOf(model.id)});
    }

    private List<CosmeticViewModel> getList(Cursor c) {
        List<CosmeticViewModel> cosmeticViewModels = new ArrayList<>();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    CosmeticViewModel cosmeticViewModel = new CosmeticViewModel();
                    cosmeticViewModel.id = c.getInt(c.getColumnIndex("id"));
                    cosmeticViewModel.cosmeticName = c.getString(c.getColumnIndex("cosmeticName"));
                    cosmeticViewModel.price = c.getDouble(c.getColumnIndex("price"));
                    cosmeticViewModel.employeeId = c.getInt(c.getColumnIndex("employeeId"));
                    cosmeticViewModels.add(cosmeticViewModel);
                } while (c.moveToNext());
            }
        }
        return cosmeticViewModels;
    }

    private ContentValues getCV(CosmeticBindingModel model) {
        ContentValues cv = new ContentValues();
        cv.put("cosmeticName", model.cosmeticName);
        cv.put("price", model.price);
        cv.put("employeeId", model.employeeId);
        return cv;
    }
}