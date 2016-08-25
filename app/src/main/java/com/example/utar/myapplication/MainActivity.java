package com.example.utar.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    BluetoothConnectionController mConnController = null;

    ArrayList<Device> connectedClientDevices;
    Device connectedServerDevice;
    ArrayList<Device> connectedServerDevices;

    ArrayAdapter<String> chatAdapter;
    ArrayList<String> chatList;

    ListView chatDevices;

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            BluetoothDevice device;
            switch (msg.what) {
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    device = msg.getData().getParcelable(Constants.CONNECTED_CLIENT);
                    if(device!= null)
                        connectedClientDevices.add(new Device(device.getName(),device.getAddress()));
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedServerDevices = new ArrayList<Device>();
        connectedClientDevices = new ArrayList<Device>();
        mConnController = new BluetoothConnectionController(getApplicationContext(), mHandler);

        chatDevices = (ListView) findViewById(R.id.chatlist);
        chatDevices.setOnItemClickListener(new onChatListItemClickListener());

        showChatList();


        //ArrayList<String> connectedList = new ArrayList<String>();
        // mConnController.setArrayList(connectedList);
        //connectedList = mConnController.getArrayList();

        mConnController.starts();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // Adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_distance:
                Intent intent = new Intent(this, DistanceActivity.class);
                intent.putParcelableArrayListExtra(Constants.CONNECTED_SERVER, connectedServerDevices);
                intent.putParcelableArrayListExtra(Constants.CONNECTED_CLIENT,connectedClientDevices);
                startActivity(intent);
                return true;
            case R.id.action_devices:
                Intent i = new Intent(this, DeviceListActivity.class);
                startActivityForResult(i, 1);
                return true;
            case R.id.action_connected:
                Intent connectedIntent = new Intent(this, ConnectedActivity.class);
                //connectedIntent.putParcelableArrayListExtra(Constants.CONNECTED_SERVER, connectedServerDevices);
                //connectedIntent.putParcelableArrayListExtra(Constants.CONNECTED_CLIENT,connectedClientDevices);
                startActivity(connectedIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                connectedServerDevice = (Device)data.getSerializableExtra(Constants.CONNECTED_SERVER);
                if (connectedServerDevice != null){
                    Toast.makeText(getBaseContext(),"Result returned",Toast.LENGTH_SHORT).show();
                    connectedServerDevices.add(connectedServerDevice);
                    Toast.makeText(getBaseContext(),connectedServerDevice.getDeviceName() + "-> " + connectedServerDevice.getRSSI() + " added into connected devices list",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showChatList() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        chatList = new ArrayList<String>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                if(device.getBondState() == 12) {

                    chatList.add(device.getName() + "\n" + device.getAddress());
                    chatAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, chatList){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent){
                            // Get the Item from ListView
                            View view = super.getView(position, convertView, parent);
                            return view;
                        }
                    };
                }
            }
            chatDevices.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
        }
    }

    private class onChatListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            String tmp = (String) parent.getItemAtPosition(position);
            String otherUser = tmp.split("\n")[0];

            if (otherUser != null) {

                Intent intent = new Intent(MainActivity.this, ChatActivity.class);


                /**
                 * The on-click listener for pair devices in the ListView
                 * Connect devices
                 */
                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                intent.putExtra(Constants.BLUETOOTH_DEVICE_MAC, address);

                Toast.makeText(getApplicationContext(), "Trying to connect with " + device.getName(),
                        Toast.LENGTH_LONG).show();
                startActivity(intent);

                // Create the result Intent and include the MAC address
                //Intent intent = new Intent();
                //intent.putExtra(EXTRA_DEVICE_ADDRESS, address);


                //mConnController.connectDevice(device);

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        mConnController.starts();
    }

    @Override
    public void onStart() {
        super.onStart();

        mConnController.starts();
    }
}
