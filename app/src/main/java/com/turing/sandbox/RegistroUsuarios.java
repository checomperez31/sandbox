package com.turing.sandbox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistroUsuarios.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistroUsuarios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroUsuarios extends Fragment implements View.OnClickListener, InterfaceData{

    private String login, firstName, lastName, email;

    private final String TAG = "REGISTRO";

    private OnFragmentInteractionListener mListener;

    public RegistroUsuarios() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegistroUsuarios.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistroUsuarios newInstance() {
        RegistroUsuarios fragment = new RegistroUsuarios();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_registro_usuarios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.registro_btnregistrar).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registro_btnregistrar:{
                login = ((TextInputEditText)getView().findViewById(R.id.registro_etusuario)).getText().toString();
                firstName = ((TextInputEditText)getView().findViewById(R.id.registro_etfirstName)).getText().toString();
                lastName = ((TextInputEditText)getView().findViewById(R.id.registro_etlastName)).getText().toString();
                email = ((TextInputEditText)getView().findViewById(R.id.registro_etemail)).getText().toString();

                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("login", login);
                    jsonObject.put("firstName", firstName);
                    jsonObject.put("lastName", lastName);
                    jsonObject.put("email", email);

                }catch(JSONException jsone) {
                    Log.i(TAG, jsone.getMessage());
                }

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-type", "application/json");

                Comunicaciones com = new Comunicaciones(getActivity());
                com.getSomethingJSON(Constants.url + Constants.userdata, Request.Method.POST, jsonObject, headers, this);
                break;
            }
        }
    }

    @Override
    public void mostrarDatos(String datos) {

    }

    @Override
    public void mostrarDatos(JSONObject datos) {
        Log.i(TAG, datos.toString());
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
        void onFragmentInteraction(Uri uri);
    }
}
