package afyabora.xeonite.com.myapplication.appconfig;

import android.content.Context;

import java.util.Locale;

import afyabora.xeonite.com.myapplication.R;

/**
 * Created by hornellama on 16/05/2018.
 */

public class AppConfig {

    private static String language;


    public static String getHintSearch(Context ctx){
        String currentHint="";
        switch (Locale.getDefault().getLanguage()){
            case "fr":
                currentHint= ctx.getResources().getString(R.string.hint_fr_search);
                break;

            case "es":
                currentHint= ctx.getResources().getString(R.string.hint_es_search);
                break;
            case "pt":
                currentHint= ctx.getResources().getString(R.string.hint_pt_search);
                break;
            default:
                currentHint= ctx.getResources().getString(R.string.hint_en_search);
                break;
        }
        return currentHint;
    }
    public static String getMessageOutConnection(Context ctx){
        String message="";
        switch (Locale.getDefault().getLanguage()){
            case "fr":
                message= ctx.getResources().getString(R.string.msg_not_cnx_fr);
                break;

            case "es":
                message= ctx.getResources().getString(R.string.msg_not_cnx_es);
                break;
            case "pt":
                message= ctx.getResources().getString(R.string.msg_not_cnx_pt);
                break;
            default:
                message= ctx.getResources().getString(R.string.msg_not_cnx_en);
                break;
        }
        return message;
    }

    public static String getMessageDialogGPS(Context ctx){
        String message="";
        switch (Locale.getDefault().getLanguage()){
            case "fr":
                message= ctx.getResources().getString(R.string.msg_GPS_fr);
                break;

            case "es":
                message= ctx.getResources().getString(R.string.msg_GPS_es);
                break;
            case "pt":
                message= ctx.getResources().getString(R.string.msg_GPS_pt);
                break;
            default:
                message= ctx.getResources().getString(R.string.msg_GPS_en);
                break;
        }
        return message;
    }

}
