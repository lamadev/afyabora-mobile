package afyabora.xeonite.com.myapplication.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import afyabora.xeonite.com.myapplication.MainActivity;
import afyabora.xeonite.com.myapplication.R;
import afyabora.xeonite.com.myapplication.appconfig.AdapterViewList;


/**
 * Created by hornellama on 14/05/2018.
 */

public class DoctorsFragment extends Fragment {

    private HashMap<String, Integer> mItems=null;
    private AdapterViewList mAdapter=null;
    private RecyclerView mRecyclerView=null;

    public DoctorsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }
    
        
    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_fragment_doctors, container, false);
       // android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.mytoolbar2);
        //((MainActivity)getActivity()).setSupportActionBar(toolbar);
        //setHasOptionsMenu(true);

        try{
            TextView lblSpeciality=view.findViewById(R.id.lbl_speciality);
            SwipeRefreshLayout swipeRefresh=(SwipeRefreshLayout)view.findViewById(R.id.loaderSwipe);
            swipeRefresh.setColorSchemeColors(Color.GREEN, Color.BLACK, Color.GREEN);
            
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Toast.makeText(getContext(), "In Refreshing....", Toast.LENGTH_SHORT).show();
                    mItems.clear();
                    mAdapter= new AdapterViewList(mItems);
                    mRecyclerView.setAdapter(mAdapter);
                }
            });
           // Typeface tf=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-LightItalic.ttf");
            //lblSpeciality.setTypeface(tf);
            TextView lblNameDoctor=view.findViewById(R.id.textView);
            //lblNameDoctor.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Light.ttf"));
            mRecyclerView = (RecyclerView)view.findViewById(R.id.doctorView);
            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mItems= new HashMap<>();

            for (int i=0;i<=80;i++){
                mItems.put("comedy"+i, R.drawable.iconscogs);
            }

            mAdapter= new AdapterViewList(mItems);
            mRecyclerView.setAdapter(mAdapter);
        }catch(Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;
    }
}
