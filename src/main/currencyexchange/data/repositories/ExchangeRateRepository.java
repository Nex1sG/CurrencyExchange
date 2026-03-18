package main.currencyexchange.data.repositories;

import main.currencyexchange.input.exceptions.CurrencyNotFoundException;
import main.currencyexchange.input.exceptions.DatabaseException;
import main.currencyexchange.data.models.Currency;
import main.currencyexchange.data.models.ExchangeRate;

import static main.currencyexchange.input.utils.ConnectionManager.open;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository implements CrudRepository<ExchangeRate> {

    private final CurrencyRepository currencyRepository;
    public ExchangeRateRepository(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public ExchangeRate createExchangeRate(ResultSet rs) throws SQLException {

        ExchangeRate exchangeRate = new ExchangeRate();

        Currency baseCurrency = currencyRepository
                .findById(rs.getLong("baseCurrencyId"))
                .orElseThrow(() -> new CurrencyNotFoundException("Base currency not found"));

        Currency targetCurrency = currencyRepository
                .findById(rs.getLong("targetCurrencyId"))
                .orElseThrow(() -> new CurrencyNotFoundException("Target currency not found"));

        exchangeRate.setId(rs.getLong("id"));
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(rs.getDouble("rate"));

        return exchangeRate;
    }



    public Optional<ExchangeRate> findByCurrencies(String baseCode, String targetCode) {

        String query = """
        SELECT er.id, er.baseCurrencyId, er.targetCurrencyId, er.rate
        FROM exchange_rates er
        JOIN currencies bc ON er.baseCurrencyId = bc.id
        JOIN currencies tc ON er.targetCurrencyId = tc.id
        WHERE bc.code = ? AND tc.code = ?
        """;

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);

            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(createExchangeRate(rs));

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find exchange rate with base/taget codes="
                    + baseCode + "/" + targetCode, e);
        }
    }


    @Override
    public Optional<ExchangeRate> findById(long id) {

        String query = "SELECT id, basecurrencyid, targetcurrencyid, rate FROM exchange_rates WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(createExchangeRate(rs));

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find exchange rate with id=" + id, e);
        }
    }


    @Override
    public void save(ExchangeRate exchangeRate){

        String query = "INSERT INTO exchange_rates (basecurrencyid, targetcurrencyid, rate) VALUES (?, ?, ?)";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, exchangeRate.getBaseCurrency().getId());
            statement.setLong(2, exchangeRate.getTargetCurrency().getId());
            statement.setDouble(3, exchangeRate.getRate());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find exchange rate with base/taget currencies="
                    + exchangeRate.getBaseCurrency() + "/" + exchangeRate.getTargetCurrency(), e);
        }
    }


    @Override
    public boolean update(ExchangeRate exchangeRate){

        String query = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setDouble(1, exchangeRate.getRate());
            statement.setLong(2, exchangeRate.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find exchange rate with base/taget currencies="
                    + exchangeRate.getBaseCurrency() + "/" + exchangeRate.getTargetCurrency(), e);
        }
    }

    @Override
    public boolean delete(long id) {

        String query = "DELETE FROM exchange_rates WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find exchange rate with id=" + id, e);
        }
    }

    @Override
    public List<ExchangeRate> findAll(){

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT id, basecurrencyid, targetcurrencyid, rate FROM exchange_rates";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ExchangeRate exchangeRate =createExchangeRate(rs);
                if(exchangeRate != null) exchangeRates.add(exchangeRate);

            }

            return exchangeRates;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all exchange rates", e);
        }
    }
}

