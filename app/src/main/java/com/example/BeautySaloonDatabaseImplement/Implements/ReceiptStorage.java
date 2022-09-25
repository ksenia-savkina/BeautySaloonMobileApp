package com.example.BeautySaloonDatabaseImplement.Implements;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Pair;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReceiptBindingModel;
import com.example.BeautySaloonBusinessLogic.Interfaces.IReceiptStorage;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReceiptViewModel;
import com.example.BeautySaloonViewEmployee.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReceiptStorage implements IReceiptStorage {

    SimpleDateFormat formatter;

    @Override
    public List<ReceiptViewModel> getFullList() {
        Cursor c = MainActivity.db.rawQuery("select R.id, R.totalcost, R.purchasedate, R.employeeid, RC.cosmeticid, C.cosmeticName, RC.count" +
                " from receipts R join receiptCosmetics RC" +
                " on R.id = RC.receiptId" +
                " join cosmetics C on RC.cosmeticId = C.id" +
                " order by R.id;", null);
        //Cursor c = MainActivity.db.query("receipts", null, null, null, null, null, null);
        return getList(c);
    }

    @Override
    public List<ReceiptViewModel> getFilteredList(ReceiptBindingModel model) {
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        if (model == null) {
            return null;
        }
        Cursor c;
        if (model.purchaseDate != null)
            c = MainActivity.db.rawQuery("select R.id, R.totalcost, R.purchasedate, R.employeeid, RC.cosmeticid, C.cosmeticName, RC.count" +
                    " from receipts R join receiptCosmetics RC" +
                    " on R.id = RC.receiptId" +
                    " join cosmetics C on RC.cosmeticId = C.id" +
                    " where R.purchasedate = ?" +
                    " order by R.id;", new String[]{formatter.format(model.purchaseDate)});
            //c = MainActivity.db.query("receipts", null, "purchaseDate = ?", new String[]{formatter.format(model.purchaseDate)}, null, null, null);
        else
            c = MainActivity.db.rawQuery("select R.id, R.totalcost, R.purchasedate, R.employeeid, RC.cosmeticid, C.cosmeticName, RC.count" +
                    " from receipts R join receiptCosmetics RC" +
                    " on R.id = RC.receiptId" +
                    " join cosmetics C on RC.cosmeticId = C.id" +
                    " where R.employeeId= ?" +
                    " order by R.id;", new String[]{String.valueOf(model.employeeId)});
        //c = MainActivity.db.query("receipts", null, "employeeId = ?", new String[]{String.valueOf(model.employeeId)}, null, null, null);

        //Cursor c = MainActivity.db.query("receipts", null, "purchaseDate = ?", new String[]{formatter.format(model.purchaseDate)}, null, null, null);
        return getList(c);
    }

    @Override
    public ReceiptViewModel getElement(ReceiptBindingModel model) {
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        if (model == null) {
            return null;
        }
        Cursor c = null;
        if (model.id > -1)
            c = MainActivity.db.rawQuery("select R.id, R.totalcost, R.purchasedate, R.employeeid, RC.cosmeticid, C.cosmeticName, RC.count" +
                    " from receipts R join receiptCosmetics RC" +
                    " on R.id = RC.receiptId" +
                    " join cosmetics C on RC.cosmeticId = C.id" +
                    " where R.id = ?;", new String[]{String.valueOf(model.id)});
        else if (model.purchaseDate != null)
            c = MainActivity.db.rawQuery("select R.id, R.totalcost, R.purchasedate, R.employeeid, RC.cosmeticid, C.cosmeticName, RC.count" +
                    " from receipts R join receiptCosmetics RC" +
                    " on R.id = RC.receiptId" +
                    " join cosmetics C on RC.cosmeticId = C.id" +
                    " where R.purchasedate = ?;", new String[]{formatter.format(model.purchaseDate)});
        if (getList(c).size() > 0)
            return getList(c).get(0);
        return null;
    }

    @Override
    public void insert(ReceiptBindingModel model) {
        MainActivity.db.beginTransaction();
        try {
            MainActivity.db.insert("receipts", null, getCV(model));
            Cursor cursor = MainActivity.db.rawQuery("SELECT  * FROM receipts", null);
            int id = 0;
            if (cursor.moveToLast()) {
                id = cursor.getInt(0);
            }
            for (Map.Entry entry : model.receiptCosmetics.entrySet()) {
                MainActivity.db.insert("receiptCosmetics", null, getCVRC(id, entry));
            }
            MainActivity.db.setTransactionSuccessful();
        } catch (Exception ex) {
            throw ex;
        } finally {
            MainActivity.db.endTransaction();
        }
    }

    @Override
    public void update(ReceiptBindingModel model) throws Exception {
        ReceiptViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        MainActivity.db.beginTransaction();
        try {
            Cursor c = MainActivity.db.query("receiptCosmetics", null, "receiptid = ?", new String[]{String.valueOf(model.id)}, null, null, null);
//            Cursor c = MainActivity.db.rawQuery("select RC.receiptid, RC.cosmeticid, C.cosmeticName, RC.count" +
//                    " from cosmetics C join receiptCosmetics RC" +
//                    " on RC.cosmeticId = C.id" +
//                    " where RC.receiptid = ?;", new String[]{String.valueOf(model.id)});

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        int cosmeticId = c.getInt(c.getColumnIndex("cosmeticId"));
                        if (!model.receiptCosmetics.containsKey(cosmeticId)) {
                            MainActivity.db.delete("receiptCosmetics", "cosmeticId = ? and receiptId = ?", new String[]{String.valueOf(cosmeticId), String.valueOf(model.id)});
                        } else {
                            Map<Integer, Pair<String, Integer>> map = new HashMap<>();
//                        map.put(cosmeticId, new Pair<>(c.getString(c.getColumnIndex("cosmeticName")),
//                                c.getInt(c.getColumnIndex("count"))));
                            map.put(cosmeticId, model.receiptCosmetics.get(cosmeticId));
                            for (Map.Entry entry : map.entrySet()) {
                                MainActivity.db.update("receiptCosmetics", getCVRC(model.id, entry),
                                        "receiptId = ? and cosmeticId = ?", new String[]{String.valueOf(model.id), String.valueOf(cosmeticId)});
                            }

                            model.receiptCosmetics.remove(cosmeticId);
                        }
                    } while (c.moveToNext());
                }
            }
            MainActivity.db.update("receipts", getCV(model), "id = ?", new String[]{String.valueOf(model.id)});
            for (Map.Entry entry : model.receiptCosmetics.entrySet()) {
                MainActivity.db.insert("receiptCosmetics", null, getCVRC(model.id, entry));
            }
            MainActivity.db.setTransactionSuccessful();
        } catch (Exception ex) {
            throw ex;
        } finally {
            MainActivity.db.endTransaction();
        }
    }

    @Override
    public void delete(ReceiptBindingModel model) throws Exception {
        ReceiptViewModel element = getElement(model);
        if (element == null) {
            throw new Exception("Элемент не найден");
        }
        MainActivity.db.delete("receiptCosmetics", "receiptId = ?", new String[]{String.valueOf(model.id)});
        MainActivity.db.delete("receipts", "id = ?", new String[]{String.valueOf(model.id)});
    }

    private List<ReceiptViewModel> getList(Cursor c) {
        int id = -1;
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        List<ReceiptViewModel> receiptViewModels = new ArrayList<>();
        ReceiptViewModel receiptViewModel = null;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    int position = c.getPosition();
                    int newId = c.getInt(c.getColumnIndex("id"));

//                    if (c.moveToNext()) {
//                        if (newId != c.getInt(c.getColumnIndex("id"))) {
//                            c.moveToPosition(position);
//                            receiptViewModel = new ReceiptViewModel();
//                            receiptViewModel.receiptCosmetics = new HashMap<>();
//
//                            receiptViewModel.id = newId;
//                            receiptViewModel.totalCost = c.getDouble(c.getColumnIndex("totalCost"));
//                            try {
//                                receiptViewModel.purchaseDate = formatter.parse(c.getString(c.getColumnIndex("purchaseDate")));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            receiptViewModel.employeeId = c.getInt(c.getColumnIndex("employeeId"));
//                        }
//                        else
//                            c.moveToPosition(position);
//                    } else {
//                        c.moveToPosition(position);
//                    }

                    if (newId != id) {
                        receiptViewModel = new ReceiptViewModel();
                        receiptViewModel.receiptCosmetics = new HashMap<>();

                        receiptViewModel.id = newId;
                        receiptViewModel.totalCost = c.getDouble(c.getColumnIndex("totalCost"));
                        try {
                            receiptViewModel.purchaseDate = formatter.parse(c.getString(c.getColumnIndex("purchaseDate")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        receiptViewModel.employeeId = c.getInt(c.getColumnIndex("employeeId"));
                    }


                    receiptViewModel.receiptCosmetics.put(c.getInt(c.getColumnIndex("cosmeticId")),
                            new Pair<>(c.getString(c.getColumnIndex("cosmeticName")), c.getInt(c.getColumnIndex("count"))));


                    if (c.moveToNext()) {
                        if (newId != c.getInt(c.getColumnIndex("id"))) {
                            receiptViewModels.add(receiptViewModel);
                        }
                        c.moveToPosition(position);
                    } else {
                        receiptViewModels.add(receiptViewModel);
                        break;
                    }

                    id = c.getInt(c.getColumnIndex("id"));
                } while (c.moveToNext());
            }
        }
        return receiptViewModels;
    }

    private ContentValues getCV(ReceiptBindingModel model) {
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        ContentValues cv = new ContentValues();
        cv.put("totalCost", model.totalCost);
        cv.put("purchaseDate", formatter.format(new Date()));
        cv.put("employeeId", model.employeeId);
        return cv;
    }

    private ContentValues getCVRC(int id, Map.Entry<Integer, Pair<String, Integer>> entry) {
        ContentValues cv = new ContentValues();
        cv.put("cosmeticId", entry.getKey());
        cv.put("receiptId", id);
        cv.put("count", entry.getValue().second);
        return cv;
    }

