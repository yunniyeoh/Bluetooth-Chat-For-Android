package com.example.utar.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Yumiko on 7/20/2016.
 */
public class DistanceActivity extends AppCompatActivity {

    Button btnGetDistance;
    TextView deviceDetails;
    ArrayList<Device> connectedServerDevices;
    ArrayList<Device> connectedClientDevices;
    ArrayList<Device> connectedDevices;
    ArrayList<Device> foundDevices;
    BluetoothAdapter mBluetoothAdapter;
    Boolean registerBroadcastReceiver = true;
    RecyclerView mRecyclerView;
    CustomRecyclerViewAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Device> calconnectedDevices;
    int countForReceiver = 0;
    boolean closed;
    private static final double A = 0.42093;
    private static final double B = 5.9476;
    private static final double C = 0.54992;
    private static final short closed_t = -65;
    private static final short open_t = -40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);


        // Initialize back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
        connectedServerDevices = new ArrayList<Device>();
        connectedClientDevices = new ArrayList<Device>();
        connectedDevices = new ArrayList<Device>();
        foundDevices = new ArrayList<Device>();
        Intent intent = getIntent();



        if (intent != null) {
            connectedServerDevices = intent.getParcelableArrayListExtra(Constants.CONNECTED_SERVER);
            connectedDevices.addAll(connectedServerDevices);
            connectedClientDevices = intent.getParcelableArrayListExtra(Constants.CONNECTED_CLIENT);
            connectedDevices.addAll(connectedClientDevices);
        }

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_distance_recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // surrounding environment is initially set open
        closed = false;

        registerReceiver(getConnectedClientDevices, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleEnvironment);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    closed = true;// The toggle is enabled
                } else {
                    closed = false; // The toggle is disabled
                }
                calconnectedDevices = calculateDistance(connectedDevices);
                Collections.sort(calconnectedDevices);
                mAdapter.notifyDataSetChanged();
            }
        });

        calconnectedDevices = calculateDistance(connectedDevices);

        mAdapter = new CustomRecyclerViewAdapter(calconnectedDevices);
        mAdapter.setOnItemClickListener(itemClickedListener);
        mRecyclerView.setAdapter(mAdapter);

        //mRecyclerView.setOnItemClickListener();
    }

    private CustomRecyclerViewAdapter.MyClickListener itemClickedListener = new CustomRecyclerViewAdapter.MyClickListener() {
        @Override
        public void onItemClick(int position, View v) {
            deviceDetails = (TextView) findViewById(R.id.deviceDetails);
            deviceDetails.setText("Device name: " + connectedDevices.get(position).getDeviceName() +
                    "\nRSSI: " + connectedDevices.get(position).getRSSI() +
                    "\nEstimated Distance: " + connectedDevices.get(position).getDistance());
        }

    };



    public ArrayList<Device> calculateDistance(ArrayList<Device> connectedDevices) {
        //sortDevices();
        //ArrayList<Double> distanceList = new ArrayList<Double>();
        Log.i("calculate distance", "called");
        int i = 0;
        short rssi;
        double distance;
        for (Device device : connectedDevices) {
            rssi = device.getRSSI();
            if(rssi >= (short)-20 || rssi < (short) - 120) {
                connectedDevices.get(i).setDistance(0);
                connectedDevices.get(i).setRSSI((short)0);
            }
            else{
                if (closed)
                    distance = A * Math.pow(((double) rssi / (double) closed_t), B) + C;
                else
                    distance = A * Math.pow(((double) rssi / (double) open_t), B) + C;
                if (distance > 50)
                    distance = 50;
                Toast.makeText(getBaseContext(), Double.toString(distance), Toast.LENGTH_SHORT).show();
                connectedDevices.get(i).setDistance(distance);
            }
            i++;
        }
        return connectedDevices;
    }

    private final BroadcastReceiver getConnectedClientDevices = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            short rssi;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                for (Device connectedClientDevice : connectedClientDevices) {
                    if (device.getAddress().equals(connectedClientDevice.getDeviceAddress()))
                    {
                        int i = 0;
                        if(connectedDevices.isEmpty()){
                            Device device1 = new Device(device.getName(), device.getAddress());
                            device1.setRSSI(rssi);
                            connectedDevices.add(device1);
                            Toast.makeText(getBaseContext(), device1.getDeviceName() + device1.getRSSI(), Toast.LENGTH_SHORT).show();
                        }else{
                            for(Device connectedDevice:connectedDevices){
                                if(device.getAddress().equals(connectedDevice.getDeviceAddress()))
                                    connectedDevices.get(i).setRSSI(rssi);
                                else{
                                    Device device1 = new Device(device.getName(), device.getAddress());
                                    device1.setRSSI(rssi);
                                    connectedDevices.add(device1);
                                    Toast.makeText(getBaseContext(), device1.getDeviceName() + device1.getRSSI(), Toast.LENGTH_SHORT).show();
                                }
                                i++;
                            }
                        }
                    }
                }
                calconnectedDevices = calculateDistance(connectedDevices);
                Collections.sort(calconnectedDevices);
                mAdapter.notifyDataSetChanged();
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu without menu items
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Return to main activity
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStop() {
        super.onStop();
        unregisterReceiver(getConnectedClientDevices);
    }
}