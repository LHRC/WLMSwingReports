/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlmswingreports;

/**
 *
 * @author mdonovan
 */
import com.mchange.v2.c3p0.*;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import winelistmanager.SessionManager.Restaurant;

public class ConnectionPool {
      
    public ComboPooledDataSource cpds;
    
    private ConnectionPool() {
        cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass( "org.postgresql.Driver" );
            //loads the jdbc driver
            //cpds.setJdbcUrl( "jdbc:postgresql://127.0.0.1/inventory" ); 
            //cpds.setJdbcUrl( "jdbc:postgresql://34.237.131.238/inventory" ); 
            //cpds.setUser("postgres"); cpds.setPassword("1261brg");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }
    }

    // TODO - not complete implementation
//    public void connect(Restaurant r){
//        try {
//            String user = r.getDBUser() == null ? "inventory" : r.getDBUser();
//            String password = r.getDBPassword() == null ? "1261brg" : r.getDBPassword();
//            cpds.setDriverClass( "org.postgresql.Driver" );
//            //loads the jdbc driver
//            //cpds.setJdbcUrl( "jdbc:postgresql://127.0.0.1/inventory" ); 
//            cpds.setJdbcUrl( "jdbc:postgresql://" + r.getRestaurantDatabaseIPAddress() + "/" + r.getDbName() ); 
//            if (r.getReadOnly()){
//                // read only access
//                cpds.setUser("postgres"); 
//                cpds.setPassword("1261brg"); 
//            }else{
//                // read / write access
//                cpds.setUser(user); 
//                cpds.setPassword(password);
//            }
//        } catch (PropertyVetoException ex) {
//            Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println(ex.getMessage());
//        }
//    }
    
    public ComboPooledDataSource getComboPooledDataSource(){
        
        // TODO log / error handling
        if (cpds== null){
            System.out.println("Null DB Connection");
        }
        return cpds;   
    }
    
    public static ConnectionPool getInstance() {
        return ConnectionPoolHolder.INSTANCE;
    }
    
    private static class ConnectionPoolHolder {

        private static final ConnectionPool INSTANCE = new ConnectionPool();
    }
}
