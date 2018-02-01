package com.turing.sandbox;

import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BlankFragment.OnFragmentInteractionListener,
        EditTextFragment.OnFragmentInteractionListener,
        RESTFragment.OnFragmentInteractionListener,
        ListaAutores.OnFragmentInteractionListener{

    BlankFragment blankFragment;
    EditTextFragment editTextFragment;
    RESTFragment restFragment;
    ListaAutores fragmentAutores;
    CameraFragment cameraFragment;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentByTag("Camera");

        blankFragment = new BlankFragment();
        fragmentAutores = ListaAutores.newInstance();
        editTextFragment = EditTextFragment.newInstance();
        restFragment = new RESTFragment();

        if(cameraFragment == null) {
            cameraFragment = new CameraFragment();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.FragmentContent, BlankFragment.newInstance()).commit();
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
        getSupportActionBar().show();
        if (id == R.id.nav_camera) {
            transaction.replace(R.id.FragmentContent, blankFragment, "Blank Fragment");
        } else if (id == R.id.nav_gallery) {
            transaction.replace(R.id.FragmentContent, editTextFragment, "Fragment ET");
        } else if (id == R.id.nav_slideshow) {
            transaction.replace(R.id.FragmentContent, restFragment, "GET");
        } else if (id == R.id.nav_manage) {
            transaction.replace(R.id.FragmentContent, fragmentAutores, "Autores");
        } else if (id == R.id.nav_share) {
            getSupportActionBar().hide();
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        id = savedInstanceState.getInt("ID");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_camera) {
            transaction.replace(R.id.FragmentContent, blankFragment, "Blank Fragment");
        } else if (id == R.id.nav_gallery) {
            transaction.replace(R.id.FragmentContent, editTextFragment, "Fragment ET");
        } else if (id == R.id.nav_slideshow) {
            transaction.replace(R.id.FragmentContent, restFragment, "GET");
        } else if (id == R.id.nav_manage) {
            transaction.replace(R.id.FragmentContent, fragmentAutores, "Autores");
        } else if (id == R.id.nav_share) {
            getSupportActionBar().hide();
            transaction.replace(R.id.FragmentContent, cameraFragment, "Camera");
        }
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("ID", id);
        super.onSaveInstanceState(outState);
    }
}
