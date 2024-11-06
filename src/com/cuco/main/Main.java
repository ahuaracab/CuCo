package com.cuco.main;

import com.cuco.model.Conversion;
import com.cuco.service.CurrencyConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String HISTORY_JSON = "src/resources/history.json";
    private static final Path HISTORY_PATH = Paths.get(HISTORY_JSON);
    private static final String CURRENCIES_JSON = "src/resources/currencies.json";
    private static final Path CURRENCIES_PATH = Paths.get(CURRENCIES_JSON);
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CurrencyConverter currencyConverter = new CurrencyConverter();

        printBanner();

        while (true) {
            printMenu();

            System.out.print(ANSI_RED + "Seleccione una opción: " + ANSI_RESET);
            int option = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            if (option == 1) {
                System.out.print("Ingrese el código de moneda base (Ejm: USD): ");
                String baseCurrency = scanner.nextLine().toUpperCase();
                System.out.print("Ingrese el código de moneda objetivo (Ejm: PEN): ");
                String targetCurrency = scanner.nextLine().toUpperCase();
                System.out.print("Ingrese el monto a convertir (Ejm: 150.65): ");
                double amount = scanner.nextDouble();

                try {
                    Conversion conversion = currencyConverter.convertCurrency(baseCurrency, targetCurrency, amount);
                    String result = amount + " " + baseCurrency + " equivalen a " + conversion.conversion_result() + " " + targetCurrency;
                    System.out.println(result);
                    saveToJson(baseCurrency, targetCurrency, amount, conversion.conversion_result());
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Finalizando la aplicación");
                }

            } else if (option == 2) {
                displayCurrencyCodes();

            } else if (option == 3) {
                displayHistory();

            } else if (option == 4) {
                deleteHistoryJson();

            } else if (option == 5) {
                System.out.println("Saliendo de la aplicación.");
                break;

            } else {
                System.out.println("Opción no válida, por favor intente nuevamente.");
            }
        }
    }

    private static void printBanner() {
        String banner = ANSI_RED + """
                ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                ░░░░█████╗░░░░░░░░░░ █████╗░░░░░░░░░░░
                ░░░██╔══██╗██╗░░░██╗██╔══██╗░█████╗░░░
                ░░░██║░░╚═╝██║░░░██║██║░░╚═╝██╔══██╗░░
                ░░░██║░░██╗██║░░░██║██║░░██╗██║░░██║░░
                ░░░╚█████╔╝╚██████╔╝╚█████╔╝╚█████╔╝░░
                ░░░░╚════╝░░╚═════╝░░╚════╝░░╚════╝░░░
                ░░░░░░░░░░CUrrency COnverter░░░░░░░░░░
                ░░░░░░░░Autor: Angelo Huaraca░░░░░░░░░
                """ + ANSI_RESET;

        System.out.println(banner);
    }

    private static void printMenu() {
        System.out.println(ANSI_BLUE + "╔═════════════════════════════════════╗");
        System.out.println("║                MENÚ                 ║");
        System.out.println("╠═════════════════════════════════════╣");
        System.out.println("║ 1. Convertir monedas                ║");
        System.out.println("║ 2. Ver código de monedas            ║");
        System.out.println("║ 3. Ver historial de conversiones    ║");
        System.out.println("║ 4. Borrar historial de conversiones ║");
        System.out.println("║ 5. Salir                            ║");
        System.out.println("╚═════════════════════════════════════╝" + ANSI_RESET);
    }


    private static void saveToJson(String baseCurrency, String targetCurrency, double baseAmount, double convertedAmount) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JsonObject conversionJson = new JsonObject();
        conversionJson.addProperty("baseCurrency", baseCurrency);
        conversionJson.addProperty("targetCurrency", targetCurrency);
        conversionJson.addProperty("baseAmount", baseAmount);
        conversionJson.addProperty("convertedAmount", convertedAmount);
        conversionJson.addProperty("dateTime", dateTime.format(formatter));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray historyJsonArray;

        try {
            if (Files.exists(HISTORY_PATH)) {
                String content = new String(Files.readAllBytes(HISTORY_PATH));
                historyJsonArray = gson.fromJson(content, JsonArray.class);
            } else {
                historyJsonArray = new JsonArray();
            }

            historyJsonArray.add(conversionJson);

            Files.write(HISTORY_PATH, gson.toJson(historyJsonArray).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.out.println("Error al guardar la conversión en JSON: " + e.getMessage());
        }
    }

    private static void displayHistory() {
        try {
            if (Files.exists(HISTORY_PATH)) {
                String content = new String(Files.readAllBytes(HISTORY_PATH));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonArray historyJsonArray = gson.fromJson(content, JsonArray.class);

                if (historyJsonArray.isEmpty()) {
                    System.out.println("\n=== Historial de Conversiones ===");
                    System.out.println("No hay historial de conversiones.");
                } else {
                    System.out.println("\n=== Historial de Conversiones ===");
                    for (int i = 0; i < historyJsonArray.size(); i++) {
                        JsonObject conversion = historyJsonArray.get(i).getAsJsonObject();
                        System.out.println("Fecha y Hora: " + conversion.get("dateTime").getAsString());
                        System.out.println("Moneda Base: " + conversion.get("baseCurrency").getAsString());
                        System.out.println("Moneda Objetivo: " + conversion.get("targetCurrency").getAsString());
                        System.out.println("Monto Base: " + conversion.get("baseAmount").getAsDouble());
                        System.out.println("Monto Convertido: " + conversion.get("convertedAmount").getAsDouble());
                        System.out.println("----------------------------");
                    }
                }

            } else {
                System.out.println("No hay historial de conversiones.");
            }

        } catch (IOException e) {
            System.out.println("Error al leer el historial: " + e.getMessage());
        }
    }

    private static void deleteHistoryJson() {
        try {
            Files.deleteIfExists(HISTORY_PATH);
            System.out.println("El historial de conversiones ha sido eliminado.");
        } catch (IOException e) {
            System.out.println("Error al eliminar el historial de conversiones: " + e.getMessage());
        }
    }

    private static void displayCurrencyCodes() {
        try {
            if (Files.exists(CURRENCIES_PATH)) {
                String content = new String(Files.readAllBytes(CURRENCIES_PATH));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> currencyCodes = gson.fromJson(content, type);

                System.out.println("\n=== Códigos de Monedas ===");
                for (Map.Entry<String, String> entry : currencyCodes.entrySet()) {
                    System.out.printf("%-10s: %s%n", entry.getKey(), entry.getValue());
                }
                System.out.println("========================");
            } else {
                System.out.println("Los códigos de monedas no existe.");
            }
        } catch (IOException e) {
            System.out.println("Error al leer los códigos de monedas: " + e.getMessage());
        }
    }
}
