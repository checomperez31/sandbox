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
import android.widget.Button;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RESTFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RESTFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RESTFragment extends Fragment implements InterfaceLogin{
    Button btnRest;
    TextInputEditText usuario, password;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RESTFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RESTFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RESTFragment newInstance(String param1, String param2) {
        RESTFragment fragment = new RESTFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rest, container, false);
        usuario = view.findViewById(R.id.fragment_rest_etusuario);
        password = view.findViewById(R.id.fragment_rest_etpassword);
        btnRest = view.findViewById(R.id.btnRest);
        return  view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VolleySingleton.getInstance(getContext()).getToken() == null){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", usuario.getText().toString());
                    params.put("password", password.getText().toString());
                    JSONObject JsonParams = new JSONObject(params);
                    Log.i("JSON", params + " " + JsonParams.toString());
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-type", "application/json");
                    Comunicaciones com = new Comunicaciones(getContext());
                    com.getSomethingJSON(Constants.url + Constants.autenticar,
                            new JSONObject(params),
                            headers,
                            RESTFragment.this
                    );
                }

            }
        });
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

    @Override
    public void obtenerToken(JSONObject json) {

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
