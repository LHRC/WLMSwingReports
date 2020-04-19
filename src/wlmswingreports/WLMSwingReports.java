/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlmswingreports;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
            javax.swing.SwingUtilities.invokeLater( 
           new Runnable()
           {
             public void run()
             {
               if (login())
               {
                 //Session.initiateTheoreticalCountMap();
                 createAndShowGUI();
               }
               else
               {
                 JOptionPane.showMessageDialog(null, "Invalid Login");
                 System.exit(0);
                 //return;
               }
             }
           } );
    }
   
    private static Boolean login(){
        return Boolean.TRUE;
    }
    
   public static void createAndShowGUI()
  {
    
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
    //checkUpdate();
    MainFrame mf = new MainFrame();
    mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mf.setVisible(true);
  }
}
