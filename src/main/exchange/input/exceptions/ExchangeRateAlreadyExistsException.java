package main.exchange.input.exceptions;

public class ExchangeRateAlreadyExistsException extends RuntimeException{
    public ExchangeRateAlreadyExistsException(String message){
        super(message);
    }
}
