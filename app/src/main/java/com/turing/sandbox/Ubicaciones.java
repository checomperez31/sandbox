package com.turing.sandbox;

/**
 * Created by smp_3 on 12/10/2017.
 */

public class Ubicaciones
{
    private static Ubicaciones ubicaciones;
    private OnDataChangeListener listener;
    public Double latitud = 0.0, longitud = 0.0;

    public Ubicaciones()
    {
        this.listener = null;
    }

    public static synchronized Ubicaciones getInstance()
    {
        if(ubicaciones == null)
        {
            ubicaciones = new Ubicaciones();
        }
        return ubicaciones;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
        if(listener != null)
        {
            listener.onDataChange(this.latitud, this.longitud);
        }
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
        if(listener != null)
        {
            listener.onDataChange(this.latitud, this.longitud);
        }
    }

    public void setDataChangeListener(OnDataChangeListener onDataChangeListener)
    {
        this.listener = onDataChangeListener;
    }

    public interface OnDataChangeListener
    {
        public void onDataChange(Double latitud, Double longitud);
    }
}
