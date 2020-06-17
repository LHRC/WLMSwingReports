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
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
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
 * @author Martin Donovan
 */
public class ReportsWineListLargeSmallFormatFrame extends JInternalFrame
{

    /** Creates new form ReportsInventoryExtensionFrame */
    public ReportsWineListLargeSmallFormatFrame()
    {
      try
      {
        getAllocations();
        getSubcategories();
        getReportData();
        getParameters();
        initComponents();
        this.setVisible(false);
        setUpReport();
      }
      catch (SQLException ex)
      {
        ex.printStackTrace();
      }
    }

  private void getParameters()
  {
    params.put("BUSINESS_NAME", SessionManager.getActiveRestaurant().getRestaurantName());
    params.put("CATEGORY_NAME", "Wine");
  }

  private void getSubcategories() throws SQLException
  {
    String q = "select wlsc.wine_list_subcategory_id, " +
               "wlsc.wine_list_subcategory_name from " +
               "wine_list_subcategories wlsc";
    PreparedStatement ps = conn.prepareStatement(q);
    ResultSet rs = ps.executeQuery();
    while (rs.next())
    {
      Integer scid = rs.getInt("wine_list_subcategory_id");
      String name  = rs.getString("wine_list_subcategory_name");
      scMap.put(scid, name);
    }
  }

  private void getAllocations() throws SQLException
  {
    String q = "select a.product_instance_id, sum(a.amount) as total " +
               "from allocations a, quarters q " +
               "where q.quarter_id = q.quarter_id " +
               "and q.start_date > ? " +
               "group by product_instance_id";
    PreparedStatement ps = conn.prepareStatement(q);
    ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
    ResultSet rs = ps.executeQuery();
    while (rs.next())
    {
      Integer pid = rs.getInt("product_instance_id");
      Integer total = rs.getInt("total");
      am.put(pid, total);
    }
  }

