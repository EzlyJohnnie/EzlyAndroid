package com.ezly.ezly_android.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ezly.ezly_android.Data.EzlyCategory;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Johnnie on 20/10/16.
 */

public class EzlyDBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "EzlyAndroid.db";

    private static EzlyDBHelper instance;
    private Dao<EzlyCategory, Integer> categoryDao;


    //should called in DI only
    public static synchronized EzlyDBHelper getInstance(Context context)
    {
        if (instance == null) {
            synchronized (EzlyDBHelper.class) {
                if (instance == null) {
                    instance = new EzlyDBHelper(context);
                    instance.getWritableDatabase();
                }
            }
        }

        return instance;
    }

    private EzlyDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource)
    {
        try {
            TableUtils.createTable(connectionSource, EzlyCategory.class);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        try {
            TableUtils.dropTable(connectionSource, EzlyCategory.class, true);
            onCreate(database, connectionSource);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTable(Class dataClass){
        try {
            TableUtils.clearTable(getConnectionSource(), dataClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public Dao<EzlyCategory, Integer> getUserDao() throws SQLException {
        if (categoryDao == null) {
            categoryDao = getDao(EzlyCategory.class);
        }

        return categoryDao;
    }
}
