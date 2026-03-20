package main.exchange.data.repositories;

import main.exchange.input.exceptions.CurrencyNotFoundException;
import main.exchange.input.exceptions.DatabaseException;
import main.exchange.data.models.CurrencyEntity;
import main.exchange.data.models.ExchangeRateEntity;

import static main.exchange.input.utils.PostgresConnection.open;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository implements CrudRepository<ExchangeRateEntity> {

    private final CurrencyRepository currencyRepository;
    public ExchangeRateRepository(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public ExchangeRateEntity createExchangeRate(ResultSet rs) throws SQLException {

        ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity();

        CurrencyEntity baseCurrency = currencyRepository
                .findById(rs.getLong("base_currency_id"))
                .orElseThrow(() -> new CurrencyNotFoundException("Base currency not found"));

        CurrencyEntity targetCurrency = currencyRepository
                .findById(rs.getLong("target_currency_id"))
                .orElseThrow(() -> new CurrencyNotFoundException("Target currency not found"));

        exchangeRateEntity.setId(rs.getLong("id"));
        exchangeRateEntity.setBaseCurrency(baseCurrency);
        exchangeRateEntity.setTargetCurrency(targetCurrency);
        exchangeRateEntity.setRate(rs.getBigDecimal("rate"));

        return exchangeRateEntity;
    }



    public Optional<ExchangeRateEntity> findByCurrencies(String baseCode, String targetCode) {

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

            return !rs.next() ? Optional.empty() : Optional.of(createExchangeRate(rs));

        } catch (SQLException e) {
            throw new DatabaseException(
                    new StringBuilder("Failed to find exchange rate with base/taget codes=")
                            .append(baseCode)
                            .append("/")
                            .append(targetCode)
                            .toString(), e);
        }
    }


    @Override
    public Optional<ExchangeRateEntity> findById(long id) {

        String query = "SELECT id, base_currency_id, target_currency_id, rate FROM exchange_rates WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(createExchangeRate(rs));

        } catch (SQLException e) {
            throw new DatabaseException(
                    new StringBuilder("Failed to find exchange rate with id=")
                            .append(id)
                            .toString(), e);
        }
    }


    @Override
    public void save(ExchangeRateEntity exchangeRate){

        String query = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, exchangeRate.getBaseCurrency().getId());
            statement.setLong(2, exchangeRate.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRate.getRate());

            if (exchangeRate.getBaseCurrency() == null ||
                    exchangeRate.getTargetCurrency() == null ||
                    exchangeRate.getRate() == null) {
                throw new IllegalArgumentException("ExchangeRate contains null fields");
            }
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to save exchange rate with chosen currencies", e);
        }
    }


    @Override
    public boolean update(ExchangeRateEntity exchangeRate){

        String query = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setBigDecimal(1, exchangeRate.getRate());
            statement.setLong(2, exchangeRate.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update exchange rate with chosen currencies", e);
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
            throw new DatabaseException(
            new StringBuilder("Failed to delete exchange rate with id=")
                    .append(id)
                    .toString(), e);
        }
    }

    @Override
    public List<ExchangeRateEntity> findAll(){

        List<ExchangeRateEntity> exchangeRateEntities = new ArrayList<>();
        String query = "SELECT id, base_currency_id, target_currency_id, rate FROM exchange_rates";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ExchangeRateEntity exchangeRate =createExchangeRate(rs);
                if(exchangeRate != null) exchangeRateEntities.add(exchangeRate);

            }

            return exchangeRateEntities;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all exchange rates", e);
        }
    }
}

