package tw.foxo.boise.upost.databaseHandler

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SettingDbHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_SETTING + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_USERNAME
                + " TEXT," + COLUMN_USERTOKEN + " TEXT" + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    fun deleteToken(){
        val query =
            "DELETE FROM $TABLE_SETTING"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }
    fun setToken(token:String) {
        if (this.isToken()!!){
            this.deleteToken()
        }
        val values = ContentValues()
        values.put(COLUMN_USERNAME, "need_init")
        values.put(COLUMN_USERTOKEN, token)

        val db = this.writableDatabase

        db.insert(TABLE_SETTING, null, values)
        db.close()
    }
    fun getToken():String? {
        val query =
            "SELECT * FROM $TABLE_SETTING"

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)
        var token: String? = null

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)
            token = cursor.getString(2)

        }

        db.close()
        return token
    }
    fun isToken():Boolean? {
        val query =
            "SELECT * FROM $TABLE_SETTING"

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)
        var boolean: Boolean = false

        if (cursor.moveToFirst()) {
            boolean = true
        }
        db.close()
        return boolean
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "settingDB.db"
        val TABLE_SETTING = "setting"
        val COLUMN_ID = "_id"
        val COLUMN_USERNAME = "username"
        val COLUMN_USERTOKEN = "token"
    }
}