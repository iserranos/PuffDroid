package com.openpuff;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Descubre extends Main implements View.OnClickListener {

    TextView TextoOculto;
    EditText Pass1, Pass2, Pass3;
    ImageView ImagenOriginal;
    Button BotonDescubrir;
    Bitmap bitmap;
    Menu menu;
    int menuOpcion = R.menu.menugaleria;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.descubre);

        if(savedInstanceState == null){
            // 1. on Upload click call ACTION_GET_CONTENT intent
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            // 2. pick image only
            intent.setType("image/*");
            // 3. start activity
            startActivityForResult(intent, 1);
        }


        TextoOculto = (TextView) findViewById(R.id.TextoOculto);
        Pass1 = (EditText) findViewById(R.id.Pass1);
        Pass2 = (EditText) findViewById(R.id.Pass2);
        Pass3 = (EditText) findViewById(R.id.Pass3);
        ImagenOriginal = (ImageView) findViewById(R.id.ImagenOriginal);
        BotonDescubrir = (Button) findViewById(R.id.BotonDescubrir);

        BotonDescubrir.setOnClickListener(this);

        try{
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException ignored){}
    }


    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(bitmap == null){
            savedInstanceState.putParcelable("bitmap", null);
        }else{
            savedInstanceState.putParcelable("bitmap", bitmap);
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


    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        bitmap = savedInstanceState.getParcelable("bitmap");
        if(bitmap != null){
            ImagenOriginal.setImageBitmap(bitmap);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.ItemGaleria:
                // 1. on Upload click call ACTION_GET_CONTENT intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // 2. pick image only
                intent.setType("image/*");
                // 3. start activity
                startActivityForResult(intent, 1);
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

        if (resCode == RESULT_OK && data != null) {
            Uri selectedimg = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                ImagenOriginal.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.ioException), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), R.string.necesitasImagen, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (bitmap != null) {
            String mensaje = descubrirMensaje(bitmap);
            TextoOculto.setText(getString(R.string.textoOcultoEra) + mensaje);
            Toast.makeText(getApplicationContext(), getString(R.string.fileNotFoundExcepcion) + ": " + mensaje, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.necesitasImagen), Toast.LENGTH_SHORT).show();
        }
    }


    public String descubrirMensaje(Bitmap bitmap) {
        int tamanioI;
        StringBuilder binario;
        int color;
        String cadenaNueva;
        cadenaNueva = "";
        int contador = 0;
        int i, j = 0;
        String caracter = "";
        String mensaje = "";

        externo:
        for (i = 0; i < bitmap.getHeight(); i++) {
            for (j = 0; j < bitmap.getWidth(); j++) {

                color = 0;

                color += bitmap.getPixel(i, j);
                binario = stringToBinary(Integer.toString(color));
                cadenaNueva += binario.charAt(binario.length() - 1);

                if ((j + 1) % 8 == 0) {
                    cadenaNueva = binaryToString(cadenaNueva);

                    if (Character.isDigit(cadenaNueva.charAt(cadenaNueva.length() - 1))) {
                        caracter += cadenaNueva;
                        cadenaNueva = "";
                    } else {
                        break externo;
                    }
                }

            }
        }

        tamanioI = Integer.parseInt(caracter) - 1;
        mensaje += cadenaNueva;
        j++;
        cadenaNueva = "";
        for (i = 0; i < bitmap.getHeight() && contador < tamanioI; i++) {
            for (; j < bitmap.getWidth() && contador < tamanioI; j++) {

                color = 0;

                color += bitmap.getPixel(i, j);
                binario = stringToBinary(Integer.toString(color));
                cadenaNueva += binario.charAt(binario.length() - 1);

                if ((j + 1) % 8 == 0) {
                    cadenaNueva = binaryToString(cadenaNueva);
                    mensaje += cadenaNueva;
                    cadenaNueva = "";
                    contador++;
                }

            }
        }
        return mensaje;
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

}
