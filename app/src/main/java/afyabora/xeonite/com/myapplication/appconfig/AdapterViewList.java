package afyabora.xeonite.com.myapplication.appconfig;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import afyabora.xeonite.com.myapplication.R;

/**
 * Created by hornellama on 17/05/2018.
 */

public class AdapterViewList extends RecyclerView.Adapter<AdapterViewList.Holder>{

    private HashMap<String, Integer> mItems;

    public AdapterViewList(HashMap<String, Integer> items) {
        mItems = items;
    }

    public  class Holder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public Holder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index=getAdapterPosition();
                    Toast.makeText(view.getContext(), "Index :"+index, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_doctors_view, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String key = (String) mItems.keySet().toArray()[position];
        holder.textView.setText(key);
        holder.imageView.setImageResource(mItems.get(key));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
