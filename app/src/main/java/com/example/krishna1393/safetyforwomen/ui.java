package com.example.krishna1393.safetyforwomen;

import android.Manifest;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public class ui extends Activity implements View.OnClickListener {

    private final static int PERMISSION_REQUEST = 1;
    MyReceiver myReceiver;
    EditText editTimerValue, editinterval, name, phnumber;
    Button btstartservice, btstopservice, btstarttimer, btstoptimer, btadd, btdel, btmodify, viewall;
    TextView txttimer, con, linktxt;
    int time, interval;
    TextView txtlocationview;
    ProgressBar pb;
    String phoneNo;
    String message;
    String[] auto = new String[100];
    SQLiteDatabase db2;
    CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;
    private long totalintervalinmilliseconds;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        permissonssms();
        txttimer=findViewById(R.id.tvtimer);
        viewall = findViewById(R.id.btnview);
        pb = findViewById(R.id.progressBar);
        linktxt = findViewById(R.id.textView7);
        name = findViewById(R.id.edtnamecontacts);
        phnumber = findViewById(R.id.edtphonenumcontatcs);
        btadd = findViewById(R.id.btnadd);
        btdel = findViewById(R.id.btndel);
        btmodify = findViewById(R.id.btnmodify);

        txtlocationview = findViewById(R.id.textView2);
        editTimerValue = findViewById(R.id.edttime);
        editinterval = findViewById(R.id.edtinterval);
        btstarttimer = findViewById(R.id.btnstarttimer);
        btstoptimer = findViewById(R.id.btnstoptimer);
        btstartservice = findViewById(R.id.btnstartservice);
        btstopservice = findViewById(R.id.btnstopservice);
        btstartservice.setOnClickListener(this);
        btstopservice.setOnClickListener(this);
        btstarttimer.setOnClickListener(this);
        btstoptimer.setOnClickListener(this);
        txtlocationview.setOnClickListener(this);
        btadd.setOnClickListener(this);
        btdel.setOnClickListener(this);
        btmodify.setOnClickListener(this);
        viewall.setOnClickListener(this);
        //database
        db2 = openOrCreateDatabase("register", Context.MODE_PRIVATE, null);
        db2.execSQL("CREATE TABLE IF NOT EXISTS guard(phnn NUMERIC,name VARCHAR);");
        pb.requestFocus();
        txttimer.setVisibility(View.INVISIBLE);
        btstarttimer.setVisibility(View.INVISIBLE);
        btstoptimer.setVisibility(View.INVISIBLE);
        editinterval.setVisibility(View.INVISIBLE);
        editTimerValue.setVisibility(View.INVISIBLE);
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MyService.MY_ACTION);
            registerReceiver(myReceiver, intentFilter);
        }catch (Exception e){

        }

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissonssms();
        } else {
            Toast.makeText(getApplicationContext(), "permission_denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void permissonssms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, PERMISSION_REQUEST);
        }
    }
    @Override
    public void onStart() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MyService.MY_ACTION);
            registerReceiver(myReceiver, intentFilter);
        }catch (Exception e){

        }


        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MyService.MY_ACTION);
            registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(myReceiver);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public void Provider() {
    }

    public void showMessage(String title, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void clearText() {
        name.setText("");
        phnumber.setText("");
        name.requestFocus();
    }

    public void autom() {
        final String TAG = "hello";
        Cursor cursor = db2.rawQuery("SELECT * FROM guard", null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                auto[i] = cursor.getString(0);
                Log.v(TAG, auto[i]);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    @Override
    public void onClick(View view) {
        if (view == btadd) {
            if (phnumber.getText().toString().trim().length() == 0 ||
                    name.getText().toString().trim().length() == 0) {
                showMessage("Error", "Please enter all values");
                return;
            }
            db2.execSQL("INSERT INTO guard VALUES(" + phnumber.getText() + ",'" + name.getText() + "');");
            showMessage("Success", "Contact added");
            clearText();
        }
        if (view == btdel) {
            if (name.getText().toString().trim().length() == 0) {
                showMessage("Error", "Please enter Name");
                return;
            }
            Cursor c = db2.rawQuery("SELECT * FROM guard WHERE name='" + name.getText() + "'", null);
            if (c.moveToFirst()) {
                db2.execSQL("DELETE FROM guard WHERE name='" + name.getText() + "'");
                showMessage("Success", "Contact Deleted");

            } else {
                showMessage("Error", "Invalid Name");
            }
            clearText();
            c.close();
        }
        if (view == btmodify) {
            if (name.getText().toString().trim().length() == 0) {
                showMessage("Error", "Please enter name");
                return;
            }
            Cursor c = db2.rawQuery("SELECT * FROM guard WHERE name='" + name.getText() + "'", null);
            if (c.moveToFirst()) {
                db2.execSQL("UPDATE guard SET phnn='" + phnumber.getText() +
                        "' WHERE name='" + name.getText() + "'");
                showMessage("Success", "Modified");
            } else {
                showMessage("Error", "Invalid name");
            }
            c.close();
            clearText();
        }
        if (view == btstartservice) {
            permissonssms();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                myReceiver = new MyReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(MyService.MY_ACTION);
                registerReceiver(myReceiver, intentFilter);

                //Start our own service
                Intent intent = new Intent(ui.this, MyService.class);
                startService(intent);
                pb.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(ui.this, "please enable GPS", Toast.LENGTH_LONG).show();
            }
        }
        if (view == btstopservice) {
            try {
                txtlocationview.setVisibility(View.INVISIBLE);
                editTimerValue.setVisibility(View.INVISIBLE);
                editinterval.setVisibility(View.INVISIBLE);
                btstarttimer.setVisibility(View.INVISIBLE);
                btstoptimer.setVisibility(View.INVISIBLE);
                txttimer.setVisibility(View.INVISIBLE);

                unregisterReceiver(myReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(ui.this, MyService.class);
            stopService(intent);
            Toast.makeText(ui.this, "service stopeed", Toast.LENGTH_SHORT).show();
        }
        if (view == btstarttimer) {
            Cursor c = db2.rawQuery("SELECT * FROM guard", null);
            if (!editTimerValue.getText().toString().equals("") && !editinterval.getText().toString().equals("") && !editTimerValue.getText().toString().equals("0") && !editinterval.getText().toString().equals("0") && c.getCount() != 0) {

                setTimer();
                startTimer();
                editinterval.getText().clear();
                editTimerValue.getText().clear();


            } else {
                Toast.makeText(getApplicationContext(), "Enter contacts to rum the timer", Toast.LENGTH_SHORT).show();
                Toast.makeText(ui.this, "please enter time and interval not 0", Toast.LENGTH_LONG).show();
                editinterval.getText().clear();
                editTimerValue.getText().clear();
            }
        }
        if (view == btstoptimer) {
            Toast.makeText(ui.this, "Timer is stoped", Toast.LENGTH_SHORT).show();
            try {
                countDownTimer.cancel();
                txttimer.setVisibility(View.INVISIBLE);
                btstarttimer.setVisibility(View.INVISIBLE);
                btstoptimer.setVisibility(View.INVISIBLE);
                editinterval.setVisibility(View.INVISIBLE);
                editTimerValue.setVisibility(View.INVISIBLE);

            } catch (NullPointerException e) {
            }
        }
        if (view == txtlocationview) {
            String uri = linktxt.getText().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(Intent.createChooser(intent, getString(R.string.view_location_via)));
        }
        if (view == viewall) {
            Cursor c = db2.rawQuery("SELECT * FROM guard", null);
            if (c.getCount() == 0) {
                showMessage("Error", "No records found");
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (c.moveToNext()) {
                buffer.append("phone number: " + c.getString(0) + "\n");
                buffer.append("Name: " + c.getString(1) + "\n");
            }
            showMessage("Guardian Details", buffer.toString());
            c.close();
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            txttimer.setVisibility(View.VISIBLE);
            editTimerValue.setVisibility(View.VISIBLE);
            editinterval.setVisibility(View.VISIBLE);
            btstarttimer.setVisibility(View.VISIBLE);
            btstoptimer.setVisibility(View.VISIBLE);
            txtlocationview.setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);

            // TODO Auto-generated method stub
            Double latitude = arg1.getDoubleExtra("Latitude", 0);
            Double Longitude = arg1.getDoubleExtra("Longitude", 0);
            String Provider = arg1.getStringExtra("Provider");
            String newline = System.getProperty("line.separator");
            String uri = "https://maps.google.com/?q=" + latitude + "," + Longitude;
            linktxt.setText(uri);
            txtlocationview.setText(String.format("%s: %s%s%s: %s%s%s: %s", getString(R.string.accuracy), Provider, newline,
                    getString(R.string.latitude), latitude, newline,
                    getString(R.string.longitude), Longitude));
        }
    }

    public void sendSMSMessage(String phonenum, String msg) {
        final String TAG = "";
        Log.v(TAG, msg);
        phoneNo = phonenum;
        message = msg;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_SHORT).show();
    }

    public void setTimer() {
        time = Integer.parseInt(editTimerValue.getText().toString());
        interval = Integer.parseInt(editinterval.getText().toString());
        totalTimeCountInMilliseconds = 60 * time * 1000;
        totalintervalinmilliseconds = (60 * time * 1000) / interval;
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, totalintervalinmilliseconds) {

            public void ts() {
                autom();
                int i = 0;
                while (auto[i] != null) {
                    String msg = linktxt.getText().toString();
                    Toast.makeText(getApplicationContext(), auto[i] + "" + msg, Toast.LENGTH_SHORT).show();
                    sendSMSMessage(auto[i], msg);
                    i++;
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
                //Toast.makeText(getApplicationContext(), "location"+locc.getText().toString(), Toast.LENGTH_SHORT).show();
                // Toast.makeText(getApplicationContext(), "" + gmt.getText(), Toast.LENGTH_SHORT).show();
                ts();
            }

            @Override
            public void onFinish() {
                // this function will be called when the timecount is finished
                ts();
                try {
                    txtlocationview.setVisibility(View.INVISIBLE);
                    editTimerValue.setVisibility(View.INVISIBLE);
                    editinterval.setVisibility(View.INVISIBLE);
                    btstarttimer.setVisibility(View.INVISIBLE);
                    btstoptimer.setVisibility(View.INVISIBLE);
                    txttimer.setVisibility(View.INVISIBLE);
                    unregisterReceiver(myReceiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ui.this, MyService.class);
                stopService(intent);
                try {
                    unregisterReceiver(myReceiver);
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
                Toast.makeText(ui.this, "service stopeed", Toast.LENGTH_SHORT).show();
                finish();
            }
        }.start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    try {
                        autom();
                        int i = 0;
                        while (auto[i] != null) {
                            //updateLocation(lastLocation);
                            //msg = formatLocation(lastLocation, getResources().getStringArray(R.array.link_templates)[0]) + "\n" + "help me";
                            //sendSMSMessage(auto[i], msg);
                            Toast.makeText(ui.this, linktxt.getText().toString() + "volume" + auto[i], Toast.LENGTH_LONG).show();
                            i++;
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(ui.this, "wait for location", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}