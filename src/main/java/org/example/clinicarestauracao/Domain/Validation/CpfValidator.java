package org.example.clinicarestauracao.Domain.Validation;

import java.util.regex.Pattern;

public class CpfValidator
{
    private static final Pattern cpfFormat = Pattern.compile("\\d{11}");

    private CpfValidator()
    {
    }

    public static boolean validate(String value)
    {
        if (value == null || value.isBlank())
        {
            return false;
        }

        String cpf = normalize(value);

        if (!cpfFormat.matcher(cpf).matches())
        {
            return false;
        }

        if (hasAllDigitsEqual(cpf))
        {
            return false;
        }

        int firstDigit = calculateDigit(cpf.substring(0, 9), 10);
        int secondDigit = calculateDigit(cpf.substring(0, 9) + firstDigit, 11);

        return firstDigit == Character.digit(cpf.charAt(9), 10) && secondDigit == Character.digit(cpf.charAt(10), 10);
    }

    public static String normalize(String value)
    {
        if (value == null)
        {
            return null;
        }

        return value.strip().replaceAll("[.\\-\\s]", "");
    }

    private static int calculateDigit(String base, int initialWeight)
    {
        int sum = 0;

        for (int i = 0; i < base.length(); i++)
        {
            int digit = Character.digit(base.charAt(i), 10);
            sum += digit * (initialWeight - i);
        }

        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean hasAllDigitsEqual(String cpf)
    {
        char firstDigit = cpf.charAt(0);

        return cpf.chars().allMatch(digit -> digit == firstDigit);
    }
}
