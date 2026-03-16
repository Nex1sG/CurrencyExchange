package main.currencyexchange.repositories;

import main.currencyexchange.models.Currency;
import main.currencyexchange.models.ExchangeRate;

import static main.currencyexchange.utils.ConnectionManager.open;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository implements CrudRepository<ExchangeRate> {

    public static Optional<ExchangeRate> createExchangeRate(ResultSet rs) throws SQLException {

        ExchangeRate exchangeRate = new ExchangeRate();

        CurrencyRepository currencyRepository = new CurrencyRepository();

        Optional<Currency> baseCurrency =
                currencyRepository.findById(rs.getLong("base_currency_id"));

        Optional<Currency> targetCurrency =
                currencyRepository.findById(rs.getLong("target_currency_id"));

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            return Optional.empty();
        }

        exchangeRate.setId(rs.getLong("id"));
        exchangeRate.setBaseCurrency(baseCurrency.get());
        exchangeRate.setTargetCurrency(targetCurrency.get());
        exchangeRate.setRate(rs.getDouble("rate"));

        return Optional.of(exchangeRate);
    }



    public Optional<ExchangeRate> findByCurrencies(String baseCode, String targetCode) {

        String query = """
        SELECT er.id, er.base_currency_id, er.target_currency_id, er.rate
        FROM exchange_rates er
        JOIN currencies bc ON er.base_currency_id = bc.id
        JOIN currencies tc ON er.target_currency_id = tc.id
        WHERE bc.code = ? AND tc.code = ?
        """;

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);

            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : createExchangeRate(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<ExchangeRate> findById(long id) {

        String query = "SELECT id, base_currency_id, target_currency_id, rate FROM exchange_rates WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : createExchangeRate(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void save(ExchangeRate exchangeRate){

        String query = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, exchangeRate.getBaseCurrency().getId());
            statement.setLong(2, exchangeRate.getTargetCurrency().getId());
            statement.setDouble(3, exchangeRate.getRate());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }


    @Override
    public void update(ExchangeRate exchangeRate){

        String query = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setDouble(1, exchangeRate.getRate());
            statement.setLong(2, exchangeRate.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

//    @Override
//    public void delete(String id) {
//
//        String query = "DELETE FROM exchange_rates WHERE id = ?";
//
//        try (Connection conn = open();
//             PreparedStatement statement = conn.prepareStatement(query)) {
//
//            statement.setLong(1, id);
//            statement.executeUpdate();
//
//        } catch (SQLException e) {
//            throw new RuntimeException();
//        }
//    }
    @Override
    public Optional<List<ExchangeRate>> findAll(){

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT id, base_currency_id, target_currency_id, rate FROM exchange_rates";
        CurrencyRepository currencyRepository = new CurrencyRepository();

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Optional<ExchangeRate> exchangeRate = createExchangeRate(rs);
                exchangeRate.ifPresent(exchangeRates::add);

            }

            return Optional.of(exchangeRates);

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}

