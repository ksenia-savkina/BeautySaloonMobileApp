package com.example.BeautySaloonViewEmployee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "BeautySaloon", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // создаем таблицу с полями
        db.execSQL("create table employees ("
                + "id integer primary key autoincrement,"
                + "f_name text,"
                + "l_name text,"
                + "login text,"
                + "password text,"
                + "email text,"
                + "phonenumber text" + ");");

        db.execSQL("create table cosmetics ("
                + "id integer primary key autoincrement,"
                + "cosmeticName text,"
                + "price real,"
                + "employeeId integer not null,"
                + "FOREIGN KEY (employeeId) REFERENCES employees(id)" + ");");

        db.execSQL("create table receipts ("
                + "id integer primary key autoincrement,"
                + "totalCost real,"
                + "purchaseDate text,"
                + "employeeId integer not null,"
                + "FOREIGN KEY (employeeId) REFERENCES employees(id)" + ");");


        db.execSQL("create table receiptCosmetics ("
                + "cosmeticId integer NOT NULL,"
                + "receiptId integer NOT NULL,"
                + "count integer,"
                + "FOREIGN KEY (cosmeticId) REFERENCES cosmetics(id),"
                + "FOREIGN KEY (receiptId) REFERENCES receipts(id)" + ");");


        db.execSQL("create table clients ("
                + "id integer primary key autoincrement,"
                + "clientName text,"
                + "clientSurname text,"
                + "mail text,"
                + "tel text,"
                + "login text,"
                + "password text" + ");");

        db.execSQL("create table distributions ("
                + "id integer primary key autoincrement,"
                + "issueDate text,"
                + "employeeId integer not null,"
                //+ "visitId integer,"
                + "FOREIGN KEY (employeeId) REFERENCES employees(id)" + ");");


        db.execSQL("create table distributionCosmetics ("
                + "cosmeticId integer NOT NULL,"
                + "distributionId integer NOT NULL,"
                + "count integer,"
                + "FOREIGN KEY (cosmeticId) REFERENCES cosmetics(id),"
                + "FOREIGN KEY (distributionId) REFERENCES distributions(id)" + ");");


        db.execSQL("create table purchases ("
                + "id integer primary key autoincrement,"
                + "date text,"
                + "price real,"
                + "clientId integer not null,"
                + "receiptId integer not null,"
                + "FOREIGN KEY (clientId) REFERENCES clients(id),"
                + "FOREIGN KEY (receiptId) REFERENCES receipts(id)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}