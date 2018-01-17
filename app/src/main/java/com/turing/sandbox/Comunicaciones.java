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
import com.android.volley.toolbox.JsonObjectRequest;
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

    public void getSomething(String url, JSONObject params)
    {
        final Gson gson = new Gson();
        //final Map<String, String> paramsMap = paramsGetData;
        //final Map<String, String> headersMap = headers;
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                url,
                params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.i("JSON", response.toString());
                        try {
                            if (response.has("id_token")) {
                                VolleySingleton.getInstance(context).setToken(response.getString("id_token"));
                            }
                            else{

                            }
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
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = headersMap;
                return params;
            }

            @Override
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
        Log.i("RRR", stringRequest + "");
        VolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }
}
