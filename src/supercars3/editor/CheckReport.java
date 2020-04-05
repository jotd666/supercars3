package supercars3.editor;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

import java.util.*;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class CheckReport extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton m_close = new JButton();
  BorderLayout borderLayout2 = new BorderLayout();
  JTextArea m_report_ta = new JTextArea();
  JPanel jPanel3 = new JPanel();
  JLabel jLabel1 = new JLabel();
  Border border1;

  public CheckReport(Frame frame, String title, Vector<String> report, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    if (report != null)
    {
    	for (String s : report)
    	{
    		m_report_ta.append( s + '\n');
    	}
    }
  }

  public CheckReport() {
    this(null, "", null, false);
  }
  private void jbInit() throws Exception {
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(178, 178, 178)),BorderFactory.createEmptyBorder(2,2,2,2));
    panel1.setLayout(borderLayout1);
    m_close.setText("Close");
    m_close.addActionListener(new CheckReport_m_close_actionAdapter(this));
    jPanel2.setLayout(borderLayout2);
    m_report_ta.setBorder(border1);
    m_report_ta.setCaretColor(Color.black);
    m_report_ta.setEditable(false);
    m_report_ta.setMargin(new Insets(0, 0, 0, 0));
    jLabel1.setText("Check results");
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(m_close, null);
    panel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(m_report_ta, BorderLayout.CENTER);
    panel1.add(jPanel3, BorderLayout.NORTH);
    jPanel3.add(jLabel1, null);
  }

  void m_close_actionPerformed(ActionEvent e) {
    dispose();
  }
}

class CheckReport_m_close_actionAdapter implements java.awt.event.ActionListener {
  CheckReport adaptee;

  CheckReport_m_close_actionAdapter(CheckReport adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.m_close_actionPerformed(e);
  }
}