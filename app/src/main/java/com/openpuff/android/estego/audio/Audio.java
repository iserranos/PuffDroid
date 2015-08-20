package com.openpuff.android.estego.audio;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.openpuff.android.seguridad.Seguridad;
import com.openpuff.android.utils.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Audio {
/*
     WAV File Specification
     FROM http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
    The canonical WAVE format starts with the RIFF header:
    0         4   ChunkID          Contains the letters "RIFF" in ASCII form
                                   (0x52494646 big-endian form).
    4         4   ChunkSize        36 + SubChunk2Size, or more precisely:
                                   4 + (8 + SubChunk1Size) + (8 + SubChunk2Size)
                                   This is the size of the rest of the chunk
                                   following this number.  This is the size of the
                                   entire file in bytes minus 8 bytes for the
                                   two fields not included in this count:
                                   ChunkID and ChunkSize.
    8         4   Format           Contains the letters "WAVE"
                                   (0x57415645 big-endian form).

    The "WAVE" format consists of two subchunks: "fmt " and "data":
    The "fmt " subchunk describes the sound data's format:
    12        4   Subchunk1ID      Contains the letters "fmt "
                                   (0x666d7420 big-endian form).
    16        4   Subchunk1Size    16 for PCM.  This is the size of the
                                   rest of the Subchunk which follows this number.
    20        2   AudioFormat      PCM = 1 (i.e. Linear quantization)
                                   Values other than 1 indicate some
                                   form of compression.
    22        2   NumChannels      Mono = 1, Stereo = 2, etc.
    24        4   SampleRate       8000, 44100, etc.
    28        4   ByteRate         == SampleRate * NumChannels * BitsPerSample/8
    32        2   BlockAlign       == NumChannels * BitsPerSample/8
                                   The number of bytes for one sample including
                                   all channels. I wonder what happens when
                                   this number isn't an integer?
    34        2   BitsPerSample    8 bits = 8, 16 bits = 16, etc.

    The "data" subchunk contains the size of the data and the actual sound:
    36        4   Subchunk2ID      Contains the letters "data"
                                   (0x64617461 big-endian form).
    40        4   Subchunk2Size    == NumSamples * NumChannels * BitsPerSample/8
                                   This is the number of bytes in the data.
                                   You can also think of this as the size
                                   of the read of the subchunk following this
                                   number.
    44        *   Data             The actual sound data.


NOTE TO READERS:

The thing that makes reading wav files tricky is that java has no unsigned types.  This means that the
binary data can't just be read and cast appropriately.  Also, we have to use larger types
than are normally necessary.

In many languages including java, an integer is represented by 4 bytes.  The issue here is
that in most languages, integers can be signed or unsigned, and in wav files the  integers
are unsigned.  So, to make sure that we can store the proper values, we have to use longs
to hold integers, and integers to hold shorts.

Then, we have to convert back when we want to saveAudio our wav data.

It's complicated, but ultimately, it just results in a few extra functions at the bottom of
this file.  Once you understand the issue, there is no reason to pay any more attention
to it.


ALSO:

This code won't read ALL wav files.  This does not use to full specification.  It just uses
a trimmed down version that most wav files adhere to.


*/

    private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OpenPuff/";
    private String cancion;
    private long myChunkSize;
    private long mySubChunk1Size;
    private int myFormat;
    private long myChannels;
    private long mySampleRate;
    private long myByteRate;
    private int myBlockAlign;
    private int myBitsPerSample;
    private long myDataSize;
    private byte[] datos;
    private String firma;
    private String longitud;
    private String mensaje;
    private int longitudInt;
    private String pass1;
    private String pass2;
    private String pass3;

    public Audio(@NonNull String cancion, @NonNull String firma, @NonNull String mensaje, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) throws IOException {
        Seguridad seguridad = new Seguridad();
        this.mensaje = Util.stringToBinary(seguridad.encrypt(mensaje.getBytes(), pass1.trim()));
        this.firma = Util.stringToBinary(seguridad.encrypt(firma.getBytes("UTF-8"), pass3));
        this.longitud = Util.stringToBinary(seguridad.encrypt(String.valueOf(this.mensaje.length()).getBytes("UTF-8"), pass2));
        this.cancion = cancion;
    }

    public Audio(@NonNull String cancion, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        this.cancion = cancion;
        this.pass1 = pass1;
        this.pass2 = pass2;
        this.pass3 = pass3;
    }

    // these two routines convert a byte array to a unsigned short
    private static int byteArrayToInt(byte[] b) {
        int start = 0;
        int low = b[start] & 0xff;
        int high = b[start + 1] & 0xff;
        return high << 8 | low;
    }

    // these two routines convert a byte array to an unsigned integer
    private static long byteArrayToLong(byte[] b) {
        int start = 0;
        int i;
        int len = 4;
        int cnt = 0;
        byte[] tmp = new byte[len];
        for (i = start; i < (start + len); i++) {
            tmp[cnt] = b[i];
            cnt++;
        }
        long accum = 0;
        i = 0;
        for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
            accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
            i++;
        }
        return accum;
    }

    // ===========================
