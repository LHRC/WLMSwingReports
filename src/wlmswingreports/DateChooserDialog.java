/*
 * DateChooserDialog.java
 */

// TODO
/* 
 * This class could be more robust
 * needs to be able to handle any dates, not just for current or past year
 * better checking for valid date
 */

package wlmswingreports;

import java.util.Calendar;
import java.sql.Date;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 *
 * @author  mdonovan
 */
public class DateChooserDialog extends javax.swing.JDialog
{
  
  /** Creates new form DateChooserDialog */
  public DateChooserDialog(java.awt.Frame parent, boolean modal)
  {
    super(parent, modal);
    initComponents();
    initDate();
  }

  public DateChooserDialog()
  {
    super(new JFrame(), true);
    initComponents();
    initDate();
  }
  public  DateChooserDialog(String m)
  {
    super(new JFrame(), true);
    initComponents();
    initDate();
    messageLabel.setText(m);
  }

  @Override
  public String toString()
  {
    return " " + calendar.YEAR + " " + calendar.MONTH + " " + calendar.DATE;
  }


  public LocalDate getLocalDate()
  {
      return date.toLocalDate();
  }
  
  public java.sql.Date getSQLDate(){
      return java.sql.Date.valueOf(date.toLocalDate());
  }
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        monthCombo = new JComboBox(monthVector);
        dateCombo = new JComboBox(dayVector);
        yearCombo = new JComboBox(yearVector);
        jLabel1 = new javax.swing.JLabel();
        OKButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        messageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        monthCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthComboActionPerformed(evt);
            }
        });

        dateCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateComboActionPerformed(evt);
            }
        });

        yearCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearComboActionPerformed(evt);
            }
        });

        jLabel1.setText("Please Select a Date:");

        OKButton.setText("Use This Date");
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        messageLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        messageLabel.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addGap(292, 292, 292))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(OKButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(monthCombo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cancelButton)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(yearCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(monthCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKButton)
                    .addComponent(cancelButton))
                .addGap(36, 36, 36))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void OKButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OKButtonActionPerformed
  {//GEN-HEADEREND:event_OKButtonActionPerformed
    if (checkDate())
    {
      acceptDate = true;
      this.setVisible(false);
    }
    else
    {
      JOptionPane.showMessageDialog(null, "Select a Different Date", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }//GEN-LAST:event_OKButtonActionPerformed

  private boolean checkDate()
  {
    boolean valid = false;
    Month m = (Month) monthCombo.getSelectedItem();
    calendar.set(Calendar.YEAR, (Integer) yearCombo.getSelectedItem());
    calendar.set(Calendar.MONTH, m.number);
    calendar.set(Calendar.DATE, (Integer) dateCombo.getSelectedItem());
    //System.out.println(calendar);
    date = new Date(calendar.getTimeInMillis());
    //System.out.println(date);
    valid = true;
    return valid;
  }        
  public Date getDate()
  {
    return date;
  }

  private void initDate()
  {
    calendar.set(Calendar.YEAR, (Integer) yearCombo.getSelectedItem());
    Month m = (Month) monthCombo.getSelectedItem();
    calendar.set(Calendar.MONTH, m.number );
    calendar.set(Calendar.DATE, (Integer) dateCombo.getSelectedItem());
  }
  private void monthComboActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_monthComboActionPerformed
  {//GEN-HEADEREND:event_monthComboActionPerformed
    updateDayCombo((Month) monthCombo.getSelectedItem());
    //calendar.set(Calendar.MONTH, (Integer) monthCombo.getSelectedItem());
  }//GEN-LAST:event_monthComboActionPerformed

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
  {//GEN-HEADEREND:event_cancelButtonActionPerformed
    acceptDate = false;
    this.setVisible(false);
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void yearComboActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_yearComboActionPerformed
  {//GEN-HEADEREND:event_yearComboActionPerformed
    //calendar.set(Calendar.YEAR, (Integer) yearCombo.getSelectedItem());
    updateDayCombo((Month) monthCombo.getSelectedItem());
  }//GEN-LAST:event_yearComboActionPerformed

  private void dateComboActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dateComboActionPerformed
  {//GEN-HEADEREND:event_dateComboActionPerformed
    //calendar.set(Calendar.DATE, (Integer) dateCombo.getSelectedItem());
  }//GEN-LAST:event_dateComboActionPerformed

  private void setCurrentMonth(int m)
  {
    for (int i = 0; i < monthCombo.getItemCount(); i ++)
    {
      Month month = (Month) monthCombo.getItemAt(i);
      if ( month.number == m)
      {
        monthCombo.setSelectedIndex(i);
        monthCombo.repaint();
      }
    }
  }
  
  private Vector<Integer> initDayVector()
  {
    Vector<Integer> days = new Vector();
    for (int i = 1; i <= 31; i ++)
      days.add(i);
    return days;
  }
  
  private Vector<Integer> initYearVector()
  {
    Vector<Integer> years = new Vector();
    int y = calendar.get(Calendar.YEAR);
    years.add(y);
    for (int x = 1; x < 10; x ++) // fix this
    {
      years.add(y - x);
    }
    return years;
  }
  
   private Vector<Month> initMonthVector() 
  {
    Vector<Month> monthVector = new Vector();
    monthVector.add(new Month(0, "January"));
    monthVector.add(new Month(1, "February"));
    monthVector.add(new Month(2, "March"));
    monthVector.add(new Month(3, "April"));
    monthVector.add(new Month(4, "May"));
    monthVector.add(new Month(5, "June"));
    monthVector.add(new Month(6, "July"));
    monthVector.add(new Month(7, "August"));
    monthVector.add(new Month(8, "September"));
    monthVector.add(new Month(9, "October"));
    monthVector.add(new Month(10, "November"));
    monthVector.add(new Month(11, "December"));
    return monthVector;
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String args[])
  {
    java.awt.EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        DateChooserDialog dialog = new DateChooserDialog(new javax.swing.JFrame(), true);
        dialog.addWindowListener(new java.awt.event.WindowAdapter()
        {
          public void windowClosing(java.awt.event.WindowEvent e)
          {
            System.exit(0);
          }
        });
        dialog.setVisible(true);
      }
    });
  }
 
  private void updateDayCombo(DateChooserDialog.Month month)
  {
    int m = month.number;
    calendar.set(GregorianCalendar.MONTH, m);
    int max = calendar.getActualMaximum(calendar.DAY_OF_MONTH);
    dateCombo.removeAllItems();
    for (int i = 1; i <= max; i ++)
      dateCombo.addItem(i);
    dateCombo.repaint();
  }
    
  //SimpleTimeZone est = new SimpleTimeZone(-4 * 60 * 60 * 1000, );
  private boolean acceptDate = false;
  private GregorianCalendar calendar = new GregorianCalendar();
  private Vector<Month> monthVector = initMonthVector();
  private Vector<Integer> dayVector = initDayVector();
  private Vector<Integer> yearVector = initYearVector();
  private Date date = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton OKButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox dateCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JComboBox monthCombo;
    private javax.swing.JComboBox yearCombo;
    // End of variables declaration//GEN-END:variables



  
  private class Month
  {
    public String name;
    public int number;
    public Month(int num, String n )
    {
      number = num;
      name = n;
    }
    @Override
    public String toString()
    {
      return name;
    }
  }  
}
