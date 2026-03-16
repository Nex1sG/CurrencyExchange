package main.currencyexchange.utils;

public class Utils {

    public static boolean isCorrectExchangeRequest(String baseCode, String targetCode, int amount){

        if(baseCode == null || baseCode.isEmpty()) return false;

        if(targetCode == null || targetCode.isEmpty()) return false;

        return amount >= 0 && amount <= 100_000_000;
    }


}
