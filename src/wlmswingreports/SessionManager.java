/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlmswingreports;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mdonovan
 */
public class SessionManager {
    private static Boolean isAuthenticated = true;
    private static Integer restaurantID = null;
    private static Boolean test = true;
    Restaurant BRGRDS = new Restaurant("BRG Remote - Test", 1 , 
            "blueridgegrill.cxond9zlkmhk.us-east-1.rds.amazonaws.com", 
            false, "brg_wine", 2000, 3, "brg", "brg1995");
    Restaurant BonesRDS = new Restaurant("Bones Remote - Test", 2 , 
            "blueridgegrill.cxond9zlkmhk.us-east-1.rds.amazonaws.com", 
            false, "bones_wine", 0, 4, "bones", "bones1979");
    private final List restaurantList = new ArrayList(){{

       BRGRDS.setDBPassword("1261#Lhrc!3423.Brg");
       BonesRDS.setDBPassword("1261#Lhrc!3423.Brg");
       add(BRGRDS);
       add(BonesRDS);
    }};
    private static Restaurant activeRestaurant;
    
    public static List<Restaurant> getRestaurants(){
        return new SessionManager().restaurantList;
    }
    /**
     * @return the isAuthenticated
     */
    public static Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    /**
     * @param aIsAuthenticated the isAuthenticated to set
     */
    public static void setIsAuthenticated(Boolean aIsAuthenticated) {
        isAuthenticated = aIsAuthenticated;
    }

    /**
     * @return the restaurantID
     */
    public static Integer getRestaurantID() {
        return restaurantID;
    }

    /**
     * @param aRestaurantID the restaurantID to set
     */
    public static void setRestaurantID(Integer aRestaurantID) {
        restaurantID = aRestaurantID;
    }
    
    public static void setActiveRestaurant(Restaurant r){
       activeRestaurant = r;
    }
    
    public static Restaurant getActiveRestaurant(){
        return activeRestaurant;
    }
    
        public class Restaurant{
            private String restaurantName;
            private int restaurantID;
            private String restaurantDatabaseIPAddress; 
            private Boolean readOnly;
            private String dbName;
            private Integer wineListBinNumberOffset;
            private Integer POSWineMajorCategoryID;
            private String DBUser;
            private String appUser;
            private String appPassword;
            private String DBPassword;
            
            public Restaurant(String n, int id, String ip, Boolean ro, 
                    String dbn, Integer wlbno, Integer pwmcid, 
                    String u, String pw){
                restaurantName = n;
                restaurantID = id;
                restaurantDatabaseIPAddress = ip;
                readOnly = ro;
                dbName = dbn;
                wineListBinNumberOffset = wlbno;
                POSWineMajorCategoryID = pwmcid;
                appUser = u;
                appPassword = pw;
            }
            
            public Boolean validateRestaurantLogin(String u, String p){
                //System.out.println(u + " " + p);
                //System.out.println(this.DBUser + " " + this.appPassword);
                return (     (this.appUser == null && this.appPassword == null ) 
                          || (this.appUser.equals(u.trim()) && this.appPassword.equals(p) )
                       );
            }
            
            public String toString(){
                return getRestaurantName();
            }

            /**
             * @return the restaurantName
             */
            public String getRestaurantName() {
                return restaurantName;
            }

        /**
         * @return the DBUser
         */
        public String getDBUser() {
            return DBUser;
        }

        /**
         * @param DBUser the DBUser to set
         */
        public void setDBUser(String DBUser) {
            this.DBUser = DBUser;
        }

        /**
         * @return the DBPassword
         */
        public String getDBPassword() {
            return DBPassword;
        }

        /**
         * @param DBPassword the DBPassword to set
         */
        public void setDBPassword(String DBPassword) {
            this.DBPassword = DBPassword;
        }

            /**
             * @return the restaurantID
             */
            public int getRestaurantID() {
                return restaurantID;
            }

            /**
             * @return the restaurantDatabaseIPAddress
             */
            public String getRestaurantDatabaseIPAddress() {
                return restaurantDatabaseIPAddress;
            }

            /**
             * @return the readOnly
             */
            public Boolean getReadOnly() {
                return readOnly;
            }

        /**
         * @return the dbName
         */
        public String getDbName() {
            return dbName;
        }

        /**
         * @return the wineListBinNumberOffset
         */
        public Integer getWineListBinNumberOffset() {
            return wineListBinNumberOffset;
        }

        /**
         * @return the POSWineMajorCategoryID
         */
        public Integer getPOSWineMajorCategoryID() {
            return POSWineMajorCategoryID;
        }

        /**
         * @param wineListBinNumberOffset the wineListBinNumberOffset to set
         */
        public void setWineListBinNumberOffset(Integer wineListBinNumberOffset) {
            this.wineListBinNumberOffset = wineListBinNumberOffset;
        }
        } 
}
