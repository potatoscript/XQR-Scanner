package com.potato.barcodescanner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table on the db
        db.execSQL(Constants.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //upgrade database (if there is any structure change the db version)
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        //after drop the old table recreate it again
        onCreate(db);
    }


    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //insert data into database
    //public void insertData(String name, String barcode, byte[] image, Double number) {
    public void insertData(String name, String barcode, byte[] image) {
        SQLiteDatabase database = getWritableDatabase();

        String select = "SELECT " + Constants.C_BARCODE + " FROM " + Constants.TABLE_NAME + " WHERE " + Constants.C_BARCODE + " = '" + barcode + "'";
        Cursor cursor = getData(select);
        int gotdata = 0;
        while (cursor.moveToNext()) {
            gotdata = 1;
        }
        if (gotdata == 0) {
            String sql = "INSERT INTO " + Constants.TABLE_NAME + " VALUES (NULL, ?, ?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();

            statement.bindString(1, name);
            statement.bindString(2, barcode);
            statement.bindBlob(3, image);
            //statement.bindDouble(4, 0.0);

            statement.executeInsert();

            statement.close();
        } else {

            String update = "UPDATE " + Constants.TABLE_NAME +
                    " SET " + Constants.C_REMARK + " = ?,  " + Constants.C_IMAGE + " = ?  "+
                    " WHERE " + Constants.C_BARCODE + " = '" + barcode + "'";
            SQLiteStatement statement = database.compileStatement(update);
            statement.bindString(1, name);
            statement.bindBlob(2, image);
            //statement.bindString(2, image);
            //statement.bindDouble(3, 0.0);
            statement.executeUpdateDelete();
            statement.close();
        }

        database.close();
    }

    public void importData(String name, String barcode, byte[] image, Double number) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + Constants.TABLE_NAME + " VALUES (NULL, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, barcode);
        statement.bindBlob(3, image);
        statement.bindDouble(4, 0.0);

        statement.executeInsert();

        statement.close();

        database.close();
    }


    //delete data into database
    public void deleteData(String barcode) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM " + Constants.TABLE_NAME + " WHERE " + Constants.C_BARCODE + " = '" + barcode + "'";

        SQLiteStatement statement = database.compileStatement(sql);

        statement.executeUpdateDelete();

        statement.close();
        database.close();
    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }


}
