package supercars3.editor;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JButton;

import supercars3.base.*;

public class OpponentsEditor extends JDialog
{

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JComboBox frontWeaponComboBox = null;

	private JComboBox rearWeaponComboBox = null;

	private JLabel jLabel2 = null;

	private JLabel jLabel3 = null;

	private JSlider speedSlider = null;

	private JLabel jLabel4 = null;

	private JTextField trainSpeed = null;

	private JButton okButton = null;

	private Vector<IndexString> frontWeaponsList = null;  //  @jve:decl-index=0:
	private Vector<IndexString> rearWeaponsList = null;  //  @jve:decl-index=0:
	private Vector<IndexIndexString> trainWagonList = null;  //  @jve:decl-index=0:
	
	private OpponentProperties m_props = null;
	private CircuitData m_data = null;

	private JLabel jLabel41 = null;
	
	private JTextField cpuAggressivity = null;

	private JComboBox trainWagonsComboBox = null;

	private JLabel Frequency = null;

	private JSlider trainWaitSlider = null;

	private JTextField trainFrequency = null;

	private JLabel Frequency1 = null;

	/**
	 * @param owner
	 */
	public OpponentsEditor(Frame owner,CircuitData cd)
	{
		super(owner);
		if (cd != null)
		{
			m_props = cd.get_opponent_properties();
			m_data = cd;
		}
		initialize();
	}

	private class IndexString
	{
		IndexString(String s,Equipment.Item i)
		{
			this(s,i.ordinal());
		}
		
		IndexString(String s,int i)
		{
			index = i;
			text = s;
		}
		
		int index;
		String text;
	}	
	private class IndexIndexString
	{
		IndexIndexString(String s,int i, int j)
		{
			index1 = i;
			index2 = j;
			text = s;
		}
		
		int index1,index2;
		String text;
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		 frontWeaponsList = new Vector<IndexString>();
		 rearWeaponsList = new Vector<IndexString>();
		 trainWagonList = new Vector<IndexIndexString>();
		 
		 frontWeaponsList.add(new IndexString("None", Equipment.Item.NO_WEAPON));		 
		 frontWeaponsList.add(new IndexString("Front missile", Equipment.Item.FRONT_MISSILE));		 
		 frontWeaponsList.add(new IndexString("Super missile", Equipment.Item.SUPER_MISSILE));
		 frontWeaponsList.add(new IndexString("Homer missile", Equipment.Item.HOMER_MISSILE));
		 
		 rearWeaponsList.add(new IndexString("None", Equipment.Item.NO_WEAPON));		 
		 rearWeaponsList.add(new IndexString("Super missile", Equipment.Item.SUPER_MISSILE));
		 rearWeaponsList.add(new IndexString("Rear missile", Equipment.Item.REAR_MISSILE));
		 rearWeaponsList.add(new IndexString("Mine", Equipment.Item.MINE));
		 
		 trainWagonList.add(new IndexIndexString("0->2", 1,3));		 
		 trainWagonList.add(new IndexIndexString("0->3", 1,4));		 
		 trainWagonList.add(new IndexIndexString("1->3",2,4));
		 
		 this.setSize(400, 450 );
		this.setTitle("Opponents Editor");
		this.setContentPane(getJContentPane());

