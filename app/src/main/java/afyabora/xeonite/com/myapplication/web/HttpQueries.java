package afyabora.xeonite.com.myapplication.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import afyabora.xeonite.com.myapplication.R;
import afyabora.xeonite.com.myapplication.appconfig.QueryType;

/**
 * Created by hornellama on 14/05/2018.
 */

public class HttpQueries extends AsyncTask<Void, Void, Void> {
    private AsyncQueryCallback queryCallback;
    private URL url;
    private HttpURLConnection httpCnx;
    private URLConnection urlConnection=null;
    private String urlString="";
    private String Language;
    private ProgressDialog pDialog;
    private Context context=null;
    private String message="";
    private String message_title;
    private String response="";
    public HttpQueries(Context ctx, String url, String language, AsyncQueryCallback aqc){
        this.queryCallback=aqc;
        this.Language=language;
        this.urlString=url;
        this.context=ctx;

            switch (language){
                case "fr":
                    message_title=ctx.getResources().getString(R.string.loading_viewmap_fr_title);
                    message=ctx.getResources().getString(R.string.loading_viewmap_fr_msg);
                    break;

                case "pt":
                    message_title=ctx.getResources().getString(R.string.loading_viewmap_pt_title);
                    message=ctx.getResources().getString(R.string.loading_viewmap_pt_msg);
                    break;

                case "es":
                    message_title=ctx.getResources().getString(R.string.loading_viewmap_es_title);
                    message=ctx.getResources().getString(R.string.loading_viewmap_es_msg);
                    break;
                default:
                    message_title=ctx.getResources().getString(R.string.loading_viewmap_en_title);
                    message=ctx.getResources().getString(R.string.loading_viewmap_en_msg);
                    break;
            }

    }

    @Override
    protected void onPreExecute(){
        pDialog=new ProgressDialog(this.context, R.style.MyAlertDialogStyle);
        pDialog.setTitle(message_title);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String line="";
            url=new URL(this.urlString);
            urlConnection=url.openConnection();
            httpCnx=(HttpURLConnection)urlConnection;
            httpCnx.setRequestMethod("GET");
            httpCnx.setDoInput(true);
            //httpCnx.connect();
            InputStream is=httpCnx.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
            String input="";
            while((input=reader.readLine())!=null){
                response+=input;
            }
            httpCnx.disconnect();

        } catch (MalformedURLException e) {
            Toast.makeText(context,"Http ULR:"+e.getMessage(),Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void v){
        if (pDialog.isShowing())
            pDialog.dismiss();
        try {
            JSONArray jsonArray=new JSONArray(response);
            queryCallback.getResponse(jsonArray);
        } catch (JSONException e) {
            queryCallback.getResponse(e.getMessage());

        }

    }
}
