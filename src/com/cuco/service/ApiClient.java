package com.cuco.service;

import com.cuco.exception.ApiError;
import com.cuco.exception.ApiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class ApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String apiKey;

    public ApiClient() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/config.properties"));
            this.apiKey = properties.getProperty("API_KEY");
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el archivo de configuración", e);
        }
    }

    public String getConversionRate(String baseCurrency, String targetCurrency, double amount) {
        URI uri = URI.create("https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/"
                + baseCurrency + "/" + targetCurrency + "/" + amount);

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                String json = response.body();
                ApiError error = gson.fromJson(json, ApiError.class);
                String errorType = error.errorType();

                if (errorType != null) {
                    throw new ApiException("Error de la API: " + errorType, errorType);
                } else {
                    throw new ApiException("Error desconocido de la API", "desconocido");
                }
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Ocurrió un error en la conexión a la API", e);
        }
    }
}
