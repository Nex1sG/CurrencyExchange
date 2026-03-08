package main.currencyexchange.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.name";
    private static final String PASSWORD_KEY = "db.password";

    public static Connection open(){
        try{
            return DriverManager.getConnection(URL_KEY, USER_KEY, PASSWORD_KEY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
