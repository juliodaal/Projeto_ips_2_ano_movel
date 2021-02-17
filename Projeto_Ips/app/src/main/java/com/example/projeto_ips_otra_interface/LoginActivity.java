package com.example.projeto_ips_otra_interface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {

    Fragment loadFragment;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    private String email;
    private String password;
    private Toast toastInvalidCredentials;
    private Toast toastUnexpectedResponse;
    private Toast toastErrorDashboard;
    private Toast toastErrorCatchingData;
    private int idUser;
    private int numberRecycle;
    private String nameUser;
    private String lastNameUser;
    private String emailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadFragment = new LoadFragment();

        TextView inputEmail = (TextView) findViewById(R.id.inputEmail);
        TextView inputPassword = (TextView) findViewById(R.id.inputPassword);
        Button btnSingIn = (Button) findViewById(R.id.btnSingIn);

        toastInvalidCredentials = Toast.makeText(LoginActivity.this, getResources().getText(R.string.invalidCredentials), Toast.LENGTH_SHORT);
        toastUnexpectedResponse = Toast.makeText(LoginActivity.this, getResources().getText(R.string.unexpectedResponse), Toast.LENGTH_SHORT);
        toastErrorDashboard = Toast.makeText(LoginActivity.this, getResources().getText(R.string.errorDashboard), Toast.LENGTH_SHORT);
        toastErrorCatchingData = Toast.makeText(LoginActivity.this, getResources().getText(R.string.errorCatchingData), Toast.LENGTH_SHORT);

        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                if(email.equals("")){
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.invalidEmail), Toast.LENGTH_SHORT).show();
                } else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.invalidPassword), Toast.LENGTH_SHORT).show();
                } else {
                    try { signin(email,password); } catch (Exception e){
                        Log.e("TAG", "Error getting data", e);
                    }
                }
            }
        });
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
            Intent intent = new Intent(LoginActivity.this,SupportLoginActivity.class);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signin(String email, String password) throws Exception {
        getSupportFragmentManager().beginTransaction().add(R.id.idLoadFragment,loadFragment).commit();
        // Armando objeto para enviar
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);

        // Armando consulta
        Request request = new Request.Builder()
                .url("https://projeto-final-ips-2-ano.herokuapp.com/app/login/" + email + "/" + password)
                .post(RequestBody.create(jsonObject.toString(), MEDIA_TYPE_JSON))
                .build();

        // Haciendo la consulta
        client.newCall(request).enqueue(new Callback() {

            //Si la respuesta es errada
            @Override
            public void onFailure(Call call, IOException e) {
                getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();
                Log.e("TAG", "Error receiving server response", e);
                toastInvalidCredentials.show();
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
                        JSONArray jsonArray = resJSON.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){
                            try {
                                JSONObject json = jsonArray.getJSONObject(i);
                                idUser = json.getInt("id");
                                nameUser = json.getString("nome");
                                lastNameUser = json.getString("apelido");
                                emailUser = json.getString("email");
                                numberRecycle = json.getInt("number_recycle");
                                goToDashboard();
                            } catch (JSONException e) {
                                getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();
                                toastErrorCatchingData.show();
                                e.printStackTrace();
                            }
                        }
                    } else { getSupportFragmentManager().beginTransaction().remove(loadFragment).commit(); toastInvalidCredentials.show(); }
                }
                catch (Exception e) {
                    getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();
                    toastErrorDashboard.show();
                    Log.e("TAG", "Error opening dashboard", e);
                }
            }
        });
    }

    private void goToDashboard(){
        getSupportFragmentManager().beginTransaction().remove(loadFragment).commit();

        Intent intent = new Intent(LoginActivity.this,HistoricActivity.class);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPref.edit();

        edit.putInt(DashboardActivity.IDUSER, idUser);
        edit.putString(DashboardActivity.NAMEUSER, nameUser);
        edit.putString(DashboardActivity.LASTNAMEUSER, lastNameUser);
        edit.putString(DashboardActivity.EMAILUSER, emailUser);

        edit.putInt(HistoricActivity.IDUSER, idUser);
        edit.putString(HistoricActivity.NAMEUSER, nameUser);
        edit.putString(HistoricActivity.LASTNAMEUSER, lastNameUser);
        edit.putString(HistoricActivity.EMAILUSER, emailUser);

        edit.putInt(ProfileActivity.IDUSER, idUser);
        edit.putString(ProfileActivity.NAMEUSER, nameUser);
        edit.putString(ProfileActivity.LASTNAMEUSER, lastNameUser);
        edit.putString(ProfileActivity.EMAILUSER, emailUser);
        edit.putInt(ProfileActivity.NUMBERRECYCLE, numberRecycle);

        edit.putBoolean(MainActivity.LOGED, true);
        edit.putInt(MainActivity.IDUSERMAIN, idUser);

        edit.commit();

        startActivity(intent);
    }
}