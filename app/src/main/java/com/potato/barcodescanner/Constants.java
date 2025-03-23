package com.potato.barcodescanner;

public class Constants {

    //dir name
    public static final String potatoDir = "POTATOSCRIPT/XQR Scanner";
    //db name
    public static final String DB_NAME = "DATA_DB.sqlite";
    //db version
    public static final int DB_VERSION = 1;
    //table name
    public static final String TABLE_NAME = "DATA_TABLE";
    //colums/fields of table
    public static final String C_ID = "ID";
    public static final String C_REMARK = "REMARK";
    public static final String C_IMAGE = "IMAGE";
    public static final String C_BARCODE = "BARCODE";
    public static final String C_NUMBER = "NUMBER";
    //public static final String C_ADDED_TIMESTAMP = "ADDED_TIME_STAMP";
    //public static final String C_UPDATED_TIMESTAMP = "UPDATED_TIME_STAMP";
    //Create table query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_REMARK + " TEXT,"
            + C_BARCODE + " TEXT,"
            + C_IMAGE + " BLOG"
            //+ C_NUMBER + " TEXT"
            + ")";


}
