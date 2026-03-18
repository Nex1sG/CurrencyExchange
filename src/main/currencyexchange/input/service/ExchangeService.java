package main.currencyexchange.input.service;

import lombok.AllArgsConstructor;
import main.currencyexchange.input.exceptions.CurrencyNotFoundException;
import main.currencyexchange.input.exceptions.ExchangeRateNotFoundException;
import main.currencyexchange.data.repositories.CurrencyRepository;
import main.currencyexchange.data.repositories.ExchangeRateRepository;
import main.currencyexchange.data.models.Currency;
import main.currencyexchange.data.models.ExchangeRate;
import main.currencyexchange.data.models.ExchangeResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@AllArgsConstructor
public class ExchangeService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final static String USD_CODE = "USD";
    private static final int SCALE = 6;

    public ExchangeResponse exchange(String baseCode, String targetCode, BigDecimal amount){
        Currency baseCurrency = currencyRepository.findByCode(baseCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found: " + baseCode));

        Currency targetCurrency = currencyRepository.findByCode(targetCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found: " + targetCode));

        ExchangeRate rate = exchangeRateRepository.findByCurrencies(
                baseCurrency.getCode(), targetCurrency.getCode()).orElseThrow(() ->
                new ExchangeRateNotFoundException("Exchange rate not found in database"));


        BigDecimal converted = amount.multiply(rate.getRate());

        ExchangeResponse response = new ExchangeResponse();
        response.setBaseCurrency(baseCurrency);
        response.setTargetCurrency(targetCurrency);
        response.setRate(rate.getRate());
        response.setAmount(amount);
        response.setConvertedAmount(converted);

        return response;
    }

    public BigDecimal findExchangeRate(String baseCode, String targetCode){
        Optional<ExchangeRate> directRate = exchangeRateRepository.findByCurrencies(baseCode, targetCode);
        if (directRate.isPresent()) {
            return directRate.get().getRate();
        }
        Optional<ExchangeRate> reverseRate = exchangeRateRepository.findByCurrencies(targetCode, baseCode);
        if (reverseRate.isPresent()) {
            return BigDecimal.ONE.divide(reverseRate.get().getRate(), 6, RoundingMode.DOWN);
        }

        return findRateThroughUSD(baseCode, targetCode);
    }

    private BigDecimal findRateThroughUSD(String baseCode, String targetCode) {

        if (USD_CODE.equals(baseCode)) {
            return getDirectUSDRate(targetCode);
        }

        if (USD_CODE.equals(targetCode)) {
            return BigDecimal.ONE.divide(getDirectUSDRate(baseCode), SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal usdToBase = getDirectUSDRate(baseCode);
        BigDecimal usdToTarget = getDirectUSDRate(targetCode);

        return usdToTarget.divide(usdToBase, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal getDirectUSDRate(String currencyCode) {
        Optional<ExchangeRate> usdRate = exchangeRateRepository.findByCurrencies(USD_CODE, currencyCode);

        if (usdRate.isPresent()) {
            return usdRate.get().getRate();
        }

        Optional<ExchangeRate> reverseUsdRate = exchangeRateRepository.findByCurrencies(currencyCode, USD_CODE);
        if (reverseUsdRate.isPresent()) {
            return BigDecimal.ONE.divide(reverseUsdRate.get().getRate(), SCALE, RoundingMode.HALF_UP);
        }

        throw new ExchangeRateNotFoundException("Cannot make exchange through USD cross-course");
    }
}

