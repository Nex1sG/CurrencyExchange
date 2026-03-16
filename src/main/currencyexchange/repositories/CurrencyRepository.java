package main.currencyexchange.repositories;

import main.currencyexchange.models.Currency;

import static main.currencyexchange.utils.ConnectionManager.open;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CurrencyRepository implements CrudRepository<Currency>{


    protected static Currency currencyCreating(ResultSet resultSet) throws SQLException{
        Currency currency = new Currency();
        currency.setId(resultSet.getLong("id"));
        currency.setCode(resultSet.getString("code"));
        currency.setFullName(resultSet.getString("full_name"));
        currency.setSign(resultSet.getString("sign"));
        return currency;
    }

    public Optional<Currency> findByCode(String code){
        String query = "SELECT * FROM currencies WHERE code = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, code);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(currencyCreating(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currency> findById(long id) {
        String query = "SELECT id, code, full_name, sign FROM currencies WHERE id = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(currencyCreating(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<Currency>> findAll(){
        String query = "SELECT id, code, full_name, sign FROM currencies ";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            ResultSet rs = statement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (rs.next()) currencies.add(currencyCreating(rs));

            return currencies.isEmpty() ? Optional.empty() : Optional.of(currencies);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void save(Currency currency){
        String query = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void update(Currency currency) {

        String query = "UPDATE currencies SET code = ?, full_name = ?, sign = ? WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.setLong(4, currency.getId());

            statement.executeUpdate();

        }catch (SQLException e) {
            throw new RuntimeException();
        }
    }

//    @Override
    public void delete(String code){
        String query = "DELETE FROM currencies WHERE code = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, code);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
