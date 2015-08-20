package com.openpuff.android.vistas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class Oculta extends Main implements View.OnClickListener {

    private static final int OPCIONPORTADORIMAGEN = 1;
    private static final int OPCIONPORTADORCANCION = 2;
    private static final int OPCIONMENSAJEIMAGEN = 3;

    @NonNull
    private final LSB lsb;
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
    //private int maxTexto = 0;

    public Oculta() {
        lsb = new LSB();
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

        pass1 = (EditText) findViewById(R.id.OcultaPass1);
        pass2 = (EditText) findViewById(R.id.OcultaPass2);
        pass3 = (EditText) findViewById(R.id.OcultaPass3);

        botonContrasenia = (Button) findViewById(R.id.OcultaBotonContrasenia);

        botonOcultar = (Button) findViewById(R.id.OcultaBotonOcultar);

        botonPortador.setOnClickListener(this);
        botonMensaje.setOnClickListener(this);
        botonContrasenia.setOnClickListener(this);
        botonOcultar.setOnClickListener(this);

        try {
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
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
            savedInstanceState.putString("botonPortador", "Elige el portador");
        } else {
            savedInstanceState.putString("botonPortador", "Cambia el portador");
        }

        if (bitmapMensaje != null) {
            savedInstanceState.putParcelable("bitmapMensaje", bitmapMensaje);
        }

        if (cancionPortador != null) {
            savedInstanceState.putString("mensajeMensaje", mensajeMensaje);
        }

        if (botonMensaje.getText().equals("Elige el mensaje")) {
            savedInstanceState.putString("botonMensaje", "Elige el mensaje");
        } else {
            savedInstanceState.putString("botonMensaje", "Cambia el mensaje");
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

        if (botonOcultar.getVisibility() == View.VISIBLE) {
            savedInstanceState.putBoolean("botonOcultar", true);
        } else {
            savedInstanceState.putBoolean("botonOcultar", false);
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Util.hideKeyboard(this.getCurrentFocus(), (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE));

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

        String botonPortadorB = savedInstanceState.getString("botonPortador");
        if (botonPortadorB != null) {
            botonPortador.setText(botonPortadorB);
        }

        bitmapMensaje = savedInstanceState.getParcelable("bitmapMensaje");
        if (bitmapMensaje != null) {
            imagenMensaje.setImageBitmap(bitmapMensaje);
        }

        mensajeMensaje = savedInstanceState.getString("mensajeMensaje");
        if (mensajeMensaje != null) {
            textoMensaje.setText(mensajeMensaje);
        }

        String botonMensajeB = savedInstanceState.getString("botonMensaje");
        if (botonMensajeB != null) {
            botonPortador.setText(botonMensajeB);
        }

        String botonContraseniaS = savedInstanceState.getString("botonContrasenia");
        if (botonContraseniaS != null) {
            botonContrasenia.setText(botonContraseniaS);
        }

        String textoPass1 = savedInstanceState.getString("textoPass1");
        if (textoPass1 != null) {
            pass1.setText(this.textoPass1);
        }

        String textoPass2 = savedInstanceState.getString("textoPass2");
        if (textoPass2 != null) {
            pass2.setText(this.textoPass2);
        }

        String textoPass3 = savedInstanceState.getString("textoPass3");
        if (textoPass3 != null) {
            pass3.setText(this.textoPass3);
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Util.hideKeyboard(this.getCurrentFocus(), (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE));
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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

    private void opcionMensajeImagen(@NonNull Intent data) {
        Uri selectedimg = data.getData();
        try {
            bitmapMensaje = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
            imagenMensaje.setImageBitmap(bitmapMensaje);
            imagenMensaje.setVisibility(View.VISIBLE);
            /*if (bitmapMensaje.getRowBytes() * bitmapMensaje.getHeight() > 16588800) {
                Util.showAToast(getApplicationContext(), getString(R.string.imagenPesada), Toast.LENGTH_LONG);
            } else if (maxTexto >= 16) {
                imagenMensaje.setImageBitmap(bitmapMensaje);
                imagenMensaje.setVisibility(View.VISIBLE);
            } else {
                Util.showAToast(getApplicationContext(), getString(R.string.imagenGrande), Toast.LENGTH_LONG);
            }*/

        } catch (FileNotFoundException e) {
            Util.showAToast(getApplicationContext(), getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_LONG);
        } catch (IOException e) {
            Util.showAToast(getApplicationContext(), getString(R.string.ioException), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);

        Util.hideKeyboard(this.getCurrentFocus(), (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE));
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
                recuperarDatos();
                break;
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
                intent.setType("audio/*");
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

        int portador = 0;
        int mensaje = 0;

        if (bitmapPortador != null || cancionPortador != null) {
            if (bitmapPortador != null) {
                this.bitmapPortador = bitmapPortador.copy(bitmapPortador.getConfig(), true);
                portador = 1;
            }
            if (cancionPortador != null) {
                portador = 2;
            }
        } else {
            Util.showAToast(getApplicationContext(), "", Toast.LENGTH_LONG);
            return;
        }

        if (!textoMensaje.getText().toString().trim().equals("") || bitmapMensaje != null) {
            if (!textoMensaje.getText().toString().trim().equals("")) {
                mensajeMensaje = textoMensaje.getText().toString().trim();
                mensaje = 1;
            }
            if (bitmapMensaje != null) {
                this.bitmapMensaje = bitmapMensaje.copy(bitmapMensaje.getConfig(), true);
                mensaje = 2;
            }
        } else {
            Util.showAToast(getApplicationContext(), "", Toast.LENGTH_LONG);
            return;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getString(R.string.cargando));
        pd.setTitle(getString(R.string.espere));
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        if (portador == 1 && mensaje == 1) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        lsb.ocultarMensaje(bitmapPortador, mensajeMensaje, textoPass1, textoPass2, textoPass3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
        if (portador == 1 && mensaje == 2) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        lsb.ocultarMensaje(bitmapPortador, bitmapMensaje, textoPass1, textoPass2, textoPass3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
        if (portador == 2 && mensaje == 1) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        lsb.ocultarMensaje(cancionPortador, mensajeMensaje, textoPass1, textoPass2, textoPass3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
        if (portador == 2 && mensaje == 2) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        lsb.ocultarMensaje(cancionPortador, bitmapMensaje, textoPass1, textoPass2, textoPass3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
        pd.dismiss();

        Util.showAToast(getApplicationContext(), "Todo OK", Toast.LENGTH_LONG);
        /*final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getString(R.string.cargando));
        pd.setTitle(getString(R.string.espere));
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        try {
            if (portador == 1) {
                if (mensaje == 1) {
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lsb.ocultarMensaje(bitmapPortador, mensajeMensaje, textoPass1, textoPass2, textoPass3);
                                }
                            });
                        }
                    };
                    mThread.start();
                }
                if (mensaje == 2) {
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lsb.ocultarMensaje(bitmapPortador, bitmapMensaje, textoPass1, textoPass2, textoPass3);
                                }
                            });
                        }
                    };
                    mThread.start();
                }
                imagenMensaje.setImageBitmap(bitmapPortador);
            }

            if (portador == 2) {
                if (mensaje == 1) {
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lsb.ocultarMensaje(cancionPortador, mensajeMensaje, textoPass1, textoPass2, textoPass3);
                                }
                            });
                        }
                    };
                    mThread.start();
                }
                if (mensaje == 2) {
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lsb.ocultarMensaje(cancionPortador, bitmapMensaje, textoPass1, textoPass2, textoPass3);
                                }
                            });
                        }
                    };
                    mThread.start();
                }
                textoPortador.setText(cancionPortador);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pd.dismiss();*/
    }

    /*private void storeImage() {

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
                    if (bitmapPortador != null) {
                        bitmapPortador.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    }
                    fos.flush();
                    fos.close();
                } catch (IOException ignored) {
                }

                Uri contentUri = Uri.fromFile(mediaFile);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
            }
        };
        mThread.start();
        pd.dismiss();
        Util.showAToast(getApplicationContext(), getString(R.string.imagenguardadaOK), Toast.LENGTH_LONG);
    }*/
}
