package supercars3.base;

import java.io.File;
import java.util.Vector;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class DirectoryBase 
{
  public static final String CIRCUIT_EXTENSION = ".sc3";
  public static final String ROOT_DIR_VARIABLE = "SC3_ROOT_DIR";
  private static String m_root = null;
 
  static public String get_user_path()
  {
	  return System.getProperty("user.dir") + File.separator;
  }
  
  static public String get_circuit_path()
  {
    return get_root() + "circuits" + File.separator;
  }

  static public String get_car_path()
  {
    return get_root() + "cars" + File.separator;
  }
  static public String get_images_path()
  {
    return get_root() + "images" + File.separator;
  }
  
   static public void env_check() throws Exception
   {	   
		  if (get_root() == null)
		  {
			  m_root = "D:\\jff_data\\jbproject\\SuperCars3\\";
			  //throw new Exception(ROOT_DIR_VARIABLE+" has not been set");
		  }
   }
   
   static public String get_comm_screen_root()
   {
	   return get_root() + "commscreens" + File.separator;
   }
  static public synchronized String get_root()
  {
	  if (m_root == null)
	  {
		  m_root = System.getProperty(ROOT_DIR_VARIABLE) + File.separator;
	  }
    return m_root;
  }

  static public String get_music_path()
  {
	  return get_root() + "music" + File.separator;
  }
  static public String get_sound_path()
  {
	  return get_root() + "sounds" + File.separator;
  }
  
  static public String get_font_path()
  {
	  return get_root() + "fonts" + File.separator;
  }
  
  static public Vector<String> get_cars()
  {
    File f = new File(get_car_path());
    String [] raw_list = f.list();

    Vector<String> rval = new Vector<String>();
    
    for (String s : raw_list)
    {
    	File info = new File(f.getAbsolutePath() + File.separator + s + File.separator + "info.sc3");
    	if (info.exists())
    	{
    		rval.add(s);
    	}
    }
    
    
    return rval;
  }
}