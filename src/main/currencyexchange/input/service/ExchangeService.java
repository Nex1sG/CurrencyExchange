package main.currencyexchange.input.service;

import lombok.AllArgsConstructor;
import main.currencyexchange.input.exceptions.CurrencyNotFoundException;
import main.currencyexchange.input.exceptions.ExchangeRateNotFoundException;
import main.currencyexchange.data.repositories.CurrencyRepository;
import main.currencyexchange.data.repositories.ExchangeRateRepository;
import main.currencyexchange.data.models.Currency;
import main.currencyexchange.data.models.ExchangeRate;
import main.currencyexchange.data.models.ExchangeResponse;

import java.util.Optional;

@AllArgsConstructor
public class ExchangeService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeResponse exchange(String baseCode, String targetCode, int amount){
        Optional<Currency> baseCurrency = currencyRepository.findByCode(baseCode);
        Optional<Currency> targetCurrency = currencyRepository.findByCode(targetCode);

        if(baseCurrency.isEmpty() || targetCurrency.isEmpty()){
            throw new CurrencyNotFoundException("Currency not found: " + baseCode + "/" + targetCode);
        }
        Optional<ExchangeRate> rate = exchangeRateRepository.findByCurrencies(baseCurrency.get().getCode(), targetCurrency.get().getCode());
        if (rate.isEmpty()) {
            throw new ExchangeRateNotFoundException("Exchange rate not found in database");
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

