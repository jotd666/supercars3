package supercars3.editor;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import supercars3.base.*;
import supercars3.sys.ParameterParser;

import java.io.*;


/**
 * <p>
 * Titre :
 * </p>
 * <p>
 * Description :
 * </p>
 * <p>
 * Copyright : Copyright (c) 2005
 * </p>
 * <p>
 * Société :
 * </p>
 * 
 * @author non attribuable
 * @version 1.0
 */

public class FrmEditor extends JFrame implements Observer
{
	enum MenuActionType { ADDCONTROLPOINT ,MOVECONTROLPOINT ,MOVECONTROLPOINT_VERTICAL,
		MOVECONTROLPOINT_HORIZONTAL ,ADDREGION ,EDITREGION ,CREATEROUTE ,TOGGLE_LAKE,
		DEFINECARSTARTINGPOINTS ,DEFINECARSTARTINGPOINT ,ADDPOINTTOROUTE,
		DEFINERESUMEPOINT ,CHECKCONSISTENCY, SAVEPROJECT ,
		SELECTOBJECT , DELETESELECTION ,LOADPROJECT, CLEARCARSTART, DELETECARSTART,
		SPLIT_REGION_HORIZONAL, SPLIT_REGION_VERTICAL}


	private JMenuBar jMenuBar1 = new JMenuBar();

	private JMenu jMenuFile = new JMenu();
	private JMenu jMenuView = new JMenu();
	private JMenu jMenuFileRecent = new JMenu();
	private JMenu jMenuFileCircuitDirectory = new JMenu();

	private JMenuItem jMenuFileExit = new JMenuItem();

	private JMenuItem jMenuFileNew = new JMenuItem();
	private JMenuItem jMenuFileGenShadows = new JMenuItem();
	private JMenuItem jMenuFileAuditDir = new JMenuItem();
	private JMenuItem jMenuFileEditDir = new JMenuItem();
	private JMenuItem jMenuFileUpdateDir = new JMenuItem();

	private JMenu jMenuViewZones = new JMenu();
	private JMenuItem jMenuViewZoneSegments = new JCheckBoxMenuItem();
	private JMenuItem jMenuViewZonePoints = new JCheckBoxMenuItem();
	private JMenu jMenuViewRoutes = new JMenu();

	private JToolBar m_tool_bar = new JToolBar();

	private JButton m_edit_region_button;
	private JButton m_split_region_h_button;
	private JButton m_split_region_v_button;

	private JButton m_set_resume_point_button;

	private JLabel m_status_bar = new JLabel();

	private CircuitData m_data;

	private ScrolledCanvas m_scrolled_window;

	private JMenuItem jMenuFileSave = new JMenuItem();

	private JMenuItem jMenuFileLoad = new JMenuItem();

	private JMenu jMenuEdit = new JMenu();

	private JMenuItem jMenuEditDescription = new JMenuItem();
	private JMenuItem jMenuEditOpponents = new JMenuItem();
	private JMenuItem jMenuEditClearCarStart = new JMenuItem();
	private JMenuItem jMenuEditDeleteCarStart = new JMenuItem();

	private JMenuItem jMenuHelpAbout = new JMenuItem();

	private JMenu jMenuHelp = new JMenu();

	private Vector<String> m_recent_files = new Vector<String>();
	
	private static final int MAX_RECENT_FILES = 4;
	
	public void update(Observable obs, Object x)
	{
		Circuit.Message msg = (Circuit.Message) obs;

		switch (msg.get_code())
		{
		case Circuit.ROUTE_ADDED:
			m_set_resume_point_button.setEnabled(true);
			set_status("Route added");
			add_route_sub_menu((Route)x);
			break;
		case Circuit.POINT_REMOVED:
			m_set_resume_point_button
					.setEnabled(m_data.get_route_list().size() != 0);
			set_status("Point removed");
			break;
		case Circuit.REGION_SELECT_TOGGLE:
			set_region_context_enabled(m_scrolled_window.m_circuit
					.get_selected_zone() != null);
			set_add_region_status();
			break;
		}

	}

	// Construire le cadre
	public FrmEditor()
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		m_data = new CircuitData(null);
		m_scrolled_window = new ScrolledCanvas(m_data, this);

