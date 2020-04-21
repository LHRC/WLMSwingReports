/*
 * LoginDialog.java 
 */

package wlmswingreports;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


/**
 * @author mdonovan
 */



public class LoginDialog
{
  JFrame parent = new JFrame("Log In");
  String title;
  JDialog jd;
  JPanel namePanel = new JPanel();
  JPanel passwordPanel = new JPanel();
  JPanel buttonPanel = new JPanel();
  JTextField nameField = new JTextField(20);
  JPasswordField passwordField = new JPasswordField(20);
  JButton login = new JButton("Log in");
  JButton cancel = new JButton("Cancel");
  JComboBox categoryBox;
  ActionListener al = (ActionListener) new myActionListener();
  /*
  {
     public void actionPerformed(ActionEvent e)
     {  
       String pass = new String(passwordField.getPassword());
       if (nameField.getText().equals("admin") && pass.equals("password"))
       {
         Session.AUTHENTICATED = true;
       }
       parent.dispose();
     }
  };
  */
  /** Creates a new instance of LoginDialog */
  
  public LoginDialog()
  {
    getLocation();
    
    /////TODO
    //add login validation here
    

  } /** End method LoginDialog */
  

   private void getLocation()
   {
     LocationChooser lc = new LocationChooser();
     lc.setVisible(true);
   }

   
//     private void getCategories()
//    {   
//      String query = "select major_category_id, major_category_name from major_categories";
//      //Connection conn = ApplicationData.getConnection();
//      try
//      {
//        Statement statement = conn.createStatement();
//        statement.setQueryTimeout(ApplicationData.QUERY_TIMEOUT);
//        ResultSet rs = statement.executeQuery(query);
//        while (rs.next())
//        {
//          categories.add(new Category(rs.getInt("major_category_id"), rs.getString("major_category_name")));
//        }
//      }
//      catch (SQLException ex)
//      {
//        JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
//        parent.dispose();
//      }
//    }
     
  private class myActionListener implements ActionListener
  {
    // stub - also needs to handle category selection
    public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() == login)
      {
        Category c = (Category) categoryBox.getSelectedItem();
       // Session.setMajorCategoryID( c.getId());
//        Session.MAJOR_CATEGORY_NAME = c.getName();
        authenticateUser();
      }
      else
        //Session.AUTHENTICATED = false;
      parent.dispose();
    }
    
    // stub
    private void authenticateUser()
    {
       String pass = new String(passwordField.getPassword());
    } // end authenticate user
    
  } // end myActionListener
  
  private class Category
  {
    private int categoryId;
    private String categoryName;
    
    public Category(int num, String name)
    {
      categoryId = num;
      categoryName = name;
    }
    public String toString()
    {
      return categoryName;
    }

    public int getId()
    {
      return categoryId;
    }
    
    public String getName()
    {
      return categoryName;
    }
  }         
}