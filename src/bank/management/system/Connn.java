package bank.management.system;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.*;

public class Connn {
    Connection connection;
    static Statement statement;
    public Connn(){
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankSystem","root","Ketan@01114");
            statement = connection.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }
}
