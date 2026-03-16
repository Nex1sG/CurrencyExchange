package main.currencyexchange.exceptions;

public class ExchangeRateAlreadyExistsException extends RuntimeException{
    public ExchangeRateAlreadyExistsException(String message){
        super(message);
    }
}
