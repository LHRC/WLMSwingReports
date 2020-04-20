/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlmswingreports;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author mdonovan
 */
public class WLMSwingReports {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if(login()){
            createAndShowGUI();
        }
    }
    
   public static void createAndShowGUI()
   {
    setLookAndFeel();
    //MainFrame mf = new MainFrame();
    JFrame mainFrame = new MainFrame();
    //mainFrame.setSize(1200, 900);
    //mainFrame.setMinimumSize(new Dimension(1200, 900));
    mainFrame.setBackground(Color.yellow);
    mainFrame.setVisible(true);
    //mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //mf.setVisible(true);
    //JPanel mainPanel = new JPanel();
    //mainPanel.setSize(800, 600);
    //mainPanel.setBackground(Color.red);
    //mainFrame.add(mainPanel);
    //mainPanel.setVisible(true);
   }
   
     private static boolean login()
  {
      return Boolean.TRUE;
//    if (ApplicationData.REQUIRE_LOGIN == false)
//    {
//      Session.MAJOR_CATEGORY_ID = 1;
//      return true;
//    }
//    else
//    {
//      LoginDialog ld = new LoginDialog();
//      return (Session.AUTHENTICATED); // && checkEnvironment()) ;
//    }
  }
   
   private static void setLookAndFeel(){
    try
    {
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    }
    catch (UnsupportedLookAndFeelException ex)
    {
      ex.printStackTrace();
    }
    catch (IllegalAccessException ex)
    {
      ex.printStackTrace();
    }
    catch (ClassNotFoundException ex)
    {
      ex.printStackTrace();
    }
    catch (InstantiationException ex)
    {
      ex.printStackTrace();
    }
   }
}
