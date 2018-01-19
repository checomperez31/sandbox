package com.turing.sandbox;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by smp_3 on 19/01/2018.
 */

public class Prueba {
    private static String APP_DIRECTORY = "FindMyPet";//directorio principal
    private static String MEDIA_DIRECTORY = "Perdidos";//subdirectorio para fotos de mascotas perdidas

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    /*private String mPath;

    private FloatingActionButton agregarImg;
    private ImageView ivImagen;
    private EditText nombre, raza, colorm, edad, peso, altura, estado, municipio, notas, datosUsuario, datosEmail, datosTelefono;
    private DatePicker fecha;
    private Button btnListo;
    private Bitmap imgBitmap;
    private TextInputLayout lnombre, lraza, lcolorm, ledad, lpeso, laltura, lestado, lmunicipio, lnotas;
    private static final String TAG = Perdidos.class.getSimpleName();
    private String idUsuario, nombreUsuario, datosemail, datostelefono;
    private String nombreImagen;
    private boolean mod = false;
    private int dia = 1, mes = 1, anio = 2016;
    private ObjetoPerdidos obj;
    private RadioButton macho, hembra;
    private boolean imgSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perdidos_layout);

        UserSessionManager session;
        //session class instance
        session = new UserSessionManager(getApplicationContext());
        //Check user login
        // if user is not logged in, this will redirect user to login activity
        if(session.checkLogin()) finish();
        //get user data from session
        HashMap<String,String> user = session.getUserDetails();

        idUsuario = user.get(UserSessionManager.KEY_ID);
        nombreUsuario = user.get(UserSessionManager.KEY_NOMC);
        datosemail = user.get(UserSessionManager.KEY_EMAIL);
        datostelefono = user.get(UserSessionManager.KEY_TEL);

        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.RPtoolbar);
        setSupportActionBar(toolbar);

        //Boton y imageviewer de la imagen de la mascorta perdida
        agregarImg = (FloatingActionButton) findViewById(R.id.RPbtnImg);
        ivImagen = (ImageView) findViewById(R.id.RPIVimgMascota);

        //Elementos para mandar llamar el ObjetoPerdidos
        nombre = (EditText) findViewById(R.id.RPETNombre);
        macho = (RadioButton) findViewById(R.id.PRBM);
        hembra = (RadioButton) findViewById(R.id.PRBH);
        raza = (EditText) findViewById(R.id.RPETRaza);
        colorm = (EditText) findViewById(R.id.RPETColor);
        edad = (EditText) findViewById(R.id.RPETEdad);
        peso = (EditText) findViewById(R.id.RPETPeso);
        altura = (EditText) findViewById(R.id.RPETAltura);
        estado = (EditText) findViewById(R.id.RPETEstado);
        municipio = (EditText) findViewById(R.id.RPETMunicipio);
        fecha = (DatePicker) findViewById(R.id.RPDPFecha);
        notas = (EditText) findViewById(R.id.RPETNotas);
        lnombre = (TextInputLayout) findViewById(R.id.RPTILNombre);
        lraza = (TextInputLayout) findViewById(R.id.RPTILRaza);
        lcolorm = (TextInputLayout) findViewById(R.id.RPTILColor);
        ledad = (TextInputLayout) findViewById(R.id.RPTILEdad);
        lpeso = (TextInputLayout) findViewById(R.id.RPTILPeso);
        laltura = (TextInputLayout) findViewById(R.id.RPTILAltura);
        lestado = (TextInputLayout) findViewById(R.id.RPTILEstado);
        lmunicipio = (TextInputLayout) findViewById(R.id.RPTILMunicipio);
        lnotas = (TextInputLayout) findViewById(R.id.RPTILNotas);
        btnListo = (Button) findViewById(R.id.RPbtnListo);

        datosEmail = (EditText) findViewById(R.id.PETCorreoElectronico);
        datosUsuario = (EditText) findViewById(R.id.PETNombreUsuario);
        datosTelefono = (EditText) findViewById(R.id.PETTelefono);

        datosEmail.setText(datosemail);
        datosTelefono.setText(datostelefono);
        datosUsuario.setText(nombreUsuario);

        datosEmail.setEnabled(false);
        datosTelefono.setEnabled(false);
        datosUsuario.setEnabled(false);

        //confirmamos que existan los permisos necesarios
        if(requierePermiso())
        {
            agregarImg.setEnabled(true);
        }
        else
        {
            agregarImg.setEnabled(false);
        }

        if(!mod)
        {
            Calendar fechaActual = Calendar.getInstance();
            anio = fechaActual.get(Calendar.YEAR);
            mes = fechaActual.get(Calendar.MONTH);
            dia = fechaActual.get(Calendar.DATE);
        }

        fecha.init(anio, mes, dia, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,int dayOfMonth) {

                if(isDateAfter(view)){
                    Calendar mCalendar = Calendar.getInstance();
                    view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
                }
            }


            private boolean isDateAfter(DatePicker tempView) {
                Calendar mCalendar = Calendar.getInstance();
                Calendar tempCalendar = Calendar.getInstance();
                tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(), 0, 0, 0);
                if(tempCalendar.after(mCalendar))
                    return true;
                else
                    return false;
            }
        });

        agregarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions();
            }
        });

        btnListo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                comprobarFormulario(view);
            }
        });


        if(getIntent().getExtras().getString("Estado").equals("Ver"))
        {
            obj = (ObjetoPerdidos)getIntent().getExtras().getSerializable("Objeto");
            datosEmail.setText(obj.getEmail());
            datosTelefono.setText(obj.getTelefono());
            datosUsuario.setText(obj.getNombrecompleto());
            nombre.setText(obj.getNombre());
            raza.setText(obj.getRaza());
            colorm.setText(obj.getColores());
            edad.setText(obj.getEdad());
            byte[] decodeString = Base64.decode(obj.getImagen(), Base64.NO_WRAP);
            Bitmap img = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            ivImagen.setImageBitmap(img);
            peso.setText(obj.getPeso());
            altura.setText(obj.getAltura());
            estado.setText(obj.getEstado());
            municipio.setText(obj.getMunicipio());
            StringTokenizer st = new StringTokenizer(obj.getFecha(), "-");
            anio = Integer.parseInt(st.nextToken());
            mes  = Integer.parseInt(st.nextToken()) - 1;
            dia = Integer.parseInt(st.nextToken());
            fecha.updateDate(anio, mes, dia);
            notas.setText(obj.getNotas());
            mod = true;
            imgSelected = true;
            if(obj.getSexo().equals("Macho"))
            {
                macho.setChecked(true);
            }
            else
            {
                hembra.setChecked(true);
            }

            if(!getIntent().getExtras().getString("Mod").equals("Y"))
            {
                agregarImg.setVisibility(View.INVISIBLE);
                hembra.setEnabled(false);
                macho.setEnabled(false);
                nombre.setEnabled(false);
                raza.setEnabled(false);
                colorm.setEnabled(false);
                edad.setEnabled(false);
                peso.setEnabled(false);
                altura.setEnabled(false);
                estado.setEnabled(false);
                municipio.setEnabled(false);
                fecha.setEnabled(false);
                notas.setEnabled(false);
                btnListo.setVisibility(View.INVISIBLE);
            }
        }
    }*/

