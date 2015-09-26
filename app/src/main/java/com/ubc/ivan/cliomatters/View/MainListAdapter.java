package com.ubc.ivan.cliomatters.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubc.ivan.cliomatters.Model.Matter;
import com.ubc.ivan.cliomatters.R;

import java.util.ArrayList;

/**
 * Created by ivan on 22/09/15
 */
public class MainListAdapter extends ArrayAdapter<Matter> {
    public static final String TAG = MainListAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<Matter> mMatters;

    public MainListAdapter(Context context, int resource, ArrayList<Matter> matters) {
        super(context, resource, matters);

        this.mContext = context;
        this.mMatters = matters;
    }

    private void logException(Exception e) {
        Log.e(TAG, "Excepption caught! ", e);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_list_item, null);
        }

        Matter matter = getItem(position);

        try {
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            ImageView imageButton = (ImageView) convertView.findViewById(R.id.moreIcon);

        title.setText(matter.getDisplayName());
        subtitle.setText(matter.getDescription());
            if (matter.getStatus().equals("Open")) {
                imageButton.setImageResource(R.drawable.ic_button_warning);
            } else {
                imageButton.setImageResource(R.drawable.ic_action_more);
            }
        } catch (Exception e) {
            logException(e);
        }

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
