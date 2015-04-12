package com.openpuff;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private final TextWatcher controlPassAOcultar = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
            if (s.length() == maxPass) {
                Toast.makeText(getApplicationContext(), getString(R.string.noMasCaracteres), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private final Seguridad seguridad = new Seguridad();
    private EditText TextoAOcultar;
    private EditText Pass1/*, Pass2, Pass3*/;
    private Button BotonOcultar;
    private Button BotonGuardar;
    private ImageView ImagenOriginal;
    @Nullable
    private Bitmap bitmap = null;
    private int maxTexto = 0;
    private final TextWatcher controlTextoAOcultar = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
            if (s.length() == maxTexto && maxTexto != 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.noMasCaracteres), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mostrarAlerta();
        } else {
            onRestoreInstanceState(savedInstanceState);
        }
        setContentView(R.layout.oculta);

        BotonOcultar = (Button) findViewById(R.id.BotonOcultar);
        BotonGuardar = (Button) findViewById(R.id.BotonGuardar);

        TextoAOcultar = (EditText) findViewById(R.id.TextoAOcultar);
        TextoAOcultar.addTextChangedListener(controlTextoAOcultar);

        Pass1 = (EditText) findViewById(R.id.Pass1);
        Pass1.addTextChangedListener(controlPassAOcultar);
        /*Pass2 = (EditText) findViewById(R.id.Pass2);
        Pass2.addTextChangedListener(controlPassAOcultar);
        Pass3 = (EditText) findViewById(R.id.Pass3);
        Pass3.addTextChangedListener(controlPassAOcultar);*/

        ImagenOriginal = (ImageView) findViewById(R.id.ImagenOriginal);

        BotonOcultar.setOnClickListener(this);
        try {
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
    }

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

        /*if (Pass2.getText().toString().equals("")) {
            savedInstanceState.putString("pass2", "");
        } else {
            savedInstanceState.putString("pass2", Pass2.getText().toString());
        }

        if (Pass3.getText().toString().equals("")) {
            savedInstanceState.putString("pass3", "");
        } else {
            savedInstanceState.putString("pass3", Pass3.getText().toString());
        }*/

    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
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

        /*String pass2 = savedInstanceState.getString("pass2");
        if (!pass2.equals("")) {
            Pass2.setText(pass2);
        }

        String pass3 = savedInstanceState.getString("pass3");
        if (!pass3.equals("")) {
            Pass3.setText(pass3);
        }*/
    }

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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        int menuOpcion = R.menu.menugaleria;
        inflater.inflate(menuOpcion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void onActivityResult(int reqCode, int resCode, @NonNull Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (resCode == RESULT_OK) {
            switch (reqCode) {
                case CAMARA:

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, GALERIA);
                    Toast.makeText(getApplicationContext(), getString(R.string.eligeFoto), Toast.LENGTH_LONG).show();
                    break;

                case GALERIA:
                    Uri selectedimg = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                        maxTexto = (bitmap.getWidth() / 8) - 5;
                        if (maxTexto >= 16) {
                            ImagenOriginal.setImageBitmap(bitmap);
                            TextoAOcultar.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTexto)});
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.imagenGrande), Toast.LENGTH_LONG).show();
                        }

                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.fileNotFoundExcepcion), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.ioException), Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.necesitasImagen), Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(@NonNull View view) {
        super.onClick(view);

        hideKeyboard();
        switch (view.getId()) {
            case R.id.BotonOcultar:
                if (bitmap != null) {
                    recuperarDatos();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.necesitasImagen), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.BotonGuardar:
                assert bitmap != null;
                storeImage(bitmap);
                finish();
                break;
        }
    }

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

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                startActivityForResult(intent, GALERIA);

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
        assert bitmap != null;
        this.bitmap = bitmap.copy(bitmap.getConfig(), true);

        String texto = TextoAOcultar.getText().toString().trim();
        if (texto.equals("0") || texto.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.textoNoVacio), Toast.LENGTH_LONG).show();
            return;
        }

        String pass1 = Pass1.getText().toString().trim();
        if (pass1.equals("") || pass1.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.pass1NoVacia), Toast.LENGTH_LONG).show();
            return;
        }

        /*String pass2 = Pass2.getText().toString().trim();
        String pass3 = Pass3.getText().toString().trim();
        if ((pass2.equals("") || pass2.length() == 0) && (pass3.equals("") || pass3.length() == 0) && Pass2.getVisibility() == View.VISIBLE && Pass3.getVisibility() == View.VISIBLE) {
            mostrarAlertaPass2y3();
            if (!Pass2.getText().equals(Pass1.getText()) && !Pass3.getText().equals(Pass1.getText())) {
                return;
            }
        } else {
            if ((pass2.equals("") || pass2.length() == 0) && Pass2.getVisibility() == View.VISIBLE) {
                mostrarAlertaPass2();
                if (!Pass2.getText().equals(Pass1.getText())) {
                    return;
                }
            }
            if ((pass3.equals("") || pass3.length() == 0) && Pass3.getVisibility() == View.VISIBLE) {
                mostrarAlertaPass3();
                if (!Pass3.getText().equals(Pass1.getText())) {
                    return;
                }
            }
        }*/
        ProgressDialog dialog = ProgressDialog.show(this, getString(R.string.cargando), getString(R.string.espere), true);
        try {
            String textoFinal = seguridad.encrypt(texto, pass1);
            ocultarMensaje(textoFinal.length() + "-" + textoFinal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }

    private void ocultarMensaje(@NonNull String texto) {
        StringBuilder dato = stringToBinary(texto);

        StringBuilder binario;
        int color = 0;
        String cadenaNueva;

        for (int i = 0; i != dato.length(); i++, color = 0) {

            assert bitmap != null;
            color += bitmap.getPixel(0, i);

            binario = stringToBinary(Integer.toString(color));

            cadenaNueva = binario.substring(0, binario.length() - 1);

            cadenaNueva += dato.charAt(i);

            cadenaNueva = binaryToString(cadenaNueva);

            color = Integer.parseInt(cadenaNueva);

            bitmap.setPixel(0, i, color);
        }

        ImagenOriginal.setImageBitmap(bitmap);
        BotonOcultar.setVisibility(View.GONE);
        BotonGuardar.setVisibility(View.VISIBLE);
        BotonGuardar.setOnClickListener(this);
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

        Toast.makeText(getApplicationContext(), getString(R.string.imagenguardadaOK), Toast.LENGTH_LONG).show();
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

        /*private void mostrarAlertaPass2() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getString(R.string.tituloAlertaPass));
        alerta.setMessage(getString(R.string.mensajeAlertaPass2));
        alerta.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pass2.setText(Pass1.getText());
                dialog.cancel();
            }

        });
        alerta.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alerta.show();
    }*/

    /*private void mostrarAlertaPass3() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getString(R.string.tituloAlertaPass));
        alerta.setMessage(getString(R.string.mensajeAlertaPass3));
        alerta.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pass3.setText(Pass1.getText());
                dialog.cancel();
            }

        });
        alerta.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alerta.show();
    }*/

    /*private void mostrarAlertaPass2y3() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle(getString(R.string.tituloAlertaPass));
        alerta.setMessage(getString(R.string.mensajeAlertaPass2y3));
        alerta.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pass2.setText(Pass1.getText());
                Pass3.setText(Pass1.getText());
                dialog.cancel();
            }

        });
        alerta.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alerta.show();
    }*/
}
