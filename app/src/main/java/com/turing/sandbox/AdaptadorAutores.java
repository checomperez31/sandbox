package com.turing.sandbox;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Adaptador
 */

public class AdaptadorAutores extends RecyclerView.Adapter<AdaptadorAutores.ViewHolderAutores> implements InterfaceData{

    private ArrayList<Autor> autores;
    private Context context;

    public AdaptadorAutores(Context context){
        autores = new ArrayList<>();
        this.context = context;
        if(VolleySingleton.getInstance(context).getToken() != null) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-type", "application/json");
            headers.put("Authorization", "Bearer " + VolleySingleton.getInstance(this.context).getToken());
            Comunicaciones com = new Comunicaciones(this.context);
            com.getSomethingString(Constants.url + Constants.autores,
                    new HashMap<String, String>(),
                    headers,
                    this
            );
        }
    }

    @Override
    public AdaptadorAutores.ViewHolderAutores onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_autor, null, false);
        return new AdaptadorAutores.ViewHolderAutores(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderAutores holder, int position) {
        holder.nombre.setText(autores.get(position).getNombre());
        holder.edad.setText(autores.get(position).getEdad());
        holder.numero.setText(autores.get(position).getNumero());
    }

    @Override
    public int getItemCount() {
        return autores.size();
    }

    @Override
    public void mostrarDatos(String datos) {
        try{
            Gson gson = new Gson();
            JSONArray array = new JSONArray(datos);
            for(int i = 0; i < array.length(); i++){
                autores.add(gson.fromJson(array.get(i).toString(), Autor.class));
                Log.i("JSON", array.get(i).toString());
                notifyDataSetChanged();
            }
        }
        catch (JSONException jsone){
            Log.i("JSON", jsone.getMessage());
        }

    }

    public class ViewHolderAutores extends RecyclerView.ViewHolder{
        private TextView nombre;
        private TextView edad;
        private TextView numero;
        public ViewHolderAutores(View itemView) {
            super(itemView);
            nombre  = itemView.findViewById(R.id.viewholder_nombre);
            edad = itemView.findViewById(R.id.viewholder_edad);
            numero = itemView.findViewById(R.id.viewholder_numero);
        }
    }
}
