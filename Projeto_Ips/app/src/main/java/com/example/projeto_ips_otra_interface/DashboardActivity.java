package com.example.projeto_ips_otra_interface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DashboardActivity extends AppCompatActivity {

    Fragment loadFragment;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    public static final String ID = "ID";
    public static final String QUANTITY = "QUANTITY";
    public static final String IDUSER = "IDUSER";
    public static final String NAMEUSER = "NAMEUSER";
    public static final String LASTNAMEUSER = "LASTNAMEUSER";
    public static final String EMAILUSER = "EMAILUSER";

    private int id;
    private int idUser;
    private int quantity;
    private EditText inputId;
    private EditText inputCurrentQuantity;
    private EditText inputWeight;
    private Double weight;
    private String date;
    private Calendar calendar;
    private int currentYear, currentMonth, currentDay;
    private Toast toastErrorPackingData;
    private Toast toastInvalidData;
    private Toast toastUnexpectedResponse;
    private Toast toastErrorShowingFeedBack;
    private Toast toastDataRegisterSuccessfuly;

    private String userName;
    private String lastName;
    private String emailUser;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_dashboard);

        readValues();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadFragment = new LoadFragment();

        toastErrorPackingData = Toast.makeText(DashboardActivity.this, getResources().getText(R.string.errorPackingData), Toast.LENGTH_SHORT);
        toastInvalidData = Toast.makeText(DashboardActivity.this, getResources().getText(R.string.invalidData), Toast.LENGTH_SHORT);
        toastUnexpectedResponse = Toast.makeText(DashboardActivity.this, getResources().getText(R.string.unexpectedResponse), Toast.LENGTH_SHORT);
        toastErrorShowingFeedBack = Toast.makeText(DashboardActivity.this, getResources().getText(R.string.errorShowingFeedBack), Toast.LENGTH_SHORT);
        toastDataRegisterSuccessfuly = Toast.makeText(DashboardActivity.this, getResources().getText(R.string.dataRegisterSuccessfuly), Toast.LENGTH_SHORT);

        Button btnSendBoxData = (Button) findViewById(R.id.btnSendBoxData);

        btnSendBoxData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputId = (EditText) findViewById(R.id.inputId);
                inputCurrentQuantity = (EditText) findViewById(R.id.InputCurrentQuantity);
                inputWeight = (EditText) findViewById(R.id.inputWeight);

                calendar = Calendar.getInstance();
                currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                currentMonth = calendar.get(Calendar.MONTH) + 1;
                currentYear = calendar.get(Calendar.YEAR);

                String iId = inputId.getText().toString();
                String iCQ = inputCurrentQuantity.getText().toString();
                String iW = inputWeight.getText().toString();

                if(iId.equals("") || iId.equals("0")){
                    Toast.makeText(DashboardActivity.this, getResources().getText(R.string.invalidId), Toast.LENGTH_SHORT).show();
                } else if(iCQ.equals("") || iCQ.equals("0")){
                    Toast.makeText(DashboardActivity.this, getResources().getText(R.string.invalidQuantity), Toast.LENGTH_SHORT).show();
                } else if(iW.equals("") || iW.equals("0")){
                    Toast.makeText(DashboardActivity.this, getResources().getText(R.string.invalidWeight), Toast.LENGTH_SHORT).show();
                } else {
                    id = Integer.parseInt(iId);
                    quantity = Integer.parseInt(iCQ);
                    weight = Double.parseDouble(iW);
                    date = currentYear + "-" + currentMonth + "-" + currentDay;
                    sendData();
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int id = item.getItemId();
                switch (id){
                    case R.id.btnHistoric:
                        intent = new Intent(DashboardActivity.this,HistoricActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnDashboard:
                        intent = new Intent(DashboardActivity.this,DashboardActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnProfile:
                        intent = new Intent(DashboardActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnLogOut:
                        intent = new Intent(DashboardActivity.this,MainActivity.class);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putInt(DashboardActivity.IDUSER, 0);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.qrcode){
            Intent intent = new Intent(DashboardActivity.this,QrCodeActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.support){
            Intent intent = new Intent(DashboardActivity.this,SupportActivity.class);
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
        id = sharedPref.getInt(ID, 0);
        quantity = sharedPref.getInt(QUANTITY,0);
        idUser = sharedPref.getInt(IDUSER,0);

        EditText inputId = (EditText) findViewById(R.id.inputId);
        EditText inputCurrentQuantity = (EditText) findViewById(R.id.InputCurrentQuantity);

        inputId.setText("" + id);
        inputCurrentQuantity.setText("" + quantity);

        userName = sharedPref.getString(NAMEUSER, "No Name");
        lastName = sharedPref.getString(LASTNAMEUSER, "No LastName");
        emailUser = sharedPref.getString(EMAILUSER, "No User Email");
    }

    private void sendData(){
        getSupportFragmentManager().beginTransaction().add(R.id.idLoadFragmenttwo,loadFragment).commit();
        // Armando objeto para enviar
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("quantity", quantity);
            jsonObject.put("weight", weight);
            jsonObject.put("date", date);
        } catch (JSONException e) {
            getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();
            toastErrorPackingData.show();
            //e.printStackTrace();
        }

        // Armando consulta
        Request request = new Request.Builder()
                .url("https://projeto-final-ips-2-ano.herokuapp.com/app/box/register/" + id + "/" + quantity + "/" + weight + "/" + date + "/" + idUser)
                .post(RequestBody.create(jsonObject.toString(), MEDIA_TYPE_JSON))
                .build();

        // Haciendo la consulta
        client.newCall(request).enqueue(new Callback() {

            //Si la respuesta es errada
            @Override
            public void onFailure(Call call, IOException e) {
                getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();
                Log.e("TAG", "Error receiving server response", e);
                toastInvalidData.show();
            }

            //Si la respuesta es acertada
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();
                    toastUnexpectedResponse.show();
                    throw new IOException("Unexpected code " + response);
                }

                try {
                    ResponseBody responseBody = response.body();
                    String responseString = responseBody.string();

                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject resJSON = jsonObject.getJSONObject("response");
                    String message = resJSON.getString("message");

                    if(message.equals("success")){
                        inputId.setText("");
                        inputCurrentQuantity.setText("");
                        inputWeight.setText("");
                        getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();
                        toastDataRegisterSuccessfuly.show();
                    } else {getSupportFragmentManager().beginTransaction().remove(loadFragment).commit(); toastInvalidData.show(); }
                }
                catch (Exception e) {
                    getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();
                    toastErrorShowingFeedBack.show();
                    Log.e("TAG", "Error opening dashboard", e);
                }
            }
        });
    }
}