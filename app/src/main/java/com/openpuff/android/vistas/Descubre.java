package com.openpuff.android.vistas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openpuff.android.R;
import com.openpuff.android.estego.lsb.LSB;
import com.openpuff.android.utils.Util;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Descubre extends Main implements View.OnClickListener {

    private static final int OPCIONPORTADORIMAGEN = 1;
    private static final int OPCIONPORTADORCANCION = 2;
    @NonNull
    private final TextWatcher controlPassAOcultar;
    @NonNull
    private final LSB lsb;
    private ImageView imagenPortador;
    private TextView textoPortador;
    private Button botonPortador;
    private EditText pass1;
    private EditText pass2;
    private EditText pass3;
    private Button botonContrasenia;
    private TextView textoMensaje;
    private Button botonDescubrir;
    private String resultado;

    @Nullable
    private Bitmap bitmapPortador = null;
    @Nullable
    private String cancionPortador = null;
    @Nullable
    private Bitmap bitmapMensaje = null;
    @Nullable
    private String mensajeMensaje = null;
    @Nullable
    private String textoPass1 = null;
    @Nullable
    private String textoPass2 = null;
    @Nullable
    private String textoPass3 = null;

    public Descubre() {
        lsb = new LSB();
        controlPassAOcultar = new TextWatcher() {
            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
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
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.descubre);

        imagenPortador = (ImageView) findViewById(R.id.DescubreImagenPortador);
        textoPortador = (TextView) findViewById(R.id.DescubreTextoPortador);
        botonPortador = (Button) findViewById(R.id.DescubreBotonPortador);

        pass1 = (EditText) findViewById(R.id.DescubrePass1);
        pass2 = (EditText) findViewById(R.id.DescubrePass2);
        pass3 = (EditText) findViewById(R.id.DescubrePass3);

        pass1.addTextChangedListener(controlPassAOcultar);
        pass2.addTextChangedListener(controlPassAOcultar);
        pass3.addTextChangedListener(controlPassAOcultar);

        botonContrasenia = (Button) findViewById(R.id.DescubreBotonContrasenia);

        textoMensaje = (TextView) findViewById(R.id.DescubreTextoOculto);

        botonDescubrir = (Button) findViewById(R.id.DescubreBotonDescubrir);
        botonDescubrir.setOnClickListener(this);
        botonPortador.setOnClickListener(this);
        botonContrasenia.setOnClickListener(this);

        try {
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
    }

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

        if (botonContrasenia.getText().equals("Añade una contraseña")) {
            savedInstanceState.putString("botonContrasenia", "Añade una contraseña");
        } else {
            savedInstanceState.putString("botonContrasenia", "Elimina una contraseña");
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

        if (botonDescubrir.getVisibility() == View.VISIBLE) {
            savedInstanceState.putBoolean("botonDescubrir", true);
        } else {
            savedInstanceState.putBoolean("botonDescubrir", false);
        }
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

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

        mensajeMensaje = savedInstanceState.getString("mensajeMensaje");
        if (mensajeMensaje != null) {
            textoMensaje.setText(mensajeMensaje);
        }

        String botonContraseniaS = savedInstanceState.getString("botonContrasenia");
        if (botonContraseniaS != null) {
            botonContrasenia.setText(botonContraseniaS);
        }

        textoPass1 = savedInstanceState.getString("textoPass1");
        if (textoPass1 != null) {
            pass1.setText(textoPass1);
        }

        textoPass2 = savedInstanceState.getString("textoPass2");
        if (textoPass2 != null) {
            pass2.setText(textoPass2);
        }

        textoPass3 = savedInstanceState.getString("textoPass3");
        if (textoPass3 != null) {
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

        boolean botonDescubrirB = savedInstanceState.getBoolean("botonDescubrir");
        if (botonDescubrirB) {
            botonDescubrir.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Util.hideKeyboard(this.getCurrentFocus(), (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE));

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, @Nullable Intent data) {
        if (resCode == RESULT_OK && data != null) {
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
            }
        }
    }

    private void opcionPortadorImagen(@NonNull Intent data) {
        Uri selectedimg = data.getData();
        try {
            bitmapPortador = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
            /*String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedimg, projection, null, null, null);
            if (cursor != null) {
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                //THIS IS WHAT YOU WANT!
                //nombrePortador = cursor.getString(column_index_data);
                cursor.close();
            }*/

            imagenPortador.setImageBitmap(bitmapPortador);
            imagenPortador.setVisibility(View.VISIBLE);
            /*if (bitmapPortador.getRowBytes() * bitmapPortador.getHeight() > 16588800) {
                Util.showAToast(getApplicationContext(), getString(R.string.imagenPesada), Toast.LENGTH_LONG);
            } else if (maxTexto >= 16) {
                imagenPortador.setImageBitmap(bitmapPortador);
                imagenPortador.setVisibility(View.VISIBLE);
            } else {
                Util.showAToast(getApplicationContext(), getString(R.string.imagenGrande), Toast.LENGTH_LONG);
            }*/

        } catch (FileNotFoundException e) {
            Util.showAToast(getApplicationContext(), getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_LONG);
        } catch (IOException e) {
            Util.showAToast(getApplicationContext(), getString(R.string.ioException), Toast.LENGTH_LONG);
        }
    }

    private void opcionPortadorCancion(@Nullable Intent data) {
        if ((data != null) && (data.getData() != null)) {
            Uri cancion = data.getData();

            String scheme = cancion.getScheme();
            String path = "";

            if (scheme.equals("content")) {
                String[] proj = {MediaStore.Audio.Media.DATA};
                Cursor cursor = this.getContentResolver().query(cancion, proj, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (cursor.getColumnIndex(MediaStore.Audio.Media.DATA) != -1) {
                        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    }
                    cursor.close();
                }
            }
            String exten = path.substring(path.length() - 4);
            if (exten.equals(".wav")) {
                textoPortador.setText(path);
                textoPortador.setVisibility(View.VISIBLE);
                cancionPortador = path;
            } else {
                Util.showAToast(getApplicationContext(), "Audio file must be a WAV file.", Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        Util.hideKeyboard(this.getCurrentFocus(), (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE));

        switch (view.getId()) {
            case R.id.DescubreBotonPortador:
                pulsarPortador();
                break;
            case R.id.DescubreBotonContrasenia:
                pulsarContrasenia();
                break;
            case R.id.DescubreBotonDescubrir:
                recuperarDatos();
                break;
        }

        /*if (bitmapPortador != null && textoMensaje.getVisibility() == View.GONE) {
            recuperarDatos();
        } else if (bitmapPortador == null) {
            lsb.showAToast(getString(R.string.necesitasImagen), Toast.LENGTH_SHORT);
        }*/
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
                intent.setType("audio/wav");
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

    private void pulsarContrasenia() {
        if (botonContrasenia.getText().equals("Añade una contraseña")) {
            if (pass2.getVisibility() == View.VISIBLE) {
                pass3.setVisibility(View.VISIBLE);
                botonContrasenia.setText("Elimina una contraseña");
                return;
            } else {
                pass2.setVisibility(View.VISIBLE);
            }
        }

        if (botonContrasenia.getText().equals("Elimina una contraseña")) {
            if (pass3.getVisibility() == View.GONE) {
                pass2.setVisibility(View.GONE);
                botonContrasenia.setText("Añade una contraseña");
            } else {
                pass3.setVisibility(View.GONE);
            }
        }
    }

    private void recuperarDatos() {

        textoPass1 = pass1.getText().toString().trim();
        if (textoPass1.length() <= 0) {
            Util.showAToast(getApplicationContext(), getString(R.string.pass1NoVacia), Toast.LENGTH_LONG);
            return;
        }

        if (pass2.getVisibility() == View.VISIBLE) {
            textoPass2 = pass2.getText().toString().trim();
            if (textoPass2.length() < 0) {
                Util.showAToast(getApplicationContext(), getString(R.string.pass1NoVacia), Toast.LENGTH_LONG);
                return;
            }
            if (pass3.getVisibility() == View.VISIBLE) {
                textoPass3 = pass3.getText().toString().trim();
                if (textoPass3.length() < 0) {
                    Util.showAToast(getApplicationContext(), getString(R.string.pass1NoVacia), Toast.LENGTH_LONG);
                    return;
                }
            } else {
                textoPass3 = textoPass1;
            }
        } else {
            textoPass2 = textoPass1;
            textoPass3 = textoPass1;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getString(R.string.cargando));
        pd.setTitle(getString(R.string.espere));
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        Thread thread = new Thread();

        if (bitmapPortador != null) {
            thread = new Thread() {
                @Override
                public void run() {
                    try {
                        bitmapPortador = bitmapPortador.copy(bitmapPortador.getConfig(), true);
                        resultado = lsb.descubrirMensaje(bitmapPortador, textoPass1, textoPass2, textoPass3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        if (cancionPortador != null) {
            thread = new Thread() {
                @Override
                public void run() {
                    try {
                        resultado = lsb.descubrirMensaje(cancionPortador, textoPass1, textoPass2, textoPass3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        thread.start();
        try {
            thread.join();
            if (resultado != null) {
                textoMensaje.setVisibility(View.VISIBLE);
                if (resultado.equals("")) {
                    textoMensaje.setText("La imagen se ha guardado en la carpeta");
                } else {
                    textoMensaje.setText(getString(R.string.textoOcultoEra) + resultado);
                }
            } else {
                Util.showAToast(getApplicationContext(), "Error al obtener la extensión", Toast.LENGTH_LONG);
                return;
            }
            Util.showAToast(getApplicationContext(), "TODO OK", Toast.LENGTH_LONG);
        } catch (InterruptedException e) {
            Util.showAToast(getApplicationContext(), "TODO KO", Toast.LENGTH_LONG);
        } finally {
            pd.dismiss();
        }

        /*try {
            if (portador == 1) {
                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        if (bitmapPortador != null) {
                            resultado = lsb.descubrirMensaje(bitmapPortador, textoPass1, textoPass2, textoPass3);
                        }
                        String firma = lsb.descubrirFirma(resultado);
                        switch (firma) {
                            case TEXT:
                                textoMensaje.setVisibility(View.VISIBLE);
                                textoMensaje.setText(getString(R.string.textoOcultoEra) + firma);
                                break;
                            case PNG:
                                Bitmap bmp = BitmapFactory.decodeByteArray(resultado, 0, resultado.length);
                                imagenMensaje.setVisibility(View.VISIBLE);
                                imagenMensaje.setImageBitmap(bmp);
                                break;
                            default:
                                Util.showAToast(getApplicationContext(), "Error al obtener la extensión", Toast.LENGTH_LONG);
                                break;
                        }

                    }
                };
                mThread.start();

            }
            if (portador == 2) {
                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        resultado = lsb.descubrirMensaje(cancionPortador, textoPass1, textoPass2, textoPass3);
                        String firma = lsb.descubrirFirma(resultado);
                        switch (firma) {
                            case TEXT:
                                textoMensaje.setVisibility(View.VISIBLE);
                                textoMensaje.setText(getString(R.string.textoOcultoEra) + firma);
                                break;
                            case PNG:
                                Bitmap bmp = BitmapFactory.decodeByteArray(resultado, 0, resultado.length);
                                imagenMensaje.setVisibility(View.VISIBLE);
                                imagenMensaje.setImageBitmap(bmp);
                                break;
                            default:
                                Util.showAToast(getApplicationContext(), "Error al obtener la extensión", Toast.LENGTH_LONG);
                                break;
                        }
                    }
                };
                mThread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        pd.dismiss();*/
    }

}
