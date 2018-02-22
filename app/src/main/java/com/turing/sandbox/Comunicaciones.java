package com.turing.sandbox;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by smp_3 on 15/01/2018.
 */

public class Comunicaciones {
    Context context;
    InterfaceData mListener;

    public Comunicaciones(Context context, InterfaceData listener)
    {
        this.context = context;
        mListener = listener;
    }

    public void getSomethingJSON(String url, int method, JSONObject params, final Map<String, String> headers, final InterfaceData interfaceData)
    {
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                method,
                url,
                params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        interfaceData.mostrarDatos(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.i("Error", "Error: " + error.getMessage());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mParams = headers;
                return mParams;
            }


        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        VolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }

    public void getSomethingString(String url, int method, final JSONObject param, final Map<String, String> headers, final InterfaceData adapter)
    {
        StringRequest stringRequest = new StringRequest(
                method,
                url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        adapter.mostrarDatos(response);
                        Log.i("DCOM", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("DCOM", error.getMessage());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = headers;
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return super.getBody();
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        VolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }

    public void registrarAsistencia(String url, final JSONObject params, final Map<String, String> headers, final InterfaceData interfaceDace)
    {
        final Gson gson = new Gson();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.url + Constants.userdata + "/checo",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("RES", response);
                        try{
                            JSONObject user = new JSONObject(response);
                            params.put("user", user);
                        }
                        catch(JSONException jsone){

                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.i("Error", "Error: " + error.getMessage());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mParams = headers;
                return mParams;
            }

            /*@Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = paramsMap;
                return params;
            }*/
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        Log.i("RRR", stringRequest.toString() + "");
        VolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }
}
