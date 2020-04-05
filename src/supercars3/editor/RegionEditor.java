package supercars3.editor;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

import supercars3.base.*;

import javax.swing.event.*;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class RegionEditor extends JDialog implements Observer {
  Zone m_zone;
  JPanel m_main_panel = null;
  Panel m_segment_edition = new Panel();
  JPanel m_checkpoint_edition = new JPanel();
  JLabel jLabel1 = new JLabel();
  JButton m_bounds_13 = new JButton();
  JCheckBox m_fence_cb = new JCheckBox();
  JButton m_bounds_24 = new JButton();
  SegmentSelector m_region_segments = new SegmentSelector();
  JButton m_ok = new JButton();
  JComboBox m_slope_cbox = new JComboBox();
  JComboBox m_region_type_cbox = new JComboBox();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JComboBox m_finish_line_cbox = new JComboBox();

  public void update(Observable o,Object x)
  {
    Boundary b = ((SegmentSelector.ObservableSegment)o).get_boundary();
    boolean v = (b != null);

    	
    m_segment_edition.setVisible(v);

    if (b != null)
    {
      // update gadgets according to boundary properties
      m_fence_cb.setSelected(b.is_fence());
      // ...
      // load the selected segment
      m_slope_cbox.setSelectedIndex(b.get_slope_type());

    }

  }

  public RegionEditor(Frame frame, boolean modal, Zone zp) {
	  
	  
    super(frame, zp== null ? "Design" : "Edit region "+zp.get_name(), modal);
	  Zone z = zp;
	  if (z == null)
	  {
		  // for the designer
		  Collection<ControlPoint> c = new HashSet<ControlPoint>();
		  c.add(new ControlPoint(0,0));
		  c.add(new ControlPoint(10,0));
		  c.add(new ControlPoint(0,10));
		  c.add(new ControlPoint(10,10));
		  
		  z = new Zone(c,1);
	  }
    m_zone = z;
    try {
      jbInit();
      pack();
    }


    catch(Exception ex) {
      ex.printStackTrace();
    }

    m_region_segments.set_observer(this);
    m_region_segments.set_zone(m_zone);
    m_segment_edition.setVisible(false);

	m_region_type_cbox.setSelectedIndex(m_zone.get_visible_type().ordinal());

    m_finish_line_cbox.setSelectedIndex(m_zone.get_checkpoint_type().ordinal());
  }

  public RegionEditor() {
    this(null, false, null);
  }
  private void jbInit() throws Exception {
    this.setResizable(false);
    m_main_panel = (JPanel)this.getContentPane();

    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    m_main_panel.setLayout(null);
     m_segment_edition.setBounds(new Rectangle(168, 0, 226, 265));
    m_segment_edition.setLayout(null);

    m_checkpoint_edition.setBounds(new Rectangle(10, 52, 214, 207));
    m_checkpoint_edition.setLayout(null);
    jLabel1.setText("region segments");
    jLabel1.setBounds(new Rectangle(14, 0, 97, 30));
    m_bounds_13.setBounds(new Rectangle(20, 276, 134, 25));
    m_bounds_13.setText("Fences for 1 & 3");
    m_bounds_13.addActionListener(new RegionEditor_m_bounds_13_actionAdapter(this));
    m_bounds_13.addActionListener(new RegionEditor_m_bounds_13_actionAdapter(this));
    m_fence_cb.setToolTipText("Segment is a fence, cars cannot pass through it");
    m_fence_cb.setText("Fixed fence");
    m_fence_cb.setBounds(new Rectangle(70, 0, 87, 43));
    m_fence_cb.addChangeListener(new RegionEditor_m_fence_cb_changeAdapter(this));
    
    m_region_type_cbox.addItem("Normal region");
    m_region_type_cbox.addItem("Hidden");
    m_region_type_cbox.addItem("Jump");
    m_region_type_cbox.addItem("Top-priority");
    m_region_type_cbox.addItem("Gates");
    m_region_type_cbox.addItem("Train start");
    m_region_type_cbox.addItem("Train end");
       
    m_region_type_cbox.setBounds(new Rectangle(18, 195, 130, 26));

    m_bounds_24.setText("Fences for 2 & 4");
    m_bounds_24.addActionListener(new RegionEditor_m_bounds_24_actionAdapter(this));
    m_bounds_24.addActionListener(new RegionEditor_m_bounds_24_actionAdapter(this));
    m_bounds_24.setBounds(new Rectangle(158, 276, 134, 25));
    m_region_segments.setBounds(new Rectangle(20, 36, 150, 150));
    m_region_segments.setBackground(Color.WHITE);
    m_region_segments.setSize(new java.awt.Dimension(150, 150));
    m_ok.setBounds(new Rectangle(179, 305, 64, 25));
    m_ok.setText("Done");
    m_ok.addActionListener(new RegionEditor_m_ok_actionAdapter(this));
    m_slope_cbox.addItem("Normal");
    m_slope_cbox.addItem("Slope bottom");
    m_slope_cbox.addItem("Slope top");
    m_slope_cbox.addItem("Slope bottom & opposite top");
    m_slope_cbox.addItem("Slope top & opposite bottom");
    m_slope_cbox.setBounds(new Rectangle(10, 32, 202, 25));
    m_slope_cbox.addActionListener(new RegionEditor_m_slope_cbox_actionAdapter(this));
    jLabel2.setText("Slope type");
    jLabel2.setBounds(new Rectangle(10, 0, 74, 25));
 

    m_finish_line_cbox.addItem("No finish");
    m_finish_line_cbox.addItem("Finish");
    m_finish_line_cbox.addItem("Checkpoint");
 
    m_finish_line_cbox.addActionListener(new RegionEditor_m_finish_line_cbox_actionAdapter(this));
    //m_finish_line_cbox.setActionCommand("comboBoxChanged");
    m_finish_line_cbox.setBounds(new Rectangle(18, 238, 150, 24));


    m_finish_line_cbox.addActionListener(new RegionEditor_m_finish_line_cbox_actionAdapter(this));
    m_segment_edition.add(m_checkpoint_edition, null);
    m_checkpoint_edition.add(m_slope_cbox, null);
    m_segment_edition.add(m_fence_cb, null);
    m_main_panel.add(m_region_type_cbox, null);
	m_region_type_cbox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			m_region_type_cboxActionPerformed(evt);
		}
	});
    m_main_panel.add(m_bounds_24, null);
    m_main_panel.add(m_bounds_13, null);
    m_main_panel.add(m_finish_line_cbox, null);
    m_main_panel.add(m_ok, null);
    m_main_panel.add(jLabel1, null);
    m_main_panel.add(m_region_segments);
    m_main_panel.add(m_segment_edition, null);
    m_checkpoint_edition.add(jLabel2, null);
    m_checkpoint_edition.add(jLabel4, null);
    m_checkpoint_edition.add(jLabel3, null);
    m_fence_cb.addActionListener(new RegionEditor_m_fence_cb_actionAdapter(this));

  }


  void m_ok_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  void m_bounds_13_actionPerformed(ActionEvent e) {
    preset_bounds(true);
  }
  void m_bounds_24_actionPerformed(ActionEvent e) {
   preset_bounds(false);
  }

   private void preset_bounds(boolean t)
   {
    m_zone.get_boundary(0).set_fence(t);
    m_zone.get_boundary(2).set_fence(t);
    m_zone.get_boundary(1).set_fence(!t);
    m_zone.get_boundary(3).set_fence(!t);
    m_zone.unselect_boundaries();
    m_segment_edition.setVisible(false);
    m_region_segments.repaint();
  }



  void m_fence_cb_actionPerformed(ActionEvent e)
  {
    boolean is_fence = m_fence_cb.isSelected();
    //m_checkpoint_edition.setVisible(!is_fence);
    m_zone.get_selected_boundary().set_fence(is_fence);
    m_region_segments.repaint();
  }




  void m_fence_cb_stateChanged(ChangeEvent e) {
    m_fence_cb_actionPerformed(null);
  }



  void m_finish_line_cbox_actionPerformed(ActionEvent e) {
    m_zone.set_checkpoint_type(m_finish_line_cbox.getSelectedIndex());
  }

  void m_slope_cbox_actionPerformed(ActionEvent e) {
    int zt = Boundary.SLOPE_NORMAL;
    boolean oppose = false;
    switch(m_slope_cbox.getSelectedIndex())
    {
        case 3:
          // bottom with top opposition
          zt = Boundary.SLOPE_BOTTOM;
          oppose = true;
          break;
        case 4:

          // top with bottom opposition
          zt = Boundary.SLOPE_TOP;
          oppose = true;
          break;
        case 1:
          // bottom, no oppose
          zt = Boundary.SLOPE_BOTTOM;
          break;
        case 2:
          // top, no oppose
          zt = Boundary.SLOPE_TOP;
          break;

        default:
          break;
    }
    m_zone.set_selected_boundary_type(zt,oppose);
    m_region_segments.repaint();
  }
  
