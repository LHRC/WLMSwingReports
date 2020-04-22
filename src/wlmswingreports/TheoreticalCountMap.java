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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mdonovan
 */
public class TheoreticalCountMap {
    
    private LocalDateTime lastUpdated;
    private Boolean activeOnly;
    private Map<Integer, Float> theoreticalMap;
    //private Map<Integer, ArrayList<Integer>> locationIDMap;
    private Boolean updateComplete;
    private LocalDate theoDate;
    
    private HashMap<Integer, ProductInstanceLocationRecord> locationMap;
    private HashMap<Integer, ArrayList<Integer>> productInstanceMap;

    public TheoreticalCountMap() {
        theoDate = LocalDate.now();
        updateComplete = Boolean.FALSE;
        this.activeOnly = false;
        //will replace others        
        locationMap = new HashMap<>();
        productInstanceMap = new HashMap<>();
        getOnHandData();
        updateComplete = Boolean.TRUE;
        lastUpdated = LocalDateTime.now().now();
    }
    
    public TheoreticalCountMap(LocalDate date){
        theoDate = date;
        updateComplete = Boolean.FALSE;
        lastUpdated = null;
        this.activeOnly = false;
        //will replace others        
        locationMap = new HashMap<>();
        productInstanceMap = new HashMap<>();
        getOnHandData();
        updateComplete = Boolean.TRUE;
        lastUpdated = LocalDateTime.now().now();
    }
    
    public TheoreticalCountMap(LocalDate date, Boolean activeOnly)
    {
        theoDate = date;
        updateComplete = Boolean.FALSE;
        lastUpdated = null;
        this.activeOnly = activeOnly;
        //will replace others        
        locationMap = new HashMap<>();
        productInstanceMap = new HashMap<>();
        getOnHandData();
        updateComplete = Boolean.TRUE;
        lastUpdated = LocalDateTime.now().now();
    }
      
    private void getOnHandData() {
        String sql = activeOnly ?  "select * from get_theoreticals_active_product_instances_only(?)" : 
                "select * from get_theoreticals_all(?)";
        try (Connection c = SessionManager.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(theoDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer pilid = rs.getInt("product_instance_location_id");
                Integer pid = rs.getInt("product_instance_id");
                Float begInv = rs.getFloat("beginning_inventory");
                Float purch = rs.getFloat("purchases");
                Float gSales = rs.getFloat("glass_sales"); 
                Float bSales = rs.getFloat("bottle_sales");
                Float tTo = rs.getFloat("transfers_to");
                Float tFrom = rs.getFloat("transfers_from");
                Float theoEnd = rs.getFloat("theoretical_on_hand");
                ProductInstanceLocationRecord pilr = new ProductInstanceLocationRecord(
                        pilid, pid, begInv, purch, gSales, bSales, tTo, tFrom, theoEnd);
                locationMap.put(pilid, pilr);
                if(! productInstanceMap.containsKey(pid)){
                    productInstanceMap.put(pid, new ArrayList<>());
                }
                productInstanceMap.get(pid).add(pilid);
            }
            ps.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(TheoreticalCountMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //TODO deprecate
    public HashMap<Integer, Float> getTheoreticalMapCopy(){
        return new HashMap<Integer, Float>(theoreticalMap);
    }


    
    //TODO this needs null checking in case no location id
    public Float getTheoreticalByProductInstanceID(Integer id) {
        if(productInstanceMap.containsKey(id)){
            float sum = 0.0f;
            for(Integer i : productInstanceMap.get(id)){
                sum += getTheoreticalByProductInstanceLocationID(i);
            }
            return sum;
        } else return null;
    }
    
    public Float getProductInstanceTotalTheoreticalByProductInstanceLocationID(Integer id){
        TheoreticalCountMap.ProductInstanceLocationRecord pilr = locationMap.get(id);
        return getTheoreticalByProductInstanceID(pilr.productInstanceID);
    }
    
    public Float getTheoreticalByProductInstanceLocationID(Integer id){
        return locationMap.containsKey(id) ? locationMap.get(id).theoreticalOnHand : null;
    }

    public TheoreticalCountMap.ProductInstanceLocationRecord getAllDataForProductInstanceLocationID(Integer id){
        return locationMap.containsKey(id) ? locationMap.get(id) : null;
    }
    
    public ArrayList<TheoreticalCountMap.ProductInstanceLocationRecord> getAllDataForProductInstanceID(Integer id){
        ArrayList<TheoreticalCountMap.ProductInstanceLocationRecord> al = new ArrayList<>();
        if(! locationMap.containsKey(id))
            return null;
        for (Integer i : productInstanceMap.get(id)){
            al.add(locationMap.get(i));
        }
        return al;
    }
    
    public LocalDateTime getLastUpdated(){
        return lastUpdated;
    }
    
    public Boolean isDayChangedSinceLastUpdate(){
        return lastUpdated.toLocalDate().isBefore(LocalDate.now());
    }

    /**
     * @return the updateComplete
     */
    public Boolean getUpdateComplete() {
        return updateComplete;
    }
    
    public LocalDate getDateOfTheoreticalValue(){
        return theoDate;
    }
  
    public class ProductInstanceLocationRecord{
        public final Integer productInstanceLocationID;
        public final Integer productInstanceID;
        public final Float beginningInventory;
        public final Float purchases;
        public final Float glassSales;
        public final Float bottleSales;
        public final Float transfersTo;
        public final Float transfersFrom;
        public final Float theoreticalOnHand;
        
        public ProductInstanceLocationRecord(Integer pilid, Integer pid, Float begInv, Float purch, Float glSales, Float btlSales, Float transTo, Float transFrom, Float theoOnHand){
            this.productInstanceLocationID = pilid;
            this.productInstanceID = pid;
            this.beginningInventory = begInv;
            this.purchases = purch;
            this.glassSales = glSales;
            this.bottleSales = btlSales;
            this.transfersTo = transTo;
            this.transfersFrom = transFrom;
            this.theoreticalOnHand = theoOnHand;
        }
        
        public String toString(){
            return productInstanceLocationID + " " + productInstanceID + " " + beginningInventory +
                    " " + purchases + " " +  " " + glassSales + " " +
                    " " + bottleSales + " " + theoreticalOnHand;
        }
    }
}
