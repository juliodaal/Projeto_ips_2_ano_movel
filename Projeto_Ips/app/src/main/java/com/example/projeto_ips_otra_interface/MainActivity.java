package com.example.projeto_ips_otra_interface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String LOGED = "LOGED";
    public static final String IDUSERMAIN = "IDUSERMAIN";
    private boolean loged;
    private int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readValues();

        Button btnGoSignIn = (Button) findViewById(R.id.btnGoSignIn);
        Button btnGoToWebSite = (Button) findViewById(R.id.btnGoToWebSite);

        btnGoSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btnGoToWebSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://projeto-final-ips-2-ano.herokuapp.com"));
                startActivity(browserIntent);
            }
        });
    }

    private void readValues() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        loged = sharedPref.getBoolean(LOGED, false);
        idUser = sharedPref.getInt(IDUSERMAIN, 0);
        if(loged == true){
            Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putInt(DashboardActivity.IDUSER, idUser);
            startActivity(intent);
        }
    }
}