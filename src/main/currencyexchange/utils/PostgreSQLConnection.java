package main.currencyexchange.utils;

//import lombok.get;
import java.sql.Connection;
import java.sql.SQLException;
import static main.currencyexchange.utils.ConnectionManager.open;

public class PostgreSQLConnection {
    private static Connection conn = null;

    private static boolean connecting(){
        try{
            Class.forName("org.postgresql.Driver");
            conn = open();

            if(conn != null && !conn.isClosed()){
                System.out.println("Successful connection");
                return true;
            }
        } catch (ClassNotFoundException e){
            System.out.println("CNFE error: ");
        } catch (SQLException e){
            System.out.println("SQLException error: ");
        }

        return false;
    }
}
