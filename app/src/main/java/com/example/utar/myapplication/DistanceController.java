package com.example.utar.myapplication;

/**
 * Created by Kei Yeng on 21-Jul-16.
 */

import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kei Yeng on 05-Jul-16.
 */
public class DistanceController {

    private static final double A = 0.42093;
    private static final double B = 6.9476;
    private static final double C = 0.54992;
    private boolean closed;
    private static final short closed_t = -75;
    private static final short open_t = -30;
    private ArrayList<String> connectedDeviceList;
    private ArrayAdapter<String> connectedDeviceAdapter;
    ArrayList<Device> connectedDevices;
    // private  final  bluetoothDevices = null;

    public DistanceController(ArrayList<Device> connectedDevices) {
        this.connectedDevices = new ArrayList<Device>();
    }

    /*public void setConnectedDevices(ArrayList<Device> connectedServerDevices){
        this.connectedServerDevices = connectedServerDevices;
    }*/

    public ArrayList<Device> getConnectedDevices(){
        //sortDevices();
        return this.connectedDevices;
    }

    public void setClosed(boolean closed){
        this.closed = closed;
    }

    public ArrayList<Device> calculateDistance(){
        //sortDevices();
        //ArrayList<Double> distanceList = new ArrayList<Double>();
        Log.i("calculate distance","called");
        int i = 0;
        double distance;
        for(Device device : this.connectedDevices){
            if(closed)
                distance = A * Math.pow((device.getRSSI() / closed_t), B) + C;
            else
                distance = A * Math.pow((device.getRSSI() / open_t),B)+C;
            this.connectedDevices.get(i).setDistance(distance);
            i++;
        }
        return this.connectedDevices;
    }

    public void sortDevices (){
       // ArrayList<Device> sortedDevices = new ArrayList<Device>();
            Collections.sort(connectedDevices);
        Log.i("sort devices","called");
    }

}




