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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    private static final int GALERIA = 1;
    private static final int CAMARA = 2;
    private static final int maxPass = 16;
    @NonNull
    private final Seguridad seguridad;
    @NonNull
    private final TextWatcher controlPassAOcultar;
    @NonNull
    private final TextWatcher controlTextoAOcultar;
    private Toast toast;
    private EditText TextoAOcultar;
    private EditText Pass1;
    private Button BotonOcultar;
    private Button BotonGuardar;
    private ImageView ImagenOriginal;
    @Nullable
    private Bitmap bitmap = null;
    private int maxTexto = 0;

    public Oculta() {
        seguridad = new Seguridad();
        controlPassAOcultar = new TextWatcher() {
            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
                if (s.length() >= 8) {
                    BotonOcultar.setClickable(true);
                    BotonOcultar.setTextColor(getResources().getColor(R.color.letraBoton));
                } else {
                    BotonOcultar.setClickable(false);
                    BotonOcultar.setTextColor(Color.TRANSPARENT);
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

        BotonOcultar = (Button) findViewById(R.id.BotonOcultar);
        BotonGuardar = (Button) findViewById(R.id.BotonGuardar);

        TextoAOcultar = (EditText) findViewById(R.id.TextoAOcultar);
        TextoAOcultar.addTextChangedListener(controlTextoAOcultar);

        Pass1 = (EditText) findViewById(R.id.Pass1);
        Pass1.addTextChangedListener(controlPassAOcultar);

        ImagenOriginal = (ImageView) findViewById(R.id.ImagenOriginal);

        BotonOcultar.setOnClickListener(this);
        BotonOcultar.setClickable(false);
        BotonOcultar.setTextColor(Color.TRANSPARENT);
        BotonGuardar.setOnClickListener(this);

        try {
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
        toast = new Toast(this);

        if (savedInstanceState == null) {
            mostrarAlerta();
        } else {
            onRestoreInstanceState(savedInstanceState);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (bitmap == null) {
            savedInstanceState.putParcelable("bitmap", null);
        } else {
            savedInstanceState.putParcelable("bitmap", bitmap);
        }

        if (TextoAOcultar.getText().toString().equals("")) {
            savedInstanceState.putString("textoAOcultar", "");
        } else {
            savedInstanceState.putString("textoAOcultar", TextoAOcultar.getText().toString());
        }

        if (Pass1.getText().toString().equals("")) {
            savedInstanceState.putString("pass1", "");
        } else {
            savedInstanceState.putString("pass1", Pass1.getText().toString());
        }

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        hideKeyboard();
        bitmap = savedInstanceState.getParcelable("bitmap");
        if (bitmap != null) {
            ImagenOriginal.setImageBitmap(bitmap);
        }

        String textoAOcultar = savedInstanceState.getString("textoAOcultar");
        if (!textoAOcultar.equals("")) {
            TextoAOcultar.setText(textoAOcultar);
        }

        String pass1 = savedInstanceState.getString("pass1");
        if (!pass1.equals("")) {
            Pass1.setText(pass1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        hideKeyboard();
        switch (item.getItemId()) {
            case android.R.id.home:
                if (BotonOcultar.getVisibility() == View.GONE && BotonGuardar.getVisibility() == View.VISIBLE) {
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

            case R.id.ItemGaleria:
                mostrarAlerta();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int menuOpcion = R.menu.menugaleria;
        inflater.inflate(menuOpcion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, @NonNull Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (resCode == RESULT_OK) {
            switch (reqCode) {
                case CAMARA:
                    irAGaleria(CAMARA);
                    break;

                case GALERIA:
                    Uri selectedimg = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                        maxTexto = (bitmap.getWidth() / 8) - 5;
                        if (bitmap.getRowBytes() * bitmap.getHeight() > 16588800) {
                            showAToast(getString(R.string.imagenPesada), Toast.LENGTH_LONG);
                            irAGaleria(GALERIA);
                        } else if (maxTexto >= 16) {
                            ImagenOriginal.setImageBitmap(bitmap);
                            TextoAOcultar.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTexto)});
                        } else {
                            showAToast(getString(R.string.imagenGrande), Toast.LENGTH_LONG);
                        }

                    } catch (FileNotFoundException e) {
                        showAToast(getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_LONG);
                    } catch (IOException e) {
                        showAToast(getString(R.string.ioException), Toast.LENGTH_LONG);
                    }

                    break;
            }
        } else {
            showAToast(getString(R.string.necesitasImagen), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);

        hideKeyboard();
        switch (view.getId()) {
            case R.id.BotonOcultar:
                if (bitmap != null) {
                    recuperarDatos();
                } else {
                    showAToast(getString(R.string.necesitasImagen), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.BotonGuardar:
                if (bitmap != null) {

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
                            storeImage(bitmap);
                        }
                    };
                    mThread.start();
                    pd.dismiss();
                    showAToast(getString(R.string.imagenguardadaOK), Toast.LENGTH_LONG);
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && BotonOcultar.getVisibility() == View.GONE && BotonGuardar.getVisibility() == View.VISIBLE) {

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

    private void mostrarAlerta() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getString(R.string.tituloAlerta));
        alerta.setMessage(getString(R.string.mensajeAlerta));
        alerta.setPositiveButton(getString(R.string.opcion2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {

                irAGaleria(GALERIA);
                dialog.cancel();
            }
        });
        alerta.setNegativeButton(getString(R.string.opcion1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {

                Intent camera = new Intent();
                camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMARA);

                dialog.cancel();
            }
        });
        alerta.show();
    }

    private void recuperarDatos() {
        if (bitmap != null) {
            this.bitmap = bitmap.copy(bitmap.getConfig(), true);
        }

        String texto = TextoAOcultar.getText().toString().trim();
        if (texto.equals("0") || texto.length() == 0) {
            showAToast(getString(R.string.textoNoVacio), Toast.LENGTH_LONG);
            return;
        }

        String pass1 = Pass1.getText().toString().trim();
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
                    String textoFinal = seguridad.encrypt(TextoAOcultar.getText().toString().trim(), Pass1.getText().toString().trim());
                    ocultarMensaje(textoFinal.length() + "-" + textoFinal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        };
        BotonOcultar.setVisibility(View.GONE);
        BotonGuardar.setVisibility(View.VISIBLE);
        mThread.start();
        ImagenOriginal.setImageBitmap(bitmap);
    }

    private void ocultarMensaje(@NonNull String texto) {
        StringBuilder dato = stringToBinary(texto);

        StringBuilder binario;
        int color = 0;
        String cadenaNueva;
        int i = 0, j = 0, contador = 0;

        for (; contador != dato.length() && bitmap != null; i++, color = 0) {
            try {
                color += bitmap.getPixel(j, i);
                binario = stringToBinary(Integer.toString(color));
                cadenaNueva = binario.substring(0, binario.length() - 1);
                cadenaNueva += dato.charAt(contador);
                contador++;
                cadenaNueva = binaryToString(cadenaNueva);
                color = Integer.parseInt(cadenaNueva);
                bitmap.setPixel(j, i, color);
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

    @NonNull
    private String binaryToString(@NonNull String input) {
        String output = "";
        for (int i = 0; i <= input.length() - 8; i += 8) {
            int k = Integer.parseInt(input.substring(i, i + 8), 2);
            output += (char) k;
        }
        return output;
    }

    @NonNull
    private StringBuilder stringToBinary(@NonNull String textoOculto) {
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

    private void irAGaleria(int opcion) {
        if (opcion == CAMARA) {
            showAToast(getString(R.string.eligeFoto), Toast.LENGTH_LONG);
        } else {
            showAToast(getString(R.string.necesitasImagen), Toast.LENGTH_SHORT);
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        startActivityForResult(intent, 1);
    }

}
