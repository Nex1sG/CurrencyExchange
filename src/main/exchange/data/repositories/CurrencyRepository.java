package main.exchange.data.repositories;

import main.exchange.input.exceptions.CurrencyAlreadyExistsException;
import main.exchange.input.exceptions.DatabaseException;
import main.exchange.data.models.CurrencyEntity;

import static main.exchange.input.utils.PostgresConnection.open;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CurrencyRepository implements CrudRepository<CurrencyEntity>{


    protected static CurrencyEntity mapToCurrency(ResultSet resultSet) throws SQLException{
        CurrencyEntity currency = new CurrencyEntity();
        currency.setId(resultSet.getLong("id"));
        currency.setCode(resultSet.getString("code"));
        currency.setName(resultSet.getString("full_name"));
        currency.setSign(resultSet.getString("sign"));
        return currency;
    }

    public Optional<CurrencyEntity> findByCode(String code){
        String query = "SELECT id, code, full_name, sign FROM currencies WHERE code = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, code);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(mapToCurrency(rs));

        } catch (SQLException e) {
            throw new DatabaseException(
                    new StringBuilder("Failed to save currency with code=")
                            .append(code)
                            .toString(), e);
        }
    }

    @Override
    public Optional<CurrencyEntity> findById(long id) {
        String query = "SELECT id, code, full_name, sign FROM currencies WHERE id = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            return !rs.next() ? Optional.empty() : Optional.of(mapToCurrency(rs));

        } catch (SQLException e) {
            throw new DatabaseException(
                    new StringBuilder("Failed to fetch currency with id=")
                            .append(id)
                            .toString(), e);
        }
    }

    @Override
    public List<CurrencyEntity> findAll(){
        String query = "SELECT id, code, full_name, sign FROM currencies ";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            ResultSet rs = statement.executeQuery();
            List<CurrencyEntity> currencies = new ArrayList<>();
            while (rs.next()) currencies.add(mapToCurrency(rs));

            return currencies;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch currencies for find all", e);
        }
    }


    @Override
    public void save(CurrencyEntity currency){
        String query = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("unique")) {
                throw new CurrencyAlreadyExistsException(
                        new StringBuilder("Currency already exists: ")
                                .append(currency.getCode())
                                .toString());
            }
            throw new DatabaseException(
                    new StringBuilder("Failed to save currency with code=")
                            .append(currency.getCode())
                            .toString(), e);
        }
    }

    @Override
    public boolean update(CurrencyEntity currency) {

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
            throw new DatabaseException(
                    new StringBuilder("Failed to update currency with id=")
                            .append(currency.getId())
                            .toString(), e);
        }
    }

    @Override
    public boolean delete(long id){
        String query = "DELETE FROM currencies WHERE code = ?";
        try (Connection conn = open();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e){
            throw new DatabaseException(
                    new StringBuilder("Failed to fetch currency with id=")
                            .append(id)
                            .toString(), e);
        }
    }
}
