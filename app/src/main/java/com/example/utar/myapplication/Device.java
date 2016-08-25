package com.example.utar.myapplication;

/**
 * Created by Kei Yeng on 21-Jul-16.
*/


    /**
     * Created by Yumiko on 7/1/2016.
     */
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.Comparator;
import java.lang.Short;

public class Device implements Comparable<Device>, Parcelable, Serializable{
        private boolean connected;
        private String deviceName;
        private String deviceAddress;
        private short RSSI;
        private double distance;


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceName);
        dest.writeString(deviceAddress);
        dest.writeInt((int) RSSI);
        dest.writeDouble(distance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    Device(Parcel in) {
        this.deviceName = in.readString();
        this.deviceAddress = in.readString();
        this.RSSI = (short)in.readInt();
        this.distance = in.readDouble();
    }

    public static final Parcelable.Creator<Device> CREATOR
            = new Parcelable.Creator<Device>() {

        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
        public Device(String deviceName, String deviceAddress) {

            this.deviceName = deviceName;
            this.deviceAddress = deviceAddress;
            this.RSSI = 0;
            this.distance = 0;
        }

        public boolean getConnected() {
            return connected;
        }

        public void setDistance (double distance){this.distance = distance;}

        public double getDistance(){return distance;}

        public String getDeviceName() {
            return deviceName;
        }

        public short getRSSI() {
            return RSSI;
        }

        public String getDeviceAddress() {
            return deviceAddress;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public void setDeviceAddress(String deviceAddress) {
            this.deviceAddress = deviceAddress;
        }

        public void setRSSI(short deviceRSSI) {
            this.RSSI = deviceRSSI;
        }

        @Override
        public int compareTo(@NonNull Device compareDevice) {

           // return new Short(d1.getRSSI()).compareTo(d2.getRSSI());
            short compareRSSI = compareDevice.getRSSI();
            if(this.RSSI > compareRSSI)
                return -1;
            else if (this.RSSI < compareRSSI)
                return 1;
            else
                return 0;
            //int compareRssi = (int)(((Device)compareDevice).getRSSI());

            //return (int)this.getRSSI() - compareRssi;
            //return compareage-this.studentage;
        }

        @Override
        public String toString() {
            return "[ device = " + deviceAddress + ", RSSI = " + getRSSI();
        }



    }


