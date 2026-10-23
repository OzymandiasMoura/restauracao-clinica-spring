package org.example.clinicarestauracao.Domain.Validation;

import java.util.regex.Pattern;

public final class CepValidator
{
    private static final Pattern cepFormat = Pattern.compile("(\\d{8}|\\d{5}-\\d{3})");

    private CepValidator()
    {
    }

    public static boolean validate(String value)
    {
        if (value == null || value.isBlank())
        {
            return false;
        }

        String cep = normalize(value);

        return cepFormat.matcher(cep).matches();
    }

    public static String normalize(String value)
    {
        if (value == null)
        {
            return null;
        }

        return value.strip().replace("-", "");
    }
}
