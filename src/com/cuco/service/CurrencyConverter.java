package com.cuco.service;

import com.cuco.exception.ApiException;
import com.cuco.exception.Exceptions;
import com.cuco.model.Conversion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CurrencyConverter {
    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Conversion convertCurrency(String baseCurrency, String targetCurrency, double amount) {
        try {
            String json = apiClient.getConversionRate(baseCurrency, targetCurrency, amount);
            return gson.fromJson(json, Conversion.class);
        } catch (ApiException e) {
            Exceptions exceptions = new Exceptions();
            String errorMessage = exceptions.handleApiError(e.getErrorType());
            throw new RuntimeException("Error en la conversión: " + errorMessage, e);
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado en la conversión", e);
        }
    }
}
