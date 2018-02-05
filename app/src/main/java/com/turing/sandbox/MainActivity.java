package com.turing.sandbox;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BlankFragment.OnFragmentInteractionListener,
        EditTextFragment.OnFragmentInteractionListener,
        RESTFragment.OnFragmentInteractionListener,
        ListaAutores.OnFragmentInteractionListener,
        FragmentAsistencias.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener{

    BlankFragment blankFragment;
    EditTextFragment editTextFragment;
    RESTFragment restFragment;
    ListaAutores fragmentAutores;
    CameraFragment cameraFragment;
    FragmentAsistencias fragmentAsistencias;
    int id;
    boolean showCamera = false;

    private final int MY_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        *Manejo de sesiones
        UserSessionManager session;
        session = new UserSessionManager(getApplicationContext());
        if(session.isUserLoggedIn()){
            Intent i = new Intent(getApplicationContext(),MainDenarius.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }


        //Manejo de Session
        session = new UserSessionManager(getApplicationContext());

        if(!session.isUserLoggedIn()) finish();


        UserSessionManager session;
                                            session = new UserSessionManager(context);

                                            session.createUserLoginSession(usuario.getIdUsuario(), usuario.getUsuario(), usuario.getNombre(), usuario.getCorreo(), usuario.getSexo(), usuario.getFecha_Nac());

                                            Intent i = new Intent(context, MainDenarius.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         context.startActivity(i);

         */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requierePermiso();

        cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentByTag("Camera");
        fragmentAsistencias = (FragmentAsistencias) getSupportFragmentManager().findFragmentByTag("Asistencias");

        blankFragment = new BlankFragment();
        fragmentAutores = ListaAutores.newInstance();
        editTextFragment = EditTextFragment.newInstance();
        restFragment = new RESTFragment();

        if(cameraFragment == null) {
            cameraFragment = new CameraFragment();
            fragmentAsistencias = FragmentAsistencias.newInstance();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.FragmentContent, BlankFragment.newInstance()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(showCamera){
            getSupportActionBar().show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_VISIBLE
            );
            showCamera = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, fragmentAsistencias, "Asistencias").commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        id = item.getItemId();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_camera) {
            transaction.replace(R.id.FragmentContent, blankFragment, "Blank Fragment");
        } else if (id == R.id.nav_gallery) {
            transaction.replace(R.id.FragmentContent, editTextFragment, "Fragment ET");
        } else if (id == R.id.nav_slideshow) {
            transaction.replace(R.id.FragmentContent, restFragment, "GET");
        } else if (id == R.id.nav_manage) {
            transaction.replace(R.id.FragmentContent, fragmentAutores, "Autores");
        } else if (id == R.id.nav_asistencia) {
            if(!showCamera) {
                transaction.replace(R.id.FragmentContent, fragmentAsistencias, "Asistencias");
            }else{
                transaction.replace(R.id.FragmentContent, cameraFragment, "AsistCamera");
            }
        } else if (id == R.id.nav_share) {
            getSupportActionBar().hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            transaction.replace(R.id.FragmentContent, cameraFragment, "Camera");
        } else if (id == R.id.nav_send) {

        }

        transaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCallToCamera(int userId) {
        Log.i("CTC", userId + "");
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        showCamera = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle args = new Bundle();
        args.putInt("IdUser", userId);
        cameraFragment = CameraFragment.newInstance();
        cameraFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, cameraFragment, "CameraAsist").commit();
    }

    @Override
    public void onCallBack(int userId) {
        Log.i("CTC", userId + "");
        getSupportActionBar().show();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
        );
        showCamera = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, fragmentAsistencias, "Asistencias").commit();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        id = savedInstanceState.getInt("ID");
        showCamera = savedInstanceState.getBoolean("SHOWCAM");
        getSupportActionBar().show();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_camera) {
            transaction.replace(R.id.FragmentContent, blankFragment, "Blank Fragment");
        } else if (id == R.id.nav_gallery) {
            transaction.replace(R.id.FragmentContent, editTextFragment, "Fragment ET");
        } else if (id == R.id.nav_slideshow) {
            transaction.replace(R.id.FragmentContent, restFragment, "GET");
        } else if (id == R.id.nav_manage) {
            transaction.replace(R.id.FragmentContent, fragmentAutores, "Autores");
        } else if (id == R.id.nav_asistencia) {
            if(!showCamera) {
                transaction.replace(R.id.FragmentContent, fragmentAsistencias, "Asistencias");
            }else{
                getSupportActionBar().hide();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                );
                transaction.replace(R.id.FragmentContent, cameraFragment, "AsistCamera");
            }
        } else if (id == R.id.nav_share) {
            transaction.replace(R.id.FragmentContent, cameraFragment, "Camera");
        }
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("ID", id);
        outState.putBoolean("SHOWCAM", showCamera);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            Log.i("OR", "LAND");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i("OR", "PORT");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
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
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
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
