/*
 * MainFrame.java
 *
 * Created on May 15, 2007, 6:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package wlmswingreports;


import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;



/**
 *
 * @author Martin Donovan
 */ 
public class MainFrame extends JFrame
{
  private JPanel content;
  private JInternalFrame newFrame;
  JDesktopPane newPane;
  public JPanel mainPanel;
 // private Vector<MenuTreeNode> nodeVector = new Vector<MenuTreeNode>();
  
  /** Creates a new instance of MainFrame */
  public MainFrame()
  {
    super();
    newPane = new JDesktopPane();
    JMenuBar mb = new JMenuBar();
    JMenu jm = new JMenu("Menu 1");
    JMenuItem jmi = new JMenuItem("item1");
    jmi.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("clicked");
            item1Clickec();
        }
    });
    jm.add(jmi);
    mb.add(jm);
    setJMenuBar(mb);
    content = (JPanel) getContentPane();
    String space = "                         ";
    String cr = "\u00a9";
    setTitle("Swing Reports Utility");
    setSize(1600,900);
    content.setLayout(new GridLayout(1,1) );
    mainPanel = new JPanel();
    mainPanel.setSize(800, 600);
    //JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    this.add(new JMenuBar());
    //mainPane.setDividerLocation(100);
    //mainPane.setBackground(Color.BLACK);
    //MainMenuTree mmt = new MainMenuTree(ApplicationData.getMenuTreeModel(), mainPane);
   // mainPane.setLeftComponent(new JScrollPane());
    JDesktopPane pane = new JDesktopPane();

    pane.setBackground(Color.BLACK);
    //mainPane.setRightComponent(pane);
    //mainPane.setVisible(true);
    content.add(mainPanel);
  }
  
  private void item1Clickec(){
      newFrame = new JInternalFrame("Item 1 Frame");
      newFrame.setBackground(Color.red);
      newFrame.setVisible(true);
      //mainPanel.add(newFrame);
      //mainPanel.setComponentZOrder(newFrame, 0);
      //content.add(newFrame);
      //content.setComponentZOrder(newFrame, 0);
      //newFrame.setVisible(true);
      //this.setComponentZOrder(newFrame, 0);
      //newPane.add(newFrame);
      //newPane.setVisible(true);
      //mainPanel.add(newFrame);
      //mainPanel.setComponentZOrder(newFrame, 0);
  }
}
