/*
 * ReportsInventoryExtensionFrame.java
 *
 * Created on May 8, 2008, 7:32 PM
 */

package wlmswingreports;

import com.jasperassistant.designer.viewer.ReportViewer;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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
 * @author  mdonovan
 */
public class ReportsInventoryDiscrepanciesFrame extends JInternalFrame
{
  /** Creates new form ReportsInventoryExtensionFrame */
  public ReportsInventoryDiscrepanciesFrame()
  {

    initComponents();
    getInventoryDate();
    getTheoreticals();
    getParameters();
    getCols();
    try
    {
      getData();
      rds = new ReportDataSource(cols, data);
      exportToExcel();
      setupReport();
      conn.close();
    }
    catch (java.sql.SQLException ex)
    {
      ex.printStackTrace();
    }
    catch (net.sf.jasperreports.engine.JRException ex)
    {
      ex.printStackTrace();
    }
  }

  private void exportToExcel()
  {
    int reply = JOptionPane.showConfirmDialog(null, "Export to Excel?", "Export", JOptionPane.YES_NO_OPTION);
    if (reply == JOptionPane.YES_OPTION)
    {
      //ReportExporter re = new ReportExporter(cols, data, reportName);
    }
  }
  
  private void setupReport() throws JRException
  {
//    JasperReport jasperReport =
//        JasperCompileManager.compileReport(reportSource);
//    JasperCompileManager.compileReportToFile(reportSource, compiledReport);
    JasperReport jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader.
            loadObject(new File(compiledReport));

    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);
    //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);

    JasperViewer.viewReport(jasperPrint, false);
  }

  private void getParameters()
  {
    params.put("INVENTORY_DATE", dateDialog.getSelectedDate().toString());
    params.put("BUSINESS_NAME", SessionManager.getActiveRestaurant().getRestaurantName());
    params.put("CATEGORY_NAME", "Wine");
  }
  
  private void getTheoreticals()
  {
   // theoMap = new TheoreticalCountMap(new SimpleDate(dateDialog.getSelectedDate()));
  }

  private void getCols()
  {
    cols.add("Product Name");
    cols.add("Quantity");
    cols.add("Theoretical");
    cols.add("Units Missing");
    cols.add("Unit Price");
    cols.add("Extended");
  }

  private void getInventoryDate()
  {
    dateDialog = new InventoryDateChooserDialog(new JFrame(), true);
    dateDialog.setVisible(true);
  }
    
  private void getData() throws SQLException
  {
     int inventoryId = dateDialog.getSelectedInventoryId();
     
     // fix this to use the correct category
     String q = "select pi.product_instance_id, pi.inventory_name, id.product_cost, " +
              "sum(id.quantity_counted) as sum_quantity " +
              "from inventory_details id, product_instances pi, " +
              "product_instance_location_associations pila, products p " +
              "where  pi.product_id = p.product_id " +
              "and id.product_instance_location_id = pila.product_instance_location_id " +
              "and pila.product_instance_id = pi.product_instance_id " +
              "and inventory_id = ? " +
              "and p.major_category_id = ? " +
              "group by pi.product_instance_id, pi.inventory_name, id.product_cost " +
              "order by pi.inventory_name";
 
      PreparedStatement ps = conn.prepareStatement(q);
      ps.setInt(1, inventoryId);
      ps.setInt(2, Session.getWineMajorCategoryId());
      ResultSet rs = ps.executeQuery();
      while (rs.next())
      {
        Vector<Object> line = new Vector();
        String iName = rs.getString("inventory_name");
        Integer pid = rs.getInt("product_instance_id");
        Float theo = theoMap.getTheoreticalByProductInstanceID(pid);
        Float qc = rs.getFloat("sum_quantity");
        Float pCost = rs.getFloat("product_cost");
        Float d = theo - qc;
        Float ext = d * pCost;
        line.add(iName);
        line.add(qc);
        line.add(theo);
        line.add(d);
        line.add(pCost);
        line.add(ext);
        if (d != 0)
        {
          data.add(line);
        }
      }
      Comparator c = new Comparator()
      {
        @Override
        public int compare(Object o1, Object o2)
        {
          Vector v1 = (Vector) o1;
          Vector v2 = (Vector) o2;
          if ( (Float) v1.elementAt(5) > (Float) v2.elementAt(5))
            return -1;
          else if ((Float) v1.elementAt(5) < (Float) v2.elementAt(5))
            return 1;
          else return 0;
        }
      };
      Collections.sort(data, c);
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
            .addGap(0, 271, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
  private Connection conn = SessionManager.getConnection();
  private Vector<String> cols = new Vector();
  private Vector<Vector<Object>> data = new Vector();
  private InventoryDateChooserDialog dateDialog;
 
    DecimalFormat df = ApplicationData.STANDARD_DECIMAL_FORMAT;
  String reportPath = "lib/reports/";
  String compiledReport = reportPath + "InventoryDiscrepanciesReport.jasper";
  String reportSource = reportPath + "InventoryDiscrepanciesReport.jrxml";
  String reportName = "InventoryExceptionReport";
  Map<String, Object> params = new HashMap<String, Object>();
  ReportViewer rv = new ReportViewer();
  JasperReport jr = null;
  JasperPrint jp = null;
  net.sf.jasperreports.engine.JRResultSetDataSource ds;
  private ReportDataSource rds;
  TheoreticalCountMap theoMap;

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
            return o.toString();
          else
            return (Float) o;
        }
        else
          return null;
      }
      else
        return null;
    }
  }
}
