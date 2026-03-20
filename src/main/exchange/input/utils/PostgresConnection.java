package main.exchange.input.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnection {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = PostgresConnection.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            properties.load(input);
     
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection open() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
        String url = properties.getProperty("main.db.url");
        String user = properties.getProperty("main.db.name");
        String password = properties.getProperty("main.db.password");

        return DriverManager.getConnection(url, user, password);
    }

    public static boolean isConnecting(){
        try{
            Connection conn = open();
            return (conn != null && !conn.isClosed());

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
