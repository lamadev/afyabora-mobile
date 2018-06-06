package afyabora.xeonite.com.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hornellama on 14/05/2018.
 */

public class PersistenceInit extends SQLiteOpenHelper {

    private static  final String QueryHospitals="create table hospitals(id integer primary key autoincrement,codeISO text not null,countryName text not null, town text not null, content text not null);";
    private static final String  QueryDoctors="create table doctors(id integer primary key,content text not null) ;";
    private static String QueryPharmacies="create table pharmacies(id integer primary key,content text not null) ;";
    private static String QueryParam="create table params(id integer primary key,content text not null) ;";
    public PersistenceInit(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(QueryHospitals);
        sqLiteDatabase.execSQL(QueryDoctors);
        sqLiteDatabase.execSQL(QueryPharmacies);
        sqLiteDatabase.execSQL(QueryParam);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
