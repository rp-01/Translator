/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conn;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Raj
 */
public class ConnectionClass {
public static Connection connection;
    public static Connection getCon() {
        
        
        try {
            
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite::resource:db/dictionary.db");
            
                    
        
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