		try {
			init_widgets();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public FrmEditor(String circuit_file)
	{
		this();
		load_project(DirectoryBase.get_circuit_path() + circuit_file);
	}

	private void set_status(String status)
	{
		m_status_bar.setText(status);
	}

	private ImageIcon load_icon(String name)
	{
		String image_path = DirectoryBase.get_root() + File.separator + "icons"
				+ File.separator + name;
		return new ImageIcon(image_path);

		// return new ImageIcon(FrmEditor.class.getResource(name));
	}

	private JButton insert_toolbar_button(String file, String tooltip, ActionListener a)
	{
		JButton button = new JButton();
		button.setIcon(load_icon(file));
		button.setToolTipText(tooltip);
		button.addActionListener(a);
		m_tool_bar.add(button);
		return button;
	}
	private JButton insert_toolbar_button(String file, String tooltip, MenuActionType type)
	{
		JButton button = new JButton();
		button.setIcon(load_icon(file));
		button.setToolTipText(tooltip);
		button.addActionListener(new ToolbarActionAdapter(this,type));
		m_tool_bar.add(button);
		return button;
	}

	private void load_recent_files()
	{
		String recent_file = DirectoryBase.get_user_path() + ".sc3_recent";
	
		try
		{
			ParameterParser p = ParameterParser.open(recent_file);
			p.startBlockVerify("SC3_RECENT_FILES");
			
			int nb_recent = p.readInteger("nb_recent");
			
			for (int i = 0; i < nb_recent; i++)
			{
				m_recent_files.add(p.readString("file"));
			}
			
			p.endBlockVerify();
		}
		catch(IOException e)
		{
			
		}
	}
	private void save_recent_files()
	{
		String recent_file = DirectoryBase.get_user_path() + ".sc3_recent";
	
		try
		{
			ParameterParser p = ParameterParser.create(recent_file);
			p.startBlockWrite("SC3_RECENT_FILES");
			
			p.write("nb_recent",m_recent_files.size());
			
			for (String s : m_recent_files)
			{
				p.write("file",s);
			}
			
			p.endBlockWrite();
			p.close();
		}
		catch(IOException e)
		{
			
		}
	}
	
	
	
	private class ShowSegmentsActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			m_scrolled_window.m_circuit.toggle_zones_segments_displayed();
		}
	}
	private class ShowPointsActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			m_scrolled_window.m_circuit.toggle_zones_points_displayed();
		}
	}
	
	private class RouteActionListener implements ActionListener
	{
		int route_number;
	
		public RouteActionListener(int rn) 
		{
			route_number = rn;
		}
		
		public void actionPerformed(ActionEvent evt) 
		{
			m_scrolled_window.m_circuit.toggle_displayed_route(route_number);
		}
	}
	private class RecentActionListener implements ActionListener
	{
		String project_file;
	
		public RecentActionListener(String s) {
			project_file = s;
		
		}
		
		public void actionPerformed(ActionEvent evt) 
		{
			load_project_with_modify_check(project_file);
		}
	}
	
	private void set_region_context_enabled(boolean s)
	{
		m_edit_region_button.setEnabled(s);
		m_split_region_h_button.setEnabled(s);
		m_split_region_v_button.setEnabled(s);
	}

	private void init_widgets() throws Exception
	{
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());

		ToolbarActionAdapter load_project_action_adapter = new ToolbarActionAdapter(this,MenuActionType.LOADPROJECT);
		ToolbarActionAdapter save_project_action_adapter = new ToolbarActionAdapter(this,MenuActionType.SAVEPROJECT);
		
		insert_toolbar_button("new.png", "New",new FrmEditor_jMenuFileNew_actionAdapter());
		insert_toolbar_button("open.png", "Open",load_project_action_adapter);
		insert_toolbar_button("save.png", "Save",save_project_action_adapter);
		insert_toolbar_button("select.png", "Select object",MenuActionType.SELECTOBJECT);
		insert_toolbar_button("move.png", "Move control point",MenuActionType.MOVECONTROLPOINT);
		insert_toolbar_button("move_horiz.png", "Move control point horizontally",MenuActionType.MOVECONTROLPOINT_HORIZONTAL);
		insert_toolbar_button("move_vert.png", "Move control point vertically",MenuActionType.MOVECONTROLPOINT_VERTICAL);
		insert_toolbar_button("delete.png", "Delete selection",MenuActionType.DELETESELECTION);
		insert_toolbar_button("yellowcircle.png", "New/kill control point",MenuActionType.ADDCONTROLPOINT);
		insert_toolbar_button("box.png", "New region",MenuActionType.ADDREGION);

		m_edit_region_button = insert_toolbar_button("editbook.png","Edit region",MenuActionType.EDITREGION);
		m_split_region_h_button = insert_toolbar_button("split_horizontal.png", "Split region horizontally",MenuActionType.SPLIT_REGION_HORIZONAL);
		m_split_region_v_button = insert_toolbar_button("split_vertical.png", "Split region vertically",MenuActionType.SPLIT_REGION_VERTICAL);

		insert_toolbar_button("thread.png", "Create route",MenuActionType.CREATEROUTE);
		insert_toolbar_button("lake.png", "Toggle lake seed point",MenuActionType.TOGGLE_LAKE);
		m_set_resume_point_button = insert_toolbar_button("goalflag.png","Define point as resume point",MenuActionType.DEFINERESUMEPOINT);
		m_set_resume_point_button.setEnabled(false);

		insert_toolbar_button("bluecircle.png","Add point to route",MenuActionType.ADDPOINTTOROUTE);
		insert_toolbar_button("car.png","Define point as car starting point on route",MenuActionType.DEFINECARSTARTINGPOINT);
		insert_toolbar_button("car_multi.png","Define n car starting points given 2 points on route",MenuActionType.DEFINECARSTARTINGPOINTS);
		insert_toolbar_button("check.png", "Check consistency",MenuActionType.CHECKCONSISTENCY);

		set_region_context_enabled(false);
		
		jMenuFileSave.addActionListener(save_project_action_adapter);
		jMenuFileSave.setText("Save");

		jMenuFileLoad.setText("Load");
		jMenuFileLoad.addActionListener(load_project_action_adapter);
		
		jMenuEdit.setText("Edit");
		jMenuEditDescription.setText("Description");
		jMenuEditDescription
				.addActionListener(new FrmEditor_jMenuEditDescription_actionAdapter(
						this));
		jMenuEditOpponents.setText("Opponents");
		jMenuEditOpponents
				.addActionListener(new FrmEditor_jMenuEditOpponents_actionAdapter(
						this));
		jMenuEditClearCarStart.setText("Undefine car start points");
		jMenuEditClearCarStart.addActionListener(new ToolbarActionAdapter(this,MenuActionType.CLEARCARSTART));
		
		jMenuEditDeleteCarStart.setText("Delete car start points");
		jMenuEditDeleteCarStart.addActionListener(new ToolbarActionAdapter(this,MenuActionType.DELETECARSTART));
				
		jMenuHelpAbout.setText("About");
		jMenuHelpAbout
				.addActionListener(new FrmEditor_jMenuHelpAbout_ActionAdapter(
						this));
		jMenuHelp.setText("Help");
		contentPane.add(m_tool_bar, BorderLayout.NORTH);
		contentPane.add(m_status_bar, BorderLayout.SOUTH);
		contentPane.add(m_scrolled_window);
		this.setSize(new Dimension(650, 450));
		contentPane.setSize(getSize());
		this.setTitle("Circuit Editor");
		jMenuFile.setText("File");
		jMenuFileExit.setText("Quit");
		jMenuFileExit
				.addActionListener(new FrmEditor_jMenuFileExit_ActionAdapter(
						this));
		
		jMenuFileNew.setText("New");
		jMenuFileNew
				.addActionListener(new FrmEditor_jMenuFileNew_actionAdapter(
						));
		jMenuFile.add(jMenuFileNew);
		jMenuFile.addSeparator();
		jMenuFileGenShadows.setText("Generate shadow image");
		jMenuFile.add(jMenuFileGenShadows);
		jMenuFileGenShadows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jMenuFileGenShadowsActionPerformed(evt);
			}
		});
		
		jMenuFileCircuitDirectory.setText("Circuit Directory");
		jMenuFileAuditDir.setText("Audit");
		jMenuFileEditDir.setText("Edit");
		jMenuFileUpdateDir.setText("Update");
		jMenuFileCircuitDirectory.add(jMenuFileAuditDir);
		jMenuFileAuditDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jMenuFileAuditDirActionPerformed(evt);
			}
		});
		jMenuFileCircuitDirectory.add(jMenuFileEditDir);
		jMenuFileEditDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jMenuFileEditDirActionPerformed(evt);
			}
		});
		jMenuFileCircuitDirectory.add(jMenuFileUpdateDir);
		jMenuFileUpdateDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jMenuFileUpdateDirActionPerformed(evt);
			}
		});
		
		jMenuFileRecent.setText("Recent");
		
		// load recent
		
		load_recent_files();
		
		update_recent_menu_item();
		
		
		jMenuView.setText("View");
		jMenuViewZoneSegments.setText("segments");
		jMenuViewZonePoints.setText("points");

		jMenuViewZonePoints.addActionListener(new ShowPointsActionListener());
		jMenuViewZoneSegments.addActionListener(new ShowSegmentsActionListener());
		
		jMenuViewZones.setText("Zone ...");
		jMenuViewZones.add(jMenuViewZonePoints);
		jMenuViewZones.add(jMenuViewZoneSegments);
		
		jMenuViewRoutes.setText("Routes");
		jMenuView.add(jMenuViewZones);
		jMenuView.add(jMenuViewRoutes);
		
		jMenuViewZones.setSelected(true);
		
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileLoad);
		jMenuFile.add(jMenuFileSave);
		jMenuFile.add(jMenuFileCircuitDirectory);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileRecent);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileExit);
		jMenuBar1.add(jMenuFile);
		jMenuBar1.add(jMenuView);
		jMenuBar1.add(jMenuEdit);
		jMenuBar1.add(jMenuHelp);
		jMenuEdit.add(jMenuEditDescription);
		jMenuEdit.add(jMenuEditOpponents);
		jMenuEdit.add(jMenuEditClearCarStart);
		jMenuEdit.add(jMenuEditDeleteCarStart);
		this.setJMenuBar(jMenuBar1);
		jMenuHelp.add(jMenuHelpAbout);

	}

	// Opération Fichier | Quitter effectuée
	public void jMenuFileExit_actionPerformed(ActionEvent e)
	{
		int option = 0;
		if (m_data.is_modified()) {
			option = JOptionPane.showConfirmDialog(this,
					"Circuit modified, really quit?", "Confirm",
					JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
		}
		if (option == 0) 
		{
			save_recent_files();
			System.exit(0);
		}
	}

	// Opération Aide | A propos effectuée
	public void jMenuHelpAbout_actionPerformed(ActionEvent e)
	{
		FrmEditor_AboutBox dlg = new FrmEditor_AboutBox(this);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(true);
		dlg.pack();
		dlg.setVisible(true);
	}

	// Supplanté, ainsi nous pouvons sortir quand la fenêtre est fermée
	protected void processWindowEvent(WindowEvent e)
	{
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			jMenuFileExit_actionPerformed(null);
		} else {
			super.processWindowEvent(e);
		}
	}

	void jMenuFileNew_actionPerformed(ActionEvent e)
	{
		JFileChooser fc = new JFileChooser(DirectoryBase.get_circuit_path());
		CircuitImageFileFilter ff = new CircuitImageFileFilter();
		fc.setFileFilter(ff);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
		{		
			int option = 0;
			String image_file = fc.getSelectedFile().getAbsolutePath();
			
			if (m_data.is_modified())
			{
				option = JOptionPane.showConfirmDialog(this,
						"Current project has not been saved. New project anyway?", "Confirm",
						JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
			}
			if (option == 0)
			{
				   int idx = image_file.lastIndexOf('.');
				    String project_file = image_file.substring(0, idx) +
				        DirectoryBase.CIRCUIT_EXTENSION;
				    
				    if (new File(project_file).exists())
					{
						option = JOptionPane.showConfirmDialog(this,
								"Project file already exists, create new one?", "Confirm",
								JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
					}
			}
			if (option == 0)
			{	
				m_data.new_project(image_file);
				load_image();
			}
		}

	}

	private void load_image()
	{
		try {
			m_scrolled_window.m_circuit.load_main_image();
			set_status("Circuit image for \"" + m_data.get_project_file()
					+ "\" loaded");
		} catch (IOException ex) {
			set_status("Circuit image not loaded: " + ex.toString());
		}
	}

	void jMenuFileSave_actionPerformed(ActionEvent e)
	{
		save_project();
	}


	
	private void save_project()
	{
		String project_file = m_data.get_project_file();

		if (project_file == null) 
		{
			JFileChooser fc = new JFileChooser(DirectoryBase.get_circuit_path());
			fc.showSaveDialog(this);
			File f = fc.getSelectedFile();
			if (f != null)
			{
				project_file = f.getAbsolutePath();
			}
		}
		if (project_file != null) 
		{
			try {
				m_data.set_project_file(project_file);
				m_data.save();
				add_to_recent_files(project_file);
				set_status("Project \"" + project_file + "\" has been saved");
			} 
			catch (java.io.IOException e) 
			{
				set_status(e.toString());
			}
		}
	}

	
	private void load_project(String project_name)
	{
		boolean parsing_errors = false;
	
		try 
		{
			// load the vector data
			m_data.unchecked_load(project_name, false);

		} catch (Exception e) {
			parsing_errors = true;
		}
		try 
		{
			// load the image
			m_scrolled_window.m_circuit.load_main_image();
			boolean enable_points = m_data.get_route_list().size() != 0;
			m_set_resume_point_button.setEnabled(enable_points);
			String err = parsing_errors ? "with parsing errors" :
				"without errors";
			set_status("Project \"" + project_name + "\" has been loaded "+err);
			
			add_to_recent_files(project_name);
			update_view_menu_item();
		}
		catch (Exception e) 
		{
			set_status(e.toString());			
		}
	}
	private void add_to_recent_files(String s)
	{
		int found = -1;
		for (int i = 0; i < m_recent_files.size() && found == -1; i++)
		{
			if (m_recent_files.elementAt(i).equals(s))
			{
				found = i;
			}
		}
		
		if (found == -1)
		{
			if (m_recent_files.size() < MAX_RECENT_FILES)
			{
				m_recent_files.add(s);
			}
			else
			{
				// scroll down the other files
				for (int i = 1; i < MAX_RECENT_FILES; i++)
				{
					m_recent_files.set(MAX_RECENT_FILES-i,
							m_recent_files.elementAt(MAX_RECENT_FILES-i-1));
				}
				m_recent_files.set(0,s);
			}
		}
		else
		{
			if (found != 0)
			{
				// swap in history
				
				String swp = m_recent_files.elementAt(0);
				m_recent_files.set(0,m_recent_files.elementAt(found));
				m_recent_files.set(found,swp);
			}
		}
		
		update_recent_menu_item();
		
		
			
	}
	
	private void update_recent_menu_item()
	{
		jMenuFileRecent.removeAll();
		
		for (String s : m_recent_files)
		{
			JMenuItem item = new JMenuItem(s);
			item.addActionListener(new RecentActionListener(s));

			jMenuFileRecent.add(item);
		}
	}
	
	private void add_route_sub_menu(Route r)
	{
		JMenuItem item = new JCheckBoxMenuItem("Route "+r.get_name());
		item.setSelected(true);
		m_scrolled_window.m_circuit.m_data.toggle_filtered_route(r.get_name());
		item.addActionListener(new RouteActionListener(r.get_name()));

		jMenuViewRoutes.add(item);
	}
	
	private void update_view_menu_item()
	{
		jMenuViewRoutes.removeAll();
		
		jMenuViewZonePoints.setSelected(true);
		jMenuViewZoneSegments.setSelected(true);

		if (m_data != null)
		{
			m_scrolled_window.m_circuit.m_data.reset_display_filters();
			
			Collection<Route> routes = m_data.get_route_list();

			for (Route r : routes)
			{
				add_route_sub_menu(r);
			}
		}
	}
	
	private void load_project()
	{
		JFileChooser fc = new JFileChooser(DirectoryBase.get_circuit_path());
		Sc3FileFilter ff = new Sc3FileFilter();
		fc.setFileFilter(ff);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
		{			
			load_project_with_modify_check(fc.getSelectedFile().getAbsolutePath());
			
		}
	}
	
	private void load_project_with_modify_check(String f)
	{
		int option = 0;
		
		if (m_data.is_modified())
		{
			option = JOptionPane.showConfirmDialog(this,
					"Current project has not been saved. Load project anyway?", "Confirm",
					JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
		}
		if (option == 0)
		{
			load_project(f);
		}
	}
	

	private void add_control_point()
	{
		set_status("Left click adds a control point, Right click removes it"
				+ " & all associated objects");
		set_edition_mode(Circuit.ClickMode.ADD_CONTROL_POINT);
	}

	private void select_object()
	{
		set_status("Click on/inside object to select it");
		set_edition_mode(Circuit.ClickMode.SELECT_OBJECT);
	}

	private void delete_selection()
	{
		set_status("Selected objects deleted");
		m_scrolled_window.m_circuit.delete_selection();
	}

	private void move_control_point(boolean h, boolean v)
	{
		set_status("Select point to move");
		if (h && v)
		{
			set_edition_mode(Circuit.ClickMode.MOVE_CONTROL_POINT);
		}
		else if (v)
		{
			set_edition_mode(Circuit.ClickMode.MOVE_CONTROL_POINT_VERTICAL);
		}
		else
		{
			set_edition_mode(Circuit.ClickMode.MOVE_CONTROL_POINT_HORIZONTAL);
		}
			
	}
	

	private void audit_circuit_directory()
	{
		JFileChooser fc = new JFileChooser(DirectoryBase.get_circuit_path());
		fc.setDialogTitle("Select circuit directory to audit");
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
		{		
			String selected = fc.getSelectedFile().getName();
			Vector<String> v = new Vector<String>();
			CircuitDirectory cd = null;
			try
			{
				cd = CircuitDirectory.build_for_audit(selected);
			}
			catch (Exception e)
			{
				v.add("info.sc3 file of directory \""+selected+"\"is not valid:");
				v.add(e.getMessage());
			}
			
			if (cd != null)
			{
				v = cd.audit();
			}
			

			CheckReport cr = new CheckReport(this, "Check Report Window", v,
					true);
			cr.setSize(400, 400);
			cr.setLocationRelativeTo(this);
			cr.setVisible(true);
			
		}
	}
	private void update_circuit_directory(boolean force)
	{		
		JFileChooser fc = new JFileChooser(DirectoryBase.get_circuit_path());

		fc.setDialogTitle("Select circuit directory to update");

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
		{		
			Vector<String> messages = new Vector<String>();
			
			String selected = fc.getSelectedFile().getName();

			File candidate = new File(DirectoryBase.get_circuit_path() +
					selected);
			if (candidate.isDirectory())
			{
				String [] files = candidate.list();
				for (String f : files)
				{
					int idx = (f.lastIndexOf('.'));
					if ((idx > 0) && 
							(f.substring(idx,f.length()).equals(".sc3") && 
							!f.equals("info.sc3")))
					{
						CircuitData cd = new CircuitData(null);
						try
						{
							
							cd.unchecked_load(candidate.getAbsolutePath() + File.separator + f, false);
							if (force)
							{
								try
								{
									cd.save();
									messages.add(f + " was forced to update");
								}
								catch (IOException e2)
								{
									messages.add(f + " could not be written");
								}								
							}
						}
						catch (Exception e)
						{
							// save it with corrected syntax
							try
							{
								cd.save();
								messages.add(f + " was outdated, and successfully updated");
							}
							catch (IOException e2)
							{
								messages.add(f + " could not be written");
							}
						}
					}
				}
				CheckReport cr = new CheckReport(this, "Update Report Window", messages,
						true);
				cr.setSize(400, 400);
				cr.setLocationRelativeTo(this);
				cr.setVisible(true);

			}

			
		}
	}
	
	private void edit_circuit_directory()
	{
		JFileChooser fc = new JFileChooser(DirectoryBase.get_circuit_path());
		fc.setDialogTitle("Select circuit directory to edit");
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
		{		
			String selected = fc.getSelectedFile().getName();

			CircuitDirectory cd = null;
			try
			{
				cd = CircuitDirectory.build_for_audit(selected);
			}
			catch (Exception e)
			{
				cd = new CircuitDirectory();
				cd.directory = selected;
			}
			
			FrmCircuitDirectoryEditor cr = new FrmCircuitDirectoryEditor(this,cd);
			//cr.setSize(400, 400);
			cr.setLocationRelativeTo(this);
			cr.setVisible(true);
			
		}
	}
	
	private void generate_shadows()
	{
		File image_file = m_scrolled_window.m_circuit.m_data.get_shadow_image_file();
		int option = 0;
		
		if (image_file.exists())
		{
			option = JOptionPane.showConfirmDialog(this,
					"Shadow image already exists. Overwrite ?", "Confirm",
					JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
		}
		if (option == 0)
		{
			Cursor old_cursor = getCursor();
			try
			{
				this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				m_scrolled_window.m_circuit.generate_shadows();
				set_status("Shadow image file "+image_file.toString()+" written");
			}
			catch (IOException e)
			{
				set_status("Cannot write image: "+e.getMessage());
			}
			setCursor(old_cursor);
		}
	}

	private void set_edition_mode(Circuit.ClickMode m)
	{
		m_scrolled_window.m_circuit.set_mode(m);
	}
	private void set_add_region_status()
	{
		if (m_edit_region_button.isEnabled()) {
			set_status("Edit current region or select 4 points to define a new one");
		} else {
			set_status("Select 4 points for the region");
		}
	}

	private void add_region()
	{
		set_add_region_status();
		set_edition_mode(Circuit.ClickMode.ADD_REGION);

	}

	private void define_resume_point()
	{
		set_status("Click on route control points to toggle resume state on points");
		set_edition_mode(Circuit.ClickMode.DEFINE_RESUME_POINT);
	}

	private void define_car_start_point()
	{
		set_status("Click on a point to define starting position of cars");
		set_edition_mode(Circuit.ClickMode.DEFINE_CAR_START_POINT);
	}
	
	private void delete_car_start_points(boolean remove_points)
	{
		m_data.clear_car_start_points(remove_points);
		repaint();
	}
	
	private void define_multi_car_start_point()
	{
		set_status("Select first car and last car points on a route");
		set_edition_mode(Circuit.ClickMode.DEFINE_MULTI_CAR_START_POINT);
	}



	private void add_route()
	{
		set_status("Link control points to create a new route");
		set_edition_mode(Circuit.ClickMode.ADD_ROUTE);
	}

	private void add_lake()
	{
		set_status("Click on control points to toggle lake seed state");
		set_edition_mode(Circuit.ClickMode.DEFINE_LAKE_SEED_P0INT);
	}

	private void check_consistency()
	{
		Vector<String> v = m_data.report_errors();
		if (!v.isEmpty()) {
			CheckReport cr = new CheckReport(this, "Check Report Window", v,
					true);
			cr.setSize(400, 400);
			cr.setLocationRelativeTo(this);
			cr.setVisible(true);
		} else {
			set_status("No errors found");
		}
	}
	
	private void split_region(boolean vertical)
	{
		Zone z = m_scrolled_window.m_circuit.get_selected_zone();
		
		if (!m_data.split_zone(z,vertical))
		{
			set_status("Zone " + z.get_name() + " is not rectangular enough");
		}
		else
		{
			set_status("Zone " + z.get_name() + " has been splitted");
		}
		set_region_context_enabled(false);
		repaint();
	}
	
	private void add_point_to_route()
	{
		set_status("Click close to a route to insert a point");
		set_edition_mode(Circuit.ClickMode.ADD_POINT_TO_ROUTE);
	}
	private void edit_region()
	{
		Zone z = m_scrolled_window.m_circuit.get_selected_zone();

		RegionEditor re = new RegionEditor(this, true, z);
		re.setSize(400, 380);
		re.setLocationRelativeTo(this);
		re.setVisible(true);
	}

	 void saveProject_actionPerformed(ActionEvent e)
	{
		save_project();
	}




	void loadProject_actionPerformed(ActionEvent e)
	{
		load_project();
	}


	void jMenuFileLoad_actionPerformed(ActionEvent e)
	{
		load_project();
	}
	

	
	void jMenuEditDescription_actionPerformed(ActionEvent e)
	{
		String answer = JOptionPane.showInputDialog("Number of laps", 
				 new Integer(m_data.get_nb_laps()));
		 
		 if (answer != null)
		 {
			 int nb_laps = 0;			 
			 
			 try 
			 {
				 nb_laps = Integer.parseInt(answer);
			 } 
			 catch (NumberFormatException e1) 
			 {
				
			 }
			 
			 if (nb_laps >= 1)
			 {
				
				 m_data.set_nb_laps(nb_laps);				 
			 }
		 }		
			answer = JOptionPane.showInputDialog("Missed Checkpoint Tolerance", 
					 new Integer(m_data.get_missed_checkpoint_tolerance()));
			 
			 if (answer != null)
			 {
				 int mct = 0;			 
				 
				 try 
				 {
					 mct = Integer.parseInt(answer);
				 } 
				 catch (NumberFormatException e1) 
				 {
					
				 }
				 
				 if (mct >= 0)
				 {
					
					 m_data.set_missed_checkpoint_tolerance(mct);				 
				 }
			 }				 
	}
	void jMenuEditOpponents_actionPerformed(ActionEvent e)
	{
		OpponentsEditor oe = new OpponentsEditor(this,m_data);
		oe.setLocationRelativeTo(this);
		oe.setVisible(true);
	}
	
	private void jMenuFileGenShadowsActionPerformed(ActionEvent evt) 
	{
		generate_shadows();
	}

	private void jMenuFileAuditDirActionPerformed(ActionEvent evt) 
	{
		audit_circuit_directory();
	}
	private void jMenuFileEditDirActionPerformed(ActionEvent evt) 
	{
		edit_circuit_directory();
	}
	private void jMenuFileUpdateDirActionPerformed(ActionEvent evt) 
	{
		update_circuit_directory(true);
	}

	private class FrmEditor_jMenuFileNew_actionAdapter implements
	java.awt.event.ActionListener
	{
		
		
		public void actionPerformed(ActionEvent e)
		{
			jMenuFileNew_actionPerformed(e);
		}
	}
	void toolbar_action(MenuActionType action)
	{
		switch(action)
		{
		case ADDCONTROLPOINT:
			add_control_point();
			break;
		case EDITREGION:
			edit_region();
			break;
		case MOVECONTROLPOINT:
			move_control_point(true,true);
			break;
		case MOVECONTROLPOINT_HORIZONTAL:
			move_control_point(true, false);
			break;
		case MOVECONTROLPOINT_VERTICAL:
			move_control_point(false,true);
			break;
		case ADDPOINTTOROUTE:
			add_point_to_route();
			break;
		case CREATEROUTE:
			add_route();
			break;
		case TOGGLE_LAKE:
			add_lake();
			break;
		case ADDREGION:
			add_region();
			break;
		case DEFINECARSTARTINGPOINTS:
			define_multi_car_start_point();
			break;
		case DEFINECARSTARTINGPOINT:
			define_car_start_point();
			break;
		case DELETECARSTART:
			delete_car_start_points(true);
			break;
		case CLEARCARSTART:
			delete_car_start_points(false);
			break;
		case DEFINERESUMEPOINT:
			define_resume_point();
			break;
		case CHECKCONSISTENCY:
			check_consistency();
			break;
		case SELECTOBJECT:
			select_object();
			break;
		case DELETESELECTION:
			delete_selection();
			break;
		case SAVEPROJECT:
			save_project();
			break;
		case LOADPROJECT:
			load_project();
			break;
		case SPLIT_REGION_VERTICAL:
			split_region(true);
			break;
		case SPLIT_REGION_HORIZONAL:
			split_region(false);
			break;
			default:
				break;
		}
	}
}

class FrmEditor_jMenuFileExit_ActionAdapter implements ActionListener
{
	FrmEditor adaptee;

	FrmEditor_jMenuFileExit_ActionAdapter(FrmEditor adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.jMenuFileExit_actionPerformed(e);
	}
}







class FrmEditor_saveProject_actionAdapter implements java.awt.event.ActionListener
{
	FrmEditor adaptee;

	FrmEditor_saveProject_actionAdapter(FrmEditor adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.saveProject_actionPerformed(e);
	}
}


class ToolbarActionAdapter implements
		java.awt.event.ActionListener
{
	private supercars3.editor.FrmEditor.MenuActionType action;
	private FrmEditor parent;
	
	ToolbarActionAdapter(FrmEditor parent, supercars3.editor.FrmEditor.MenuActionType action)
	{
		this.action = action;
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e)
	{
		parent.toolbar_action(action);
	}
}


class FrmEditor_jMenuEditDescription_actionAdapter implements java.awt.event.ActionListener
{
	FrmEditor adaptee;

	FrmEditor_jMenuEditDescription_actionAdapter(FrmEditor adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.jMenuEditDescription_actionPerformed(e);
	}
}
class FrmEditor_jMenuEditOpponents_actionAdapter implements java.awt.event.ActionListener
{
	FrmEditor adaptee;

	FrmEditor_jMenuEditOpponents_actionAdapter(FrmEditor adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.jMenuEditOpponents_actionPerformed(e);
	}
}

class FrmEditor_jMenuHelpAbout_ActionAdapter implements ActionListener
{
	FrmEditor adaptee;

	FrmEditor_jMenuHelpAbout_ActionAdapter(FrmEditor adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.jMenuHelpAbout_actionPerformed(e);

	}

}
