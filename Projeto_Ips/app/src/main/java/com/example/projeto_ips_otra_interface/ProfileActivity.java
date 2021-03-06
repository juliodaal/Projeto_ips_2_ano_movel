package com.example.projeto_ips_otra_interface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity {

    public static final String IDUSER = "IDUSER";
    public static final String NAMEUSER = "NAMEUSER";
    public static final String LASTNAMEUSER = "LASTNAMEUSER";
    public static final String EMAILUSER = "EMAILUSER";
    public static final String NUMBERRECYCLE = "NUMBERRECYCLE";

    private int idUser;
    private int numberRecycle;
    private String userName;
    private String lastName;
    private String emailUser;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_profile);

        readValues();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int id = item.getItemId();
                switch (id){
                    case R.id.btnHistoric:
                        intent = new Intent(ProfileActivity.this,HistoricActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnDashboard:
                        intent = new Intent(ProfileActivity.this,DashboardActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnProfile:
                        intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnLogOut:
                        intent = new Intent(ProfileActivity.this,MainActivity.class);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putInt(ProfileActivity.IDUSER, 0);
                        idUser = 0;
                        edit.putBoolean(MainActivity.LOGED, false);
                        edit.putInt(MainActivity.IDUSERMAIN, 0);
                        edit.commit();
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        TextView idUserProfile = (TextView) findViewById(R.id.idUserProfile);
        TextView nameUserProfile = (TextView) findViewById(R.id.nameUserProfile);
        TextView emailUserProfile = (TextView) findViewById(R.id.emailUserProfile);
        TextView recycleUserProfile = (TextView) findViewById(R.id.recycleUserProfile);
        idUserProfile.setText(String.valueOf(idUser));
        nameUserProfile.setText(String.valueOf(userName + " " + lastName));
        emailUserProfile.setText(String.valueOf(emailUser));
        recycleUserProfile.setText(String.valueOf(numberRecycle));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.support){
            Intent intent = new Intent(ProfileActivity.this,SupportActivity.class);
            startActivity(intent);
            return true;
        }
        if(mToggle.onOptionsItemSelected(item)){
            TextView idUserName = (TextView) findViewById(R.id.idUserName);
            TextView idUserEmail = (TextView) findViewById(R.id.idUserEmail);

            idUserName.setText(userName + " " + lastName);
            idUserEmail.setText(emailUser);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    private void readValues() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        idUser = sharedPref.getInt(IDUSER,0);
        userName = sharedPref.getString(NAMEUSER, "No Name");
        lastName = sharedPref.getString(LASTNAMEUSER, "No LastName");
        emailUser = sharedPref.getString(EMAILUSER, "No User Email");
        numberRecycle = sharedPref.getInt(NUMBERRECYCLE, 0);
    }
}