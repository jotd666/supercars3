package supercars3.game;

import supercars3.sys.*;
import supercars3.base.*;
import supercars3.game.cars.CarSet;
import supercars3.game.gates.GateSet;
import supercars3.game.trains.TrainSet;
import supercars3.game.screens.OptionsScreenMain;
import supercars3.game.screens.RaceInfoScreen;
import supercars3.game.screens.ResultScreen;
import supercars3.game.screens.TitleScreen2;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

import java.awt.event.KeyEvent;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class MainGame extends GameState
{
  private OptionsScreenMain m_options;

  private BufferedImage m_main_image;
  private BufferedImage m_shadow_image = null;
  private CircuitData m_data;
  private CircuitChecker m_circuit_checker;
  private CircuitView [] m_circuit_view;
  private CarSet m_car_set;
  private GateSet m_gate_set;
  private TrainSet m_train_set;
  private LakeImageSet m_lake_image_set = new LakeImageSet();
  
  private boolean m_options_initialized = false;
  private boolean m_game_over_flag = false;
  private RaceInfoScreen m_info_screen;
  private boolean m_game_first_render = true;
  private long m_winning_time = Long.MAX_VALUE;
  private long m_start_time;
  private CircuitView m_snapshot_view;
  private GameOptions m_game_options = GameOptions.instance();
  private boolean m_demo_mode = false;
  
  public OptionsScreenMain get_options()
  {
	  return m_options;
  }
  

  private static final int STATE_START = 0;
  private static final int STATE_ATTENTION = 1;
  private static final int STATE_SET = 4;
  private static final int STATE_GO = 5;
  
  private int m_state;
  private boolean m_paused;
  private SfxSet m_sfx_set;
  
  public MainGame(OptionsScreenMain opts, boolean demo_mode)
  {
	  m_sfx_set = opts.get_sfx_set();
	  
    m_car_set = opts.get_car_set(); // shortcut
    m_gate_set = new GateSet();
    m_train_set = new TrainSet();
    m_options = opts;
    m_options_initialized = true;
    m_demo_mode = demo_mode;
     
    if (demo_mode)
    {
    	set_maximum_duration(15000);
    	set_fadeinout_time(500, 1000);    	
    }
  }

  /**
   * starts game directly with default options (debug)
   */
  public MainGame() throws Exception
  {
    this(new OptionsScreenMain(),false);
    m_options_initialized = false;
  }

  private BufferedImage apply_shadow(BufferedImage circuit)
  {
	  File shadow_image_file = m_data.get_shadow_image_file();
	  BufferedImage shadow = null;
	  
	  try
	  {
		  BufferedImage shadow_image = ImageIO.read(shadow_image_file);
		  int image_width = shadow_image.getWidth();
		  int image_height = shadow_image.getHeight();
		  shadow = new BufferedImage(image_width, image_height,
				  BufferedImage.TYPE_INT_ARGB);
		  
		  shadow.getGraphics().drawImage(shadow_image, 0, 0, null);
	  
		  for (int x = 0; x < image_width; x++) 
		  {
			  for (int y = 0; y < image_height; y++) 
			  {
				  int rgb = shadow.getRGB(x, y);
				  //int red = (rgb & 0xFF0000) >> 16;
				  //int green = (rgb & 0x00FF00) >> 8;
				  int blue = rgb & 0x0000FF;
				  
				  if (blue != 0)
				  {
					  rgb = circuit.getRGB(x,y);
					  circuit.setRGB(x,y,rgb * 2);
					  shadow.setRGB(x,y,0x80000000);
				  }
				  else
				  {
					  shadow.setRGB(x,y,0x00000000);
				  }
			  }
		  }
	  }
	  catch (IOException e)
	  {
		  
	  }
  	  
	  return shadow;
  }
 
  public void end_init() 
  {
    try 
    {
    	// read from ImageIO rather than from GTGE because we don't want it cached
    	
    	BufferedImage circuit = ImageIO.read(new File(m_data.get_main_image_file()));
      
	  int image_width = circuit.getWidth();
	  int image_height = circuit.getHeight();
	  
	  BufferedImage circuit_rgb = new BufferedImage(image_width, image_height,
			  BufferedImage.TYPE_INT_RGB);
	  circuit_rgb.getGraphics().drawImage(circuit, 0, 0, null);

	  
	  BufferedImage shadow_image = apply_shadow(circuit_rgb);

	  ImageScale2x circuit_scaler = new ImageScale2x(circuit_rgb);
	  m_main_image = circuit_scaler.getScaledImage();
	  
	 /* m_main_image = new BufferedImage((int) (image_width * m_circuit_scale),
              (int) (image_height * m_circuit_scale),
              BufferedImage.TYPE_INT_RGB);
	  
	  AffineTransform tx = AffineTransform.getScaleInstance(m_circuit_scale,
			  m_circuit_scale);
	  AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
	  op.filter(circuit_rgb, m_main_image);*/

	  m_main_image.flush();

	  if (shadow_image != null)
	  {
		  ImageScale2x shadow_scaler = new ImageScale2x(shadow_image);
		  // a shadow exists for this image
		  m_shadow_image = shadow_scaler.getScaledImage();
		 
		  m_shadow_image.flush();
		  

	  }
	  circuit = null;
	  shadow_image = null;
	  circuit_scaler = null;
	  m_circuit_view = null;
	  m_snapshot_view = null;
	  
	  // force garbage collection
	  
	  System.gc();
	  
	  
	  /*Runtime runtime = Runtime.getRuntime();
	  System.out.println("Total memory : "+runtime.totalMemory()+"\tfree : "+runtime.freeMemory());*/
	   
      // ensure that all route control points match one and only one zone
 
      m_data.resolve_zone_ambiguities();
 
      // MAIN INIT ROUTINE
      
      // initialise all cars (zones, points, nb laps...)
      
      m_car_set.race_start_pass_1(m_data);

      
      if (m_options.get_player_cars() != null)
      {
    	  int nb_players = m_options.get_player_cars().length;
    	  boolean dual_play = nb_players > 1;

    	  m_circuit_view = new CircuitView[nb_players];
    	  for (int i = 0; i < nb_players; i++)
    	  {
    		  m_circuit_view[i] = new CircuitView
    		  (m_car_set,m_gate_set,m_train_set,m_lake_image_set,m_options.get_player_cars()[i],
    				  m_game.bsInput,m_main_image,m_shadow_image,
    				  m_data.get_top_priority_zones(),
    				  (getWidth()*i)/nb_players+1,0,
    				  (getWidth()/nb_players)-2,getHeight(),dual_play);
    	  }
      }
      else
      {
    	  // computer only (debug mode): centered on first computer car
    	  
    	  m_circuit_view = new CircuitView[1];

    	  m_circuit_view[0] = new CircuitView
    	  (m_car_set,m_gate_set,m_train_set,m_lake_image_set,m_options.get_car_set().get_item(0),
    			  m_game.bsInput,m_main_image,m_shadow_image,
    			  m_data.get_top_priority_zones(),0,0,
    			  (getWidth())-2,getHeight(),false);

   	  
      }
      
      m_snapshot_view = new CircuitView
	  (m_car_set,m_gate_set,m_train_set,m_lake_image_set,m_options.get_car_set().get_item(0),
			  m_game.bsInput,m_main_image,m_shadow_image,
			  m_data.get_top_priority_zones(),0,0,
			  m_main_image.getWidth(),m_main_image.getHeight(),false);
      
      m_circuit_checker = new CircuitChecker(m_data,m_car_set,m_gate_set,m_train_set);
      
      m_car_set.race_start_pass_2(m_circuit_checker,m_data.get_opponent_properties());
       
      //m_data.link_zones_and_points();
      
      m_gate_set.init(m_data,bsLoader);
      
      m_train_set.init(m_data, bsLoader, m_circuit_checker);
      
      m_lake_image_set.init(m_data,m_main_image);
      
      m_game_over_flag = false;
    }
    
    // if a programming error occurs during the init phase,
    // it is intercepted here
    
    catch (Exception e) {
    	e.printStackTrace();
      String msg = e.getMessage();
      if (msg == null)
      {
        msg = "unknown exception";
      }
      show_error(msg);
    }
    
    if (!m_demo_mode)
    {
    	m_game.stop_music();
    }

  }
 private void create_circuit_snapshot()
 {
	 BufferedImage snapshot_image;

	 Rectangle ib = m_snapshot_view.get_image_bounds();
	 snapshot_image = new BufferedImage(ib.width, ib.height, BufferedImage.TYPE_INT_RGB);
	 m_snapshot_view.render((Graphics2D)snapshot_image.getGraphics());

	 save_snapshot(snapshot_image, "full");
 }
 protected void p_update()
 {
	 if (m_info_screen != null)
	 {
		 m_info_screen.update(get_elapsed_time());
		 if (m_info_screen.is_escaped())
		 {
			 fadeout();
		 }
		 if (is_fadeout_done())
		 {
			 m_game.stop_music();
			 end_sounds();
			 set_next(new TitleScreen2());
		 }
	 }
	 else
	 {
		 if (m_game.bsInput.isKeyPressed(KeyEvent.VK_F2))
		 {
			 create_circuit_snapshot();
		 }
		 if (m_state == STATE_START)
		 {
			 m_state = STATE_ATTENTION;
			 m_game.load_random_music();
		 }
		 if ((m_state <= STATE_SET) && (!m_paused))
		 {
			 long limit = m_state * 1000;
			 if (get_state_elapsed_time() > limit)
			 {
				 if (m_state == STATE_SET)
				 {
					 // play "shoot" sound
					 m_sfx_set.play(SfxSet.Sound.horn_high_long);
				 }
				 else
				 {
					 // play "horn" sound
					 m_sfx_set.play(SfxSet.Sound.horn_low);
				 }
				 // state transition
				 m_state++;    	
				 if (m_state == STATE_GO)
				 {
					 m_start_time = get_state_elapsed_time();
				 }
			 }
		 }
		 else
		{
			if (m_game.bsInput.isKeyPressed(KeyEvent.VK_P))
			{
				m_paused = !m_paused;
			}

			if (m_paused)
			{
				no_elapsed_time(); // fixes pause bug in race timer
				
			}
				
			if ((m_state == STATE_GO) && (!m_paused))
			{
				// cheat keys
				
				cheat_keys();
				
				// let game run

				int car_state = (m_car_set.update(get_elapsed_time(),
						get_state_elapsed_time(),m_circuit_checker));

				switch (car_state)
				{
				case CarSet.GAME_OVER:
				case CarSet.WINNER:
					
					if ((car_state == CarSet.GAME_OVER) && (!GameOptions.instance().get_cheat_mode()))
					{
						m_game_over_flag = true;
					}
					
					// force rank computation
					m_car_set.compute_ranks();

					m_winning_time = m_circuit_checker.extrapolate_winning_time(get_state_elapsed_time() - m_start_time);

					fadeout();
					break;

				case CarSet.NO_EVENT:
				
					if (get_state_elapsed_time() % 1000 < 100)
					{
						// update once in a while
						m_car_set.compute_ranks();    	   	 
					}
				
					break;
				}
			}
		}
		
		if(!m_paused)
		{
			m_gate_set.update(get_elapsed_time(),m_car_set);
			m_train_set.update(get_elapsed_time(),m_car_set,m_circuit_checker);
			m_lake_image_set.update(get_elapsed_time());
		}
		if (is_fadeout_done())
		{
			m_game.stop_music();
			m_car_set.stop_sounds();

			if (is_escaped())
			{
				end_sounds();
				set_next(new TitleScreen2());
			}
			else
			{
				set_next(new ResultScreen(m_options,m_car_set.get_item(0).get_driver(),
						m_winning_time,m_game_over_flag));
			}
		}
	}
  }
 
 private void end_sounds()
 {
	 m_car_set.end_sounds();
	 m_sfx_set.dispose();
 }
  protected void p_init()
  {
	  try 
	  {

		  if (!m_options_initialized)
		  {
			  m_options.init(m_dimension,m_game);
		  }

		  set_fadeinout_time(500,500);

		  int circuit_index = m_options.get_circuit();
		  
		  m_data = new CircuitData(m_options.get_circuit_set());
		  
		  String circuit_directory = m_options.get_circuit_set().directory;
		  
		  Localizer.load(circuit_directory);
		  String s = circuit_directory + File.separator + circuit_index + ".sc3";
		  
		  m_data.load(s,true);
		  m_game_first_render = true;
		  
		  m_info_screen = null;
		  if (!m_demo_mode)
		  {
			  m_info_screen = new RaceInfoScreen(m_data, this, circuit_index);
			  m_info_screen.init(m_dimension, m_game);
			  m_state = STATE_START;
		  }
		  else
		  {
			  end_init();
			  m_state = STATE_GO;
		  }
		  m_paused = false;
	  }
	  catch (Exception e) 
	  {
		  e.printStackTrace();
		  // if an error occurs during the init phase trapped here
		  show_error(e.getMessage());
	  }

  }

  private void cheat_keys()
  {
 		GameOptions go = GameOptions.instance();
 		if (go.get_cheat_mode())
 		{
 			if (m_game.bsInput.isKeyPressed(KeyEvent.VK_F))
 			{
 				go.freeze_cpu_cars = !go.freeze_cpu_cars;
 			}
 			else if (m_game.bsInput.isKeyPressed(KeyEvent.VK_V))
 			{
 				go.debug_mode = !go.debug_mode;
 			}
 			
 			boolean cpu_only_mode = go.get_car_moves() == GameOptions.CarMoves.cpu_only;
 				
 			for (CircuitView cv : m_circuit_view)
 			{
 				cv.update_cheat_keys(cpu_only_mode);
 			}
 		}
  }
  protected void p_render(Graphics2D g)
  {
	  // first show information screen
	  
	  if ((m_info_screen != null) && (m_info_screen.is_escaped() || (!m_info_screen.is_fadeout_done())))
	  {
		  m_info_screen.render(g);
	  }
	  else
	  {
		  if (m_game_first_render)
		  {
			  m_car_set.init_sounds();

			  m_game_first_render = false;
		  }

		  m_info_screen = null;

		  for (CircuitView cv : m_circuit_view)
		  {
			  cv.render(g);
		  }

		  if (m_game_options.debug_mode)
		  {
			  m_circuit_checker.render(g);
		  }
	  }
  }
  
 

}
