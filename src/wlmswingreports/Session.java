/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlmswingreports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author mdonovan
 */
public class Session {
    
    private Session() {
    }
    
    
    public static Session getInstance() {
        return SessionHolder.INSTANCE;
    }
    
    private static class SessionHolder {

        private static final Session INSTANCE = new Session();
    }
    
    public static Integer getWineMajorCategoryId(){
        Integer id = null;
        String sql = "select major_category_id from major_categories "
                + "where lower(major_category_name) = 'wine' ";
        try(Connection c = ConnectionPool.getInstance().cpds.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                id = rs.getInt(1);
            
        } catch (SQLException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }


}
