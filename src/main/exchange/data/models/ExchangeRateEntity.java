package main.exchange.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExchangeRateEntity {

    private long id;
    private CurrencyEntity baseCurrency;
    private CurrencyEntity targetCurrency;
    private BigDecimal rate;

}
