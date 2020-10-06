package com.heycode.testsensors;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class BluetoothEx extends AppCompatActivity {

    Button on,off, showPairedList, find;
    ListView mListView;
    TextView mTextView;

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mBluetoothDevices;
    ArrayAdapter<String> mStringArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_ex);

        on = findViewById(R.id.btn_on);
        off = findViewById(R.id.btn_off);
        showPairedList = findViewById(R.id.btn_listall);
        find = findViewById(R.id.btn_find);
        mListView = findViewById(R.id.bt_list_view);
        mTextView = findViewById(R.id.bt_text_view);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter==null){
            mTextView.setText("Status: Not Supported");
            on.setEnabled(false);
            off.setEnabled(false);
            showPairedList.setEnabled(false);
            find.setEnabled(false);
            Toast.makeText(BluetoothEx.this, "Your device doesn't support BT!", Toast.LENGTH_SHORT).show();
        }else {
            on.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    turnOnBluetooth();
                }
            });
            off.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    turnOffBluetooth();
                }
            });

            showPairedList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPairedDevices();
                }
            });
            mStringArrayAdapter = new ArrayAdapter<>(BluetoothEx.this, android.R.layout.simple_list_item_1);
            mListView.setAdapter(mStringArrayAdapter);

            find.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findDevices();
                }
            });
        }


    }

    ////////////Bluetooth ON
    public void turnOnBluetooth(){
        if(mBluetoothAdapter!=null){
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, 121);
            Toast.makeText(this, "Bluetooth is ON", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Bluetooth is Already ON", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 121){
            if(mBluetoothAdapter.isEnabled()){
                mTextView.setText("Status: ENABLED");
                on.setEnabled(false);
                off.setEnabled(true);
            }else {
                mTextView.setText("Status: DISABLED");
            }
        }
    }

    //////////////Bluetooth OFF
    public void turnOffBluetooth(){
        mBluetoothAdapter.disable();
        mTextView.setText("Status: DISABLED");
        Toast.makeText(this, "Bluetooth is OFF", Toast.LENGTH_SHORT).show();
        on.setEnabled(true);
        off.setEnabled(false);
    }

    //////////////Bluetooth SHOWING PAIRED DEVICES
    public void showPairedDevices(){
        mBluetoothDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bd: mBluetoothDevices){
            mStringArrayAdapter.add(bd.getName()+"\n"+bd.getAddress());
        }
        Toast.makeText(this, "Showing Paired Devices..", Toast.LENGTH_SHORT).show();
    }

    //////////// SHOWING Nearby Devices
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device!=null){
                    mStringArrayAdapter.add(device.getName()+"\n"+device.getAddress());
                    mStringArrayAdapter.notifyDataSetChanged();

                    Toast.makeText(context, "Device is showing", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "SoRRRRRRYYYYYYYY", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    public void findDevices(){
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }else {
            mStringArrayAdapter.clear();
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBroadcastReceiver, filter);
            Toast.makeText(this, "Found new devices..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}