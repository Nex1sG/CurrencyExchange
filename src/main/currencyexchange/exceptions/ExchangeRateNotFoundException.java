package main.currencyexchange.exceptions;

public class ExchangeRateNotFoundException extends RuntimeException{
    public ExchangeRateNotFoundException(String message){
        super(message);
    }
}