// CONVERT JAVA TYPES TO BYTES
// ===========================
    // returns a byte array of length 4
    @NonNull
    private static byte[] intToByteArray(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0x00FF);
        b[1] = (byte) ((i >> 8) & 0x000000FF);
        b[2] = (byte) ((i >> 16) & 0x000000FF);
        b[3] = (byte) ((i >> 24) & 0x000000FF);
        return b;
    }

    // convert a short to a byte array
    @NonNull
    private static byte[] shortToByteArray(short data) {
        return new byte[]{(byte) (data & 0xff), (byte) ((data >>> 8) & 0xff)};
    }

    public void ocultar() throws IOException {
        read();
        ocultarFirma();
        ocultarLongitud();
        ocultarDatos();
        saveAudio();
    }

    public String descubrir() throws Exception {
        Seguridad seguridad = new Seguridad();
        read();
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
                Bitmap bitmap = BitmapFactory.decodeByteArray(datos, 0, datos.length);
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2, true);
                saveImage(bitmap);
                return "";
            } else if (firma.equals("txt")) {
                return new String(datos, "UTF-8");
            }
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] read() {
        DataInputStream inFile;
        datos = null;
        byte[] tmpLong = new byte[4];
        byte[] tmpInt = new byte[2];

        try {
            inFile = new DataInputStream(new FileInputStream(cancion));
            String chunkID = "" + (char) inFile.readByte() + (char) inFile.readByte() + (char) inFile.readByte() + (char) inFile.readByte();
            inFile.read(tmpLong); // read the ChunkSize
            myChunkSize = byteArrayToLong(tmpLong);
            String format = "" + (char) inFile.readByte() + (char) inFile.readByte() + (char) inFile.readByte() + (char) inFile.readByte();
            String subChunk1ID = "" + (char) inFile.readByte() + (char) inFile.readByte() + (char) inFile.readByte() + (char) inFile.readByte();
            inFile.read(tmpLong); // read the SubChunk1Size
            mySubChunk1Size = byteArrayToLong(tmpLong);
            inFile.read(tmpInt); // read the audio format.  This should be 1 for PCM
            myFormat = byteArrayToInt(tmpInt);
            inFile.read(tmpInt); // read the # of channels (1 or 2)
            myChannels = byteArrayToInt(tmpInt);
            inFile.read(tmpLong); // read the samplerate
            mySampleRate = byteArrayToLong(tmpLong);
            inFile.read(tmpLong); // read the byterate
            myByteRate = byteArrayToLong(tmpLong);
            inFile.read(tmpInt); // read the blockalign
            myBlockAlign = byteArrayToInt(tmpInt);
            inFile.read(tmpInt); // read the bitspersample
            myBitsPerSample = byteArrayToInt(tmpInt);
            // print what we've read so far
            System.out.println("SubChunk1ID:" + subChunk1ID + " SubChunk1Size:" + mySubChunk1Size + " AudioFormat:" + myFormat + " Channels:" + myChannels + " SampleRate:" + mySampleRate);
            // read the data chunk header - reading this IS necessary, because not all wav files will have the data chunk here - for now, we're just assuming that the data chunk is here
            String dataChunkID = "" + (char) inFile.readByte() + (char) inFile.readByte() + (char) inFile.readByte() + (char) inFile.readByte();
            inFile.read(tmpLong); // read the size of the data
            myDataSize = byteArrayToLong(tmpLong);
            // read the data chunk
            datos = new byte[(int) myDataSize];
            inFile.read(datos);
            // close the input stream
            inFile.close();
        } catch (Exception e) {
            return null;
        }

        return datos;
    }

    // write out the wav file
    private void saveAudio() throws IOException {
        File dir = new File(PATH);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return;
            }
        }

        String[] nombre = cancion.split("/");
        String name = PATH + nombre[nombre.length - 1];

        DataOutputStream outFile = new DataOutputStream(new FileOutputStream(name));

        // write the wav file per the wav file format
        outFile.writeBytes("RIFF");                    // 00 - RIFF
        outFile.write(intToByteArray((int) myChunkSize), 0, 4);        // 04 - how big is the rest of this file?
        outFile.writeBytes("WAVE");                    // 08 - WAVE
        outFile.writeBytes("fmt ");                    // 12 - fmt
        outFile.write(intToByteArray((int) mySubChunk1Size), 0, 4);    // 16 - size of this chunk
        outFile.write(shortToByteArray((short) myFormat), 0, 2);        // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
        outFile.write(shortToByteArray((short) myChannels), 0, 2);    // 22 - mono or stereo? 1 or 2?  (or 5 or ???)
        outFile.write(intToByteArray((int) mySampleRate), 0, 4);        // 24 - samples per second (numbers per second)
        outFile.write(intToByteArray((int) myByteRate), 0, 4);        // 28 - bytes per second
        outFile.write(shortToByteArray((short) myBlockAlign), 0, 2);    // 32 - # of bytes in one sample, for all channels
        outFile.write(shortToByteArray((short) myBitsPerSample), 0, 2);    // 34 - how many bits in a sample(number)?  usually 16 or 24
        outFile.writeBytes("data");                    // 36 - data
        outFile.write(intToByteArray((int) myDataSize), 0, 4);        // 40 - how big is this data chunk
        outFile.write(datos);                        // 44 - the actual data itself - just a long string of numbers
        outFile.flush();
        outFile.close();
    }

    private void saveImage(Bitmap bitmap) throws IOException {
        File file = new File(PATH);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(file.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        FileOutputStream fos = new FileOutputStream(mediaFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    private void ocultarFirma() {
        if (firma != null) {
            String valor;
            String binario;
            String byteFinal;
            int longitud = firma.length();
            int j = 0;
            char last;
            for (int i = 0; i < longitud; i++) {
                valor = String.valueOf(datos[i]);
                last = this.firma.charAt(j);
                binario = Util.stringToBinary(valor);
                if (binario.charAt(binario.length() - 1) != last) {
                    binario = binario.substring(0, binario.length() - 1);
                    byteFinal = Util.binaryToString(binario + last);
                    if (byteFinal.equals("129")) {
                        byteFinal = "121";
                    }
                    if (byteFinal.equals("-129")) {
                        byteFinal = "-121";
                    }
                    datos[i] = Byte.valueOf(byteFinal);
                }
                j++;
            }
        }
    }

    private void ocultarLongitud() {
        if (this.longitud != null) {
            String binario;
            String byteFinal;
            int j = 0;
            String valor;
            char last;
            int longitud = 992;
            for (int i = 496; i < longitud; i++) {
                valor = String.valueOf(datos[i]);
                last = this.longitud.charAt(j);
                binario = Util.stringToBinary(valor);
                if (binario.charAt(binario.length() - 1) != last) {
                    binario = binario.substring(0, binario.length() - 1);
                    byteFinal = Util.binaryToString(binario + last);
                    if (byteFinal.equals("129")) {
                        byteFinal = "121";
                    }
                    if (byteFinal.equals("-129")) {
                        byteFinal = "-121";
                    }
                    datos[i] = Byte.valueOf(byteFinal);
                }
                j++;
            }
        }
    }

// ===========================
// CONVERT BYTES TO JAVA TYPES
// ===========================

    private void ocultarDatos() {
        String binario, byteFinal, valor;
        int longitud = mensaje.length() + 992, j = 0;
        char last;
        for (int i = 992; i < longitud; i++, j++) {
            valor = String.valueOf(datos[i]);
            last = this.mensaje.charAt(j);
            binario = Util.stringToBinary(valor);
            if (binario.charAt(binario.length() - 1) != last) {
                binario = binario.substring(0, binario.length() - 1);
                byteFinal = Util.binaryToString(binario + last);
                if (byteFinal.equals("129")) {
                    byteFinal = "121";
                }
                if (byteFinal.equals("-129")) {
                    byteFinal = "-121";
                }
                datos[i] = Byte.valueOf(byteFinal);
            }
        }
    }

    private String descubrirFirma() {
        String binario = "";
        for (int i = 0; i < 496; i++) {
            if ((datos[i] & 1) == 0) {
                binario += "0";
            } else {
                binario += "1";
            }
        }
        return Util.binaryToString(binario);
    }

    private String descubrirLongitud() {
        String binario = "";
        for (int i = 496; i < 992; i++) {
            if ((datos[i] & 1) == 0) {
                binario += "0";
            } else {
                binario += "1";
            }
        }
        return Util.binaryToString(binario);
    }

    private String descubrirMensaje() {
        String binario = "";
        longitudInt += 992;
        for (int i = 992; i < longitudInt; i++) {
            if ((datos[i] & 1) == 0) {
                binario += "0";
            } else {
                binario += "1";
            }
        }
        return Util.binaryToString(binario);
    }

}
