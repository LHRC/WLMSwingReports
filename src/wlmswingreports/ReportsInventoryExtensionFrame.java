/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ReportsInventoryExtensionFrame.java
 *
 * Created on Jun 22, 2009, 8:03:11 PM
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Martin Donovan
 */
public class ReportsInventoryExtensionFrame extends JInternalFrame
{
    public static DecimalFormat STANDARD_DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    /** Creates new form ReportsInventoryExtensionFrame */
    public ReportsInventoryExtensionFrame()
    {
      //super(mit);
      //getConnection();
      getClearanceOnly();
      getInventoryDate();
      getReportData();
      getParameters();
      initComponents();
      this.setVisible(false);
      //exportToExcel();
      setUpReport();
    }

  
  private void exportToExcel()
  {
    int reply = JOptionPane.showConfirmDialog(null, "Export to Excel?", "Export", JOptionPane.YES_NO_OPTION);
    if (reply == JOptionPane.YES_OPTION)
    {
      //ReportExporter re = new ReportExporter(cols, data, reportName);
    }
  }

  private void getClearanceOnly()
  {
//    isClearanceOnly = ( JOptionPane.showConfirmDialog(null, "Show Clearance Products Only?")
//                     == JOptionPane.YES_OPTION );
      isClearanceOnly = Boolean.FALSE;
  }

  private void getParameters()
  {
    params.put("INVENTORY_DATE", LocalDate.now().toString());
    params.put("BUSINESS_NAME", "LHRC");
    params.put("CATEGORY_NAME", "Wine");
  }

    private void setUpReport() {
        try {
            InputStream jasper1 = getClass().getResourceAsStream(compiledReport);
            JasperReport jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader.
                    loadObject(jasper1);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage());
            ex.printStackTrace();
        }
    }
    
  private void getInventoryDate()
  {
    dateDialog = new InventoryDateChooserDialog(new JFrame(), true);
    dateDialog.setVisible(true);
  }

  private void getReportData()
  {
    try
    {
      getCols();
      getData();
      rds = new ReportDataSource(cols, data);
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
  }
  private void getCols()
  {
    cols.add("Product");
    cols.add("Room");
    cols.add("Quantity");
    cols.add("Theoretical");
    cols.add("Unit Price");
    cols.add("Extended");
  }

    private void getData() throws SQLException {
        String clearanceClause = isClearanceOnly
                ? " and pi.is_clearance = 'true' " : "";
        String sql = "select pi.inventory_name, id.product_cost, r.room_name, id.quantity_counted,"
                + " id.theoretical_quantity, id.product_cost * id.quantity_counted as extended"
                + " from inventory_details id, product_instances pi, rooms r, "
                + " product_instance_location_associations pila, products p "
                + " where  pi.product_id = p.product_id "
                + " and id.product_instance_location_id = pila.product_instance_location_id "
                + " and pila.product_instance_id = pi.product_instance_id "
                + " and pila.location_room_id = r.room_id and inventory_id = ? "
                + " and p.major_category_id = "
                + "(select major_category_id from major_categories where lower(major_category_name) = 'wine') "
                + clearanceClause
                + " order by r.room_name,pila.location_column, "
                + " pila.location_row,  pi.inventory_name";
        try (Connection conn = SessionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            int inventoryId = dateDialog.getSelectedInventoryId();
            //int inventoryId = 960;
            ps.setInt(1, inventoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> line = new Vector();
                line.add(rs.getString("inventory_name"));
                line.add(rs.getString("room_name"));
                line.add(rs.getFloat("quantity_counted"));
                line.add(rs.getFloat("theoretical_quantity"));
                line.add(rs.getFloat("product_cost"));
                line.add(rs.getFloat("extended"));
                data.add(line);
            }
            ps.close();
            conn.close();
        }
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
            .addGap(0, 353, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 112, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
  //static MenuItemType mit = MenuItemType.REPORTS_INVENTORY_EXTENSION;
  private InventoryDateChooserDialog dateDialog;
  //private Connection conn = ApplicationData.getConnection();
  private Vector<String> cols = new Vector();
  private Vector<Vector<Object>> data = new Vector();
  private DefaultTableModel invTableModel;
  DecimalFormat df = STANDARD_DECIMAL_FORMAT;
  String reportPath = "lib/reports/";
  String compiledReport = "../res/InventoryExtensionReport.jasper";
  String reportSource = reportPath + "InventoryExtensionReport.jrxml";
  String reportDest = "JasperTest.html";
  String reportName = "InventoryExtensionReport";
  Map<String, Object> params = new HashMap<String, Object>();
  ReportViewer rv = new ReportViewer();
  JasperReport jr = null;
  JasperPrint jp = null;
  net.sf.jasperreports.engine.JRResultSetDataSource ds;
  private ReportDataSource rds;
  boolean isClearanceOnly;
}
