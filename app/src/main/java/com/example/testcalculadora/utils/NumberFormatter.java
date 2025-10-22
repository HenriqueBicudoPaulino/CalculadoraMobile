package com.example.testcalculadora.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberFormatter {

    public static String format(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#.##########", symbols);
        String result = df.format(number);

        // Remove trailing zeros after decimal point
        if (result.contains(",")) {
            result = result.replaceAll("0*$", "").replaceAll(",$", "");
        }

        return result;
    }

    public static double parse(String str) {
        return Double.parseDouble(str.replace(",", "."));
    }

    public static boolean isOperator(String str) {
        // MODIFICADO: Adicionado "^" como um operador
        return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("^");
    }
}
