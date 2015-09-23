package com.ubc.ivan.cliomatters.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ubc.ivan.cliomatters.Model.Matter;
import com.ubc.ivan.cliomatters.R;

import java.util.ArrayList;

/**
 * Created by ivan on 22/09/15
 */
public class MainListAdapter extends ArrayAdapter<Matter> {

    private Context mContext;
    private ArrayList<Matter> mMatters;

    public MainListAdapter(Context context, int resource, ArrayList<Matter> matters) {
        super(context, resource, matters);

        this.mContext = context;
        this.mMatters = matters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_list_item, null);
        }

        Matter matter = getItem(position);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);

        title.setText(matter.getDisplayName());
        subtitle.setText(matter.getDescription());

        return convertView;
    }

    @Override
    public int getCount() {
        return mMatters.size();
    }

    @Override
    public Matter getItem(int position) {
        return mMatters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
