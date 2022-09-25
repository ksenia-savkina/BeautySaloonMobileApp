package com.example.BeautySaloonDatabaseImplement.Implements;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Pair;

import com.example.BeautySaloonBusinessLogic.BindingModels.DistributionBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IDistributionStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.DistributionViewModel;
import com.example.BeautySaloonViewEmployee.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DistributionStorage implements IDistributionStorage {

    SimpleDateFormat formatter;

    @Override
    public List<DistributionViewModel> getFullList() {
        Cursor c = MainActivity.db.rawQuery("select D.id, D.issueDate, D.employeeid, DC.cosmeticid, C.cosmeticName, DC.count" +
                " from distributions D join distributionCosmetics DC" +
                " on D.id = DC.distributionId" +
                " join cosmetics C on DC.cosmeticId = C.id" +
                " order by D.id;", null);
        return getList(c);
    }

    @Override
    public List<DistributionViewModel> getFilteredList(DistributionBindingModel model) {
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        if (model == null) {
            return null;
        }
        Cursor c;
        if (model.issueDate != null)
            c = MainActivity.db.rawQuery("select D.id, D.issueDate, D.employeeid, DC.cosmeticid, C.cosmeticName, DC.count" +
                    " from distributions D join distributionCosmetics DC" +
                    " on D.id = DC.distributionId" +
                    " join cosmetics C on DC.cosmeticId = C.id" +
                    " where D.issueDate = ?" +
                    " order by D.id;", new String[]{formatter.format(model.issueDate)});
        else
            c = MainActivity.db.rawQuery("select D.id, D.issueDate, D.employeeid, DC.cosmeticid, C.cosmeticName, DC.count" +
                    " from distributions D join distributionCosmetics DC" +
                    " on D.id = DC.distributionId" +
                    " join cosmetics C on DC.cosmeticId = C.id" +
                    " where D.employeeId= ?" +
                    " order by D.id;", new String[]{String.valueOf(model.employeeId)});
        return getList(c);
    }

    @Override
    public DistributionViewModel getElement(DistributionBindingModel model) {
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        if (model == null) {
            return null;
        }
        Cursor c = null;
        if (model.id > -1)
            c = MainActivity.db.rawQuery("select D.id, D.issueDate, D.employeeid, DC.cosmeticid, C.cosmeticName, DC.count" +
                    " from distributions D join distributionCosmetics DC" +
                    " on D.id = DC.distributionId" +
                    " join cosmetics C on DC.cosmeticId = C.id" +
                    " where D.id = ?;", new String[]{String.valueOf(model.id)});
        else if (model.issueDate != null)
            c = MainActivity.db.rawQuery("select D.id, D.issueDate, D.employeeid, DC.cosmeticid, C.cosmeticName, DC.count" +
                    " from distributions D join distributionCosmetics DC" +
                    " on D.id = DC.distributionId" +
                    " join cosmetics C on DC.cosmeticId = C.id" +
                    " where D.issueDate = ?;", new String[]{formatter.format(model.issueDate)});
        if (getList(c).size() > 0)
            return getList(c).get(0);
        return null;
    }

    @Override
    public void insert(DistributionBindingModel model) {
        MainActivity.db.beginTransaction();
        try {
            MainActivity.db.insert("distributions", null, getCV(model));
            Cursor cursor = MainActivity.db.rawQuery("SELECT  * FROM distributions", null);
            int id = 0;
            if (cursor.moveToLast()) {
                id = cursor.getInt(0);
            }
            for (Map.Entry entry : model.distributionCosmetics.entrySet()) {
                MainActivity.db.insert("distributionCosmetics", null, getCVDC(id, entry));
            }
            MainActivity.db.setTransactionSuccessful();
        } catch (Exception ex) {
            throw ex;
        } finally {
            MainActivity.db.endTransaction();
        }
    }

    @Override
    public void update(DistributionBindingModel model) throws Exception {
        DistributionViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        MainActivity.db.beginTransaction();
        try {
            Cursor c = MainActivity.db.query("distributionCosmetics", null, "distributionid = ?", new String[]{String.valueOf(model.id)}, null, null, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        int cosmeticId = c.getInt(c.getColumnIndex("cosmeticId"));
                        if (!model.distributionCosmetics.containsKey(cosmeticId)) {
                            MainActivity.db.delete("distributionCosmetics", "cosmeticId = ? and distributionId = ?", new String[]{String.valueOf(cosmeticId), String.valueOf(model.id)});
                        } else {
                            Map<Integer, Pair<String, Integer>> map = new HashMap<>();
                            map.put(cosmeticId, model.distributionCosmetics.get(cosmeticId));
                            for (Map.Entry entry : map.entrySet()) {
                                MainActivity.db.update("distributionCosmetics", getCVDC(model.id, entry),
                                        "distributionId = ? and cosmeticId = ?", new String[]{String.valueOf(model.id), String.valueOf(cosmeticId)});
                            }
                            model.distributionCosmetics.remove(cosmeticId);
                        }
                    } while (c.moveToNext());
                }
            }
            MainActivity.db.update("distributions", getCV(model), "id = ?", new String[]{String.valueOf(model.id)});
            for (Map.Entry entry : model.distributionCosmetics.entrySet()) {
                MainActivity.db.insert("distributionCosmetics", null, getCVDC(model.id, entry));
            }
            MainActivity.db.setTransactionSuccessful();
        } catch (Exception ex) {
            throw ex;
        } finally {
            MainActivity.db.endTransaction();
        }
    }

    @Override
    public void delete(DistributionBindingModel model) throws Exception {
        DistributionViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        MainActivity.db.delete("distributionCosmetics", "distributionId = ?", new String[]{String.valueOf(model.id)});
        MainActivity.db.delete("distributions", "id = ?", new String[]{String.valueOf(model.id)});
    }

    private List<DistributionViewModel> getList(Cursor c) {
        int id = -1;
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        List<DistributionViewModel> distributionViewModels = new ArrayList<>();
        DistributionViewModel distributionViewModel = null;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    int position = c.getPosition();
                    int newId = c.getInt(c.getColumnIndex("id"));

                    if (newId != id) {
                        distributionViewModel = new DistributionViewModel();
                        distributionViewModel.distributionCosmetics = new HashMap<>();

                        distributionViewModel.id = newId;
                        try {
                            distributionViewModel.issueDate = formatter.parse(c.getString(c.getColumnIndex("issueDate")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        distributionViewModel.employeeId = c.getInt(c.getColumnIndex("employeeId"));
                    }


                    distributionViewModel.distributionCosmetics.put(c.getInt(c.getColumnIndex("cosmeticId")),
                            new Pair<>(c.getString(c.getColumnIndex("cosmeticName")), c.getInt(c.getColumnIndex("count"))));


                    if (c.moveToNext()) {
                        if (newId != c.getInt(c.getColumnIndex("id"))) {
                            distributionViewModels.add(distributionViewModel);
                        }
                        c.moveToPosition(position);
                    } else {
                        distributionViewModels.add(distributionViewModel);
                        break;
                    }

                    id = c.getInt(c.getColumnIndex("id"));
                } while (c.moveToNext());
            }
        }
        return distributionViewModels;
    }

    private ContentValues getCV(DistributionBindingModel model) {
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        ContentValues cv = new ContentValues();
        cv.put("issueDate", formatter.format(new Date()));
        cv.put("employeeId", model.employeeId);
        return cv;
    }

    private ContentValues getCVDC(int id, Map.Entry<Integer, Pair<String, Integer>> entry) {
        ContentValues cv = new ContentValues();
        cv.put("cosmeticId", entry.getKey());
        cv.put("distributionId", id);
        cv.put("count", entry.getValue().second);
        return cv;
    }
}