package com.ubc.ivan.cliomatters.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubc.ivan.cliomatters.Model.Matter;
import com.ubc.ivan.cliomatters.R;

/**
 * Created by ivan on 23/09/15.
 */
public class InfoFragment extends Fragment {
    private static final String TAG = InfoFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matter_details, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        Matter matter = ((DetailsActivity) getActivity()).getMatter();

        try {
            TextView number = (TextView) getView().findViewById(R.id.txtNumber);
            TextView practiceArea = (TextView) getView().findViewById(R.id.txtPracticeArea);
            TextView clientName = (TextView) getView().findViewById(R.id.txtClientName);
            TextView date = (TextView) getView().findViewById(R.id.txtOpenDate);
            TextView status = (TextView) getView().findViewById(R.id.txtStatus);
            TextView billing = (TextView) getView().findViewById(R.id.txtBilling);

            number.setText(matter.getDisplayName() + "\n" + matter.getDescription());
            clientName.setText(matter.getClientName());

            if (matter.getOpenDate().equals("null")) {
                date.setText(R.string.lable_not_applicable);
            } else {
                date.setText(matter.getOpenDate());
            }

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
        } catch (NullPointerException e) {
            logException(e);
        }
    }

    private void logException(Exception e) {
        Log.e(TAG, "Exception caught! ", e);
    }

}
