package com.example.alos1.middasecgf;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.aflak.bluetooth.Bluetooth;

import static java.lang.Integer.parseInt;

public class MenuPrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Bluetooth.CommunicationCallback {

    private String name;
    private Bluetooth b;
    private Button send,results,save;
    private ScrollView scrollView;
    private boolean registered=false;
    GraphView graph;
    private LineGraphSeries<DataPoint> mSeries;
    String celDoctor = "";
    String momsName = "";
    DBHelper dbHelper;
    TextView bpmText;
    int counter=0;
    String values;
    int flagWait=0;
    //FlagValue 0 --> Ready to receive , Cant Show
    //          1 --> Cant Do anything
    //          2 --> Have received , ready to show/save
    int flagValue=1;
    Handler handler = new Handler();
    TextView IdBufferIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bpmText=(TextView)findViewById(R.id.bpm);
        results = (Button) findViewById(R.id.results);
        save = (Button) findViewById(R.id.save);
        send = (Button)findViewById(R.id.send);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        graph = (GraphView) findViewById(R.id.graph);
        momsName = String.valueOf(R.string.pacientenombre);
        send.setEnabled(false);

        b = new Bluetooth(this);
        b.enableBluetooth();

        b.setCommunicationCallback(this);

        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();

        dbHelper = new DBHelper(this);

        Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
        b.connectToDevice(b.getPairedDevices().get(pos));

        //Boton Start
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStart();
            }
        });

        //Boton Show
        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonShow();
            }
        });
        //Bton Save
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSave();
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered=true;

        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(1200);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setScrollableY(true);
        graph.getViewport().setScalable(true);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nvHistorial) {
            Intent i = new Intent(MenuPrincipalActivity.this, History.class);
            startActivity(i);
        } else if (id == R.id.nvPromedioFCF) {

        } else if (id == R.id.nvPromedioFC) {

        } else if (id == R.id.nvPromedioPA) {


        } else if (id == R.id.nvConfiguracion) {

        } else if (id == R.id.nvConProxima) {


        } else if (id == R.id.nvMedicamentos) {

        } else if (id == R.id.nvClinica) {
           navegarEmergencia();
        } else if (id == R.id.nvCompartir) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,"Hoy el corazón de mi bebé late a: 140pm");
            startActivity(Intent.createChooser(intent, "Sonidos de bebé"));
        } else if (id == R.id.nvEnvioAlerta) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void navegarEmergencia(){
        Uri gmmIntentUri = Uri.parse("google.navigation:q=19.2419502,-103.7368206&avoid=tf");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
    private void ejecutarParaEjecutar(){
        handler.postDelayed(new Runnable() {
            public void run() {
                buttonStart();
            }
        }, 30000);
    }
    private void ejecutarParaVer(){
        handler.postDelayed(new Runnable() {
            public void run() {
                buttonShow();
            }
        }, 5000);
    }

    private void buttonStart(){
        flagWait=0;
        values="";
        counter=0;
        String msg = "m";
        b.send(msg);
        flagValue=0;
        Toast.makeText(MenuPrincipalActivity.this, "Taking Test...", Toast.LENGTH_LONG).show();
        ejecutarParaEjecutar();
        ejecutarParaVer();
    }
    private void buttonShow(){
        if (flagValue==2) {
            mSeries.resetData(generateData());
            int bpm=Bpm();
            calcDanger(bpm);
        }else{
            Toast.makeText(MenuPrincipalActivity.this, "You Haven't Taken a Heart Rate Test", Toast.LENGTH_SHORT).show();
        }
        ejecutarParaVer();

    }
    private void buttonSave(){
        Log.d("flag","flagValue"+flagValue);
        if (flagValue==2){
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            dbHelper.insertPerson(values,dateFormat.format(date));
            Toast.makeText(MenuPrincipalActivity.this, "Graph Successfully Saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MenuPrincipalActivity.this, "Nothing to Save", Toast.LENGTH_SHORT).show();
        }
    }

    private void calcDanger(int bpm){
        double min=60;
        double max=100;
        double dBpm=(double)bpm;
        if (dBpm<min){
            Toast.makeText(this, "Your heart rate is below normal", Toast.LENGTH_SHORT).show();
            sendSMS(1);
        }else if(dBpm>max){
            Toast.makeText(this, "Your heart rate is above normal", Toast.LENGTH_SHORT).show();
            sendSMS(2);
        }else{
            Toast.makeText(this, "Your heart rate is normal", Toast.LENGTH_SHORT).show();
        }
    }
    //envia mensaje
    public void sendSMS(int caso) {
        String date = (android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", new java.util.Date()).toString());
        String msmEmergency = "Aviso";
        SmsManager sms = SmsManager.getDefault();
        switch (caso) {
            case 1:
                msmEmergency = "FCF Baja de :"+ momsName + date;
                sms.sendTextMessage(celDoctor,null,msmEmergency,null,null);
                Toast.makeText(getApplicationContext(), msmEmergency, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                msmEmergency = "FCF Alta" + date;
                sms.sendTextMessage(celDoctor,null,msmEmergency,null,null);
                Toast.makeText(getApplicationContext(), msmEmergency, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private int Bpm() {
        int beats=0;
        String[] parts=values.split(" ");
        int count = parts.length;
        //Log.d("Length", String.valueOf(count));
        for (int i=0; i<count; i++) {
            int max=0;
            while(Integer.parseInt(parts[i])>650){
                if(Integer.parseInt(parts[i])>max){
                    max=Integer.parseInt(parts[i]);
                }
                i++;
            }
            if (max!=0){
                beats++;
            }
        }
        Log.d("Beats", String.valueOf(beats));
        float time = (float) (count*0.006);
        int bpm= (int) (beats*60/time);
        bpmText.setText(Integer.toString(bpm));
        return bpm;
    }

    private DataPoint[] generateData() {
        String[] parts=values.split(" ");
        //Log.d("Debug", "parts=" + values);
        int count = parts.length;
        DataPoint[] dataValues = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y= parseInt(parts[i]);
            DataPoint v = new DataPoint(x, y);
            dataValues[i] = v;
        }
        return dataValues;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registered) {
            unregisterReceiver(mReceiver);
            registered=false;
        }
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        //Display("Connected to "+device.getName()+" - "+device.getAddress());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send.setEnabled(true);
            }
        });
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
        b.connectToDevice(device);
    }

    @Override
    public void onMessage(String message) {
        //Log.d("Debug","message="+message);
        flagWait++;
        if (flagWait>5 && flagValue==0) {
            if (counter < 1200) {
                if (counter == 0) {
                    values = message;
                    values += " ";
                } else {
                    values += message;
                    values += " ";
                }
                //Log.d("Debug", "parts=" + values);
                counter += 20;
                //Log.d("Flag","flagValue="+flagValue);
            }else{
                Log.d("Debug", "parts=" + values);
                flagValue=2;
            }
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Error: "+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.connectToDevice(device);
                    }
                }, 2000);
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(MenuPrincipalActivity.this, ConexionActivity.class);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };
}