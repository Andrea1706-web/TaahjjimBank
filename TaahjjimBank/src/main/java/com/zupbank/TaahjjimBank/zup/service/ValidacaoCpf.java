package com.zupbank.TaahjjimBank.zup.service;

import org.springframework.stereotype.Service;

@Service
public class ValidacaoCpf {

    public static boolean isValid(String cpfProprietario) {
        return false;
    }

    public boolean isValidCpf(String cpf) {
        // Remover caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Verificar se o CPF tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verificar se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcular os dígitos verificadores
        int[] pesos = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int primeiroDigito = calcularDigitoVerificador(cpf.substring(0, 9), pesos);
        int segundoDigito = calcularDigitoVerificador(cpf.substring(0, 9) + primeiroDigito, new int[]{11, 10, 9, 8, 7, 6, 5, 4, 3, 2});

        // Verificar se os dígitos verificadores são válidos
        return cpf.equals(cpf.substring(0, 9) + primeiroDigito + segundoDigito);
    }

    private int calcularDigitoVerificador(String str, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < str.length(); i++) {
            soma += Character.getNumericValue(str.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}

