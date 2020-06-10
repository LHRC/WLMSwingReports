/*
 * ReportsCostPeriodFrame.java
 *
 * Created on October 26, 2008, 10:34 AM
 */

package wlmswingreports;

import com.jasperassistant.designer.viewer.ReportViewer;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author  Martin Donovan
 */
public class ReportsCostPeriodFrame extends JInternalFrame
{

    /** Creates new form ReportsCostPeriodFrame */
  public ReportsCostPeriodFrame()
  {
    //super();
    initComponents();
    getInventoryDates();
    getData();

    setupReport();
    this.dispose();
  }
  
  private void getInventoryDates()
  {
    startDateDialog = new InventoryDateChooserDialog(new JFrame(), true, "Select a Start Date");
    startDateDialog.setVisible(true);
    endDateDialog = new InventoryDateChooserDialog(new JFrame(), true, "Select an End Date");
    endDateDialog.setVisible(true);
  } 
  
  private void getData()
  {
    try
    {
      getSales();
      getPurchases();
      getStartingInventory();
      getEndingInventory();
      getCredits();
      getCostOfSales();
      getCostPercent();
      getParameters();
      conn.close();
    }
    catch (java.sql.SQLException ex)
    {
      ex.printStackTrace();
    }
    catch (java.text.ParseException ex)
    {
      ex.printStackTrace();
    }
  }