private void m_region_type_cboxActionPerformed(ActionEvent evt) {	
	
	m_zone.set_visible_type(m_region_type_cbox.getSelectedIndex());
}

}



class RegionEditor_m_bounds_13_actionAdapter implements java.awt.event.ActionListener {
  RegionEditor adaptee;

  RegionEditor_m_bounds_13_actionAdapter(RegionEditor adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.m_bounds_13_actionPerformed(e);
  }
}

class RegionEditor_m_bounds_24_actionAdapter implements java.awt.event.ActionListener {
  RegionEditor adaptee;

  RegionEditor_m_bounds_24_actionAdapter(RegionEditor adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.m_bounds_24_actionPerformed(e);
  }
}

class RegionEditor_m_ok_actionAdapter implements java.awt.event.ActionListener {
  RegionEditor adaptee;

  RegionEditor_m_ok_actionAdapter(RegionEditor adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.m_ok_actionPerformed(e);
  }
}

class RegionEditor_m_fence_cb_actionAdapter implements java.awt.event.ActionListener {
  RegionEditor adaptee;

  RegionEditor_m_fence_cb_actionAdapter(RegionEditor adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.m_fence_cb_actionPerformed(e);
  }
}

class RegionEditor_m_fence_cb_changeAdapter implements javax.swing.event.ChangeListener {
  RegionEditor adaptee;

  RegionEditor_m_fence_cb_changeAdapter(RegionEditor adaptee) {
    this.adaptee = adaptee;
  }
  public void stateChanged(ChangeEvent e) {
    adaptee.m_fence_cb_stateChanged(e);
  }
}



class RegionEditor_m_slope_cbox_actionAdapter implements java.awt.event.ActionListener {
  RegionEditor adaptee;

  RegionEditor_m_slope_cbox_actionAdapter(RegionEditor adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.m_slope_cbox_actionPerformed(e);
  }
}



class RegionEditor_m_finish_line_cbox_actionAdapter implements java.awt.event.ActionListener {
  RegionEditor adaptee;

  RegionEditor_m_finish_line_cbox_actionAdapter(RegionEditor adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.m_finish_line_cbox_actionPerformed(e);
  }
}