    private void setUpReport() {
        try {
            InputStream jasper1 = getClass().getResourceAsStream(compiledReport);
            JasperReport jasperReport = (JasperReport) net.sf.jasperreports.
                    engine.util.JRLoader.loadObject(jasper1);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }
  
  private void getReportData() throws SQLException
  {
    getCols();
    getData();
    rds = new ReportDataSource(cols, data);
  }

  private void getCols()
  {
    cols.add("Category");
    cols.add("Bin Number");
    cols.add("Wine List Name");
    cols.add("Vintage");
    cols.add("Bottle Size");
    cols.add("Sale Price");
    cols.add("Description");
    cols.add("Subcategory");
  }

  private String getBottleSize(int bs, String vu)
  {
    if (bs == 750 && "ml".equals(vu))
      return null;
    else if (bs >= 1000 && "ml".equals(vu))
    {
      float size = (float) bs;
      return "" + single.format(size / 1000) + " L";
    }
    else if (bs < 1000 && "ml".equals(vu))
      return "" + bs + " " + vu;
    else
      return "" + bs + " " + vu;
  }

  private void getData() throws SQLException
  {
     String q = "select wlc.wine_list_category_name, pi.wine_list_name, " +
              "pi.wine_list_description, pi.sale_price, " +
              "v.vintage_year, pi.product_instance_id, " +
              "bs.bottle_size, vu.volume_unit_name, " +
              "pi.wine_list_bin_number, pi.wine_list_subcategory_id " +
              "from product_instances pi inner join products p " +
              "on pi.product_id = p.product_id " +
              "inner join wine_list_categories wlc " +
              "on pi.wine_list_category_id = wlc.wine_list_category_id " +
              "inner join bottle_sizes bs " +
              "on pi.bottle_size_id = bs.bottle_size_id " +
              "inner join  volume_units vu " +
              "on bs.bottle_size_unit = vu.volume_unit_id " +
              "left outer join vintages v " +
              "on pi.vintage_id = v.vintage_id " +
              "where pi.wine_list_bin_number is not null " +
              "and pi.bottle_size_id <> " +
              "(select bottle_size_id from bottle_sizes where " +
              "bottle_size = 750 and bottle_size_unit = " +
              "(select volume_unit_id from volume_units where volume_unit_name = 'ml') ) " +
              "and pi.wine_list_name is not null and " +
              "pi.sale_price is not null " +
              "and p.major_category_id = " +
              "(select major_category_id from major_categories where " +
              "lower(major_category_name) = 'wine') " +
              "order by wlc.wine_list_category_name, pi.wine_list_subcategory_id, "+
              "pi.wine_list_name, v.vintage_year ";
    PreparedStatement ps = conn.prepareStatement(q);
    ResultSet rs = ps.executeQuery();
    while (rs.next())
    {
      Vector<Object> line = new Vector();
      Integer pid = rs.getInt("product_instance_id");
      String name = rs.getString("wine_list_name");
            Integer scid = rs.getInt("wine_list_subcategory_id");
      Integer year = rs.getInt("vintage_year");
      String yearString;
      if (rs.wasNull())
      {
//        if (name.endsWith(",\"")   )
//          name.replace(",\"", "\"");
//        else if (name.endsWith(",”"))
//          name.replace(",”", "”");
        yearString = "";
      }
      else
      {
        yearString = year.toString();
        if( ! (name.endsWith(",\"")   || name.endsWith(",”")) )
          name += ",";
      }
      line.add(rs.getString("wine_list_category_name"));
      line.add(rs.getInt("wine_list_bin_number"));
      line.add(name);
      line.add(yearString);
      int bs = rs.getInt("bottle_size");
      String vu = rs.getString("volume_unit_name");
      line.add(getBottleSize(bs, vu));
      line.add(rs.getFloat("sale_price"));
      line.add(rs.getString("wine_list_description"));
      String scName = scid == null ? null : scMap.get(scid);
      line.add(scName);
      if (tcm.getTheoreticalByProductInstanceID(pid) > 0)
      {
        if ( ! am.containsKey(pid) )
          data.add(line);
        else
        {
          if (tcm.getTheoreticalByProductInstanceID(pid) > am.get(pid) )
            data.add(line);
        }
      }
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
            .addGap(0, 259, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 46, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

  TheoreticalCountMap tcm = new TheoreticalCountMap();
  HashMap<Integer, String> scMap = new HashMap();
  HashMap<Integer, Integer> am = new HashMap();
  private Connection conn = SessionManager.getConnection();
  private Vector<String> cols = new Vector();
  private Vector<Vector<Object>> data = new Vector();
  DecimalFormat df = WLMSwingReports.STANDARD_DECIMAL_FORMAT;
  DecimalFormat single = new DecimalFormat("#,##0.0");
  String compiledReport = "WineListByCategoryReport.jasper";
  String reportDest = "JasperTest.html";
  Map<String, Object> params = new HashMap<String, Object>();
  ReportViewer rv = new ReportViewer();
  JasperReport jr = null;
  JasperPrint jp = null;
  net.sf.jasperreports.engine.JRResultSetDataSource ds;
  private ReportDataSource rds;

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
          if ( c == 1 )
          {
            return Integer.parseInt(o.toString());
          }
          else if (c == 5)
          {
//            DecimalFormatSymbols sym = new DecimalFormatSymbols();
//            sym.setGroupingSeparator(',');
//            DecimalFormat form = new DecimalFormat("");
//            form.setDecimalFormatSymbols(sym);
//            Number num = null;
//            try
//            {
//              num = form.parse("123,456");
//            }
//            catch (ParseException ex)
//            {
//              ex.printStackTrace();
//            }
//            Float f = num.floatValue();
//            return f;
              return Float.parseFloat(o.toString());
          }
          else
            return o.toString();
        }
        else
          return null;
      }
      else
        return null;
    }
  }
}
