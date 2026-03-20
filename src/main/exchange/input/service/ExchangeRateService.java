package main.exchange.input.service;

import main.exchange.data.models.ExchangeRateEntity;
import main.exchange.data.repositories.CurrencyRepository;
import main.exchange.data.repositories.ExchangeRateRepository;
import main.exchange.input.exceptions.CurrencyNotFoundException;
import main.exchange.input.exceptions.ExchangeRateAlreadyExistsException;
import main.exchange.input.exceptions.ExchangeRateNotFoundException;

import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

    private final static CurrencyRepository currencyRepository = new CurrencyRepository();
    private final static ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(currencyRepository);

    public ExchangeRateEntity readExchangeRate(String baseCode, String targetCode){
        Optional<ExchangeRateEntity> exchangeRate = exchangeRateRepository.findByCurrencies(baseCode, targetCode);
        if(exchangeRate.isPresent()) return exchangeRate.get();
        throw new ExchangeRateNotFoundException(
                new StringBuilder("Exchange rate with base/target code=")
                        .append(baseCode)
                        .append(targetCode)
                        .append(" is not found in database").toString());
    }

    public List<ExchangeRateEntity> readExchangeRate(){
        List<ExchangeRateEntity> exchangeRates = exchangeRateRepository.findAll();
        if(!exchangeRates.isEmpty()) return  exchangeRates;
        throw new ExchangeRateNotFoundException("Exchange rates not found in database");
    }

//    public ExchangeRateEntity readExchangeRate(int id){
//        Optional<ExchangeRateEntity> exchangeRate = exchangeRateRepository.findById(id);
//        if(exchangeRate.isPresent()) return exchangeRate.get();
//        throw new ExchangeRateNotFoundException(
//                new StringBuilder("Exchange rate with id=")
//                        .append(id)
//                        .append(" is not found in database").toString());
//    }

    public void create(ExchangeRateEntity exchangeRate) {
        if (exchangeRateRepository.findByCurrencies(
                exchangeRate.getBaseCurrency().getCode(),
                exchangeRate.getTargetCurrency().getCode()
        ).isPresent()) {
            throw new ExchangeRateAlreadyExistsException("Exchange rate already exists");
        }

        exchangeRateRepository.save(exchangeRate);
    }
    public void update(ExchangeRateEntity exchangeRate){
        Optional<ExchangeRateEntity> existing = exchangeRateRepository.findByCurrencies(
                exchangeRate.getBaseCurrency().getCode(),
                exchangeRate.getTargetCurrency().getCode()
        );

        if (existing.isEmpty()) {
            throw new ExchangeRateNotFoundException("Exchange rate not found");
        }

        existing.get().setRate(exchangeRate.getRate());

        exchangeRateRepository.update(existing.get());
    }

    public void delete(long id){
        boolean isAffected = exchangeRateRepository.delete(id);
        if(!isAffected) throw new CurrencyNotFoundException(
                new StringBuilder("Currency with id =")
                        .append(id)
                        .append(" not found")
                        .toString());
    }
}
