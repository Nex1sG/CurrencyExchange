package main.exchange.input.service;

import main.exchange.input.exceptions.CurrencyAlreadyExistsException;
import main.exchange.input.exceptions.CurrencyNotFoundException;
import main.exchange.input.exceptions.ExchangeRateNotFoundException;
import main.exchange.data.repositories.CurrencyRepository;
import main.exchange.data.models.CurrencyEntity;

import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final static CurrencyRepository currencyRepository = new CurrencyRepository();

    public CurrencyEntity readCurrencyByCode(String code){
        Optional<CurrencyEntity> currencyEntity = currencyRepository.findByCode(code);
        return currencyEntity.orElseThrow(() -> new ExchangeRateNotFoundException(
                new StringBuilder("Exchange rate with code=")
                        .append(code)
                        .append( " is not found in database")
                        .toString()));
    }

    public List<CurrencyEntity> readAllCurrencies(){
        List<CurrencyEntity> currencies = currencyRepository.findAll();
        if(!currencies.isEmpty()) return  currencies;
        throw new ExchangeRateNotFoundException("Exchange rates not found in database");
    }

//    public CurrencyEntity readCurrencyByID(int id){
//        Optional<CurrencyEntity> currencyEntity = currencyRepository.findById(id);
//        return currencyEntity.orElseThrow(() -> new ExchangeRateNotFoundException(
//                new StringBuilder("Exchange rate with id=")
//                        .append(id)
//                        .append( " is not found in database")
//                        .toString()));
//    }

    public void createCurrency(CurrencyEntity currency){
        currencyRepository.save(currency);
    }

    public void updateCurrency(CurrencyEntity currency){
        boolean isAffected = currencyRepository.update(currency);
        if(!isAffected) throw new CurrencyAlreadyExistsException(
                new StringBuilder("Currency with code =")
                        .append(currency.getCode())
                        .append(" already exists")
                        .toString());
       }

    public void deleteCurrency(long id){
        boolean isAffected = currencyRepository.delete(id);
        if(!isAffected) throw new CurrencyNotFoundException(
                new StringBuilder("Currency with id =")
                        .append(id)
                        .append(" not found in database")
                        .toString());
    }
}
