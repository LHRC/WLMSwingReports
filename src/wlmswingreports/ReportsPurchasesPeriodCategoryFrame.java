/*
 * ReportsPurchasesPeriodFrame.java
 *
 * Created on July 29, 2008, 9:25 PM
 */

package wlmswingreports;

import com.jasperassistant.designer.viewer.ReportViewer;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;


/**
 *
 * @author  Martin Donovan
 */
public class ReportsPurchasesPeriodCategoryFrame extends JInternalFrame
{
  
  /** Creates new form ReportsPurchasesPeriodFrame */
  public ReportsPurchasesPeriodCategoryFrame()
  {
    //super(mit);
    initComponents();
    getDateRange();
    startDate = startDateChooser.getLocalDate();
    endDate = endDateChooser.getLocalDate();
    try 
    {
      getParameters();
      getCols();
      getData();
      setupReport();
      conn.close();
      this.dispose();
    }
    catch (java.sql.SQLException ex)
    {
      ex.printStackTrace();
    }

  }

  private void getParameters()
  {
    params.put("BUSINESS_NAME", SessionManager.getActiveRestaurant().getRestaurantName());
    params.put("CATEGORY_NAME", "Wine");
    params.put("START_DATE", startDate.toString());
    params.put("END_DATE", endDate.toString());
  }

    private void setupReport() {
        //JasperReport jasperReport;
        try {
            rds = new ReportDataSource(cols, data);

            InputStream jasper1 = getClass().getResourceAsStream("PurchasePeriodCategoryReport.jasper");

            JasperReport jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader.
                    loadObject(jasper1);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);
            //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);

            //JasperExportManager.exportReportToHtmlFile(jasperPrint, reportDest);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }

  private void getData() throws SQLException 
  {
    String q = "select i.invoice_id, i.vendor_invoice_id, c.company_name, " +
               "i.invoice_date, i.payment_date, " +
               "pi.inventory_name, id.number_received, id.price_per_unit, " +
               "id.number_received * id.price_per_unit as extended " +
               "from invoices i, invoice_details id, companies c, " +
               "product_instance_location_associations pila, product_instances pi, " +
               "products p " +
               "where id.product_instance_location_id = pila.product_instance_location_id " +
               "and pi.product_instance_id = pila.product_instance_id " +
               "and pi.product_id = p.product_id " +
               "and i.vendor_company_id = c.company_id " +
               "and i.invoice_id = id.invoice_id " +
               "and payment_date between ? and ? " +
               "and p.major_category_id = ? " +
               "order by i.payment_date, c.company_name, i.invoice_id, " +
               "pi.inventory_name ";

    PreparedStatement ps = conn.prepareStatement(q);
    ps.setDate(1, java.sql.Date.valueOf(startDate));
    ps.setDate(2, java.sql.Date.valueOf(endDate));
    ps.setInt(3, Session.getWineMajorCategoryId());
    ResultSet rs = ps.executeQuery();
    while (rs.next())
    {
      Vector<Object> v = new Vector();
      Integer invId = rs.getInt("invoice_id");
      v.add(invId);
      v.add(rs.getString("vendor_invoice_id"));
      v.add(rs.getDate("invoice_date").toString());
      v.add(rs.getDate("payment_date").toString());
      v.add(rs.getString("inventory_name"));
      v.add(rs.getFloat("price_per_unit"));
      v.add(rs.getString("company_name"));
      v.add(rs.getFloat("extended"));
      v.add(rs.getFloat("number_received"));
      data.add(v);
    }
    conn.close();
  }

  private void getCols()
  {
    cols.add("Invoice ID");
    cols.add("Vendor Invoice Number");
    cols.add("Invoice Date");
    cols.add("Payment Date");
    cols.add("Product Name");
    cols.add("Cost");
    cols.add("Vendor Name");
    cols.add("Extended");
    cols.add("Number Received");
  }
  
    private void getDateRange() {

        //frame.add(datePicker);
        startDateChooser = new DateChooserDialog("Select a Start Date");
        startDateChooser.setVisible(true);
        endDateChooser = new DateChooserDialog("Select an End Date");
        endDateChooser.setVisible(true);
        if (!isValidRange()) {
            JOptionPane.showMessageDialog(null, "Invalid Date Range ", "Error", JOptionPane.ERROR_MESSAGE);
            getDateRange();
        }
    }
  
  private boolean isValidRange()
  {
    return true;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 76, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
  private boolean compileReport = Boolean.FALSE;
  //private boolean compileReport = true;
  String reportPath = "lib/reports/";
  String compiledReport = reportPath + "PurchasePeriodCategoryReport.jasper";
  String reportSource = reportPath + "PurchasePeriodCategoryReport.jrxml";
  Map<String, Object> params = new HashMap<String, Object>();
  ReportViewer rv = new ReportViewer();
  JasperReport jr = null;
  JasperPrint jp = null;
  net.sf.jasperreports.engine.JRResultSetDataSource ds;
  private Vector<Vector<Object>> data = new Vector();
  private Vector<String> cols = new Vector();
  private ReportDataSource rds;
 
  private DateChooserDialog startDateChooser;
  private DateChooserDialog endDateChooser;
  private LocalDate startDate;
  private LocalDate endDate;
  private Connection conn = SessionManager.getConnection();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

  private class ReportDataSource implements JRDataSource
  {
    Vector<Vector<Object>> data;
    Vector<String> cols;
    int index = -1;

    public ReportDataSource(Vector<String> c, Vector<Vector<Object>> d)
    {
      cols = c;
      data = d;
    }

    @Override
    public boolean next() throws JRException
    {
      index ++;
      return index < data.size();
    }

    public int getColIndex(JRField colName)
    {
      String cn = colName.getName();
      int colIndex = -1;
      for (int i = 0; i < cols.size(); i ++)
      {
        //System.out.println("cols " + cols.elementAt(i));
        if (cn.equals(cols.elementAt(i)))
         colIndex = i;
      }
      return colIndex;
    }

    @Override
    public Object getFieldValue(JRField colName) throws JRException
    {
      int c = getColIndex(colName);
      if (c != -1 && index < data.size() && c < data.elementAt(index).size())
      {
        Object o = data.elementAt(index).elementAt(c);
        if (o != null)
        {
          if (c == 0)
          {
            return Integer.parseInt(o.toString());
          }
          if (c == 5 || c == 7 || c == 8)
          {
            return Float.parseFloat(o.toString());
          }
          else
            return o;
        }
        else
          return null;
      }
      else
        return null;
    }
  }
}
