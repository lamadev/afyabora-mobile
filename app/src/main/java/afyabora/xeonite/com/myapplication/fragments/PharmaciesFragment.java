package afyabora.xeonite.com.myapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import afyabora.xeonite.com.myapplication.R;


/**
 * Created by hornellama on 14/05/2018.
 */

public class PharmaciesFragment extends Fragment {
    public PharmaciesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_pharmacy, container, false);
    }
}
