package supercars3.game.screens;

import java.awt.*;

import supercars3.base.*;
import supercars3.game.MainGame;
import supercars3.game.GameState;
import supercars3.sys.Localizer;

public class RaceInfoScreen extends GameState
{
  private CircuitData m_data;
  private MainGame m_main_game;
  private boolean m_data_loaded = false;
  private String m_title;
  private String m_comments;
  private String m_location;
  private WoodBackground m_wood;

  private OptionsScreenMain get_options()
  {
	  return m_main_game.get_options();
  }
  public RaceInfoScreen(CircuitData data,MainGame game,int circuit_index)
  {
    m_data = data;
    m_main_game = game;
    m_comments = Localizer.value("comments_"+circuit_index);
    m_location = Localizer.value("location_"+circuit_index);
    
    set_fadeinout_time(200,200);
  }

	
  protected void p_update() 
  {

    if (m_data_loaded && ((m_game.return_pressed()) || m_game.fire_pressed()))
    {
    	fadeout();
    }
    if ( (!is_fadein()) && (!m_data_loaded)) 
    {
    	m_main_game.end_init();
    	m_data_loaded = true;
    }
    
  }
  protected void p_init() 
  {
	  
	  m_title = Localizer.value("LEVEL_X").replaceAll("%LEVEL%", 
			  Localizer.value(get_options().get_circuit_set().name,true).toUpperCase())+
			  " - "+Localizer.value("RACE")+" "+ get_options().get_circuit();
	  
		m_wood = new WoodBackground(getWidth(),getHeight());
  }


  protected void p_render(Graphics2D g)
  {
    m_wood.render(g);

    RecordTime r = get_options().get_record_time();
    
    draw_string(g, m_title,PIXEL_TO_RATIO*32);
    draw_2_strings(g, Localizer.value("Location"), m_location,PIXEL_TO_RATIO*98,true);
    draw_2_strings(g, Localizer.value("Course record"), r.time() + " - " + r.driver,PIXEL_TO_RATIO*156,true);
    draw_2_strings(g, Localizer.value("Number Of Laps"), ""+m_data.get_nb_laps(),PIXEL_TO_RATIO*214,true);
    draw_2_strings(g, Localizer.value("Information"),m_comments,PIXEL_TO_RATIO*280,true);

 


  if (m_data_loaded)
  {
    if ((get_state_elapsed_time() % 1000) < 500)
    {
      draw_string(g, Localizer.value("PRESS RETURN"),0.90);
    }
  }
  else
  {
    draw_string(g, Localizer.value("LOADING")+ " ...",0.90);
  }

}

}