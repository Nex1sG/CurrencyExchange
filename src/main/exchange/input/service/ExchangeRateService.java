package main.exchange.input.service;

import main.exchange.data.models.ExchangeRate;
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

    public ExchangeRate readExchangeRate(String baseCode, String targetCode){
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCurrencies(baseCode, targetCode);
        if(exchangeRate.isPresent()) return exchangeRate.get();
        throw new ExchangeRateNotFoundException("Exchange rate with base/target code=" +
                baseCode + targetCode + " is not found in database");
    }

    public List<ExchangeRate> readExchangeRate(){
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();
        if(!exchangeRates.isEmpty()) return  exchangeRates;
        throw new ExchangeRateNotFoundException("Exchange rates not found in database");
    }

    public ExchangeRate readExchangeRate(int id){
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findById(id);
        if(exchangeRate.isPresent()) return exchangeRate.get();
        throw new ExchangeRateNotFoundException("Exchange rate with id=" + id +
                " is not found in database");
    }

    public void create(ExchangeRate exchangeRate) {
        if (exchangeRateRepository.findByCurrencies(
                exchangeRate.getBaseCurrency().getCode(),
                exchangeRate.getTargetCurrency().getCode()
        ).isPresent()) {
            throw new ExchangeRateAlreadyExistsException("Exchange rate already exists");
        }

        exchangeRateRepository.save(exchangeRate);
    }
    public void update(ExchangeRate exchangeRate){
        Optional<ExchangeRate> existing = exchangeRateRepository.findByCurrencies(
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
        if(!isAffected) throw new CurrencyNotFoundException("Currency with id =" +
                id + " not found");
    }
}
