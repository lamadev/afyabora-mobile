package afyabora.xeonite.com.myapplication;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

public class osmActivity extends AppCompatActivity {

    private MapView mapView;
    private MapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_osm);
        mapView=findViewById(R.id.osmView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
       // mapView.setBuiltInZoomControls(true);
       // mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(12);
        GeoPoint startPoint = new GeoPoint(-4.4049,15.4076);
        IGeoPoint geoPoint=startPoint;
        mapController.animateTo(startPoint);
    }
}
