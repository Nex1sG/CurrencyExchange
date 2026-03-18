package main.currencyexchange.input.exceptions;

public class ExchangeRateAlreadyExistsException extends RuntimeException{
    public ExchangeRateAlreadyExistsException(String message){
        super(message);
    }
}
