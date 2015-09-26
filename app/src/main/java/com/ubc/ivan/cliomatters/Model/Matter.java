package com.ubc.ivan.cliomatters.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ivan on 21/09/15.
 */
public class Matter implements Parcelable {
    public static final Creator<Matter> CREATOR = new Creator<Matter>() {
        @Override
        public Matter createFromParcel(Parcel in) {
            return new Matter(in);
        }

        @Override
        public Matter[] newArray(int size) {
            return new Matter[size];
        }
    };
    private int id;
    private Client client;
    private String displayName, clientName, description, openDate, status, practiceArea;
    private Boolean billable;

    public Matter(int id, String displayName,
                  String clientName, String description,
                  String openDate, String status,
                  Boolean billable, String practiceArea) {
        this.id = id;
        //this.client = client;
        this.displayName = displayName;
        this.clientName = clientName;
        this.description = description;
        this.openDate = openDate;
        this.status = status;
        this.billable = billable;
        this.practiceArea = practiceArea;
    }

    public Matter(Parcel in) {
        id = in.readInt();
        displayName = in.readString();
        clientName = in.readString();
        description = in.readString();
        openDate = in.readString();
        status = in.readString();
        practiceArea = in.readString();
        billable = in.readByte() != 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPracticeArea() {
        return practiceArea;
    }

    public void setPracticeArea(String practiceArea) {
        this.practiceArea = practiceArea;
    }

    public Boolean getBillable() {
        return billable;
    }

    public void setBillable(Boolean billable) {
        this.billable = billable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(displayName);
        dest.writeString(clientName);
        dest.writeString(description);
        dest.writeString(openDate);
        dest.writeString(status);
        dest.writeString(practiceArea);
        dest.writeByte((byte) (billable ? 1 : 0));
    }
}
