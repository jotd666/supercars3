package supercars3.game;

import com.golden.gamedev.*;
import com.golden.gamedev.engine.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.io.*;

import joystick.Joystick;

import supercars3.base.DirectoryBase;
import supercars3.base.Equipment;
import supercars3.game.screens.JffScreen;
import supercars3.game.screens.OptionsScreenMain;
import supercars3.sys.*;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class SCGame extends Game
{
   	public static GameFont NORMAL_BITMAP_FONT = null;
   	public static GameFont NORMAL_BITMAP_GRAY_FONT = null;
   	public static GameFont NUMBER_BITMAP_FONT = null;

  private GameState m_state = null;
  private MusicModule m_music = new MusicModule();
  private Mp3Play m_mp3_music = null;
  private AffineTransform m_transform = AffineTransform.getTranslateInstance(5000,5000); // get rid of GTGE logo
  private OptionsScreenMain m_options_screen;
  private Joystick m_joystick = Joystick.create(0);
  private int m_old_joy_button_state = 0;
  
  private static final int MAX_ELAPSED_TIME = 80;
  
  public final static Dimension m_dimension = new Dimension(640,480);

  public OptionsScreenMain get_options_screen()
  {
	  return m_options_screen;
  }
  
  public void finish()
  {
	  stop_music();
	  GameOptions.instance().save();
	  m_options_screen.save_records();
	  super.finish();
  }
  public SCGame() throws Exception
  {
	  m_options_screen = new OptionsScreenMain();
  }
  
  public void stop_music()
  {
		m_music.stop();
	  
	  if (m_mp3_music != null)
	  {
		  m_mp3_music.stop();
		  m_mp3_music = null;
	  }
    RandomInGameMusic rm = m_options_screen.get_random_music();
    if (rm != null) rm.stop();

  }

  public void load_random_music()
  {
	  GameOptions.MusicSelection ms = GameOptions.instance().get_music_mode();

	  switch (ms)
	  {
	  case full_music:
	  case music_in_game:
		  RandomInGameMusic rm = m_options_screen.get_random_music();
		  rm.play_next();
		  break;
	  }
	  
  }
  public boolean is_music_playing()
  {
    return m_music.is_playing();
  }
  public void load_music(String music_file)
  {
	  GameOptions.MusicSelection ms = GameOptions.instance().get_music_mode();

	  switch (ms)
	  {
	  case full_music:
	  case music_in_menus:
		  String path = DirectoryBase.get_music_path() + music_file;
		  try
		  {
			  stop_music();
			  
			  if (new File(path+".mp3").exists())
			  {
				  // mp3 version available: play mp3
				  m_mp3_music = new Mp3Play(path);
				  m_mp3_music.play();
			  }
			  else
			  {
				  path += ".mod";
				  m_music.load(path,true);
			  }
		  }
		  catch (Exception e)
		  {
			  m_state.show_error(path+": "+e.getMessage());
		  }
		  
		  break;
	  default:

		  stop_music();
	  break;
	  }
  }


  public void initResources()
  {
    distribute = true;

    System.setProperty("user.dir",supercars3.base.DirectoryBase.get_root());
    bsIO.setMode(BaseIO.WORKING_DIRECTORY);

    hideCursor();

    try
    {    	   
    	NORMAL_BITMAP_FONT = new GameFont("normal_letters");
       	NORMAL_BITMAP_GRAY_FONT = new GameFont("normal_letters",new Color(206,206,206),
    			new Color(140,140,140));
      	NUMBER_BITMAP_FONT = new GameFont("big_numbers");
    }
    catch (IOException e)
    {
    }
    
    this.bsGraphics.setWindowTitle(Localizer.value("window title")+
    		" - "+Localizer.value("version")+" "+Version.STRING_VALUE);

    if (GameOptions.instance().with_intro)
    {
    	showLogo();
      	m_state = new JffScreen();
    }
    else
    {
    	m_state = m_options_screen;
    	
    	switch (GameOptions.instance().test_mode)
    	{
    	case GameOptions.SHOP_TEST:
    		Equipment eq = new Equipment(false);
    		eq.add_money(7000);
    		eq.set_health(2*Equipment.MAX_HEALTH/3);
    		eq.cast_prices();    		
    		m_state = new supercars3.game.shop.RepairScreen(m_options_screen,eq,Localizer.value("player 1"));
    		break;
       	case GameOptions.SPECS_TEST:
    		m_state = new supercars3.game.screens.FirstHarrisonScreen();
    		break;
       	case GameOptions.DEMO_TEST:
    		m_state = new supercars3.game.screens.GameDemo();
    		break;
    	case GameOptions.COMM_SCREEN_TEST:
    		m_state = new supercars3.game.screens.CommunicationScreen(1,null,
    				GameOptions.instance().comm_screen_test,null);
    		break;
    		default:
    			break;
    	}
    }

    m_state.init(m_dimension,this);

  }

  public void render(Graphics2D g)
  {
    m_state.render(g);
    // avoid GTGE FPS+logo
    // g.setClip(0, 0, 0, 0); // doesn't work since 0.2.3
    g.setTransform(m_transform); // so FPS/GTGE logo does not trash display
  }

  public void update(long elapsed)
  {
	  // if elapsed time is too big (slow cpu)
	  // call it with smaller values in order to avoid
	  // "tunnel" effects with weapons, trains, and cars
	  
	  long elapsed_to_go = elapsed;
	  GameState s = null;
	  	  
	  while ((elapsed_to_go > MAX_ELAPSED_TIME) && (s == null))
	  {
		  s = m_state.update(elapsed_to_go);
		  elapsed_to_go -= MAX_ELAPSED_TIME;
	  }
	  
	  if ((s == null) && (elapsed_to_go > 0))
	  {
		  s = m_state.update(elapsed_to_go);
		  
	  }
	  if (s != null)
	  {
		  // screen change
		  m_state = s;
		  m_state.init(m_dimension,this);
	  }
  }

 
  
  public boolean fire_pressed()
  {
	  int joy_button_state = 0;
	  
	  if (m_joystick != null)
	  {
		  joy_button_state = m_joystick.getButtons();
	  }
	  
	  boolean rval = keyPressed(KeyEvent.VK_SPACE);
	  
	  if ((!rval) && (m_joystick != null))
	  {
		  rval = (m_old_joy_button_state == 0) && (joy_button_state != 0);
	  }
	  m_old_joy_button_state = joy_button_state;
	  
	  return rval;
  }
  public boolean return_pressed()
  {
	  return keyPressed(KeyEvent.VK_ENTER);
  }

  /****************************************************************************/
/***************************** START-POINT **********************************/
/****************************************************************************/

  private static void usage()
  {
	  System.out.println("Options: -full-screen -no-intro");
	  
  }
   public static void main(String[] args) throws Exception
   {
	   DirectoryBase.env_check();
	   
	   GameLoader game = new GameLoader();
	   GameOptions opts = GameOptions.instance();
	   
	   
	   int i = 0;
	   while (i < args.length)
     {
    	 String arg = args[i];
    	 String nextarg="";
    	 if (i < args.length - 1)
    	 {
    		 nextarg = args[i+1];
    	 }
    	 if (arg.charAt(0) == '-')
    	 {
       		 if (arg.equalsIgnoreCase("-shop-test"))
    		 {
    			 opts.test_mode = GameOptions.SHOP_TEST;
    			 opts.with_intro = false;
    		 }
       		 else if (arg.equalsIgnoreCase("-demo"))
    		 {
       			 opts.test_mode  = GameOptions.DEMO_TEST;
       			 opts.with_intro = false;      			 
    		 }
       		 else if (arg.equalsIgnoreCase("-specs-test"))
    		 {
       			 opts.test_mode = GameOptions.SPECS_TEST;
    			 opts.with_intro = false;
    		 }
    		 else if (arg.equalsIgnoreCase("-full-screen"))
    		 {
    			 opts.full_screen = true;
    		 }
    		 else if (arg.equalsIgnoreCase("-no-intro"))
    		 {
    			 opts.with_intro = false;
    		 }
    		 else if (arg.equalsIgnoreCase("-debug"))
    		 {
    			 opts.debug_mode = true;
    		 }
    		 else if (arg.equalsIgnoreCase("-commscreen-test"))
    		 {
    			 opts.test_mode = GameOptions.COMM_SCREEN_TEST;
       			 opts.with_intro = false;
       			 opts.comm_screen_test = nextarg;
   		 }
     		 else
    		 {
    			 usage();
    			 System.exit(1);
    		 }
    	 }
    	 i++;
     }
	   SCGame scgame = new SCGame();

	
     game.setup(scgame, m_dimension, opts.full_screen, false /* double buffering */);

     game.start();
   }

}
