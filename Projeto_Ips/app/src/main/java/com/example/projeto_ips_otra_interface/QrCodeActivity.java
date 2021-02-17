package com.example.projeto_ips_otra_interface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class QrCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private final OkHttpClient client = new OkHttpClient();

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    private boolean mPermissionDenied = false;
    private int id;
    private int quantity;
    private Toast toastErrorCatchingData;
    private Toast toastInvalidDataServer;
    private Toast toastUnexpectedResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);

        toastErrorCatchingData = Toast.makeText(QrCodeActivity.this, getResources().getText(R.string.errorCatchingData), Toast.LENGTH_SHORT);
        toastInvalidDataServer = Toast.makeText(QrCodeActivity.this, getResources().getText(R.string.invalidDataServer), Toast.LENGTH_SHORT);
        toastUnexpectedResponse = Toast.makeText(QrCodeActivity.this, getResources().getText(R.string.unexpectedResponse), Toast.LENGTH_SHORT);

        setContentView(mScannerView);
        enableScanner();
    }

    private void enableScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, REQUEST_CAMERA,
                    Manifest.permission.CAMERA, true);
        } else {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CAMERA) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.CAMERA)) {
            // Activar a funcionalicidades de localização se a permissão foi dada.
            enableScanner();
        } else {
            // Flag a "true" para mostrar o erro das permissões em falta.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // Se a flag estiver a true
        if (mPermissionDenied) {
            // A permissão não foi dada e deve-se mostrar um diálogo de erro.
            showMissingPermissionError();
            mPermissionDenied = false;
        } else {
            enableScanner();
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        final String result = rawResult.getText();
        Log.d("QRCodeScanner", result);

        // Armando consulta
        Request request = new Request.Builder()
                .url(result)
                .build();

        // Haciendo la consulta
        client.newCall(request).enqueue(new Callback() {

            //Si la respuesta es errada
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "Error receiving server response", e);
                toastErrorCatchingData.show();
            }

            //Si la respuesta es acertada
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    toastUnexpectedResponse.show();
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
                                id = json.getInt("id");
                                quantity = json.getInt("quantidade_atual");
                                goToDashboard();
                            } catch (JSONException e) {
                                toastErrorCatchingData.show();
                                e.printStackTrace();
                            }
                        }
                    } else {
                        toastInvalidDataServer.show();
                        Log.e("TAG", "Error catching data");
                    }
                }
                catch (Exception e) {
                    toastErrorCatchingData.show();
                    Log.e("TAG", "Error catching data", e);
                }
            }
        });
    }

    private void goToDashboard(){
        Intent intent = new Intent(QrCodeActivity.this,DashboardActivity.class);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putInt(DashboardActivity.ID, id);
        edit.putInt(DashboardActivity.QUANTITY, quantity);
        edit.commit();
        startActivity(intent);
    }
    private void goToDashboardWithOutData(){
        Intent intent = new Intent(QrCodeActivity.this,DashboardActivity.class);
        startActivity(intent);
    }
}