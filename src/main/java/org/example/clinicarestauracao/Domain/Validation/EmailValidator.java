package org.example.clinicarestauracao.Domain.Validation;

import java.util.Locale;
import java.util.regex.Pattern;

public final class EmailValidator
{
    private static final Pattern emailFormat = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private EmailValidator()
    {
    }

    public static boolean validate(String value)
    {
        if (value == null || value.isBlank())
        {
            return false;
        }

        String email = normalize(value);

        if (email.length() > 254)
        {
            return false;
        }

        if (email.contains(".."))
        {
            return false;
        }

        return emailFormat.matcher(email).matches();
    }

    public static String normalize(String value)
    {
        if (value == null)
        {
            return null;
        }

        return value.strip().toLowerCase(Locale.ROOT);
    }
}