    /*public void comprobarFormulario(View v)
    {
        if(!comprobarNombre())
        {
            return;
        }
        if(!comprobarRaza())
        {
            return;
        }
        if(!comprobarColores())
        {
            return;
        }
        if(!comprobarEdad())
        {
            return;
        }

        String imagenString = "";
        if(imgSelected)
        {
            try
            {
                imgBitmap = ((BitmapDrawable)ivImagen.getDrawable()).getBitmap();
                imgBitmap = redimensionarImagen(imgBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                byte[] bytearray = stream.toByteArray();
                imagenString = Base64.encodeToString(bytearray, Base64.NO_WRAP);
                stream.close();
            }
            catch(IOException ioe)
            {
                Toast.makeText(getApplicationContext(), "No se ha podido cargar la imagen, intente de nuevo", Toast.LENGTH_LONG).show();
                ioe.printStackTrace();
                return;
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Selecciona una imagen para la publicacion", Toast.LENGTH_LONG).show();
            return;
        }
        final String stringImagen = imagenString;


        int anio = fecha.getYear();
        int mes = fecha.getMonth() + 1;
        int dia = fecha.getDayOfMonth();*/
        /*final String fechaString = anio + "-" + mes + "-" + dia;


        Long timestamp = System.currentTimeMillis()/1000;
        nombreImagen = timestamp.toString();

        String sexo;
        if(macho.isChecked())
        {
            sexo = "Macho";
        }
        else if(hembra.isChecked())
        {
            sexo = "Hembra";
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Selecciona el sexo de la mascota", Toast.LENGTH_LONG).show();
            return;
        }

        ObjetoPerdidos objPer = new ObjetoPerdidos(nombre.getText().toString(), sexo, raza.getText().toString(), colorm.getText().toString(),
                edad.getText().toString(), peso.getText().toString(), peso.getText().toString(), stringImagen, nombreImagen, estado.getText().toString(),
                municipio.getText().toString(), fechaString, notas.getText().toString(), "Perdido", idUsuario);

        if(mod)
        {
            objPer.setID(obj.getID());
            Modificaciones mf = new Modificaciones(Perdidos.this, objPer);
            mf.execute();
            finish();
        }
        else
        {
            ProgressDialogInBackground pb = new ProgressDialogInBackground(Perdidos.this, objPer);
            pb.execute();
            finish();
        }
    }



    public boolean comprobarNombre()
    {
        if(nombre.getText().toString().trim().isEmpty())
        {
            lnombre.setError("Ingresa su Nombre para la Publicación");
            lnombre.requestFocus();
            return false;
        }
        else
        {
            lnombre.setErrorEnabled(false);
        }
        return true;
    }

    public boolean comprobarRaza()
    {
        if(raza.getText().toString().trim().isEmpty())
        {
            lraza.setError("Ingresa la Raza para la Publicación");
            lraza.requestFocus();
            return false;
        }
        else
        {
            lraza.setErrorEnabled(false);
        }
        return true;
    }

    public boolean comprobarColores()
    {
        if(colorm.getText().toString().trim().isEmpty())
        {
            lcolorm.setError("Ingresa el(los) color(es) para la Publicación");
            lcolorm.requestFocus();
            return false;
        }
        else
        {
            lcolorm.setErrorEnabled(false);
        }
        return true;
    }

    public boolean comprobarEdad()
    {
        if(edad.getText().toString().trim().isEmpty())
        {
            ledad.setError("Ingresa la edad para la Publicación");
            ledad.requestFocus();
            return false;
        }
        else
        {
            ledad.setErrorEnabled(false);
        }
        return true;
    }

    public boolean requierePermiso()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(agregarImg, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            }).show();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    public void showOptions()
    {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(Perdidos.this);
        builder.setTitle("Elige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    openCamera();
                }else if(option[which] == "Elegir de galeria"){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + APP_DIRECTORY + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });


                    imgBitmap = BitmapFactory.decodeFile(mPath);
                    ivImagen.setImageBitmap(imgBitmap);
                    imgSelected = true;
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    ivImagen.setImageURI(path);
                    imgSelected = true;
                    break;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(Perdidos.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                agregarImg.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Perdidos.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }
    */
        /*
    public Bitmap redimensionarImagen(Bitmap bmp)
    {
        int ancho = bmp.getWidth();
        int alto = bmp.getHeight();
        int newAncho = 0;
        int newAlto = 0;
        if(ancho > alto)
        {
            newAncho = 600;
            newAlto = (newAncho * alto) / ancho;
        }
        else
        {
            newAlto = 600;
            newAncho = (newAlto * ancho) / alto;
        }

        float escaladoAncho = ((float) newAncho) / ancho;
        float escaladoAlto = ((float) newAlto) / alto;

        Matrix matrix = new Matrix();
        matrix.postScale(escaladoAncho, escaladoAlto);

        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, ancho, alto, matrix, false);
        return newBmp;
    }*/
}
