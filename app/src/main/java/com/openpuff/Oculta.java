package com.openpuff;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Oculta extends Main implements View.OnClickListener {

    EditText TextoAOcultar, Pass1, Pass2, Pass3;
    Button BotonOcultar;
    ImageView ImagenOriginal;
    Bitmap bitmap = null;
    Menu menu;
    int menuOpcion = R.menu.menugaleria;

    private static final int GALERIA = 1;
    private static final int CAMARA = 2;
    private static final int ACEPTAR = 1;
    private static final int CANCELAR = 2;
    public static String path = "";
    public int opcion = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mostrarAlerta();
        }else{
            onRestoreInstanceState(savedInstanceState);
        }
        setContentView(R.layout.oculta);

        TextoAOcultar = (EditText) findViewById(R.id.TextoAOcultar);
        Pass1 = (EditText) findViewById(R.id.Pass1);
        Pass2 = (EditText) findViewById(R.id.Pass2);
        Pass3 = (EditText) findViewById(R.id.Pass3);
        BotonOcultar = (Button) findViewById(R.id.BotonOcultar);
        ImagenOriginal = (ImageView) findViewById(R.id.ImagenOriginal);

        // add click listener to button
        BotonOcultar.setOnClickListener(this);
        try {
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        if(bitmap == null){
            savedInstanceState.putParcelable("bitmap", null);
        }else{
            savedInstanceState.putParcelable("bitmap", bitmap);
        }

        if(TextoAOcultar.getText().toString().equals("")){
            savedInstanceState.putString("textoAOcultar", "");
        }else{
            savedInstanceState.putString("textoAOcultar", TextoAOcultar.getText().toString());
        }

        if(Pass1.getText().toString().equals("")){
            savedInstanceState.putString("pass1", "");
        }else{
            savedInstanceState.putString("pass1", Pass1.getText().toString());
        }

        if(Pass2.getText().toString().equals("")){
            savedInstanceState.putString("pass2", "");
        }else{
            savedInstanceState.putString("pass2", Pass2.getText().toString());
        }

        if(Pass3.getText().toString().equals("")){
            savedInstanceState.putString("pass3", "");
        }else{
            savedInstanceState.putString("pass3", Pass3.getText().toString());
        }

    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        bitmap = savedInstanceState.getParcelable("bitmap");
        if(bitmap != null){
            ImagenOriginal.setImageBitmap(bitmap);
        }

        String textoAOcultar = savedInstanceState.getString("textoAOcultar");
        if(!textoAOcultar.equals("")){
            TextoAOcultar.setText(textoAOcultar);
        }

        String pass1 = savedInstanceState.getString("pass1");
        if(!pass1.equals("")){
            Pass1.setText(pass1);
        }

        String pass2 = savedInstanceState.getString("pass2");
        if(!pass2.equals("")){
            Pass2.setText(pass2);
        }

        String pass3 = savedInstanceState.getString("pass3");
        if(!pass3.equals("")){
            Pass3.setText(pass3);
        }
    }

    private void mostrarAlerta() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getString(R.string.tituloAlerta));
        alerta.setMessage(getString(R.string.mensajeAlerta));
        alerta.setPositiveButton(getString(R.string.opcion2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 1. on Upload click call ACTION_GET_CONTENT intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // 2. pick image only
                intent.setType("image/*");
                // 3. start activity
                startActivityForResult(intent, GALERIA);

                dialog.cancel();
            }
        });
        alerta.setNegativeButton(getString(R.string.opcion1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // start the image capture Intent
                startActivityForResult(intent, CAMARA);

                dialog.cancel();
            }
        });

        alerta.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.ItemGaleria:
                mostrarAlerta();
                return true;
            case R.id.ItemAjustes:
                intent = new Intent(this, Ajustes.class);
                startActivity(intent);
                return true;
            case R.id.ItemCompartir:
                intent = new Intent(this, Comparte.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuOpcion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (resCode == RESULT_OK) {
            if (reqCode == CAMARA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                ImagenOriginal.setImageBitmap(bitmap);
            } else {
                Uri selectedimg = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                    ImagenOriginal.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.ioException), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.algunError), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        String texto = BotonOcultar.getText().toString();

        if(texto.equals(getResources().getString(R.string.botonOcultar))){
            if (bitmap != null) {
                recuperarDatos();
            }
            Toast.makeText(getApplicationContext(), getString(R.string.necesitasImagen), Toast.LENGTH_SHORT).show();
        }

        if(texto.equals(getResources().getString(R.string.botonOcultar))){
            storeImage(bitmap);
        }
    }

    private void recuperarDatos() {
        this.bitmap = bitmap.copy(bitmap.getConfig(), true);
        String texto = TextoAOcultar.getText().toString().trim();
        String pass1 = Pass1.getText().toString().trim();
        String pass2 = Pass2.getText().toString().trim();
        String pass3 = Pass3.getText().toString().trim();
        if (texto.equals("0") || texto.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.textoNoVacio), Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass1.equals("") || pass1.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.pass1NoVacia), Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass2.equals("") || pass2.length() == 0) {
            mostrarAlertaPass(getString(R.string.mensajeAlertaPass2));
            if (opcion == CANCELAR) {
                opcion = 0;
                return;
            }
        }
        if (pass3.equals("") || pass3.length() == 0) {
            mostrarAlertaPass(getString(R.string.mensajeAlertaPass3));
            if (opcion == CANCELAR) {
                opcion = 0;
                return;
            }
        }
        String textoFinal = Seguridad.generarMensaje(texto, pass1, pass2, pass3);
        //String textoInicio = Seguridad.descubrirMensaje(textoFinal, pass1, pass2, pass3);
        ocultarMensaje(textoFinal);
    }

    private void mostrarAlertaPass(String mensaje) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getString(R.string.tituloAlertaPass));
        alerta.setMessage(mensaje);
        alerta.setPositiveButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                opcion = CANCELAR;
                dialog.cancel();
            }

        });
        alerta.setNegativeButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                opcion = ACEPTAR;
                dialog.cancel();
            }
        });

        alerta.show();
    }

    private void ocultarMensaje(String texto) {
        StringBuilder dato = stringToBinary(texto);

        StringBuilder binario;
        int color = 0;
        String cadenaNueva;
        int contador = 0;

        for (int i = 0; i < bitmap.getHeight() && contador != dato.length(); i++) {
            for (int j = 0; j < bitmap.getWidth() && contador != dato.length(); j++) {

                color += bitmap.getPixel(i, j);

                binario = stringToBinary(Integer.toString(color));

                cadenaNueva = binario.substring(0, binario.length() - 1);

                cadenaNueva += dato.charAt(contador);

                cadenaNueva = binaryToString(cadenaNueva);

                color = Integer.parseInt(cadenaNueva);

                bitmap.setPixel(i, j, color);

                color = 0;
                contador++;

            }
        }
        ImagenOriginal.setImageBitmap(bitmap);
        BotonOcultar.setText(R.string.botonGuardar);
        menuOpcion = R.menu.menucompartir;
        onCreateOptionsMenu(menu);
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("Error",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Error", "Error accessing file: " + e.getMessage());
        }
        Toast.makeText(getApplicationContext(), getString(R.string.imagenguardadaOK), Toast.LENGTH_SHORT).show();
    }

    public String binaryToString(String input) {
        String output = "";
        for (int i = 0; i <= input.length() - 8; i += 8) {
            int k = Integer.parseInt(input.substring(i, i + 8), 2);
            output += (char) k;
        }
        return output;
    }


    protected StringBuilder stringToBinary(String textoOculto) {
        byte[] bytes = textoOculto.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary;
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "OpenPuff");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        path = mediaFile.toString();

        return mediaFile;
    }


}
