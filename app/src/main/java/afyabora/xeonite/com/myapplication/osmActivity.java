package afyabora.xeonite.com.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Polygon;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.Locale;

import afyabora.xeonite.com.myapplication.web.AsyncQueryCallback;
import afyabora.xeonite.com.myapplication.web.HttpQueries;
import afyabora.xeonite.com.myapplication.web.Network;

public class osmActivity extends AppCompatActivity {

    private MapView mapView;
    private IMapController mapController;
    private boolean isTracker=true;
    LocationManager mLocationManager = null;
    LocationListener mLocationListener = null;
    private Geocoder geocoder=null;
    private Address address;
    private GeoPoint startPoint=null;
    private Marker markerPoint=null;
    private TextView textViewLoadViewMap=null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        mLocationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    geocoder=new Geocoder(osmActivity.this);
                    address=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0);
                    startPoint=new GeoPoint(location.getLatitude(),location.getLongitude());
                    DrawMap(startPoint);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(osmActivity.this, "Lat :"+location.getLatitude()+"lng :"+location.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(osmActivity.this, "The Service is on", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

       try{
           runtime_permission();
           mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

           mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0.0f, mLocationListener);
       }catch (Exception e){
           Toast.makeText(getApplicationContext(), "MESSERROR :"+e.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }
        public void DrawMap(GeoPoint geoPoint){
            if (mapView==null){
                mapView=findViewById(R.id.osmView);
                mapView.setTileSource(TileSourceFactory.MAPNIK);
                mapView.setBuiltInZoomControls(false);
                mapView.setMultiTouchControls(true);
                mapController = mapView.getController();
                mapController.setZoom(14);
                mapController.animateTo(geoPoint);
                markerPoint=new Marker(mapView);
                markerPoint.setPosition(geoPoint);
                markerPoint.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
                markerPoint.setIcon(getResources().getDrawable(R.drawable.iconposition));
                mapView.getOverlays().add(markerPoint);
                mapView.setVisibility(View.VISIBLE);
                if (Network.statusConnectivity(getApplicationContext())){
                    Toast.makeText(this, "Vous etes connectee | "+Locale.getDefault().getLanguage(), Toast.LENGTH_SHORT).show();
                    getListFacilies(Network.UrlDataLocation,Locale.getDefault().getLanguage());
                }
            }else{
                mapView.getOverlays().remove(markerPoint);
                markerPoint.setPosition(geoPoint);
                markerPoint.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
                markerPoint.setIcon(getResources().getDrawable(R.drawable.iconposition));
                mapView.getOverlays().add(markerPoint);
                mapController.animateTo(geoPoint);
            }

        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_osm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String message="";
        switch (Locale.getDefault().getLanguage()){
            case "fr":
                // message_title=ctx.getResources().getString(R.string.loading_viewmap_fr_title);
                message=getResources().getString(R.string.loading_viewmap_fr_msg);
                break;

            case "pt":
                //message_title=ctx.getResources().getString(R.string.loading_viewmap_pt_title);
                message=getResources().getString(R.string.loading_viewmap_pt_msg);
                break;

            case "es":
                // message_title=ctx.getResources().getString(R.string.loading_viewmap_es_title);
                message=getResources().getString(R.string.loading_viewmap_es_msg);
                break;
            default:
                // message_title=ctx.getResources().getString(R.string.loading_viewmap_en_title);
                message=getResources().getString(R.string.loading_viewmap_en_msg);
                break;
        }



        textViewLoadViewMap=(TextView)findViewById(R.id.lbl_loading_map_hosto);
        textViewLoadViewMap.setText(message);

    }
    public boolean runtime_permission(){
        if (Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(osmActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(osmActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            isTracker=true;
            Toast.makeText(osmActivity.this, "Permission :"+isTracker, Toast.LENGTH_SHORT).show();

            return true;
        }
        isTracker=false;
        Toast.makeText(osmActivity.this, "Permission :"+isTracker, Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){

            }
        }
    }
    private void getListFacilies(String url,String lang){
        new HttpQueries(osmActivity.this, url, lang, new AsyncQueryCallback() {
            @Override
            public void getResponse(Object obj) {
                try{
                    JSONObject jObject=new JSONArray(obj.toString()).getJSONObject(0);
                    JSONArray arrayTowns=new JSONArray(obj.toString());

                    String town=jObject.getString("Town");
                    JSONArray coordonates=jObject.getJSONArray("Coordonates");
                    Polygon polygon = null;

                    JSONArray arrayAreas=jObject.getJSONArray("AreaDPS");
                    for (int i=0;i<arrayAreas.length();i++){
                        final JSONObject json=arrayAreas.getJSONObject(i);
                        GeoPoint latlng=new GeoPoint(
                                json.getJSONObject("Coordonates").getDouble("lat"),
                                json.getJSONObject("Coordonates").getDouble("lng"));
                        Marker marker=new Marker(mapView);
                        marker.setPosition(latlng);
                        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
                        marker.setIcon(getResources().getDrawable(R.drawable.iconsfindhospital));

                        marker.setTitle(json.getString("name"));
                        mapView.getOverlays().add(marker);
                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                if (!json.isNull("Hospitals")){
                                    try{
                                        JSONArray arrayHospitals=json.getJSONArray("Hospitals");
                                        Toast.makeText(osmActivity.this, "Title : "+arrayHospitals.length(), Toast.LENGTH_SHORT).show();

                                    }catch (Exception e){
                                        Toast.makeText(osmActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                return true;
                            }
                        });

                    }
                }catch(Exception e){

                }
                Toast.makeText(osmActivity.this, "RESPONSE :"+obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

}
