package com.turing.sandbox;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by smp_3 on 15/01/2018.
 */

public class Comunicaciones {
    Context context;

    public Comunicaciones(Context context)
    {
        this.context = context;
    }

    public void getSomething(String url, Map<String, String> paramsGetData)
    {
        final Gson gson = new Gson();
        final Map<String, String> paramsMap = paramsGetData;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("JSON", response);
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("response", "Respuesta: " + response);
                            //String state = jsonObject.getString("state");

                        }
                        catch(JSONException jsone)
                        {
                            Log.i("Error", "Error: " + jsone.getMessage());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.i("Error", "Error");
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                //params.put("X-Mashape-Key", "ZqWMJ3kJJZmshZKNoQNcZ0zkhiLip1YPE0Fjsn73dfYjpxAGiG");
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Token token=\"MDowN2YxODQ4OC1mYTdmLTExZTctODU1Yy0zYjRhNWFiMmQ5YWM6MGZWT0dBb1A5dVA3WVpOQTVhYlFlZGtsZVc4OTFsQkhVNmYw\"");
                //params.put("Username", "x-access-key");
                //params.put("Password", "MDowN2YxODQ4OC1mYTdmLTExZTctODU1Yy0zYjRhNWFiMmQ5YWM6MGZWT0dBb1A5dVA3WVpOQTVhYlFlZGtsZVc4OTFsQkhVNmYw");
                return params;
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
        requestQueue.add(stringRequest);
    }
}
