package supercars3.editor;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.Rectangle;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSlider;

import supercars3.base.CircuitDirectory;

public class FrmCircuitDirectoryEditor extends JDialog
{

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="127,46"

	private JTextField circuitName = null;

	private JLabel jLabel = null;

	private JTextField humanStartEngine = null;

	private JLabel hseLabel = null;

	private JLabel hseLabel1 = null;

	private JTextField cpuStartEngine = null;

	private JLabel hseLabel11 = null;

	private JTextField cpuEndEngine = null;

	private JSlider nbCircuitsSlider = null;

	private JLabel labelNbCircuits = null;

	private JButton jOkButton = null;

	private JTextField nbCircuits = null;

	private JLabel hseLabel2 = null;

	private JLabel hseLabel21 = null;

	private JTextField cpuAggressivity = null;

	private CircuitDirectory circuitDirectory = null;  //  @jve:decl-index=0:

	private JTextField cpuFrontWeapons = null;

	private JLabel hseLabel211 = null;

	private JTextField cpuRearWeapons = null;
	
	/**
	 * @param owner
	 */
	
	public FrmCircuitDirectoryEditor(Frame owner)
	{
		this(owner,null);
		initialize();
	}
	public FrmCircuitDirectoryEditor(Frame owner,CircuitDirectory cd)
	{
		super(owner);
		circuitDirectory = cd;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(400, 400);
		this.setTitle("Circuit Directory Editor");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
	{
		if (jContentPane == null) {
			
			if (circuitDirectory == null)
			{
				circuitDirectory = new CircuitDirectory();
			}
			
			hseLabel211 = new JLabel();
			hseLabel211.setBounds(new Rectangle(19, 196, 111, 16));
			hseLabel211.setText("CPU Rear weapons");
			hseLabel21 = new JLabel();
			hseLabel21.setBounds(new Rectangle(17, 159, 123, 16));
			hseLabel21.setText("CPU Front weapons");
			hseLabel2 = new JLabel();
			hseLabel2.setBounds(new Rectangle(16, 227, 113, 16));
			hseLabel2.setText("CPU Aggressivity");
			
			labelNbCircuits = new JLabel();
			labelNbCircuits.setBounds(new Rectangle(18, 263, 315, 21));
			labelNbCircuits.setText("Number of circuits (1-10)");

			hseLabel11 = new JLabel();
			hseLabel11.setBounds(new Rectangle(18, 125, 96, 16));
			hseLabel11.setText("CPU End Engine");
			hseLabel1 = new JLabel();
			hseLabel1.setBounds(new Rectangle(18, 95, 102, 16));
			hseLabel1.setText("CPU Start Engine");
			hseLabel = new JLabel();
			hseLabel.setBounds(new Rectangle(14, 58, 120, 23));
			hseLabel.setText("Human Start Engine");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(12, 19, 66, 19));
			jLabel.setText("Name");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setSize(new Dimension(390, 273));
			jContentPane.add(getCircuitName(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getHumanStartEngine(), null);
			jContentPane.add(hseLabel, null);
			jContentPane.add(hseLabel1, null);
			jContentPane.add(getCpuStartEngine(), null);
			jContentPane.add(hseLabel11, null);
			jContentPane.add(getCpuEndEngine(), null);
			jContentPane.add(getNbCircuitsSlider(), null);
			jContentPane.add(labelNbCircuits, null);
			jContentPane.add(getJOkButton(), null);
			jContentPane.add(getNbCircuits(), null);
			jContentPane.add(hseLabel2, null);
			jContentPane.add(hseLabel21, null);
			jContentPane.add(getCpuAggressivity(), null);
			jContentPane.add(getCpuFrontWeapons(), null);
			jContentPane.add(hseLabel211, null);
			jContentPane.add(getCpuRearWeapons(), null);
		}
		return jContentPane;
	}





	/**
	 * This method initializes circuitName	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCircuitName()
	{
		if (circuitName == null) {
			circuitName = new JTextField(circuitDirectory.name);
			circuitName.setBounds(new Rectangle(91, 20, 215, 20));
		}
		return circuitName;
	}

	/**
	 * This method initializes humanStartEngine	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getHumanStartEngine()
	{
		if (humanStartEngine == null) 
		{
			humanStartEngine = new JTextField(circuitDirectory.initial_human_engine+"");
			humanStartEngine.setBounds(new Rectangle(148, 63, 50, 20));
		}
		return humanStartEngine;
	}

	/**
	 * This method initializes cpuStartEngine	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCpuStartEngine()
	{
		if (cpuStartEngine == null) {
			cpuStartEngine = new JTextField(circuitDirectory.initial_cpu_engine+"");
			cpuStartEngine.setBounds(new Rectangle(148, 95, 50, 20));
		}
		return cpuStartEngine;
	}

	/**
	 * This method initializes cpuEndEngine	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCpuEndEngine()
	{
		if (cpuEndEngine == null) {
			cpuEndEngine = new JTextField(circuitDirectory.final_cpu_engine+"");
			cpuEndEngine.setBounds(new Rectangle(148, 122, 50, 20));
		}
		return cpuEndEngine;
	}
	
	private void update_nb_circuits()
	{
		int value = nbCircuitsSlider.getValue();
		circuitDirectory.nb_circuits = value;
		nbCircuits.setText(value + "");
	}
	
	/**
	 * This method initializes nbCircuitsSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getNbCircuitsSlider()
	{
		if (nbCircuitsSlider == null) {
			nbCircuitsSlider = new JSlider(1,10,circuitDirectory.nb_circuits);
			nbCircuitsSlider.setBounds(new Rectangle(19, 291, 266, 22));

			nbCircuitsSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e)
				{
					update_nb_circuits();
				}
			});
		}
		return nbCircuitsSlider;
	}
	
	private void update_circuit_directory() throws Exception
	{
		// update object
		String alt = cpuAggressivity.getText();
		String [] al;
		int [] ali;
		circuitDirectory.name = circuitName.getText();
		circuitDirectory.set_initial_human_engine(Integer.parseInt(humanStartEngine.getText()));
		circuitDirectory.set_initial_cpu_engine(Float.parseFloat(cpuStartEngine.getText()));
		circuitDirectory.set_final_cpu_engine(Float.parseFloat(cpuEndEngine.getText()));
		circuitDirectory.set_cpu_nb_front_weapons(Integer.parseInt(cpuFrontWeapons.getText()));
		circuitDirectory.set_cpu_nb_rear_weapons(Integer.parseInt(cpuRearWeapons.getText()));
		al = alt.split(" ");
		ali = new int[al.length];
		int i =0;
		for (String a : al)
		{
			ali[i++]= Integer.parseInt(a);
		}
		
		circuitDirectory.aggressivity = ali;
		
		circuitDirectory.serialize();		
		
	}
	
	private JDialog get_dialog()
	{
		return this;
	}
	/**
	 * This method initializes jOkButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJOkButton()
	{
		if (jOkButton == null) {
			jOkButton = new JButton();
			jOkButton.setBounds(new Rectangle(152, 338, 81, 25));
			jOkButton.setText("OK");

			jOkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					try
					{
						update_circuit_directory();
						
						get_dialog().dispose();
					}
					catch (Exception exc)
					{
						JOptionPane.showMessageDialog(get_dialog(), exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
		return jOkButton;
	}

	/**
	 * This method initializes nbCircuits	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNbCircuits()
	{
		if (nbCircuits == null) {
			nbCircuits = new JTextField(circuitDirectory.nb_circuits + "");
			nbCircuits.setBounds(new Rectangle(296, 293, 35, 20));
			nbCircuits.setEditable(false);
		}
		return nbCircuits;
	}
	/**
	 * This method initializes circuitDirectory	
	 * 	
	 * @return supercars3.base.CircuitDirectory	
	 */
	private CircuitDirectory getCircuitDirectory()
	{
		if (circuitDirectory == null) {
			circuitDirectory = new CircuitDirectory();
		}
		return circuitDirectory;
	}
	/**
	 * This method initializes cpuAggressivity	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCpuAggressivity()
	{
		if (cpuAggressivity == null) 
		{
			
			int [] ali = getCircuitDirectory().aggressivity;
			String ags = new String();
			boolean first_field = true;
			
			for (int a : ali)
			{
				if (first_field)
				{
					ags += ""+a;
					first_field = false;
				}
				else
				{
					ags += " "+a;
				}
			}
				
			cpuAggressivity = new JTextField(ags);
			cpuAggressivity.setBounds(new Rectangle(161, 225, 250, 20));
		}
		return cpuAggressivity;
	}

	/**
	 * This method initializes cpuFrontWeapons	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCpuFrontWeapons()
	{
		if (cpuFrontWeapons == null) {
			cpuFrontWeapons = new JTextField(getCircuitDirectory().nb_front_weapons
					+ "");
			cpuFrontWeapons.setBounds(new Rectangle(153, 159, 42, 20));
		}
		return cpuFrontWeapons;
	}

	/**
	 * This method initializes cpuRearWeapons	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCpuRearWeapons()
	{
		if (cpuRearWeapons == null) {
			cpuRearWeapons = new JTextField(getCircuitDirectory().nb_rear_weapons
					+ "");
			cpuRearWeapons.setBounds(new Rectangle(156, 197, 42, 20));
		}
		return cpuRearWeapons;
	}

}  //  @jve:decl-index=0:visual-constraint="138,18"
