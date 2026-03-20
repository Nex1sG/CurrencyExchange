package main.exchange.input.service;

import lombok.AllArgsConstructor;
import main.exchange.input.exceptions.CurrencyNotFoundException;
import main.exchange.input.exceptions.ExchangeRateNotFoundException;
import main.exchange.data.repositories.CurrencyRepository;
import main.exchange.data.repositories.ExchangeRateRepository;
import main.exchange.data.models.CurrencyEntity;
import main.exchange.data.models.ExchangeRateEntity;
import main.exchange.data.models.ExchangeResponseDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@AllArgsConstructor
public class ExchangeService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final static String USD_CODE = "USD";
    private static final int SCALE = 6;

    public ExchangeResponseDTO exchange(String baseCode, String targetCode, BigDecimal amount){
        CurrencyEntity baseCurrency = currencyRepository.findByCode(baseCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found: " + baseCode));

        CurrencyEntity targetCurrency = currencyRepository.findByCode(targetCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found: " + targetCode));

        BigDecimal rate = findExchangeRate(baseCode, targetCode);
        if(rate == null) {
            rate = exchangeRateRepository.findByCurrencies(
                    baseCurrency.getCode(), targetCurrency.getCode()).orElseThrow(() ->
                    new ExchangeRateNotFoundException("Exchange rate not found in database")).getRate();
        }


        ExchangeResponseDTO response = new ExchangeResponseDTO();
        response.setBaseCurrency(baseCurrency);
        response.setTargetCurrency(targetCurrency);
        response.setRate(rate);
        response.setAmount(amount);
        response.setConvertedAmount(amount.multiply(rate));

        return response;
    }

    public BigDecimal findExchangeRate(String baseCode, String targetCode){
        if(baseCode.equals(targetCode)) return BigDecimal.valueOf(1);

        Optional<ExchangeRateEntity> directRate = exchangeRateRepository.findByCurrencies(baseCode, targetCode);
        if (directRate.isPresent()) {
            return directRate.get().getRate();
        }
        Optional<ExchangeRateEntity> reverseRate = exchangeRateRepository.findByCurrencies(targetCode, baseCode);
        return reverseRate.map(exchangeRate -> BigDecimal.ONE.divide(exchangeRate.getRate(),
                6, RoundingMode.DOWN)).orElseGet(() -> findRateThroughUSD(baseCode, targetCode));

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

        Optional<ExchangeRateEntity> usdRate = exchangeRateRepository.findByCurrencies(USD_CODE, currencyCode);

        if (usdRate.isPresent()) {
            return usdRate.get().getRate();
        }

        Optional<ExchangeRateEntity> reverseUsdRate = exchangeRateRepository.findByCurrencies(currencyCode, USD_CODE);
        if (reverseUsdRate.isPresent()) {
            return BigDecimal.ONE.divide(reverseUsdRate.get().getRate(), SCALE, RoundingMode.HALF_UP);
        }

        throw new ExchangeRateNotFoundException("Cannot find direct / reverse / cross courses");
    }
}