   private void setupReport() 
  {
    try
    {

      // * COMMENT THIS SECTION FOR PRODUCTION CODE - FOR TESTING ONLY
//      JasperReport jasperReport =
//          JasperCompileManager.compileReport(reportSource);
//      JasperCompileManager.compileReportToFile(reportSource, compiledReport);


      InputStream jasper1 = getClass().getResourceAsStream("CostStatementReport.jasper");
      JasperReport jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader.
            loadObject(jasper1);
      // OLD   *UNCOMMENT THIS SECTION FOR PRODUCTION CODE  *
//      JasperReport jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader.
//      loadObject(new File(compiledReport));

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);
      JasperViewer.viewReport(jasperPrint, false);
    }
    catch (JRException ex)
    {
      ex.printStackTrace();
    }
  }

  private void getParameters() throws ParseException
  {
    params.put("BUSINESS_NAME", SessionManager.getActiveRestaurant().getRestaurantName());
    params.put("MAJOR_CATEGORY_NAME", "Wine");
    params.put("SALES",  df.format(salesTotal).toString());
    params.put("PURCHASES", df.format(purchasesTotal).toString());
    params.put("STARTING_INVENTORY", df.format(startingInventoryTotal).toString());
    params.put("ENDING_INVENTORY", df.format(endingInventoryTotal).toString());
    params.put("CREDITS",  df.format(creditsTotal).toString());
    params.put("COST_OF_SALES", df.format(costOfSales).toString());
    params.put("COST", pf.format(cost).toString());
    params.put("START_DATE", startDateDialog.getSelectedDate().toString());
    params.put("END_DATE", endDateDialog.getSelectedDate().toString());
  }

  private void getCostOfSales()
  {
    costOfSales = startingInventoryTotal - endingInventoryTotal  + purchasesTotal - creditsTotal;
  }

  private void getCostPercent()
  {
//    cost = costOfSales * 100 / salesTotal;
    cost = costOfSales  / salesTotal;

  }
  
  private void getSales() throws SQLException
  {
    //TODO - changed this to match sales cat report #, (6/1/2011) may need to check
    String q = "select sum (menu_price) as total " +
            "from pos_chkitems where date_of_sale  " +
            "> ? and date_of_sale <= ? and major = " +
            "(select pos_major_category_number from match_major_categories " +
            "where major_category_id = ?) " +
            "and deletion = 0";
    PreparedStatement ps = conn.prepareStatement(q);
    ps.setDate(1, startDateDialog.getSelectedDate() );
    ps.setDate(2, endDateDialog.getSelectedDate() );
    ps.setInt(3, Session.getWineMajorCategoryId());
    ResultSet rs = ps.executeQuery();
    rs.next();
    salesTotal = rs.getFloat("total");
    System.out.println("sales total " + salesTotal);
  }
  private void getEndingInventory() throws SQLException
  {
    String q = "select sum (quantity_counted * product_cost) " +
            "as total from inventory_details " +
            "where inventory_id = ?";
    PreparedStatement ps = conn.prepareStatement(q);
    ps.setInt(1, endDateDialog.getSelectedInventoryId());
    ResultSet rs = ps.executeQuery();
    rs.next();
    endingInventoryTotal = rs.getFloat("total"); 
  }
  private void getPurchases() throws SQLException
  {
    String q = "select sum (number_received * price_per_unit)" +
            " as total from invoice_details id, invoices i, " +
            "product_instance_location_associations pila, product_instances pi, " +
            "products p where " +
            "id.product_instance_location_id = pila.product_instance_location_id " +
            "and pila.product_instance_id = pi.product_instance_id and " +
            "pi.product_id = p.product_id and " +
            "id.invoice_id = i.invoice_id and i.payment_date " +
            "  > ?  and i.payment_date <= ? " +
            "and p.major_category_id = ?";
    PreparedStatement ps = conn.prepareStatement(q);
    ps.setDate(1, startDateDialog.getSelectedDate());
    ps.setDate(2, endDateDialog.getSelectedDate());
    ps.setInt(3, Session.getWineMajorCategoryId());
    ResultSet rs = ps.executeQuery();
    if (rs.next())
      purchasesTotal = rs.getFloat("total");
  }
  
  private void getCredits() throws SQLException
  {
    String q = "select sum(cprd.price * cprd.quantity) as total from " +
               "credit_purchase_record_details cprd, credit_purchase_records cpr, " +
               "product_instance_location_associations pila, product_instances pi, " +
               "products p " +
               "where " +
               "cprd.product_instance_location_id = pila.product_instance_location_id " +
               "and pila.product_instance_id = pi.product_instance_id and " +
               "pi.product_id = p.product_id " +
               "and cprd.credit_purchase_record_id = cpr.credit_purchase_record_id " +
               "and cpr.purchase_date  > ? and cpr.purchase_date <= ? " +
               "and p.major_category_id = ?";
    PreparedStatement ps = conn.prepareStatement(q);
    ps.setDate(1, startDateDialog.getSelectedDate());
    ps.setDate(2, endDateDialog.getSelectedDate());
    ps.setInt(3, Session.getWineMajorCategoryId());
    ResultSet rs = ps.executeQuery(); 
    if (rs.next())
      creditsTotal = rs.getFloat("total");
  }
  
  private void getStartingInventory() throws SQLException
  {
    String q = "select sum (quantity_counted * product_cost) " +
            "as total from inventory_details " +
            "where inventory_id = ?";
    PreparedStatement ps = conn.prepareStatement(q);
    ps.setInt(1, startDateDialog.getSelectedInventoryId());
    ResultSet rs = ps.executeQuery();
    rs.next(); 
    startingInventoryTotal = rs.getFloat("total"); 
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 82, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private InventoryDateChooserDialog startDateDialog;
private InventoryDateChooserDialog endDateDialog;
private Float salesTotal;
private Float purchasesTotal;
private Float startingInventoryTotal;
private Float endingInventoryTotal;
private Float creditsTotal;
private Float costOfSales;
private Float cost;
private Connection conn = SessionManager.getConnection();
DecimalFormat df =  new DecimalFormat("$#,##0.00");
DecimalFormat pf =  new DecimalFormat("#,##0.00%");
String reportPath = "lib/reports/";
String compiledReport = reportPath + "CostStatementReport.jasper";
String reportSource = reportPath + "CostStatementReport.jrxml";
Map<String, Object> params = new HashMap<String, Object>();
ReportViewer rv = new ReportViewer();
JasperReport jr = null;
JasperPrint jp = null;
net.sf.jasperreports.engine.JRResultSetDataSource ds;
private JREmptyDataSource rds = new JREmptyDataSource();




    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
 
}
