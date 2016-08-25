package com.example.utar.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Yumiko on 7/21/2016.
 */
public class ChatActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ListView lvMainChat;
    private EditText etMain;
    private Button btnSend;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 42;
    private static final int REQUEST_PICK_FILE = 45;
    private static final int ACTIVITY_SELECT_IMAGE = 46;
    private static final int REQUEST_CONNECT_DEVICE = 1;
   // public static final int MESSAGE_STATE_CHANGE = 1;
   // public static final int MESSAGE_READ = 2;
   // public static final int MESSAGE_WRITE = 3;

    DatabaseAdapter chatDatabaseAdapter;


    //private String connectedDeviceName = null;
    private ArrayAdapter<String> chatArrayAdapter;
    private static final String TAG = "BluetoothChatService";

    private StringBuffer outStringBuffer;
    ActionBar actionBar;

    String connected = "Connected";
    String notconnected = "Not Connected";
    String notconnectdevice = "Not Connected to a device";
    String connecting = "Connecting...";

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;
    BluetoothDevice device;

    BluetoothConnectionController mConnController;
    String bluetoothDeviceAddress;
    BluetoothAdapter mBluetoothAdapter;

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler( new Handler.Callback() {
        @Override public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothConnectionController.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to,device.getName()));
                            break;
                        case BluetoothConnectionController.STATE_CONNECTING:
                            setStatus(connecting);
                            break;
                        case BluetoothConnectionController.STATE_LISTEN:
                        case BluetoothConnectionController.STATE_NONE:
                            setStatus(notconnected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
                    chatArrayAdapter.add("Me:  " + writeMessage);
                    chatDatabaseAdapter.insertMessage(bluetoothDeviceAddress,writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    Log.e(TAG,"receive");
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
                    chatArrayAdapter.add(device.getName() + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:

                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + device.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                            Toast.makeText(ChatActivity.this, msg.getData().getString(Constants.TOAST),
                                    Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnController = new BluetoothConnectionController(getApplicationContext(), mHandler);


        //Obtain device name
       /* Bundle bundle = getIntent().getExtras();
        String otherUser = bundle.getString("otherUser");*/

        // Initialize back button

        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);
       // actionBar.setTitle(otherUser);

        getWidgetReferences();
        bindEventHandler();
        chatDatabaseAdapter = new DatabaseAdapter(this);
        chatDatabaseAdapter = chatDatabaseAdapter.open();
        Intent intent = getIntent();
        bluetoothDeviceAddress = intent.getStringExtra(Constants.BLUETOOTH_DEVICE_MAC);
        device = mBluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);

        if(bluetoothDeviceAddress!=null){
            if(!BluetoothConnectionController.BTdevice.isEmpty())
                for(BluetoothDevice checkDevice:BluetoothConnectionController.BTdevice) {
                    if (!device.getAddress().equals(checkDevice.getAddress())) {
                        mConnController.connectDevice(device);
                    }
                }
            else
                mConnController.connectDevice(device);
            String rtMsg = chatDatabaseAdapter.retrieveMessage(bluetoothDeviceAddress);






        }






        findViewById(R.id.imageView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ChatActivity.this, view);
                popupMenu.setOnMenuItemClickListener(ChatActivity.this);
                popupMenu.inflate(R.menu.list_menu);
                popupMenu.show();
            }
        });
        actionBar = getSupportActionBar();
        actionBar.setTitle(device.getName());


        //if device connected, set subtitle to connected
        //if device not connected, set subtitle to not connected
        //actionBar.setSubtitle(notconnected);

    }
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
                return true;
            case R.id.item2:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent,REQUEST_PICK_FILE);

                Toast.makeText(this, "Document Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                Intent intents = new Intent();
                intents.setType("video/*");
                intents.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intents,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
                Toast.makeText(this, "Video Clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    private void getWidgetReferences() {
        lvMainChat = (ListView) findViewById(R.id.lvMainChat);
        etMain = (EditText) findViewById(R.id.etMain);
        btnSend = (Button) findViewById(R.id.btnSend);

    }

    private void bindEventHandler() {
        etMain.setOnEditorActionListener(mWriteListener);

        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = etMain.getText().toString();
                sendMessage(message);

            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    etMain.setText(filePath);

                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        // Inflate the menu without menu items
        return true;
    }
    private final void setStatus(CharSequence subTitle) {
        //if(actionBar != null) {
        //actionBar = getSupportActionBar();
        actionBar.setSubtitle(subTitle);
        // actionBar.show();
        //}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;

        switch (item.getItemId()) {
            //Return to main activity
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.connect_scan:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendMessage(String message) {
        if (mConnController.getState() != BluetoothConnectionController.STATE_CONNECTED) {
            /*Bitmap bm = BitmapFactory.decodeResource(getResources(),R.layout.activity_device_list);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100,baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();*/

            Toast.makeText(this, notconnectdevice, Toast.LENGTH_SHORT)
                    .show();

            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mConnController.write(send);

            outStringBuffer.setLength(0);
            etMain.setText(outStringBuffer);

        }
    }
    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId,
                                      KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };
    /*
    private void setStatus(int resId) {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(resId);
    }*/

    private void setupChat() {
        chatArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        lvMainChat.setAdapter(chatArrayAdapter);

        //mConnController= new BluetoothConnectionController(this, mHandler);

        outStringBuffer = new StringBuffer("");


    }
    @Override
    public synchronized void onResume() {
        super.onResume();

        if (mConnController!= null) {
            if (mConnController.getState() == BluetoothConnectionController.STATE_NONE) {
                mConnController.starts();
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    @Override
    public void onStart() {
        super.onStart();

       // if(mConnController ==null)
          setupChat();
        //setStatus(notconnected);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */


}



    /*
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readMessage.length() > 0) {
                        mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    break;
            }
        }
    };*/


