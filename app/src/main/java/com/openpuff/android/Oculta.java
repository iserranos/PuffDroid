package com.openpuff.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Oculta extends Main implements View.OnClickListener {

    private static final int OPCIONPORTADORIMAGEN = 1;
    private static final int OPCIONPORTADORCANCION = 2;
    private static final int OPCIONMENSAJEIMAGEN = 3;
    private static final int maxPass = 16;

    @NonNull
    private final TextWatcher controlPassAOcultar;
    @NonNull
    private final TextWatcher controlTextoAOcultar;
    private final LSB lsb;
    private final Seguridad seguridad;
    private Toast toast;
    private ImageView imagenPortador;
    private TextView textoPortador;
    private Button botonPortador;
    private ImageView imagenMensaje;
    private EditText textoMensaje;
    private Button botonMensaje;
    private EditText pass1;
    private EditText pass2;
    private EditText pass3;
    private Button botonContrasenia;
    private Button botonOcultar;
    private Button botonGuardar;
    @Nullable
    private Bitmap bitmapPortador = null;
    private String cancionPortador = null;
    private Bitmap bitmapMensaje = null;
    private String mensajeMensaje = null;
    private String textoPass1 = null;
    private String textoPass2 = null;
    private String textoPass3 = null;
    private int maxTexto = 0;

    public Oculta() {
        seguridad = new Seguridad();
        lsb = new LSB();
        controlPassAOcultar = new TextWatcher() {
            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
                if (s.length() >= 8) {
                    botonOcultar.setClickable(true);
                    botonOcultar.setTextColor(getResources().getColor(R.color.letraBoton));
                } else {
                    botonOcultar.setClickable(false);
                    botonOcultar.setTextColor(Color.TRANSPARENT);
                }
            }

            @Override
            public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
                if (s.length() == maxPass) {
                    showAToast(getString(R.string.noMasCaracteres), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                String filtered_str = s.toString().trim();
                if (filtered_str.matches(".*[^a-z^A-Z0-9].*")) {
                    filtered_str = filtered_str.replaceAll("[^a-z^A-Z0-9]", "");
                    s.clear();
                    s.append(filtered_str);
                }
            }
        };
        controlTextoAOcultar = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
                if (s.length() == maxTexto && maxTexto != 0) {
                    showAToast(getString(R.string.noMasCaracteres), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                String filtered_str = s.toString();
                if (filtered_str.matches(".*[^a-z^A-Z0-9áéíóúàèìòùâêîôûäëïöüÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÄËÏÖÜÑñ +*=<>.,-;:_{}()/&%$·!?¿¡'@#|].*")) {
                    filtered_str = filtered_str.replaceAll("[^a-z^A-Z0-9áéíóúàèìòùâêîôûäëïöüÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÄËÏÖÜÑñ +*=<>.,-;:_{}()/&%$·!?¿¡'@#|]", "");
                    s.clear();
                    s.append(filtered_str);
                }
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.oculta);

        imagenPortador = (ImageView) findViewById(R.id.OcultaImagenPortador);
        textoPortador = (TextView) findViewById(R.id.OcultaTextoPortador);
        botonPortador = (Button) findViewById(R.id.OcultaBotonPortador);

        imagenMensaje = (ImageView) findViewById(R.id.OcultaImagenMensaje);
        textoMensaje = (EditText) findViewById(R.id.OcultaTextoMensaje);
        botonMensaje = (Button) findViewById(R.id.OcultaBotonMensaje);

        textoMensaje.addTextChangedListener(controlTextoAOcultar);

        pass1 = (EditText) findViewById(R.id.OcultaPass1);
        pass2 = (EditText) findViewById(R.id.OcultaPass2);
        pass3 = (EditText) findViewById(R.id.OcultaPass3);

        pass1.addTextChangedListener(controlPassAOcultar);
        pass2.addTextChangedListener(controlPassAOcultar);
        pass3.addTextChangedListener(controlPassAOcultar);

        botonContrasenia = (Button) findViewById(R.id.OcultaBotonContrasenia);

        botonOcultar = (Button) findViewById(R.id.OcultaBotonOcultar);
        botonGuardar = (Button) findViewById(R.id.OcultaBotonGuardar);

        botonPortador.setOnClickListener(this);
        botonMensaje.setOnClickListener(this);
        botonContrasenia.setOnClickListener(this);
        botonOcultar.setOnClickListener(this);
        botonGuardar.setOnClickListener(this);

        botonOcultar.setClickable(false);
        botonOcultar.setTextColor(Color.TRANSPARENT);

        try {
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
        toast = new Toast(this);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (bitmapPortador != null) {
            savedInstanceState.putParcelable("bitmapPortador", bitmapPortador);
        }

        if (cancionPortador != null) {
            savedInstanceState.putString("cancionPortador", cancionPortador);
        }

        if (botonPortador.getText().equals("Elige el portador")) {
            savedInstanceState.putBoolean("botonPortador", true);
        } else {
            savedInstanceState.putBoolean("botonPortador", false);
        }

        if (bitmapMensaje != null) {
            savedInstanceState.putParcelable("bitmapMensaje", bitmapMensaje);
        }

        if (cancionPortador != null) {
            savedInstanceState.putString("mensajeMensaje", mensajeMensaje);
        }

        if (botonMensaje.getText().equals("Elige el mensaje")) {
            savedInstanceState.putBoolean("botonMensaje", true);
        } else {
            savedInstanceState.putBoolean("botonMensaje", false);
        }

        if (textoPass1 == null) {
            savedInstanceState.putString("textoPass1", "");
        } else {
            savedInstanceState.putString("textoPass1", textoPass1);
        }

        if (textoPass2 == null) {
            savedInstanceState.putString("textoPass2", "");
        } else {
            savedInstanceState.putString("textoPass2", textoPass2);
        }

        if (textoPass3 == null) {
            savedInstanceState.putString("textoPass3", "");
        } else {
            savedInstanceState.putString("textoPass3", textoPass3);
        }

        if (pass1.getVisibility() == View.VISIBLE) {
            savedInstanceState.putBoolean("pass1", true);
        } else {
            savedInstanceState.putBoolean("pass1", false);
        }

        if (pass2.getVisibility() == View.VISIBLE) {
            savedInstanceState.putBoolean("pass2", true);
        } else {
            savedInstanceState.putBoolean("pass2", false);
        }

        if (pass3.getVisibility() == View.VISIBLE) {
            savedInstanceState.putBoolean("pass3", true);
        } else {
            savedInstanceState.putBoolean("pass3", false);
        }

        if (botonOcultar.getVisibility() == View.VISIBLE) {
            savedInstanceState.putBoolean("botonOcultar", true);
        } else {
            savedInstanceState.putBoolean("botonOcultar", false);
        }

        if (botonGuardar.getVisibility() == View.VISIBLE) {
            savedInstanceState.putBoolean("botonGuardar", true);
        } else {
            savedInstanceState.putBoolean("botonGuardar", false);
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        hideKeyboard();

        bitmapPortador = savedInstanceState.getParcelable("bitmapPortador");
        if (bitmapPortador != null) {
            imagenPortador.setImageBitmap(bitmapPortador);
            imagenPortador.setVisibility(View.VISIBLE);
        }

        cancionPortador = savedInstanceState.getString("cancionPortador");
        if (cancionPortador != null) {
            textoPortador.setText(cancionPortador);
            textoPortador.setVisibility(View.VISIBLE);
        }

        boolean botonPortadorB = savedInstanceState.getBoolean("botonPortador");
        if (botonPortadorB) {
            botonPortador.setText("Cambia el portador");
        }

        bitmapMensaje = savedInstanceState.getParcelable("bitmapMensaje");
        if (bitmapMensaje != null) {
            imagenMensaje.setImageBitmap(bitmapMensaje);
        }

        mensajeMensaje = savedInstanceState.getString("mensajeMensaje");
        if (mensajeMensaje != null) {
            textoMensaje.setText(mensajeMensaje);
        }

        boolean botonMensajeB = savedInstanceState.getBoolean("botonMensaje");
        if (botonMensajeB) {
            botonPortador.setText("Cambia el mensaje");
        }

        textoPass1 = savedInstanceState.getString("textoPass1");
        if (!textoPass1.equals("")) {
            pass1.setText(textoPass1);
        }

        textoPass2 = savedInstanceState.getString("textoPass2");
        if (!textoPass2.equals("")) {
            pass2.setText(textoPass2);
        }

        textoPass3 = savedInstanceState.getString("textoPass3");
        if (!textoPass3.equals("")) {
            pass3.setText(textoPass3);
        }

        boolean pass1B = savedInstanceState.getBoolean("pass1");
        if (pass1B) {
            pass1.setVisibility(View.VISIBLE);
        }

        boolean pass2B = savedInstanceState.getBoolean("pass2");
        if (pass2B) {
            pass2.setVisibility(View.VISIBLE);
        }

        boolean pass3B = savedInstanceState.getBoolean("pass3");
        if (pass3B) {
            pass3.setVisibility(View.VISIBLE);
        }

        boolean botonOcultarB = savedInstanceState.getBoolean("botonOcultar");
        if (botonOcultarB) {
            botonOcultar.setVisibility(View.VISIBLE);
        }

        boolean botonGuardarB = savedInstanceState.getBoolean("botonGuardar");
        if (botonGuardarB) {
            botonGuardar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        hideKeyboard();
        switch (item.getItemId()) {
            case android.R.id.home:
                if (botonOcultar.getVisibility() == View.GONE && botonGuardar.getVisibility() == View.VISIBLE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.confirmación))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                            .setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, @NonNull Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (resCode == RESULT_OK) {

            switch (reqCode) {
                case OPCIONPORTADORIMAGEN:

                    textoPortador.setText("");
                    textoPortador.setVisibility(View.GONE);
                    cancionPortador = null;
                    botonPortador.setText("Cambia el portador");

                    opcionPortadorImagen(data);

                    break;
                case OPCIONPORTADORCANCION:

                    bitmapPortador = null;
                    imagenPortador.setImageBitmap(null);
                    imagenPortador.setVisibility(View.GONE);
                    botonPortador.setText("Cambia el portador");

                    opcionPortadorCancion(data);
                    break;
                case OPCIONMENSAJEIMAGEN:

                    textoMensaje.setText(null);
                    textoMensaje.setVisibility(View.GONE);
                    mensajeMensaje = null;
                    botonMensaje.setText("Cambia el mensaje");

                    opcionMensajeImagen(data);
                    break;
            }
        }
    }

    private void opcionPortadorImagen(Intent data) {
        Uri selectedimg = data.getData();
        try {
            bitmapPortador = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
            maxTexto = (bitmapPortador.getWidth() / 8) - 5;
            if (bitmapPortador.getRowBytes() * bitmapPortador.getHeight() > 16588800) {
                showAToast(getString(R.string.imagenPesada), Toast.LENGTH_LONG);
            } else if (maxTexto >= 16) {
                imagenPortador.setImageBitmap(bitmapPortador);
                imagenPortador.setVisibility(View.VISIBLE);
            } else {
                showAToast(getString(R.string.imagenGrande), Toast.LENGTH_LONG);
            }

        } catch (FileNotFoundException e) {
            showAToast(getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_LONG);
        } catch (IOException e) {
            showAToast(getString(R.string.ioException), Toast.LENGTH_LONG);
        }
    }

    private void opcionPortadorCancion(Intent data) {
        if ((data != null) && (data.getData() != null)) {
            Uri cancion = data.getData();
            textoPortador.setText(cancion.getPath());
            textoPortador.setVisibility(View.VISIBLE);
            cancionPortador = cancion.getPath();
        }
    }

    private void opcionMensajeImagen(Intent data) {
        Uri selectedimg = data.getData();
        try {
            bitmapMensaje = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
            maxTexto = (bitmapMensaje.getWidth() / 8) - 5;
            if (bitmapMensaje.getRowBytes() * bitmapMensaje.getHeight() > 16588800) {
                showAToast(getString(R.string.imagenPesada), Toast.LENGTH_LONG);
            } else if (maxTexto >= 16) {
                imagenMensaje.setImageBitmap(bitmapMensaje);
                imagenMensaje.setVisibility(View.VISIBLE);
                //textoMensaje.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTexto)});
            } else {
                showAToast(getString(R.string.imagenGrande), Toast.LENGTH_LONG);
            }

        } catch (FileNotFoundException e) {
            showAToast(getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_LONG);
        } catch (IOException e) {
            showAToast(getString(R.string.ioException), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);

        hideKeyboard();
        switch (view.getId()) {
            case R.id.OcultaBotonPortador:
                pulsarPortador();
                break;
            case R.id.OcultaBotonMensaje:
                pulsarMensaje();
                break;
            case R.id.OcultaBotonContrasenia:
                pulsarContrasenia();
                break;
            case R.id.OcultaBotonOcultar:

                break;
            case R.id.OcultaBotonGuardar:

                break;
            /*case R.id.BotonOcultar:
                if (bitmapPortador != null) {
                    recuperarDatos();
                } else {
                    showAToast(getString(R.string.necesitasImagen), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.BotonGuardar:
                if (bitmapPortador != null) {

                    final ProgressDialog pd = new ProgressDialog(this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage(getString(R.string.cargando));
                    pd.setTitle(getString(R.string.espere));
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            storeImage(bitmapPortador);
                        }
                    };
                    mThread.start();
                    pd.dismiss();
                    showAToast(getString(R.string.imagenguardadaOK), Toast.LENGTH_LONG);
                    finish();
                }
                break;*/
        }
    }

    private void pulsarPortador() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getString(R.string.tituloAlerta));
        alerta.setMessage("¿Qué quieres usar como portador?");
        alerta.setPositiveButton("Canción", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(intent, OPCIONPORTADORCANCION);

                dialog.cancel();
            }
        });
        alerta.setNegativeButton("Imagen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                startActivityForResult(intent, OPCIONPORTADORIMAGEN);

                dialog.cancel();
            }
        });
        alerta.show();
    }

    private void pulsarMensaje() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getString(R.string.tituloAlerta));
        alerta.setMessage("¿Cúal quieres que sea tu mensaje?");
        alerta.setPositiveButton("Texto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {

                textoMensaje.setVisibility(View.VISIBLE);
                textoMensaje.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTexto)});
                bitmapMensaje = null;
                imagenMensaje.setImageBitmap(null);
                imagenMensaje.setVisibility(View.GONE);
                botonMensaje.setText("Cambia el mensaje");

                dialog.cancel();
            }
        });
        alerta.setNegativeButton("Imagen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                startActivityForResult(intent, OPCIONMENSAJEIMAGEN);

                dialog.cancel();
            }
        });
        alerta.show();
    }

    private void pulsarContrasenia() {

        if (botonContrasenia.getText().equals("Añade una contraseña")) {
            if (pass1.getVisibility() == View.VISIBLE) {
                if (pass2.getVisibility() == View.VISIBLE) {
                    pass3.setVisibility(View.VISIBLE);
                    botonContrasenia.setText("Elimina una contraseña");
                    return;
                } else {
                    pass2.setVisibility(View.VISIBLE);
                }
            } else {
                pass1.setVisibility(View.VISIBLE);
            }
        }

        if (botonContrasenia.getText().equals("Elimina una contraseña")) {
            if (pass3.getVisibility() == View.GONE) {
                if (pass2.getVisibility() == View.GONE) {
                    pass1.setVisibility(View.GONE);
                    botonContrasenia.setText("Añade una contraseña");
                } else {
                    pass2.setVisibility(View.GONE);
                }
            } else {
                pass3.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && botonOcultar.getVisibility() == View.GONE && botonGuardar.getVisibility() == View.VISIBLE) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.confirmación))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void recuperarDatos() {
        if (bitmapPortador != null) {
            this.bitmapPortador = bitmapPortador.copy(bitmapPortador.getConfig(), true);
        }

        String texto = textoMensaje.getText().toString().trim();
        if (texto.equals("0") || texto.length() == 0) {
            showAToast(getString(R.string.textoNoVacio), Toast.LENGTH_LONG);
            return;
        }

        String pass1 = this.pass1.getText().toString().trim();
        if (pass1.equals("") || pass1.length() == 0) {
            showAToast(getString(R.string.pass1NoVacia), Toast.LENGTH_LONG);
            return;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getString(R.string.cargando));
        pd.setTitle(getString(R.string.espere));
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    String textoFinal = seguridad.encrypt(textoMensaje.getText().toString().trim(), Oculta.this.pass1.getText().toString().trim());
                    ocultarMensaje(textoFinal.length() + "-" + textoFinal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        };
        botonOcultar.setVisibility(View.GONE);
        botonGuardar.setVisibility(View.VISIBLE);
        botonGuardar.setOnClickListener(this);
        mThread.start();
        imagenMensaje.setImageBitmap(bitmapPortador);
    }

    private void ocultarMensaje(@NonNull String texto) {
        StringBuilder dato = lsb.stringToBinary(texto);

        StringBuilder binario;
        int color = 0;
        String cadenaNueva;
        int i = 0, j = 0, contador = 0;

        for (; contador != dato.length() && bitmapPortador != null; i++, color = 0) {
            try {
                color += bitmapPortador.getPixel(j, i);
                binario = lsb.stringToBinary(Integer.toString(color));
                cadenaNueva = binario.substring(0, binario.length() - 1);
                cadenaNueva += dato.charAt(contador);
                contador++;
                cadenaNueva = lsb.binaryToString(cadenaNueva);
                color = Integer.parseInt(cadenaNueva);
                bitmapPortador.setPixel(j, i, color);
            } catch (Exception e) {
                j++;
                i = 0;
            }
        }
    }

    private void storeImage(@NonNull Bitmap image) {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "OpenPuff");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(mediaFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException ignored) {
        }

        Uri contentUri = Uri.fromFile(mediaFile);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showAToast(String st, int duracion) {
        try {
            toast.getView().isShown();
            toast.setText(st);
        } catch (Exception e) {
            toast = Toast.makeText(getApplicationContext(), st, duracion);
        }
        toast.show();
    }


}
