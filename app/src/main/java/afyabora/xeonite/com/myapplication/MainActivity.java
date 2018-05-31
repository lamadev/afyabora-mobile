package afyabora.xeonite.com.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.engineio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import afyabora.xeonite.com.myapplication.appconfig.AdapterViewList;
import afyabora.xeonite.com.myapplication.appconfig.AppConfig;
import afyabora.xeonite.com.myapplication.fragments.DoctorsFragment;
import afyabora.xeonite.com.myapplication.fragments.HospitalsFragment;
import afyabora.xeonite.com.myapplication.fragments.PharmaciesFragment;
import afyabora.xeonite.com.myapplication.web.AsyncQueryCallback;
import afyabora.xeonite.com.myapplication.web.HttpQueries;

public class MainActivity extends AppCompatActivity{

    private static final int DIALOG_ALERT = 10;
    public static Socket socket;
    String IPaddress = "";
    AlertDialog alertDialog = null;
    BroadcastReceiver broadCastReceiver = null;
    Button btnDoc = null;
    Button btnUser = null;
    AlertDialog.Builder builderAlert = null;
    String message = "";
    Intent pageView = null;
    RadioGroup radioGroup = null;
    Switch sMedecin;
    Switch sPatient;
    Switch sPharmacie;
    private Spinner spChoice = null;
    private String language="";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        String lang=Locale.getDefault().getLanguage();
        switch (lang){
            case "en":
                adapter.addFragment(new HospitalsFragment(), "Hospitals");
                adapter.addFragment(new DoctorsFragment(),"Doctors");
                adapter.addFragment(new PharmaciesFragment(), "pharmacies");
                break;
            case "fr":
                adapter.addFragment(new HospitalsFragment(), "Hôpitaux");
                adapter.addFragment(new DoctorsFragment(),"Medecins");
                adapter.addFragment(new PharmaciesFragment(), "Pharmacies");
                break;
            case "es":
                adapter.addFragment(new HospitalsFragment(), "hospitales");
                adapter.addFragment(new DoctorsFragment(),"médicos");
                adapter.addFragment(new PharmaciesFragment(), "farmacias");
                break;
            case "pt":
                adapter.addFragment(new HospitalsFragment(), "hospitais");
                adapter.addFragment(new DoctorsFragment(),"médicos");
                adapter.addFragment(new PharmaciesFragment(), "farmácias");
                break;
            default:
                adapter.addFragment(new HospitalsFragment(), "Hospitals");
                adapter.addFragment(new DoctorsFragment(),"Doctors");
                adapter.addFragment(new PharmaciesFragment(), "pharmacies");
                break;
        }

        viewPager.setAdapter(adapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        //setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.iconscogs);

       String[]rules=null;
        language=Locale.getDefault().getLanguage();
        Toast.makeText(getApplicationContext(), language, Toast.LENGTH_SHORT).show();
        switch (language){
            case "fr":
                rules=getResources().getStringArray(R.array.category_fr);
                break;
            case "en":
                rules=getResources().getStringArray(R.array.category_en);

                break;

            default:
                rules=getResources().getStringArray(R.array.category_fr);
                break;
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.mytabs);
        tabLayout.setupWithViewPager(viewPager);
       // TextView header=(TextView)findViewById(R.id.title_header);
        //TextView appname=(TextView)findViewById(R.id.lbl_appname);
        Typeface tf=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
      //  header.setTypeface(tf);
       // appname.setTypeface(tf);
        Button btn_saved=(Button)findViewById(R.id.btn_saved);
        btn_saved.setTypeface(tf);

        Spinner sp=(Spinner)findViewById(R.id.rules);


        ArrayAdapter<String>adp=new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,rules);
        sp.setAdapter(adp);
        //Toast.makeText(this, "Lang is: "+language, Toast.LENGTH_SHORT).show();
        btn_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iMaps=new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(iMaps);
            }
        });
        AutoCompleteTextView searchTextView=(AutoCompleteTextView)findViewById(R.id.autocomplete_streets);
        String[] countries = getResources().getStringArray(R.array.streets_kinshasa);
        searchTextView.setHint(AppConfig.getHintSearch(getApplicationContext()));
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        searchTextView.setAdapter(adapter);


      //this.getListDoctors();
        LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
        View view=inflater.inflate(R.layout.layout_fragment_doctors,null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    private class BtnActiveGPS implements View.OnClickListener{

        @Override
        public void onClick(View view) {

        }
    }
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 10:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Settings");
                builder.setMessage("Activer votre poistion pour etre visible");
                builder.setCancelable(true);
                builder.setPositiveButton("Activer", (DialogInterface.OnClickListener) new BtnActiveGPS());
                builder.setNegativeButton("Annuler", (DialogInterface.OnClickListener) new BtnActiveGPS());
                builder.create().show();
                break;
        }
        return super.onCreateDialog(id);
    }
    public void openDialog(View v) {
        Intent i=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);

    }

    public void getListDoctors(){


    }

}
