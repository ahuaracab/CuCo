package com.cuco.main;

import com.cuco.model.Conversion;
import com.cuco.service.CurrencyConverter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el código de moneda base: ");
        String baseCurrency = scanner.nextLine().toUpperCase();
        System.out.print("Ingrese el código de moneda objetivo: ");
        String targetCurrency = scanner.nextLine().toUpperCase();
        System.out.print("Ingrese el monto a convertir: ");
        double amount = scanner.nextDouble();

        try {
            CurrencyConverter currencyConverter = new CurrencyConverter();
            Conversion conversion = currencyConverter.convertCurrency(baseCurrency, targetCurrency, amount);
            System.out.println(amount + " " + baseCurrency + " equivalen a " + conversion.conversion_result() + " " + targetCurrency);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println("Finalizando la aplicación");
        }
    }
}
