package main.currencyexchange.models;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExchangeRate {

    private long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;

}
