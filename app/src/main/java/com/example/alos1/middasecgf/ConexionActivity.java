package com.example.alos1.middasecgf;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.aflak.bluetooth.Bluetooth;

public class ConexionActivity extends AppCompatActivity {

    private Bluetooth bt;
    private ListView listView;
    private List<BluetoothDevice> paired;
    private boolean registered=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver,filter);
        registered=true;

        bt = new Bluetooth(this);
        bt.enableBluetooth();
        listView = (ListView)findViewById(R.id.listDevices);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ConexionActivity.this, MenuPrincipalActivity.class);
                i.putExtra("pos", position);
                if(registered) {
                    unregisterReceiver(mReceiver);
                    registered=false;
                }
                startActivity(i);
                finish();            }
        });
        addDevicesToList();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registered) {
            unregisterReceiver(mReceiver);
            registered=false;
        }
    }
    private void addDevicesToList(){
        paired = bt.getPairedDevices();
        List<String> names = new ArrayList<>();
        for (BluetoothDevice d : paired){
            names.add(d.getName());
        }
        String[] array = names.toArray(new String[names.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, array);
        listView.setAdapter(adapter);
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();

                    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listView.setEnabled(false);
                                    }
                                });
                                Toast.makeText(ConexionActivity.this, "Turn on bluetooth", Toast.LENGTH_LONG).show();
                                break;
                            case BluetoothAdapter.STATE_ON:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addDevicesToList();
                                        listView.setEnabled(true);
                                    }
                                });
                                break;
                        }
                    }
                }
            };
}

