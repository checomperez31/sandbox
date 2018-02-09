package com.turing.sandbox;

import android.content.Context;
import android.content.Intent;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAsistencias.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAsistencias#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAsistencias extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextInputEditText etusuario;
    private Button btnRegistro;
    private Ubicaciones ubicaciones;
    private Intent servicioUbicaciones;

    public FragmentAsistencias() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FragmentAsistencias.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAsistencias newInstance() {
        FragmentAsistencias fragment = new FragmentAsistencias();
        Bundle args = new Bundle();
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

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_asistencias, container, false);
        etusuario = view.findViewById(R.id.asistencia_etusuario);
        btnRegistro = view.findViewById(R.id.asistencias_btnRegistro);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCallToCamera(Integer.parseInt(etusuario.getText().toString()));
            }
        });

        ubicaciones = Ubicaciones.getInstance();
        ubicaciones.setDataChangeListener(new Ubicaciones.OnDataChangeListener() {
            @Override
            public void onDataChange(Double latitud, Double longitud) {
                Log.i("UBICACION", "Latitud: " + latitud + "\nLongitud: " + longitud);
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
    public void onResume() {
        super.onResume();
        servicioUbicaciones = new Intent(getActivity(), ServicioUbicaciones.class);
        getActivity().startService(servicioUbicaciones);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().stopService(servicioUbicaciones);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

        void onCallToCamera(int userId);
    }
}
