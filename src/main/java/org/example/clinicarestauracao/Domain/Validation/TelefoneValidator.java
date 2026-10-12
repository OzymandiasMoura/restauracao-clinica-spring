package org.example.clinicarestauracao.Domain.Validation;

import java.util.regex.Pattern;

public final class TelefoneValidator
{
    private static final Pattern TELEFONE_FIXO_FORMAT = Pattern.compile("\\d{2}[2-5]\\d{7}");
    private static final Pattern CELULAR_FORMAT = Pattern.compile("\\d{2}9\\d{8}");

    private TelefoneValidator()
    {
    }

    public static boolean validate(String value)
    {
        if (value == null || value.isBlank())
        {
            return false;
        }

        String telefone = normalize(value);

        return TELEFONE_FIXO_FORMAT.matcher(telefone).matches() || CELULAR_FORMAT.matcher(telefone).matches();
    }

    public static String normalize(String value)
    {
        if (value == null)
        {
            return null;
        }

        return value.strip().replaceAll("[()\\-\\s]", "");
    }
}
