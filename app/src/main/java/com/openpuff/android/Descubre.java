package com.openpuff.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Descubre extends Main implements View.OnClickListener {

    private static final int maxPass = 8;
    private final TextWatcher controlPassAOcultar = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
            if (s.length() == maxPass) {
                showAToast(getString(R.string.noMasCaracteres), Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private final Seguridad seguridad = new Seguridad();
    private Toast toast;
    private TextView TextoOculto;
    private EditText Pass1;
    private ImageView ImagenOriginal;
    private Bitmap bitmap;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.descubre);

        if (savedInstanceState == null) {
            irAGaleria();
        }

        TextoOculto = (TextView) findViewById(R.id.TextoOculto);

        Pass1 = (EditText) findViewById(R.id.Pass1);
        Pass1.addTextChangedListener(controlPassAOcultar);

        ImagenOriginal = (ImageView) findViewById(R.id.ImagenOriginal);

        Button botonDescubrir = (Button) findViewById(R.id.BotonDescubrir);
        botonDescubrir.setOnClickListener(this);

        try {
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
        toast = new Toast(getApplicationContext());
    }

    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (bitmap == null) {
            savedInstanceState.putParcelable("bitmap", null);
        } else {
            savedInstanceState.putParcelable("bitmap", bitmap);
        }

        if (Pass1.getText().toString().equals("")) {
            savedInstanceState.putString("pass1", "");
        } else {
            savedInstanceState.putString("pass1", Pass1.getText().toString());
        }
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        bitmap = savedInstanceState.getParcelable("bitmap");
        if (bitmap != null) {
            ImagenOriginal.setImageBitmap(bitmap);
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
                finish();
                return true;
            case R.id.ItemGaleria:
                irAGaleria();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int menuOpcion = R.menu.menugaleria;
        inflater.inflate(menuOpcion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, @Nullable Intent data) {

        if (resCode == RESULT_OK && data != null) {
            Uri selectedimg = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                ImagenOriginal.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                showAToast(getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_LONG);
            } catch (IOException e) {
                showAToast(getString(R.string.ioException), Toast.LENGTH_LONG);
            }
        } else if (bitmap == null) {
            finish();
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        hideKeyboard();
        if (bitmap != null && TextoOculto.getVisibility() == View.GONE) {
            recuperarDatos();
        } else if (bitmap == null) {
            showAToast(getString(R.string.necesitasImagen), Toast.LENGTH_SHORT);
        }
    }

    void recuperarDatos() {

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String mensaje = descubrirMensaje(bitmap);
                        try {
                            String mensajeFinal = seguridad.decrypt(mensaje, Pass1.getText().toString().trim());
                            TextoOculto.setVisibility(View.VISIBLE);
                            TextoOculto.setText(getString(R.string.textoOcultoEra) + mensajeFinal);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();
                    }
                });
            }
        };
        mThread.start();
    }

    String descubrirMensaje(@NonNull Bitmap bitmap) {
        int tamanioI;
        StringBuilder binario;
        int color = 0;
        String cadenaNueva;
        cadenaNueva = "";
        int i = 0, j = 0;
        String caracter = "";
        String mensaje = "";

        for (; i < bitmap.getWidth(); i++, color = 0) {
            color += bitmap.getPixel(0, i);
            binario = stringToBinary(Integer.toString(color));
            cadenaNueva += binario.charAt(binario.length() - 1);
            if ((i + 1) % 8 == 0) {
                cadenaNueva = binaryToString(cadenaNueva);
                if (cadenaNueva.charAt(cadenaNueva.length() - 1) == '-') {
                    break;
                } else {
                    caracter += cadenaNueva;
                    cadenaNueva = "";
                }
            }
        }

        try {
            tamanioI = Integer.parseInt(caracter);
        } catch (NumberFormatException ignored) {
            return "";
        }

        i++;
        color = 0;
        int contador = i;
        cadenaNueva = "";
        int length = (int) (Math.log10(tamanioI) + 2);
        tamanioI += length;
        tamanioI *= 8;

        for (; contador < tamanioI; i++, color = 0) {
            try {
                color += bitmap.getPixel(j, i);
                contador++;
                binario = stringToBinary(Integer.toString(color));
                cadenaNueva += binario.charAt(binario.length() - 1);
                if ((i + 1) % 8 == 0) {
                    cadenaNueva = binaryToString(cadenaNueva);
                    mensaje += cadenaNueva;
                    cadenaNueva = "";
                }
            } catch (Exception e) {
                j++;
                i = 0;
            }
        }

        return mensaje;
    }

    @NonNull
    String binaryToString(@NonNull String input) {
        String output = "";
        for (int i = 0; i <= input.length() - 8; i += 8) {
            int k = Integer.parseInt(input.substring(i, i + 8), 2);
            output += (char) k;
        }
        return output;
    }

    @NonNull
    StringBuilder stringToBinary(@NonNull String textoOculto) {
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
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    void showAToast(String st, int duracion) {
        try {
            toast.getView().isShown();
            toast.setText(st);
        } catch (Exception e) {
            toast = Toast.makeText(getApplicationContext(), st, duracion);
        }
        toast.show();
    }

    void irAGaleria() {
        showAToast(getString(R.string.necesitasImagen), Toast.LENGTH_SHORT);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        startActivityForResult(intent, 1);
    }

}
