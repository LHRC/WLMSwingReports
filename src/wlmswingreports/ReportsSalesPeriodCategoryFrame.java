/*
 * ReportsSalesPeriodFrame.java
 *
 * Created on July 24, 2008, 7:32 PM
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
import java.util.Vector;
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
 * @author  Martin Donovan
 */
public class ReportsSalesPeriodCategoryFrame extends JInternalFrame
{
  
  /** Creates new form ReportsSalesPeriodFrame */
  public ReportsSalesPeriodCategoryFrame()
  {
    //super(mit);
    initComponents();
    getDateRange();
    getCols();
    getData();
    setupReport();
    this.dispose();
  }

  private void getCols()
  {
    cols.add("Matched");
    cols.add("Inventory Name");
    cols.add("POS Name");
    cols.add("POSPLU");
    cols.add("Sale Type");
    cols.add("Units Sold");
    cols.add("Average Unit Price");
    cols.add("Extended Amount");
    cols.add("Sales Cost Percent");
    cols.add("Inventory Units");
    cols.add("Unit Cost");
  }

private void getData()
  {
    Vector<Object> line = new Vector();
    Vector<Vector<Object>> noMatchVector = new Vector();
    Vector<Vector<Object>> matchVector = new Vector();
    startDate = startDateChooser.getLocalDate();
    endDate = endDateChooser.getLocalDate();
    //System.out.println(startDateChooser);
    Connection conn = SessionManager.getConnection();

    // CHANGED 6/16/2011 TO REFLECT NEW COST BASIS IN POS_CHKITEMS
    String q = "select pci.pos_product_name, pci.item_num, sum(pci.num_sold) as sold, " +
            "sum(pci.menu_price) as amount, pci.unit_cost_at_time_of_sale, pi.inventory_name, pci.is_inventory_unit, " +
            "pci.sales_units_sold, pci.sales_volume_unit_id, pi.bottle_size_id " +
            "from pos_chkitems pci left outer join " +
            "product_instance_location_associations pil " +
            "on pci.product_instance_location_id = pil.product_instance_location_id " +
            "left outer join product_instances pi " +
            "on pil.product_instance_id = pi.product_instance_id " +
            "where date_of_sale between ? and ? and pci.deletion = 0 " +
            "and major = (select pos_major_category_number from match_major_categories where " +
            "major_category_id = ?) " +
            "group by inventory_name, pos_product_name, item_num, is_inventory_unit, " +
            "sales_volume_unit_id, sales_units_sold, bottle_size_id, unit_cost_at_time_of_sale " +
            "order by inventory_name, is_inventory_unit, pos_product_name, " +
            "unit_cost_at_time_of_sale desc ";
//    String q = "select pci.pos_product_name, pci.item_num, sum(pci.num_sold) as sold, " +
//               "sum(pci.num_sold * pci.menu_price) as amount, pi.unit_price " +
//               "from pos_chkitems pci left outer join product_instance_location_associations pil " +
//               "on (pci.product_instance_location_id = pil.product_instance_location_id) " +
//               "left outer join product_instances pi on (pil.product_instance_id = pi.product_instance_id) " +
//               "where date_of_sale between ? and ? and pci.deletion = 0 " +
//               "and major = " +
//               "(select pos_major_category_number from match_major_categories where " +
//               " major_category_id = ?) " +
//               "group by pos_product_name, item_num, unit_price order by sold DESC, pos_product_name ";
               
    try
    {
      String saleType = null;
      getBottleSizes();
      getVolumeUnits();
      PreparedStatement ps = conn.prepareStatement(q);
      ps.setDate(1, java.sql.Date.valueOf(startDate));
      ps.setDate(2, java.sql.Date.valueOf(endDate));
      ps.setInt(3, Session.getWineMajorCategoryId());
      ResultSet rs = ps.executeQuery();
      while (rs.next())
      {
        line = new Vector();
        Boolean match = false;
        Integer sold = rs.getInt("sold");
        Integer POSPLU = rs.getInt("item_num");
        String iName = rs.getString("inventory_name");
        if (rs.wasNull())
          iName = "***NO MATCH***";
        else
          match = true;
        String POSName = rs.getString("pos_product_name");
        Float amount = rs.getFloat("amount");
        Float unitCost = rs.getFloat("unit_cost_at_time_of_sale");
        Boolean isInvUnit = rs.getBoolean("is_inventory_unit");
        //TODO Fix to handle unknown saleType 
        if (rs.wasNull())
          isInvUnit = false;
        saleType = isInvUnit ? "BOTTLE" : "GLASS";
        Float salesUnitsSold = rs.getFloat("sales_units_sold");
        Integer salesVolumeUnitID = rs.getInt("sales_volume_unit_id");
        Integer bottleSizeID = rs.getInt("bottle_size_id");
        Float avgSalePrice = amount / sold;
        Float costPercent = match ?
                getCostPercent(isInvUnit, sold, unitCost, amount,
                salesUnitsSold, salesVolumeUnitID, bottleSizeID)
                : 0;
        Float InvUnits = match ? 
                getInvUnits(isInvUnit, sold, salesUnitsSold, salesVolumeUnitID, bottleSizeID)
                : 0;
        line.add(match);
        line.add(iName);
        line.add(POSName);
        line.add(POSPLU);
        line.add(saleType);
        line.add(sold);
        line.add(avgSalePrice);
        line.add(amount);
        line.add(costPercent);
        line.add(InvUnits);
        line.add(unitCost);
        if (match)
        {
          matchVector.add(line);
        }
        else
        {
          noMatchVector.add(line);
        }
      }
      for (Vector<Object> v : noMatchVector)
      {
        data.add(v);
      }
      for (Vector<Object> v : matchVector)
      {
        data.add(v);
      }
      conn.close();
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
  }

  private Float getInvUnits(Boolean isInvUnit, Integer sold,
          Float salesUnitsSold, Integer salesVolumeUnitID, Integer bottleSizeID)
  {
    Float invUnits = null;
    if (isInvUnit)
    {
      invUnits = Float.parseFloat(sold.toString());
    }
    else
    {
      invUnits = sold * salesUnitsSold * volumeUnits.get(salesVolumeUnitID)
              / bottleSizes.get(bottleSizeID);
    }
    return invUnits;
  }

  //TODO NEEDS TO BE FIXED - MENU PRICE ONLY
  private Float getCostPercent(Boolean isInvUnit, Integer sold, Float unitPrice,
          Float amount, Float salesUnitsSold, Integer salesVolumeUnitID, Integer bottleSizeID)
  {
    Float costPercent = null;
    if (isInvUnit)
    {
      costPercent =  (sold * unitPrice) / amount;
    }
    else
    {
      Float servingSize =  (salesUnitsSold * volumeUnits.get(salesVolumeUnitID))
              / (bottleSizes.get(bottleSizeID)) ;
      costPercent = (sold * unitPrice * servingSize) / amount;
    }
    return costPercent;
  }
  
  private void getBottleSizes() throws SQLException
  {
    Connection conn = SessionManager.getConnection();
    String q = "select bs.bottle_size_id, bs.bottle_size * vu.equivalent_ml as size " +
               "from bottle_sizes bs, volume_units vu where bs.bottle_size_unit " +
               "= vu. volume_unit_id";
    PreparedStatement ps = conn.prepareStatement(q);
    ResultSet rs = ps.executeQuery();
    while (rs.next())
    {
      bottleSizes.put(rs.getInt("bottle_size_id"),rs.getFloat("size"));
    }
    conn.close();
  }

  private void getVolumeUnits() throws SQLException
  {
    Connection conn = SessionManager.getConnection();
    String q = "select volume_unit_id, equivalent_ml from volume_units";
    PreparedStatement ps = conn.prepareStatement(q);
    ResultSet rs = ps.executeQuery();
    while (rs.next())
    {
      volumeUnits.put(rs.getInt("volume_unit_id"), rs.getFloat("equivalent_ml"));
    }
    conn.close();
  }
  
  private void getDateRange()
  {
    startDateChooser = new DateChooserDialog("Select a Start Date");
    startDateChooser.setVisible(true);
    endDateChooser = new DateChooserDialog("Select an End Date");
    endDateChooser.setVisible(true);
    if (! isValidRange())
    {
      JOptionPane.showMessageDialog(null, "Invalid Date Range ", "Error", JOptionPane.ERROR_MESSAGE);
      getDateRange();
    }
  }

  private void setupReport()
  {
   try
    {
      rds = new ReportDataSource(cols, data);
//      JasperReport jasperReport =
//          JasperCompileManager.compileReport(reportSource);
//      JasperCompileManager.compileReportToFile(reportSource, compiledReport);

     InputStream jasper1 = getClass().getResourceAsStream("SalesPeriodCategoryReport.jasper");
      JasperReport jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader.
            loadObject(jasper1);

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);
      //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rds);

      //JasperExportManager.exportReportToHtmlFile(jasperPrint, reportDest);
      JasperViewer.viewReport(jasperPrint, false);
    }

    catch (JRException ex)
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
  private boolean isValidRange() 
  {
    return true; // fix
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
            .addGap(0, 272, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 62, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
  private DateChooserDialog startDateChooser;
  private DateChooserDialog endDateChooser;
  private LocalDate startDate;
  private LocalDate endDate;
  private Vector<String> cols = new Vector();
  private Vector<Vector<Object>> data = new Vector();
  private DefaultTableModel salesTableModel;
  private HashMap<Integer, Float> bottleSizes = new HashMap();
  private HashMap<Integer, Float> volumeUnits = new HashMap();

  String reportPath = "lib/reports/";
  String compiledReport = reportPath + "SalesPeriodCategoryReport.jasper";
  String reportSource = reportPath + "SalesPeriodCategoryReport.jrxml";
  Map<String, Object> params = new HashMap<String, Object>();
  ReportViewer rv = new ReportViewer();
  JasperReport jr = null;
  JasperPrint jp = null;
  net.sf.jasperreports.engine.JRResultSetDataSource ds;
  private ReportDataSource rds;
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
