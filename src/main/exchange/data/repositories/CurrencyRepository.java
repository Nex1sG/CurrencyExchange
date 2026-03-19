package main.exchange.data.repositories;

import main.exchange.input.exceptions.CurrencyAlreadyExistsException;
import main.exchange.input.exceptions.DatabaseException;
import main.exchange.data.models.Currency;

import static main.exchange.input.utils.ConnectionProvider.open;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CurrencyRepository implements CrudRepository<Currency>{


    protected static Currency mapToCurrency(ResultSet resultSet) throws SQLException{
        Currency currency = new Currency();
        currency.setId(resultSet.getLong("id"));
        currency.setCode(resultSet.getString("code"));
        currency.setName(resultSet.getString("full_name"));
        currency.setSign(resultSet.getString("sign"));
        return currency;
    }

    public Optional<Currency> findByCode(String code){
        String query = "SELECT id, code, full_name, sign FROM currencies WHERE code = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, code);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(mapToCurrency(rs));

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find currency with code=" + code, e);
        }
    }

    @Override
    public Optional<Currency> findById(long id) {
        String query = "SELECT id, code, full_name, sign FROM currencies WHERE id = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(mapToCurrency(rs));

        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch currency with id=" + id, e);
        }
    }

    @Override
    public List<Currency> findAll(){
        String query = "SELECT id, code, full_name, sign FROM currencies ";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            ResultSet rs = statement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (rs.next()) currencies.add(mapToCurrency(rs));

            return currencies;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch currencies for find all", e);
        }
    }


    @Override
    public void save(Currency currency){
        String query = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("unique")) {
                throw new CurrencyAlreadyExistsException("Currency already exists: " + currency.getCode());
            }
            throw new DatabaseException("Failed to save currency with code=" + currency.getCode(), e);
        }
    }

    @Override
    public boolean update(Currency currency) {

        String query = "UPDATE currencies SET code = ?, full_name = ?, sign = ? WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.setLong(4, currency.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;

        }catch (SQLException e) {
            throw new DatabaseException("Failed to update currency with id=" + currency.getId(), e);
        }
    }

//    @Override
    public boolean delete(long id){
        String query = "DELETE FROM currencies WHERE code = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e){
            throw new DatabaseException("Failed to delete currency with id=" + id, e);
        }
    }
}
