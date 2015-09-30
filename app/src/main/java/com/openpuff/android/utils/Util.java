package com.openpuff.android.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Util {

    public static String stringToBinary(String input) {
        byte[] bytes = input.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary.toString();
    }

    public static String binaryToString(String input) {
        String output = "";
        for (int i = 0; i <= input.length() - 8; i += 8) {
            int k = Integer.parseInt(input.substring(i, i + 8), 2);
            output += (char) k;
        }
        return output;
    }

    public static void showAToast(Context applicationContext, String string, int lengthLong) {
        Toast.makeText(applicationContext, string, lengthLong).show();
    }

    public static void hideKeyboard(View currentFocus, InputMethodManager systemService) {
        systemService.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }
}
