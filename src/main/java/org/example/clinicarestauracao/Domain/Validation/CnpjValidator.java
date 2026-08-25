package org.example.clinicarestauracao.Domain.Validation;

import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.regex.Pattern;

@NoArgsConstructor
public class CnpjValidator
{
    private static final Pattern formatCNPJ = Pattern.compile("[A-Z0-9]{12}[0-9]{2}");
    private static final int[] pesosPrimeiroDigito = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesosSegundoDigito = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public static boolean validate(String CNPJ)
    {
        if (CNPJ == null || CNPJ.isBlank())
        {
            return false;
        }

        String cnpj = normalize(CNPJ);

        if (!formatCNPJ.matcher(cnpj).matches())
        {
            return false;
        }
        if(hasAllCharactersEqual(cnpj))
        {
            return false;
        }

        String base = cnpj.substring(0, 12);

        int primeiroDigito = calcularDigito(base, pesosPrimeiroDigito);
        int segundoDigito = calcularDigito(base + primeiroDigito, pesosSegundoDigito);

        int informedFirstDigit = Character.digit(cnpj.charAt(12), 10);
        int informedSecondDigit = Character.digit(cnpj.charAt(13), 10);

        return primeiroDigito == informedFirstDigit && segundoDigito == informedSecondDigit;

    }

    private static int calcularDigito(String base, int[] pesos)
    {
        int soma = 0;

        for (int i = 0; i < base.length(); i++)
        {
            int valor = base.charAt(i) - '0';
            soma += valor * pesos[i];
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
    public static String normalize(String value)
    {
        if (value == null)
        {
            return null;
        }

        return value.strip().toUpperCase(Locale.ROOT).replaceAll("[.\\-/\\s]", "");
    }

    private static boolean hasAllCharactersEqual(String cnpj)
    {
        char firstCharacter = cnpj.charAt(0);

        return cnpj.chars().allMatch(character -> character == firstCharacter);
    }
}
