package main.currencyexchange.input.exceptions;

public class ExchangeRateNotFoundException extends RuntimeException{
    public ExchangeRateNotFoundException(String message){
        super(message);
    }
}
