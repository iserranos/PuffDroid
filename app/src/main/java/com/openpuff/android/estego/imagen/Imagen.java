package com.openpuff.android.estego.imagen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.openpuff.android.seguridad.Seguridad;
import com.openpuff.android.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Imagen {

    private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OpenPuff";
    private Bitmap bitmapMensaje;
    private Bitmap bitmapPortador;
    private String firma;
    private String mensaje;
    private String longitud;
    private int longitudInt;
    private String pass1;
    private String pass2;
    private String pass3;

    public Imagen(Bitmap bitmap) {
        this.bitmapMensaje = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
    }

    public Imagen(Bitmap bitmapPortador, @NonNull String firma, @NonNull String mensaje, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) throws UnsupportedEncodingException {
        Seguridad seguridad = new Seguridad();
        this.bitmapPortador = bitmapPortador;
        this.mensaje = Util.stringToBinary(seguridad.encrypt(mensaje.getBytes(), pass1.trim()));
        this.firma = Util.stringToBinary(seguridad.encrypt(firma.getBytes("UTF-8"), pass3));
        this.longitud = Util.stringToBinary(seguridad.encrypt(String.valueOf(this.mensaje.length()).getBytes("UTF-8"), pass2));
    }

    public Imagen(Bitmap bitmapPortador, String pass1, String pass2, String pass3) throws UnsupportedEncodingException {
        this.bitmapPortador = bitmapPortador;
        this.pass1 = pass1;
        this.pass2 = pass2;
        this.pass3 = pass3;
    }

    public void ocultar() throws IOException {
        ocultarFirma();
        ocultarLongitud();
        ocultarMensaje();
        save();
    }

    public String descubrir() throws Exception {
        Seguridad seguridad = new Seguridad();
        firma = descubrirFirma();
        firma = new String(seguridad.decrypt(firma, pass3), "UTF-8");
        if (firma.equals("")) {
            throw new Exception("No se ha podido extraer la información correctamente");
        }
        longitud = descubrirLongitud();
        longitud = new String(seguridad.decrypt(longitud, pass2), "UTF-8");
        if (longitud.equals("")) {
            throw new Exception("No se ha podido extraer la información correctamente");
        }
        longitudInt = Integer.parseInt(longitud);
        mensaje = descubrirMensaje();
        byte[] datos = seguridad.decrypt(mensaje, pass1);
        if (datos == null) {
            throw new Exception("No se ha podido extraer la información correctamente");
        } else {
            if (firma.equals("png")) {
                datos = Base64.decode(datos, Base64.DEFAULT);
                this.bitmapPortador = BitmapFactory.decodeByteArray(datos, 0, datos.length);
                this.bitmapPortador = Bitmap.createScaledBitmap(bitmapPortador, bitmapPortador.getWidth() * 2, bitmapPortador.getHeight() * 2, true);
                save();
                return "";
            } else if (firma.equals("txt")) {
                return new String(datos, "UTF-8");
            }
        }
        return null;
    }

    public byte[] read() {
        ByteArrayOutputStream streamPortador = new ByteArrayOutputStream();
        bitmapMensaje.compress(Bitmap.CompressFormat.PNG, 100, streamPortador);
        return streamPortador.toByteArray();
    }

    private void save() throws IOException {
        File file = new File(PATH);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(file.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        FileOutputStream fos = new FileOutputStream(mediaFile);
        this.bitmapPortador.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    private void ocultarLongitud() throws IllegalArgumentException {
        int tmp_count = 0;

        if (bitmapPortador.getWidth() >= bitmapPortador.getHeight()) {
            if (bitmapPortador.getWidth() < longitud.length()) {
                throw new IndexOutOfBoundsException("");
            }
            for (int fila = 0; fila < bitmapPortador.getWidth(); fila++) {
                if (tmp_count < longitud.length()) {
                    int color = bitmapPortador.getPixel(0, fila);
                    char last = longitud.charAt(tmp_count);
                    tmp_count++;
                    String valor = Integer.toBinaryString(color);
                    if (valor.charAt(valor.length() - 1) != last) {
                        valor = valor.substring(0, valor.length() - 1);
                        valor += last;
                        color = Integer.parseInt(valor, 2);
                        bitmapPortador.setPixel(0, fila, color);
                    }
                } else {
                    break;
                }
            }
        } else {
            if (bitmapPortador.getHeight() < longitud.length()) {
                throw new IndexOutOfBoundsException("");
            }
            for (int fila = 0; fila < bitmapPortador.getHeight(); fila++) {
                if (tmp_count < longitud.length()) {
                    int color = bitmapPortador.getPixel(fila, 0);
                    char last = longitud.charAt(tmp_count);
                    tmp_count++;
                    String valor = Integer.toBinaryString(color);
                    if (valor.charAt(valor.length() - 1) != last) {
                        valor = valor.substring(0, valor.length() - 1);
                        valor += last;
                        color = Integer.parseInt(valor, 2);
                        bitmapPortador.setPixel(fila, 0, color);
                    }
                } else {
                    break;
                }
            }
        }

    }

    private void ocultarFirma() throws IllegalArgumentException {
        int tmp_count = 0;
        if (bitmapPortador.getWidth() >= bitmapPortador.getHeight()) {
            if (bitmapPortador.getWidth() < firma.length()) {
                throw new IndexOutOfBoundsException("");
            }
            for (int fila = 0; fila < bitmapPortador.getWidth(); fila++) {
                if (tmp_count < firma.length()) {
                    int color = bitmapPortador.getPixel(1, fila);
                    char last = firma.charAt(tmp_count);
                    tmp_count++;
                    String valor = Integer.toBinaryString(color);
                    if (valor.charAt(valor.length() - 1) != last) {
                        valor = valor.substring(0, valor.length() - 1);
                        valor += last;
                        color = Integer.parseInt(valor, 2);
                        bitmapPortador.setPixel(1, fila, color);
                    }
                } else {
                    break;
                }
            }
        } else {
            if (bitmapPortador.getHeight() < firma.length()) {
                throw new IndexOutOfBoundsException("");
            }
            for (int fila = 0; fila < bitmapPortador.getHeight(); fila++) {
                if (tmp_count < firma.length()) {
                    int color = bitmapPortador.getPixel(fila, 1);
                    char last = firma.charAt(tmp_count);
                    tmp_count++;
                    String valor = Integer.toBinaryString(color);
                    if (valor.charAt(valor.length() - 1) != last) {
                        valor = valor.substring(0, valor.length() - 1);
                        valor += last;
                        color = Integer.parseInt(valor, 2);
                        bitmapPortador.setPixel(fila, 1, color);
                    }
                } else {
                    break;
                }
            }
        }
    }

    private void ocultarMensaje() throws IllegalArgumentException {
        int tmp_count = 0;
        if (bitmapPortador.getWidth() * (bitmapPortador.getHeight() - 2) < mensaje.length()) {
            throw new IndexOutOfBoundsException("");
        }
        for (int columna = 2; columna < bitmapPortador.getHeight(); columna++) {
            for (int fila = 0; fila < bitmapPortador.getWidth(); fila++) {
                if (tmp_count < mensaje.length()) {
                    int color = bitmapPortador.getPixel(columna, fila);
                    char last = mensaje.charAt(tmp_count);
                    tmp_count++;
                    String valor = Integer.toBinaryString(color);
                    if (valor.charAt(valor.length() - 1) != last) {
                        valor = valor.substring(0, valor.length() - 1);
                        valor += last;
                        color = Integer.parseInt(valor, 2);
                        bitmapPortador.setPixel(columna, fila, color);
                    }
                } else {
                    return;
                }
            }
        }
    }

    private String descubrirLongitud() {
        String result = "";
        if (bitmapPortador.getWidth() >= bitmapPortador.getHeight()) {
            if (bitmapPortador.getWidth() < 496) {
                throw new IndexOutOfBoundsException("");
            }
            for (int fila = 0; fila < 496; fila++) {
                if ((bitmapPortador.getPixel(0, fila) & 1) == 0) {
                    result += "0";
                } else {
                    result += "1";
                }
            }
        } else {
            if (bitmapPortador.getHeight() < 496) {
                throw new IndexOutOfBoundsException("");
            }
            for (int fila = 0; fila < 496; fila++) {
                if ((bitmapPortador.getPixel(fila, 0) & 1) == 0) {
                    result += "0";
                } else {
                    result += "1";
                }
            }
        }
        return Util.binaryToString(result);
    }

    private String descubrirFirma() {
        String result = "";
        if (bitmapPortador.getWidth() >= bitmapPortador.getHeight()) {
            if (bitmapPortador.getWidth() < 496) {
                throw new IndexOutOfBoundsException("");
            }
            for (int fila = 0; fila < 496; fila++) {
                if ((bitmapPortador.getPixel(1, fila) & 1) == 0) {
                    result += "0";
                } else {
                    result += "1";
                }
            }
        } else {
            if (bitmapPortador.getHeight() < 496) {
                throw new IndexOutOfBoundsException("");
            }
            for (int fila = 0; fila < 496; fila++) {
                if ((bitmapPortador.getPixel(fila, 1) & 1) == 0) {
                    result += "0";
                } else {
                    result += "1";
                }
            }
        }
        return Util.binaryToString(result);
    }

    private String descubrirMensaje() {
        int cont = 0;
        String result = "";
        for (int columna = 2; columna < bitmapPortador.getHeight(); columna++) {
            for (int fila = 0; fila < bitmapPortador.getWidth(); fila++) {
                if (cont < longitudInt) {
                    if ((bitmapPortador.getPixel(columna, fila) & 1) == 0) {
                        result += "0";
                    } else {
                        result += "1";
                    }
                    cont++;
                } else {
                    break;
                }
            }
        }
        return Util.binaryToString(result);
    }

}
