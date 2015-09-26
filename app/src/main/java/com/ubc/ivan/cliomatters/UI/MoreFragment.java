package com.ubc.ivan.cliomatters.UI;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubc.ivan.cliomatters.R;

/**
 * Created by ivan on 23/09/15.
 */
public class MoreFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matter_more, container, false);
    }
}
