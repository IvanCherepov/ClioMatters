package com.ubc.ivan.cliomatters.Controller;

import android.content.Context;

import com.ubc.ivan.cliomatters.MattersApiConstants;
import com.ubc.ivan.cliomatters.NetworkHandler;

import org.json.JSONObject;

/**
 * Created by ivan on 21/09/15.
 */
public class MattersController {

    protected Context mContext;

    public MattersController(Context context) {
        mContext = context;
    }

    public JSONObject getMatters() {
        JSONObject matters;

        NetworkHandler networkHandler = new NetworkHandler(mContext);
        matters = networkHandler.getJason(MattersApiConstants.CLIO_URL);

        return matters;
    }
}
