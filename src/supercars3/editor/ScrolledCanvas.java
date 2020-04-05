package supercars3.editor;

import java.awt.*;
import javax.swing.*;

import java.util.*;

import supercars3.base.*;

/**
 * <p>Titre : </p>
 * <p>Description : Scrolled manager for circuit display in editor</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class ScrolledCanvas extends JPanel
{
  Circuit m_circuit = null;
  
  private JScrollPane m_pane;
  
  public ScrolledCanvas(CircuitData data, Observer obs)
  {
    super(new BorderLayout());
    if ((data != null) && (obs != null))
    {
      m_circuit = new Circuit(data,obs);
      m_pane = new JScrollPane(m_circuit);
      add(m_pane,BorderLayout.CENTER);
    }

  }

  public ScrolledCanvas()
  {
	  this(null,null);
  }

 

}