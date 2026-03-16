package main.currencyexchange.servlet;

import main.currencyexchange.repositories.CurrencyRepository;
import main.currencyexchange.models.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final static CurrencyRepository currencyRepository = new CurrencyRepository();

    protected Currency readReq(String code){
        Optional<Currency> currency = currencyRepository.findByCode(code);
        if(currency.isPresent()) return currency.get();
        throw new NullPointerException();
    }

    protected List<Currency> readReq(){
        Optional<List<Currency>> currencies = currencyRepository.findAll();
        if(currencies.isPresent()) return  currencies.get();
        throw new NullPointerException();
    }

    protected Currency readReq(int id){
        Optional<Currency> currency = currencyRepository.findById(id);
        if(currency.isPresent()) return currency.get();
        throw new NullPointerException();
    }

}
