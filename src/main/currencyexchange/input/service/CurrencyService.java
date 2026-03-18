package main.currencyexchange.input.service;

import main.currencyexchange.input.exceptions.CurrencyAlreadyExistsException;
import main.currencyexchange.input.exceptions.CurrencyNotFoundException;
import main.currencyexchange.input.exceptions.ExchangeRateNotFoundException;
import main.currencyexchange.data.repositories.CurrencyRepository;
import main.currencyexchange.data.models.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final static CurrencyRepository currencyRepository = new CurrencyRepository();

    public Currency readReq(String code){
        Optional<Currency> currency = currencyRepository.findByCode(code);
        if(currency.isPresent()) return currency.get();
        throw new ExchangeRateNotFoundException("Exchange rate with code=" + code +
                " is not found in database");
    }

    public List<Currency> readReq(){
        List<Currency> currencies = currencyRepository.findAll();
        if(!currencies.isEmpty()) return  currencies;
        throw new ExchangeRateNotFoundException("Exchange rates not found in database");
    }

    public Currency readReq(int id){
        Optional<Currency> currency = currencyRepository.findById(id);
        if(currency.isPresent()) return currency.get();
        throw new ExchangeRateNotFoundException("Exchange rate with id=" + id +
                " is not found in database");
    }

    public void create(Currency currency){
        currencyRepository.save(currency);
    }

    public void update(Currency currency){
        boolean isAffected = currencyRepository.update(currency);
        if(!isAffected) throw new CurrencyAlreadyExistsException("Currency with code =" +
                currency.getCode() + " already exists");
       }

    public void delete(long id){
        boolean isAffected = currencyRepository.delete(id);
        if(!isAffected) throw new CurrencyNotFoundException("Currency with id =" +
                id + " not found");
    }
}
