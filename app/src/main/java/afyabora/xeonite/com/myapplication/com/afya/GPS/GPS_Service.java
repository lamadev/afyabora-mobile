package afyabora.xeonite.com.myapplication.com.afya.GPS;

/**
 * Created by hornellama on 16/12/2017.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

//import com.google.android.gms.drive.DriveFile;
//import com.google.firebase.analytics.FirebaseAnalytics.Param;

public class GPS_Service extends Service {
    private LocationListener locationListener;
    private LocationManager locationManager;


    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    public void onCreate() {
        Toast.makeText(this, "Launch Service Geopoint", Toast.LENGTH_SHORT).show();
        this.locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //Toast.makeText(GPS_Service.this, "Coordinates :"+location.getLatitude() + ";" + location.getLongitude(), Toast.LENGTH_SHORT).show();

                Intent i = new Intent("location_update");
                i.putExtra("coordinates", location.getLatitude() + ";" + location.getLongitude());
                GPS_Service.this.sendBroadcast(i);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(GPS_Service.this, "provider:" + provider, Toast.LENGTH_LONG).show();
            }

            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                GPS_Service.this.startActivity(i);
            }
        };
        this.locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0.0f, this.locationListener);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.locationManager != null) {
            this.locationManager.removeUpdates(this.locationListener);
        }
    }
}
