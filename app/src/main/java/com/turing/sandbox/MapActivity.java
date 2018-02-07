package com.turing.sandbox;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/*import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;*/

import java.util.HashMap;
import java.util.Map;

/**
 * Created by smp_3 on 12/10/2017.
 */

public class MapActivity extends AppCompatActivity
{
    //private MapView mapa;
    //private IMapController mapController;
    private Ubicaciones ubicaciones;
    //private Marker ubicacionActual;
    private TextView tvLatitud, tvLongitud;
    private Comunicaciones com;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_maps);

        Intent i = new Intent(MapActivity.this, ServicioUbicaciones.class);
        startService(i);

        //mapa = (MapView) findViewById(R.id.map);
        //mapa.setTileSource(TileSourceFactory.MAPNIK);
        tvLatitud = (TextView) findViewById(R.id.tv_latitud);
        tvLongitud= (TextView) findViewById(R.id.tv_longitud);

        //mapa.setBuiltInZoomControls(true);
        //mapa.setMultiTouchControls(true);

       // mapController = mapa.getController();
        //mapController.setZoom(16);

        ubicaciones = Ubicaciones.getInstance();
        com = new Comunicaciones(getApplicationContext());

        ubicaciones.setDataChangeListener(new Ubicaciones.OnDataChangeListener() {
            @Override
            public void onDataChange(Double latitud, Double longitud) {
                /*GeoPoint ubicacion = new GeoPoint(latitud, longitud);
                ubicacionActual = new Marker(mapa);
                ubicacionActual.setPosition(ubicacion);
                ubicacionActual.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);//Primer parametro es horizontalmente, el segundo verticalmente
                ubicacionActual.setTitle("Mi Ubicacion");
                ubicacionActual.setSubDescription(":v");
                mapController.setCenter(ubicacion);
                mapa.getOverlays().clear();
                mapa.getOverlays().add(ubicacionActual);*/
                final Double lati = latitud, longi = longitud;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvLatitud.setText("Latitud: " + lati);
                        tvLongitud.setText("Longitud: " + longi);
                    }
                });

                Map<String, String> paramsMovements = new HashMap<String, String>();
                paramsMovements.put("idpaseo", "1");
                paramsMovements.put("latitud", latitud + "");
                paramsMovements.put("longitud", longitud + "");

                //com.newRegister("http://sandbox.claresti.com/Pets/ws/insertar_ubicacion.php", paramsMovements);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}
