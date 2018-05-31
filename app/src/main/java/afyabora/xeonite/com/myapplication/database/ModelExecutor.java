package afyabora.xeonite.com.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hornellama on 14/05/2018.
 */

public class ModelExecutor {
    private SQLiteDatabase database=null;
    private Context ctx=null;
    private PersistenceInit persistenceInit=null;

    public ModelExecutor(Context _ctx){
        this.ctx=_ctx;
        persistenceInit=new PersistenceInit(this.ctx,"afyabora/afyabora.db",null,5);
    }
    public boolean openDatabase(){
        database=persistenceInit.getWritableDatabase();
        return true;
    }

    public long insertGeocodebyCountry(String country,String Town,String data){
        ContentValues values=new ContentValues();

        return  0;
    }

    public boolean closeDataBase(){
        if(database.isOpen()) database.close();
        return true;
    }
}
