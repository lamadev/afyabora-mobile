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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;

public class FirstActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MapView mapView;
    private IMapController mapController;
    private boolean isTracker=true;
    LocationManager mLocationManager = null;
    LocationListener mLocationListener = null;
    private Geocoder geocoder=null;
    private Address address;
    private GeoPoint startPoint=null;
    private Marker markerPoint=null;

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        mLocationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                geocoder=new Geocoder(FirstActivity.this);
                try {
                    address=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0);
                    startPoint=new GeoPoint(location.getLatitude(),location.getLongitude());
                    DrawMap(startPoint);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(FirstActivity.this, "Lat :"+location.getLatitude()+"lng :"+location.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(FirstActivity.this, "The Service is on", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0.0f, mLocationListener);
    }
    public void DrawMap(GeoPoint geoPoint){
        if (mapView==null){
            mapView=findViewById(R.id.MapViewHosto);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
       runtime_permission();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public boolean runtime_permission(){
        if (Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(FirstActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            isTracker=true;
            Toast.makeText(FirstActivity.this, "Permission :"+isTracker, Toast.LENGTH_SHORT).show();

            return true;
        }
        isTracker=false;
        Toast.makeText(FirstActivity.this, "Permission :"+isTracker, Toast.LENGTH_SHORT).show();

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
