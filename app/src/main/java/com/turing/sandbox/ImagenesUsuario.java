package com.turing.sandbox;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImagenesUsuario.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImagenesUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagenesUsuario extends Fragment implements InterfaceData{

    private OnFragmentInteractionListener mListener;

    ProgressDialog progress;

    String hora, usuario, fecha, ubicacion;
    private static Bitmap imagen;
    TextInputEditText etusuario, etfecha, ethora;
    ImageView imagenUsuario;
    Button btnListo;
    private final String TAG = "IMUSER";
    private Date date;


    public ImagenesUsuario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImagenesUsuario.
     */
    public static ImagenesUsuario newInstance() {
        ImagenesUsuario fragment = new ImagenesUsuario();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static ImagenesUsuario newInstance(Bitmap bitmap) {
        ImagenesUsuario fragment = new ImagenesUsuario();
        imagen = bitmap;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_imagenes_usuario, container, false);
        imagenUsuario = view.findViewById(R.id.asistencia_imagenpersona);
        etusuario = view.findViewById(R.id.asistencia_etusuario);
        etfecha = view.findViewById(R.id.asistencia_etfecha);
        ethora = view.findViewById(R.id.asistencia_ethora);
        btnListo = view.findViewById(R.id.asistencia_btnListo);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configurarControles();

    }

    private void configurarControles(){
        if(imagen != null){
            imagenUsuario.setImageBitmap(imagen);
        }

        date = Calendar.getInstance().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = sdf.format(new Date());

        etfecha.setText(currentDateandTime);
        etfecha.setEnabled(false);

        sdf = new SimpleDateFormat("hh:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        currentDateandTime = sdf.format(new Date());

        ethora.setText(currentDateandTime);
        ethora.setEnabled(false);

        btnListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(getActivity());
                progress.show();
                progress.setMensaje("Procesando");
                if(imagen != null){

                    usuario = etusuario.getText().toString().trim();

                    if(usuario.equals("") || usuario == null){
                        progress.setInvalid();
                        progress.setMensaje("No has Ingresado tu ID");
                        return;
                    }

                    imagenUsuario.setImageBitmap(imagen);

                    String base64Image = "";

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagen.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bitmapdata = baos.toByteArray();
                    base64Image = Base64.encodeToString(bitmapdata, Base64.NO_WRAP);

                    Log.d(TAG, base64Image);

                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-type", "application/json");
                    JSONObject json = new JSONObject();
                    try{
                        json.put("fecha", "2018-01-10");
                        json.put("ubicacion", "ubicacion");
                        json.put("asistenciasuserId", usuario);
                        json.put("imagen", base64Image);
                    }
                    catch(JSONException jsone){

                    }

                    Comunicaciones com = new Comunicaciones(getActivity(), ImagenesUsuario.this);
                    com.getSomethingJSON(
                            Constants.url + Constants.asistencia,
                            Request.Method.POST,
                            json,
                            headers,
                            ImagenesUsuario.this
                    );
                }
                else{
                    progress.setInvalid();
                    progress.setMensaje("No hay imagen para comparar");
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        View view = inflater.inflate(R.layout.fragment_imagenes_usuario, viewGroup);
        imagenUsuario = view.findViewById(R.id.asistencia_imagenpersona);
        etusuario = view.findViewById(R.id.asistencia_etusuario);
        etfecha = view.findViewById(R.id.asistencia_etfecha);
        ethora = view.findViewById(R.id.asistencia_ethora);
        btnListo = view.findViewById(R.id.asistencia_btnListo);

        Log.i(TAG, imagen.toString());

        configurarControles();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void agregarImagen(){

    }

    @Override
    public void mostrarDatos(String datos) {

    }

    @Override
    public void mostrarDatos(JSONObject datos) {
        Log.i("DATA", datos.toString());
        final Activity activity = getActivity();
        if(datos.has("validado")){
            try {
                boolean val = (boolean) datos.get("validado");
                if (val) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setValid();
                        }
                    });
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setInvalid();
                        }
                    });
                }
            }
            catch(JSONException jsone)
            {

            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
