package com.gipl.bluetoothprinting;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Created by Ankit on 03-Sep-19.
 */
public class BTDevice implements Parcelable {
    private String deviceName;
    private String macAddress;

    public BTDevice(String deviceName, String macAddress) {
        this.deviceName = deviceName;
        this.macAddress = macAddress;
    }

    protected BTDevice(Parcel in) {
        deviceName = in.readString();
        macAddress = in.readString();
    }

    public static final Creator<BTDevice> CREATOR = new Creator<BTDevice>() {
        @Override
        public BTDevice createFromParcel(Parcel in) {
            return new BTDevice(in);
        }

        @Override
        public BTDevice[] newArray(int size) {
            return new BTDevice[size];
        }
    };

    public String getDeviceName() {
        return deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    @NonNull
    @Override
    public String toString() {
        return deviceName==null?macAddress:deviceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(deviceName);
        parcel.writeString(macAddress);
    }
}
