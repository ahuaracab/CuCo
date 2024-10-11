package com.cuco.exception;

public class Exceptions {
    public String handleApiError(String errorType) {
        return switch (errorType) {
            case "unsupported-code" -> "Código de moneda no admitido.";
            case "malformed-request" -> "Solicitud mal formada.";
            case "invalid-key" -> "API key no válida.";
            case "inactive-account" -> "Cuenta inactiva.";
            case "quota-reached" -> "Se alcanzó la cuota permitida.";
            default -> "Error desconocido: " + errorType;
        };
    }
}
