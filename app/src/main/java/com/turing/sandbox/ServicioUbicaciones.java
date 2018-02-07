package com.turing.sandbox;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by smp_3 on 12/10/2017.
 */

public class ServicioUbicaciones extends Service
{
    public final String TAG = "UBS";
    HiloUbicacion hilo;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "Servicio Creado");
        hilo = new HiloUbicacion();
        hilo.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "Servicio Destruido");
        hilo.cancel(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class HiloUbicacion extends AsyncTask
    {
        FallbackLocationTracker locationTracker;
        Ubicaciones ubicaciones;
        private Double latitud = 0.0, longitud = 0.0;

        public HiloUbicacion()
        {
            ubicaciones = Ubicaciones.getInstance();
            locationTracker = new FallbackLocationTracker(getApplicationContext());
            locationTracker.start();
        }

        @Override
        protected Object doInBackground(Object[] params)
        {
            int tiempo = 1000;
            try
            {
                Log.i(TAG, "Iniciando localizacion");
                while(true)
                {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return 0;
                    }
                    Location location = locationTracker.getLocation();
                    if(location != null)
                    {

                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        ubicaciones.setLatitud(latitud);
                        ubicaciones.setLongitud(longitud);

                        tiempo = 15000;
                    }
                    Thread.sleep(tiempo);

                }
            }
            catch(InterruptedException ie)
            {
                Log.e(TAG, "No hay ubicacion");
            }

            return null;
        }

    }
}