//    private void createModel(ReceiptBindingModel model) {
//        if (model.id == -1) {
//            MainActivity.db.insert("receipts", null, getCV(model));
//            Cursor cursor = MainActivity.db.rawQuery("SELECT  * FROM receipts", null);
//            int id = 0;
//            if (cursor.moveToLast()) {
//                id = cursor.getInt(0);
//            }
//            for (Map.Entry entry : model.receiptCosmetics.entrySet()) {
//                MainActivity.db.insert("receiptCosmetics", null, getCVRC(id, entry));
//            }
//        } else {
////            Cursor c = MainActivity.db.rawQuery("select *" +
////                    " from receiptCosmetics " +
////                    " where receiptid = ?;", new String[]{String.valueOf((model.id))});
//            //Cursor c = MainActivity.db.query("receiptCosmetics", null, "receiptId = ?", new String[]{String.valueOf((model.id))}, null, null, null);
//            Cursor c = MainActivity.db.rawQuery("select RC.receiptid, RC.cosmeticid, C.cosmeticName, RC.count" +
//                    " from cosmetics C join receiptCosmetics RC" +
//                    " on RC.cosmeticId = C.id" +
//                    " where RC.receiptid = ?;", new String[]{String.valueOf(model.id)});
//            if (c != null) {
//                if (c.moveToFirst()) {
//                    do {
//                        int cosmeticId = c.getInt(c.getColumnIndex("cosmeticId"));
//                        if (!model.receiptCosmetics.containsKey(cosmeticId)) {
//                            MainActivity.db.delete("receiptCosmetics", "cosmeticId = ? and receiptId = ?", new String[]{String.valueOf(cosmeticId), String.valueOf(model.id)});
//                        } else {
//                            Map<Integer, Pair<String, Integer>> map = new HashMap<>();
////                        map.put(cosmeticId, new Pair<>(c.getString(c.getColumnIndex("cosmeticName")),
////                                c.getInt(c.getColumnIndex("count"))));
//                            map.put(cosmeticId, model.receiptCosmetics.get(cosmeticId));
//                            for (Map.Entry entry : map.entrySet()) {
//                                MainActivity.db.update("receiptCosmetics", getCVRC(model.id, entry),
//                                        "receiptId = ? and cosmeticId = ?", new String[]{String.valueOf(model.id), String.valueOf(cosmeticId)});
//                            }
//                            model.receiptCosmetics.remove(cosmeticId);
//                        }
//                    } while (c.moveToNext());
//                }
//            }
//            MainActivity.db.update("receipts", getCV(model), "id = ?", new String[]{String.valueOf(model.id)});
//            for (Map.Entry entry : model.receiptCosmetics.entrySet()) {
//                MainActivity.db.insert("receiptCosmetics", null, getCVRC(model.id, entry));
//            }
//
//        }
//    }
}