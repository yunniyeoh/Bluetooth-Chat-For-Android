package com.example.utar.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class ConnectedActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionController mConnController;

    ArrayList<String> connectedList = BluetoothConnectionController.connectedList;
    ArrayAdapter<String> connectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        TextView nodeviceTV = (TextView) findViewById(R.id.no_connected_tv);
        ListView connectedLV = (ListView) findViewById(R.id.connected_devices);

        connectedAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, connectedList);
        connectedLV.setAdapter(connectedAdapter);


        /*
        * When the connect list contains item
        * Text View is not visible
        */
        if(connectedList.size() > 0) {
            nodeviceTV.setVisibility(View.GONE);
        }

        /*
         * Reset Bluetooth Connection when invoke
         */
        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                mConnController.resetConnection();

            }
        });

        registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));

        registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

                //Device is connected


            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                //Device is now disconnected

            }
        }
    };
}
