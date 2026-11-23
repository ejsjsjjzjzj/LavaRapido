package model.service;

import java.util.regex.Pattern;

/**
 * Funções utilitárias de validação e formatação de dados cadastrais.
 */
public final class Validador {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    /** Placa antiga (ABC1234) ou Mercosul (ABC1D23). */
    private static final Pattern PLACA = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    private Validador() {
    }

    public static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    /** Valida o CPF pelos dígitos verificadores. */
    public static boolean cpfValido(String cpf) {
        String d = somenteDigitos(cpf);
        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(d, 9) == d.charAt(9) - '0'
                && digitoVerificador(d, 10) == d.charAt(10) - '0';
    }

    private static int digitoVerificador(String digitos, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (digitos.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    public static String formatarCpf(String cpf) {
        String d = somenteDigitos(cpf);
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    public static boolean telefoneValido(String telefone) {
        int tamanho = somenteDigitos(telefone).length();
        return tamanho == 10 || tamanho == 11;
    }

    public static String formatarTelefone(String telefone) {
        String d = somenteDigitos(telefone);
        if (d.length() == 11) {
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 7) + "-" + d.substring(7);
        }
        return "(" + d.substring(0, 2) + ") " + d.substring(2, 6) + "-" + d.substring(6);
    }

    public static boolean emailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    /** Deixa a placa em maiúsculas e sem hífen/espaços. */
    public static String normalizarPlaca(String placa) {
        return placa == null ? "" : placa.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    public static boolean placaValida(String placaNormalizada) {
        return PLACA.matcher(placaNormalizada).matches();
    }
}
