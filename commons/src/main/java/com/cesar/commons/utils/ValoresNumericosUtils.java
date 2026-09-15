package com.cesar.commons.utils;

import java.math.BigDecimal;

public class ValoresNumericosUtils {
    public static <N extends Number>void validadNumeroRequerido(N numero){
        if (numero == null)
            throw new IllegalArgumentException("El valor numerico es requerido");
    }
    public static void validarEnteroPositivo(Integer entero, String mensaje){
        validadNumeroRequerido(entero);
        if (entero<=0)
            throw new IllegalArgumentException(mensaje);
    }
    public static void validarBigDecimalPositivo(BigDecimal numero, String mensaje){
        validadNumeroRequerido(numero);
        if(numero.compareTo(BigDecimal.ZERO)<0)
            throw new IllegalArgumentException(mensaje);
    }
    public static void validarLongPositivo (Long numero, String mensaje){
        validadNumeroRequerido(numero);
        if (numero < 0)
            throw new IllegalArgumentException(mensaje);
    }
    public static void validarRangoShort (Short numero,Short min, Short max, String mensaje){
        validadNumeroRequerido(numero);
        if (numero < min || numero > max)
            throw new IllegalArgumentException(mensaje);
    }
    public static void validarRangoDouble (Double numero,Double min, Double max, String mensaje){
        validadNumeroRequerido(numero);
        if (numero < min || numero > max)
            throw new IllegalArgumentException(mensaje);
    }
}
