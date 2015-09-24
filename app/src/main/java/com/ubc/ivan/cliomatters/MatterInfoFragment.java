package com.ubc.ivan.cliomatters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubc.ivan.cliomatters.Model.Matter;

/**
 * Created by ivan on 23/09/15.
 */
public class MatterInfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matter_details, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Matter matter = ((MatterDetailsActivity) getActivity()).getMatter();

        String mID = matter.getDescription();

        TextView number = (TextView) getView().findViewById(R.id.txtNumber);
        TextView practiceArea = (TextView) getView().findViewById(R.id.txtPracticeArea);
        TextView clientName = (TextView) getView().findViewById(R.id.txtClientName);
        TextView date = (TextView) getView().findViewById(R.id.txtOpendate);
        TextView status = (TextView) getView().findViewById(R.id.txtStatus);
        TextView billing = (TextView) getView().findViewById(R.id.txtBilling);

        number.setText(matter.getDisplayName() + "\n" + matter.getDescription());
        clientName.setText(matter.getClientName());
        date.setText(matter.getOpenDate());
        status.setText(matter.getStatus());

        if (matter.getBillable()) {
            billing.setText("is billable");
        } else {
            billing.setText("is not billable");
        }

        if (matter.getPracticeArea().equals("null")) {
            practiceArea.setText("General Practice");
        } else {
            practiceArea.setText(matter.getPracticeArea());
        }

    }
}
