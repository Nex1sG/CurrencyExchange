package main.currencyexchange.servlet;

import main.currencyexchange.repositories.CurrencyRepository;
import main.currencyexchange.repositories.ExchangeRateRepository;
import main.currencyexchange.models.Currency;
import main.currencyexchange.models.ExchangeRate;
import main.currencyexchange.models.ExchangeResponse;

import java.util.Optional;

public class ExchangeService {

    private final CurrencyRepository currencyDAO = new CurrencyRepository();
    private final ExchangeRateRepository exchangeRateDAO = new ExchangeRateRepository();

    public ExchangeResponse exchange(String baseCode, String targetCode, int amount){
        Optional<Currency> baseCurrency = currencyDAO.findByCode(baseCode);
        Optional<Currency> targetCurrency = currencyDAO.findByCode(targetCode);

        if(baseCurrency.isEmpty() || targetCurrency.isEmpty()){
            throw new NullPointerException();
        }
        Optional<ExchangeRate> rate = exchangeRateDAO.findByCurrencies(baseCurrency.get().getCode(), targetCurrency.get().getCode());
        if (rate.isEmpty()) {
            throw new NullPointerException();
        }

        double converted = amount * rate.get().getRate();

        ExchangeResponse response = new ExchangeResponse();
        response.setBaseCurrency(baseCurrency.get());
        response.setTargetCurrency(targetCurrency.get());
        response.setRate(rate.get().getRate());
        response.setAmount(amount);
        response.setConvertedAmount(converted);

        return response;
    }
}

