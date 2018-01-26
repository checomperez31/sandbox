package com.turing.sandbox;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;

/**
 * Created by usuario on 1/18/2018.
 */

public class Login extends AppCompatActivity implements InterfaceData, InterfaceLogin {

    TextInputEditText usuario, password;
    Button btnIniciar;
    private final int MY_PERMISSIONS = 100;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usuario = findViewById(R.id.login_etusuario);
        password =  findViewById(R.id.login_etpassword);
        btnIniciar = findViewById(R.id.login_btninicio);

        //Pedimos Permisos si no se han otorgado
        requierePermiso();

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> params = new HashMap<String, String>();
                params.put("username", usuario.getText().toString());
                params.put("password", password.getText().toString());
                JSONObject JsonParams = new JSONObject(params);
                Log.i("JSON", params + " " + JsonParams.toString());
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-type", "application/json");
                Comunicaciones com = new Comunicaciones(getApplicationContext());
                com.getSomethingJSON(Constants.url + Constants.autenticar,
                        JsonParams,
                        headers,
                        Login.this
                );
            }
        });
    }

    @Override
    public void mostrarDatos(String datos) {
        Intent i = new Intent(Login.this, Camara.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Login.this.startActivity(i);
    }

    @Override
    public void obtenerToken(JSONObject json) {
        Log.i("JSON", json.toString());
        try {
            if (json.has("id_token")) {
                VolleySingleton.getInstance(getApplicationContext()).setToken(json.getString("id_token"));
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-type", "application/json");
                headers.put("Authorization", "Bearer " + VolleySingleton.getInstance(getApplicationContext()).getToken());
                Comunicaciones com = new Comunicaciones(getApplicationContext());
                com.getSomethingString(Constants.url + Constants.userdata,
                        new HashMap<String, String>(),
                        headers,
                        this
                );
            }
            else{
                //NO s pudo obtener el json
            }
        }
        catch(JSONException jsone){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(Login.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
            }
        }else{
            showExplanation();
        }
    }

    public boolean requierePermiso()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) && (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) && (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(getWindow().getDecorView().getRootView() , "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            }).show();
        }else{
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void showExplanation() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Login.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }
}
