package afyabora.xeonite.com.myapplication.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.model.Polygon;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import afyabora.xeonite.com.myapplication.MainActivity;
import afyabora.xeonite.com.myapplication.R;
import afyabora.xeonite.com.myapplication.appconfig.AppConfig;
import afyabora.xeonite.com.myapplication.appconfig.QueryType;
import afyabora.xeonite.com.myapplication.com.afya.GPS.GPS_Service;
import afyabora.xeonite.com.myapplication.database.ModelExecutor;
import afyabora.xeonite.com.myapplication.web.AsyncQueryCallback;
import afyabora.xeonite.com.myapplication.web.HttpQueries;
import afyabora.xeonite.com.myapplication.web.Network;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by hornellama on 14/05/2018.
 */

public class HospitalsFragment extends Fragment {

    private MapView mapView = null;
    private IMapController mapController = null;
    private final static double LNG = 15.2663;
    private final static double LAT = -4.44193;
    private GeoPoint startPoint = null;
    private BroadcastReceiver broadcastReceiver;
    private Intent iGPS = null;
    private Marker markerPoint = null;
    private View viewBase = null;
    private Geocoder geocoder = null;
    private List<Address> addresses = null;
    private static boolean refreshQuery = true;
    private android.support.v7.widget.Toolbar toolbar = null;
    private AppCompatActivity activity = null;
    private boolean isTracker;
    private TextView textViewLoadViewMap = null;
    private ModelExecutor dbExecutor = null;
    private String dataFacility = null;
    private ProgressBar pb = null;
    private boolean isGPS = false;
    private Address address;
    LocationManager mLocationManager = null;
    LocationListener mLocationListener = null;

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        mLocationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                geocoder=new Geocoder(getActivity());
                try {
                    address=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0);
                    startPoint=new GeoPoint(location.getLatitude(),location.getLongitude());
                    DrawMap(startPoint);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "Lat :"+location.getLatitude()+"lng :"+location.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(getActivity(), "The Service is on", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0.0f, mLocationListener);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (broadcastReceiver != null) {
            if (iGPS != null) {
                getActivity().getApplicationContext().stopService(iGPS);
            }
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            if (iGPS != null) {
                getActivity().getApplicationContext().stopService(iGPS);
            }
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        }
    }

    public HospitalsFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

    }



    //@SuppressLint("MissingPermission")
    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBase = inflater.inflate(R.layout.layout_fragment_hospitalviewmap, container, false);
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        String message="";
        switch (Locale.getDefault().getLanguage()){
            case "fr":
               // message_title=ctx.getResources().getString(R.string.loading_viewmap_fr_title);
                message=getActivity().getResources().getString(R.string.loading_viewmap_fr_msg);
                break;

            case "pt":
                //message_title=ctx.getResources().getString(R.string.loading_viewmap_pt_title);
                message=getActivity().getResources().getString(R.string.loading_viewmap_pt_msg);
                break;

            case "es":
               // message_title=ctx.getResources().getString(R.string.loading_viewmap_es_title);
                message=getActivity().getResources().getString(R.string.loading_viewmap_es_msg);
                break;
            default:
               // message_title=ctx.getResources().getString(R.string.loading_viewmap_en_title);
                message=getActivity().getResources().getString(R.string.loading_viewmap_en_msg);
                break;
        }



            textViewLoadViewMap=(TextView)viewBase.findViewById(R.id.lbl_loading_map);
            textViewLoadViewMap.setText(message);
             runtime_permission();

        if (runtime_permission()==false){
                // i.setFlags(DriveFile.MODE_READ_ONLY);
               //
            Toast.makeText(getContext(), "EKOTI TEEE", Toast.LENGTH_SHORT).show();
             //   iGPS = new Intent(getActivity().getApplicationContext(),GPS_Service.class);
               //getActivity().getApplicationContext().startService(iGPS);
            //this.drawMap();
            }else{
                Toast.makeText(getActivity(), "View Kinshasa", Toast.LENGTH_SHORT).show();
                startPoint=new GeoPoint(-4.3685,15.3575);
            }

            //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0.0f, this);
            // SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.hospital_fragment);
            //  mapFragment.getMapAsync(this);
            // Context ctx=getActivity().getApplicationContext();
            // this.AddMarkers(viewMapHospital);






      //  setHasOptionsMenu(true);
        return viewBase;

    }
    public void DrawMap(GeoPoint geoPoint){
        if (mapView==null){
            mapView=viewBase.findViewById(R.id.mapViewer);
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(false);
            mapView.setMultiTouchControls(true);
            mapController = mapView.getController();
            mapController.setZoom(12);
            mapController.animateTo(geoPoint);
            markerPoint=new Marker(mapView);
            markerPoint.setPosition(geoPoint);
            markerPoint.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
            markerPoint.setIcon(getResources().getDrawable(R.drawable.iconposition));
            mapView.getOverlays().add(markerPoint);
            mapView.setVisibility(View.VISIBLE);
        }else{
            mapView.getOverlays().remove(markerPoint);
            markerPoint.setPosition(geoPoint);
            markerPoint.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
            markerPoint.setIcon(getResources().getDrawable(R.drawable.iconposition));
            mapView.getOverlays().add(markerPoint);
            mapController.animateTo(geoPoint);
        }

    }
    private void drawMap(){
       // LocationServices
        
        //Toast.makeText(getActivity(), "StartPoint: "+startPoint.getLatitude(), Toast.LENGTH_SHORT).show();
        GeoPoint startPointLocalDB=new GeoPoint(-4.3685,15.3575);
        mapView=viewBase.findViewById(R.id.mapViewer);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapController= mapView.getController();
        mapController.setCenter(startPointLocalDB);
        mapController.animateTo(startPointLocalDB);
        mapController.setZoom(13);
        mapView.setVisibility(View.VISIBLE);
        markerPoint=new Marker(mapView);
        markerPoint.setPosition(startPointLocalDB);
        markerPoint.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        markerPoint.setIcon(getResources().getDrawable(R.drawable.iconposition));
        mapView.getOverlays().add(markerPoint);
        AddMarkersWithOutGPS(viewBase,"Kinshasa",startPointLocalDB);
    }
    private void drawMap(GeoPoint geoPoint){
        //Toast.makeText(getActivity(), "StartPoint: "+startPoint.getLatitude(), Toast.LENGTH_SHORT).show();
        mapView=viewBase.findViewById(R.id.mapViewer);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapController= mapView.getController();
        mapController.setCenter(geoPoint);
        mapController.animateTo(geoPoint);
        mapController.setZoom(13);
        mapView.setVisibility(View.VISIBLE);
    }
    private void AddMarker(GeoPoint center){
        try{
            if (startPoint==null){
                drawMap(center);
                startPoint=center;
                mapController.animateTo(center);
                markerPoint=new Marker(mapView);
                markerPoint.setPosition(center);
                markerPoint.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
                markerPoint.setIcon(getResources().getDrawable(R.drawable.iconposition));
                mapView.getOverlays().add(markerPoint);
                mapView.setVisibility(View.VISIBLE);

                addresses=geocoder.getFromLocation(center.getLatitude(),center.getLongitude(),1);
                Address address=addresses.get(0);
                AddMarkers(viewBase,address.getLocality(),center);
                //Toast.makeText(getActivity().getApplicationContext(), "Town:"+address.getLocality(),Toast.LENGTH_SHORT).show();
            }else {

                    mapView.getOverlays().remove(markerPoint);
                    startPoint=center;
                    mapController.animateTo(center);
                    markerPoint=new Marker(mapView);
                    markerPoint.setPosition(center);
                    markerPoint.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
                    markerPoint.setIcon(getResources().getDrawable(R.drawable.iconposition));
                    mapView.getOverlays().add(markerPoint);
                    addresses=geocoder.getFromLocation(center.getLatitude(),center.getLongitude(),1);
                    Address address=addresses.get(0);
                   // Toast.makeText(getActivity().getApplicationContext(), "Town:"+address.getLocality(),Toast.LENGTH_SHORT).show();
                    AddMarkers(viewBase,address.getLocality(),center);
                   // refreshQuery=true;


            }
        }catch (Exception e){
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


      //  startPoint = new GeoPoint(lat,lng);

    }
    public void AddMarkers(final View view, final String localityName, final GeoPoint geoPoint){
       if (refreshQuery){
           try{
               if (Network.statusConnectivity(getActivity().getApplicationContext())){
                   new HttpQueries(HospitalsFragment.this.getContext(),"http://cide-rdc.org/public/dist/dataLocation.json", Locale.getDefault().getLanguage(),new AsyncQueryCallback() {
                       @Override
                       public void getResponse(Object obj) {

                           if (obj instanceof JSONArray){
                               try{
                                   JSONObject jObject=new JSONArray(obj.toString()).getJSONObject(0);
                                   JSONArray arrayTowns=new JSONArray(obj.toString());

                                   String town=jObject.getString("Town");
                                   double lat=LAT;
                                   double lng=LNG;
                                   if (geoPoint!=null){
                                       lat=geoPoint.getLatitude();
                                       lng=geoPoint.getLongitude();
                                       //Toast.makeText(getContext(), "Not null Geopoint", Toast.LENGTH_SHORT).show();
                                   }

                                   // Add a marker in Sydney and move the camera
                                   //GeoPoint geoPoint=new GeoPoint(lat,lng);
                                   if (mapView==null){
                                       mapView=view.findViewById(R.id.mapViewer);
                                       mapView.setTileSource(TileSourceFactory.MAPNIK);
                                       mapView.setBuiltInZoomControls(true);
                                       mapView.setMultiTouchControls(true);
                                       mapController= mapView.getController();
                                       mapController.setZoom(14);
                                       startPoint = new GeoPoint(lat,lng);
                                       mapController.animateTo(startPoint);
                                   }

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
                                       marker.setIcon(getResources().getDrawable(R.drawable.iconmedicalbag));
                                       marker.setTitle(json.getString("name"));
                                       mapView.getOverlays().add(marker);
                                       marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                           @Override
                                           public boolean onMarkerClick(Marker marker, MapView mapView) {
                                               if (!json.isNull("Hospitals")){
                                                   try{
                                                       JSONArray arrayHospitals=json.getJSONArray("Hospitals");
                                                       Toast.makeText(getContext(), "Title : "+arrayHospitals.length(), Toast.LENGTH_SHORT).show();

                                                   }catch (Exception e){
                                                       Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                   }

                                               }
                                               return true;
                                           }
                                       });

                                   }
                                   mapView.setVisibility(View.VISIBLE);
                                   //getActivity().findViewById(R.id.containerHospitalMap).setVisibility(View.VISIBLE);
                                   // Toast.makeText(HospitalsFragment.this.getContext(),"Response Style :"+Boolean.toString(b), Toast.LENGTH_SHORT).show();
                               }catch(Exception e){
                                   Toast.makeText(HospitalsFragment.this.getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

                               }

                           }else{
                               Toast.makeText(HospitalsFragment.this.getContext(), "Error:"+obj.toString(), Toast.LENGTH_SHORT).show();
                           }
                       }
                   }).execute();
               }else {
                   Toast.makeText(getActivity(), AppConfig.getMessageOutConnection(getActivity()), Toast.LENGTH_SHORT).show();

               }
           }catch(Exception e){

           }
           refreshQuery=false;
       }else {

       }
    }


    public void AddMarkersWithOutGPS(final View view, final String localityName, final GeoPoint geoPoint){

            try{
                if (Network.statusConnectivity(getActivity().getApplicationContext())){

                    new HttpQueries(HospitalsFragment.this.getContext(),"http://cide-rdc.org/public/dist/dataLocation.json", Locale.getDefault().getLanguage(),new AsyncQueryCallback() {
                        @Override
                        public void getResponse(Object obj) {

                            if (obj instanceof JSONArray){
                                try{
                                    JSONObject jObject=new JSONArray(obj.toString()).getJSONObject(0);
                                    JSONArray arrayTowns=new JSONArray(obj.toString());

                                    String town=jObject.getString("Town");
                                    double lat=LAT;
                                    double lng=LNG;
                                    if (geoPoint!=null){
                                        lat=geoPoint.getLatitude();
                                        lng=geoPoint.getLongitude();
                                        //Toast.makeText(getContext(), "Not null Geopoint", Toast.LENGTH_SHORT).show();
                                    }

                                    // Add a marker in Sydney and move the camera
                                    //GeoPoint geoPoint=new GeoPoint(lat,lng);

                                        mapView=view.findViewById(R.id.mapViewer);
                                        mapView.setTileSource(TileSourceFactory.MAPNIK);
                                        mapView.setBuiltInZoomControls(true);
                                        mapView.setMultiTouchControls(true);
                                        mapController= mapView.getController();
                                        mapController.setZoom(13);
                                        startPoint = new GeoPoint(lat,lng);
                                        mapController.animateTo(startPoint);


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
                                        marker.setIcon(getResources().getDrawable(R.drawable.iconmedicalbag));
                                        marker.setTitle(json.getString("name"));
                                        mapView.getOverlays().add(marker);
                                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                                if (!json.isNull("Hospitals")){
                                                    try{
                                                        JSONArray arrayHospitals=json.getJSONArray("Hospitals");
                                                        Toast.makeText(getContext(), "Title : "+arrayHospitals.length(), Toast.LENGTH_SHORT).show();

                                                    }catch (Exception e){
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                                return true;
                                            }
                                        });

                                    }
                                    mapView.setVisibility(View.VISIBLE);
                                    //getActivity().findViewById(R.id.containerHospitalMap).setVisibility(View.VISIBLE);
                                    // Toast.makeText(HospitalsFragment.this.getContext(),"Response Style :"+Boolean.toString(b), Toast.LENGTH_SHORT).show();
                                }catch(Exception e){
                                    Toast.makeText(HospitalsFragment.this.getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }else{
                                Toast.makeText(HospitalsFragment.this.getContext(), "Error:"+obj.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).execute();
                }else {
                    Toast.makeText(getActivity(), AppConfig.getMessageOutConnection(getActivity()), Toast.LENGTH_SHORT).show();

                }
            }catch(Exception e){

            }
            //refreshQuery=false;

    }



    public boolean runtime_permission(){
        if (Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission
                .ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
              isTracker=true;
            Toast.makeText(getActivity().getApplicationContext(), "Permission :"+isTracker, Toast.LENGTH_SHORT).show();

            return true;
        }
        isTracker=false;
        Toast.makeText(getActivity().getApplicationContext(), "Permission :"+isTracker, Toast.LENGTH_SHORT).show();

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
}
