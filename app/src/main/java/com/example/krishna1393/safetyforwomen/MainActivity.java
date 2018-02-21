package com.example.krishna1393.safetyforwomen;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Random;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    EditText etusername,etpin,etphone,etotp;
    SQLiteDatabase db2;
    int temppin;
    String msgpin, phonenum;
    private Button btregister, btlogin, btnewuser,btverifyotp;
    TextView tvforgotrpin,txtvusername,txtvpin,txtvphone,txtvotp;
    int randomotp;
    int checkotp;

    private final static int PERMISSION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //database creation
        db2 = openOrCreateDatabase("register", Context.MODE_PRIVATE, null);
        db2.execSQL("CREATE TABLE IF NOT EXISTS register( username VARCHAR , password NUMERIC, phn VARCHAR );");

        //edit Textview
        etusername = findViewById(R.id.edtuser);
        etpin = findViewById(R.id.edtpin);
        etphone = findViewById(R.id.edtphone);
        etotp = findViewById(R.id.edtotp);

        //buttons
        btlogin = findViewById(R.id.btnlogin);
        btverifyotp = findViewById(R.id.btnverifyotp);
        btnewuser = findViewById(R.id.btnnewuser);
        btregister = findViewById(R.id.btnregister);

        //textview
        tvforgotrpin = findViewById(R.id.txtforgotpin);
        txtvphone=findViewById(R.id.textView9);
        txtvusername=findViewById(R.id.textView6);
        txtvpin=findViewById(R.id.textView8);
        txtvotp=findViewById(R.id.textView10);

       //setting setOnClickListener
        btregister.setOnClickListener(this);
        tvforgotrpin.setOnClickListener(this);
        btnewuser.setOnClickListener(this);
        btlogin.setOnClickListener(this);
        btverifyotp.setOnClickListener(this);
        permissonssms();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        etpin.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Cursor c = db2.rawQuery("SELECT * FROM register", null);
        if (c.getCount() == 0) {
            etusername.setVisibility(View.VISIBLE);
            etpin.setVisibility(View.VISIBLE);
            etphone.setVisibility(View.VISIBLE);
            btregister.setVisibility(View.VISIBLE);


            etotp.setVisibility(View.GONE);
            btlogin.setVisibility(View.GONE);
            btnewuser.setVisibility(View.GONE);
            tvforgotrpin.setVisibility(View.GONE);
            btverifyotp.setVisibility(View.GONE);
            c.close();
        } else {
            etusername.setVisibility(View.GONE);
            etpin.setVisibility(View.VISIBLE);
            etphone.setVisibility(View.GONE);
            btregister.setVisibility(View.GONE);
            txtvpin.setVisibility(View.VISIBLE);
            txtvusername.setVisibility(View.GONE);
            txtvphone.setVisibility(View.GONE);

            etotp.setVisibility(View.GONE);
            btlogin.setVisibility(View.VISIBLE);
            btnewuser.setVisibility(View.VISIBLE);
            tvforgotrpin.setVisibility(View.VISIBLE);
            btverifyotp.setVisibility(View.GONE);
        }
    }

    public void showMessage(String title, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public int ran(){
        Random r = new Random();
        int otp = r.nextInt(99999 - 11111)+99999;
        return otp;
    }
    @Override
    public void onClick(View view) {
        if (view == btregister) {
            if (etusername.getText().toString().trim().length() == 0 && etpin.getText().toString().trim().length() == 0 && etphone.getText().toString().trim().length() == 0) {
                showMessage("", "Enter All Details");
            } else {
                db2.execSQL("INSERT INTO register VALUES(" + "'" + etusername.getText() + "'," + etpin.getText() + "," + "'" + etphone.getText() + "'" + ");");
                Toast.makeText(getApplicationContext(),"Registered Sucessfully",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ui.class);
                startActivity(intent);
                finish();
            }
        }
        if (view == btlogin) {
            if (etpin.getText().toString().trim().length() == 0) {
                Toast.makeText(getApplicationContext(),"Enter the pin",Toast.LENGTH_SHORT).show();
            } else {
                final String Tag = "";
                Cursor c2 = db2.rawQuery("SELECT * FROM register", null);
                if (c2.moveToFirst()) {
                    temppin = c2.getInt(1);
                    Log.v(Tag, "temppin" + temppin);
                }
                try {
                    Integer lo = Integer.parseInt(etpin.getText().toString());
                    if (temppin == lo) {
                        etpin.setText("");
                        Intent intent = new Intent(MainActivity.this, ui.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(MainActivity.this, "PIN IS INCORRECT", Toast.LENGTH_SHORT).show();
                        etpin.setText("");
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "ENTER PIN", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (view == btnewuser) {
            try {
                db2.delete("register", null, null);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{

                db2.delete("guard",null,null);}
            catch (Exception e){
                e.printStackTrace();
            }
            etusername.setVisibility(View.VISIBLE);
            etpin.setVisibility(View.VISIBLE);
            etphone.setVisibility(View.VISIBLE);
            btregister.setVisibility(View.VISIBLE);
            txtvpin.setVisibility(View.VISIBLE);
            txtvusername.setVisibility(View.VISIBLE);
            txtvphone.setVisibility(View.VISIBLE);


            etotp.setVisibility(View.GONE);
            btlogin.setVisibility(View.GONE);
            btnewuser.setVisibility(View.GONE);
            tvforgotrpin.setVisibility(View.GONE);
            btverifyotp.setVisibility(View.GONE);
            etusername.requestFocus();

        }
        if (view == tvforgotrpin) {
            etusername.setVisibility(View.GONE);
            etpin.setVisibility(View.GONE);
            etphone.setVisibility(View.GONE);
            txtvotp.setVisibility(View.VISIBLE);
            btregister.setVisibility(View.GONE);
            txtvpin.setVisibility(View.GONE);

            etotp.setVisibility(View.VISIBLE);
            btlogin.setVisibility(View.GONE);
            btnewuser.setVisibility(View.VISIBLE);
            tvforgotrpin.setVisibility(View.GONE);
            btverifyotp.setVisibility(View.VISIBLE);
            String Tag = "";
            Cursor c3 = db2.rawQuery("SELECT * FROM register", null);
            if (c3.moveToFirst()) {
                phonenum = c3.getString(2);
            }
            Log.v(Tag, "email" + phonenum);
            randomotp = ran();
            msgpin = "OTP is" + randomotp;
            sendSMSMessage(phonenum, msgpin);
        }
        if (view == btverifyotp) {
            String Tag = "";
            try {
                if ((checkotp = Integer.parseInt(etotp.getText().toString())) == randomotp) {
                    Cursor c3 = db2.rawQuery("SELECT * FROM register", null);
                    if (c3.moveToFirst()) {
                        phonenum = c3.getString(2);
                        Log.v(Tag, "email" + phonenum);
                        temppin = c3.getInt(1);
                    }
                    try {
                        sendSMSMessage(phonenum, "" + temppin);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        permissonssms();
                    }
                    etusername.setVisibility(View.GONE);
                    etpin.setVisibility(View.VISIBLE);
                    etphone.setVisibility(View.GONE);
                    btregister.setVisibility(View.GONE);
                    txtvotp.setVisibility(View.GONE);


                    etotp.setVisibility(View.GONE);
                    txtvpin.setVisibility(View.VISIBLE);
                    btlogin.setVisibility(View.VISIBLE);
                    btnewuser.setVisibility(View.GONE);
                    tvforgotrpin.setVisibility(View.VISIBLE);
                    btverifyotp.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "OTP is sent to your registered phone number", Toast.LENGTH_SHORT).show();
                } else {
                    etusername.setVisibility(View.GONE);
                    etpin.setVisibility(View.GONE);
                    etphone.setVisibility(View.GONE);
                    txtvotp.setVisibility(View.VISIBLE);
                    btregister.setVisibility(View.GONE);

                    etotp.setVisibility(View.VISIBLE);
                    btlogin.setVisibility(View.GONE);
                    btnewuser.setVisibility(View.VISIBLE);
                    tvforgotrpin.setVisibility(View.GONE);
                    btverifyotp.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                }
            }catch (NumberFormatException e){
                Toast.makeText(MainActivity.this,"ENTER OTP",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissonssms();
        } else {
            finishAffinity();
            Toast.makeText(getApplicationContext(),"permission denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    String phoneNo;
    String message;
    public void sendSMSMessage(String ph, String msg) {
        final String TAG = "";
        Log.v(TAG, msg);
        phoneNo = ph;
        message = msg;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_SHORT).show();
    }
    public void permissonssms(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }

    }
}
