package com.turing.sandbox;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by usuario on 1/31/2018.
 */

public class ProgressDialog extends Dialog {

    TextView mensaje;
    ProgressBar progress;
    ImageView icono;
    Drawable valido, invalido;
    Button btnOk;
    int displayRotation = 0;

    public ProgressDialog(Context context){
        super(context);
        create();
    }

    public ProgressDialog(Context context, int displayRotation){
        super(context);
        this.displayRotation = displayRotation;
        create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(displayRotation == 0){
            setContentView(R.layout.progress_dialog_alert);
        }else{
            setContentView(R.layout.progress_dialog_alert_land);
        }

        setCancelable(false);
        mensaje = findViewById(R.id.dialog_mensaje);
        progress = findViewById(R.id.dialog_progressbar);
        icono = findViewById(R.id.dialog_icono);
        btnOk = findViewById(R.id.dialog_btnOK);
        valido = getContext().getDrawable(R.drawable.ic_check);
        valido.setTint(Color.GREEN);
        invalido = getContext().getResources().getDrawable(R.drawable.ic_cancel);
        invalido.setTint(Color.RED);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setMensaje(String msn){
        mensaje.setText(msn);
    }

    public void setValid(){
        progress.setVisibility(View.GONE);
        icono.setImageDrawable(valido);
        icono.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.VISIBLE);
        mensaje.setText("Asistencia Registrada");
    }

    public void setInvalid(){
        progress.setVisibility(View.GONE);
        icono.setImageDrawable(invalido);
        icono.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.VISIBLE);
        mensaje.setText("Reconocimiento Facial Fallido :(");
    }
}