		if (m_props != null)
		{
			getSpeedSlider().setValue(speed_value_to_slider(m_props.train_engine));

			boolean found = false;
			
			for (int i = 0; i < rearWeaponsList.size() && !found;i++)
			{
				if (rearWeaponsList.elementAt(i).index == m_props.car_rear_weapon)
				{
					getRearWeaponComboBox().setSelectedIndex(i);
					found = true;
				}
			}
			
			found = false;
			
			for (int i = 0; i < frontWeaponsList.size() && !found;i++)
			{
				if (frontWeaponsList.elementAt(i).index == m_props.car_front_weapon)
				{
					getFrontWeaponComboBox().setSelectedIndex(i);
					found = true;
				}
			}
			found = false;
			
			for (int i = 0; i < trainWagonList.size() && !found;i++)
			{
				if ((trainWagonList.elementAt(i).index1 == m_props.min_wagons) &&
						(trainWagonList.elementAt(i).index2 == m_props.max_wagons))
				{
					getTrainWagonsComboBox().setSelectedIndex(i);
					found = true;
				}
			}
		}

		 
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
	{
		if (jContentPane == null) {
			Frequency1 = new JLabel();
			Frequency1.setBounds(new Rectangle(19, 299, 97, 16));
			Frequency1.setText("Offset (seconds)");
			Frequency = new JLabel();
			Frequency.setBounds(new Rectangle(19, 257, 92, 16));
			Frequency.setText("Wait (seconds)");
			jLabel41 = new JLabel();
			jLabel41.setBounds(new Rectangle(18, 341, 63, 16));
			jLabel41.setText("Wagons");
			jLabel4 = new JLabel();
			jLabel4.setBounds(new Rectangle(18, 200, 45, 27));
			jLabel4.setText("Speed");
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(165, 161, 49, 24));
			jLabel3.setText("Trains");
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(160, 15, 93, 16));
			jLabel2.setText("Computer cars");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(14, 83, 91, 26));
			jLabel1.setText("Rear weapon");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(15, 47, 87, 26));
			jLabel.setText("Front weapon");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getFrontWeaponComboBox(), null);
			jContentPane.add(getRearWeaponComboBox(), null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(jLabel3, null);
			jContentPane.add(getSpeedSlider(), null);
			jContentPane.add(jLabel4, null);
			jContentPane.add(getTrainSpeed(), null);
			jContentPane.add(getOkButton(), null);
			jContentPane.add(jLabel41, null);
			jContentPane.add(getTrainWagonsComboBox(), null);
			jContentPane.add(Frequency, null);
			jContentPane.add(getTrainWaitSlider(), null);
			jContentPane.add(getTrainWait(), null);
			//jContentPane.add(Frequency1, null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes frontWeaponComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getFrontWeaponComboBox()
	{
		if (frontWeaponComboBox == null) {
			frontWeaponComboBox = new JComboBox();
			frontWeaponComboBox.setBounds(new Rectangle(127, 49, 224, 22));
			
			for (IndexString is : frontWeaponsList)
			{
				frontWeaponComboBox.addItem(is.text);
			}
		}
		return frontWeaponComboBox;
	}

	/**
	 * This method initializes rearWeaponComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getRearWeaponComboBox()
	{
		if (rearWeaponComboBox == null) 
		{
			rearWeaponComboBox = new JComboBox();
			rearWeaponComboBox.setBounds(new Rectangle(126, 84, 224, 22));
			for (IndexString is : rearWeaponsList)
			{
				rearWeaponComboBox.addItem(is.text);
			}		
		}
		return rearWeaponComboBox;
	}

	private double speed_slider_to_value(int sv)
	{
		return (int)(((sv / 10.0) - 1)*10) / 10.0; 
	}
	
	private int speed_value_to_slider(double sv)
	{
		return (int)((sv+1) * 10); 
	}
	
	/**
	 * This method initializes speedSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSpeedSlider()
	{
		if (speedSlider == null) {
			speedSlider = new JSlider();
			speedSlider.setBounds(new Rectangle(70, 201, 226, 25));
			speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e)
				{
					getTrainSpeed().setText(speed_slider_to_value(speedSlider.getValue()) + "");
				}
				
			});
			speedSlider.setMinimum(0);
			speedSlider.setMaximum(40);
		}
		return speedSlider;
	}

	/**
	 * This method initializes trainSpeed	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTrainSpeed()
	{
		if (trainSpeed == null) {
			trainSpeed = new JTextField();
			trainSpeed.setBounds(new Rectangle(308, 202, 43, 25));
			trainSpeed.setEditable(false);
		}
		return trainSpeed;
	}

	private JDialog get_dialog()
	{
		return this;
	}
	
	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton()
	{
		if (okButton == null) {
			okButton = new JButton();
			okButton.setBounds(new Rectangle(154, 382, 77, 29));
			okButton.setText("OK");
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (m_props != null)
					{
						m_props.train_engine = speed_slider_to_value(getSpeedSlider().getValue());
						
						m_props.car_rear_weapon = rearWeaponsList.elementAt
						(getRearWeaponComboBox().getSelectedIndex()).index;
						m_props.car_front_weapon = frontWeaponsList.elementAt
						(getFrontWeaponComboBox().getSelectedIndex()).index;
						m_props.min_wagons = trainWagonList.elementAt(getTrainWagonsComboBox().getSelectedIndex()).index1;
						m_props.max_wagons = trainWagonList.elementAt(getTrainWagonsComboBox().getSelectedIndex()).index2;
						m_props.train_wait = getTrainWaitSlider().getValue();
						
						m_data.set_modified();
					}
					get_dialog().dispose();
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes trainWagonsComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getTrainWagonsComboBox()
	{
		if (trainWagonsComboBox == null) {
			trainWagonsComboBox = new JComboBox();
			trainWagonsComboBox.setBounds(new Rectangle(135, 337, 216, 25));
			for (IndexIndexString is : trainWagonList)
			{
				trainWagonsComboBox.addItem(is.text);
			}		
			}
		return trainWagonsComboBox;
	}

	/**
	 * This method initializes trainFreqSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getTrainWaitSlider()
	{
		if (trainWaitSlider == null) {
			trainWaitSlider = new JSlider();
			trainWaitSlider.setBounds(new Rectangle(120, 258, 172, 23));
			trainWaitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e)
				{
					getTrainWait().setText(trainWaitSlider.getValue() / 1000.0 + "");
				}
			});
			
			trainWaitSlider.setMinimum(0);
			trainWaitSlider.setMaximum(5000);
			int tw = m_props.train_wait;
			trainWaitSlider.setValue(tw);
		}
		return trainWaitSlider;
	}

	/**
	 * This method initializes trainFrequency	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTrainWait()
	{
		if (trainFrequency == null) {
			trainFrequency = new JTextField();
			trainFrequency.setBounds(new Rectangle(300, 257, 66, 20));
			trainFrequency.setEditable(false);
		}
		return trainFrequency;
	}

}
