package com.ubc.ivan.cliomatters.Model;

/**
 * Created by ivan on 21/09/15.
 */
public class Matter {
    int id;
    String displayName, clientName, description, openDate, status;

    public Matter(int id, String displayName, String clientName, String description,
                  String openDate, String status) {
        this.id = id;
        this.displayName = displayName;
        this.clientName = clientName;
        this.description = description;
        this.openDate = openDate;
        this.status = status;
    }
}
