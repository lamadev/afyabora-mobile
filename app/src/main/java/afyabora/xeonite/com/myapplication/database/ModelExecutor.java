package afyabora.xeonite.com.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import afyabora.xeonite.com.myapplication.pojos.CountryData;

/**
 * Created by hornellama on 14/05/2018.
 */

public class ModelExecutor {
    private SQLiteDatabase database=null;
    private Context ctx=null;
    private PersistenceInit persistenceInit=null;

    public ModelExecutor(Context _ctx){
        this.ctx=_ctx;
        persistenceInit=new PersistenceInit(this.ctx,"afyabora.db",null,5);
    }
    public boolean openDatabase(){
        database=persistenceInit.getWritableDatabase();
        return true;
    }

    public long insertDataCountryByTown(String codeISO,String country,String Town,String data){
        ContentValues values=new ContentValues();
        values.put("codeISO",codeISO);
        values.put("countryName",country);
        values.put("town",Town);
        values.put("content",data);
        long numSave=database.insert("hospitals",null,values);

        return numSave;
    }

    public CountryData getCountryDataByTown(String codeISO,String Town){
        CountryData countryData=null;
        try {
           String query="SELECT * FROM hospitals WHERE codeISO=? AND town=?";
           Cursor cursor=database.rawQuery(query,new String[]{codeISO,Town});
           /*while (cursor.moveToNext()){
               countryData=new CountryData();
               countryData.setId(cursor.getInt(0));
               countryData.setCodeISO(cursor.getString(1));
               countryData.setCountryName(cursor.getString(2));
               countryData.setTown(cursor.getString(3));
               countryData.setContent(cursor.getString(4));
           }
           cursor.close();
           */
       }catch (Exception e){
           Toast.makeText(ctx, "Exception :"+e.getMessage(), Toast.LENGTH_SHORT).show();
       }
        return countryData;

    }

    public boolean closeDataBase(){
        if(database.isOpen()) database.close();
        return true;
    }
}
