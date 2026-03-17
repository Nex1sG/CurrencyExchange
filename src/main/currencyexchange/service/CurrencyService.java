package main.currencyexchange.service;

import main.currencyexchange.exceptions.ExchangeRateNotFoundException;
import main.currencyexchange.repositories.CurrencyRepository;
import main.currencyexchange.models.Currency;

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

}
