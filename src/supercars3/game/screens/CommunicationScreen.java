package supercars3.game.screens;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Vector;

import supercars3.base.*;
import supercars3.game.*;
import supercars3.game.cars.Car;
import supercars3.sys.GameFont;
import supercars3.sys.Localizer;
import supercars3.sys.ParameterParser;
import supercars3.sys.RandomList;

import java.awt.event.KeyEvent;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class CommunicationScreen extends GameState
{
  private BufferedImage m_wood;
  private BufferedImage m_default;
  private String m_directory;
  private String m_prefix;
  private int m_at_stake;
  private int m_minimum;
  private int m_maximum;
  private String m_intro_text;
  private String m_player_text;
  private int m_state;
  
  private GameFont m_white,m_gray;
	  
  private static final int POINTS = 0;
  private static final int MONEY = 1;
  private static final int WEAPONS = 2;
  
  private static final int INTRO = 0;
  private static final int QUESTIONS = 1;
  private static final int VERDICT = 2;
  private static final int NB_QUESTIONS = 10;
  private static final int NB_CHOICES = 3;
  
  private static final int BAD = 0;
  private static final int NEUTRAL = 1;
  private static final int CORRECT = 2;
  
  private float m_old_y_pos = 0;
  
  private int m_question_index;
  private int m_score;
  private int m_selected_answer;
  private String m_verdict_text;
  private Car m_car;
  private boolean m_error = false;
  private File m_locale_file = null;
  private GameState m_repair_screen;
  private int m_allowed_neutral;
  private int m_nb_neutral = 0;
  
  private class Answer
  {
	  public Answer(String t, int v)
	  {
		text = Localizer.value(t);
		value = v;
	  }
	  public String text;
	  public int value;
  }
  
  private class PictureTemplate
  {
	  public int x,y;
	  public String default_picture;
	  
	  public PictureTemplate(ParameterParser f) throws IOException
	  {
		  f.startBlockVerify("picture_template");
		  
		  this.x = f.readInteger("pos_x");
		  this.y = f.readInteger("pos_y");
		  this.default_picture = Localizer.value(f.readString("default"),true);
		  
		  f.endBlockVerify();
	  }
  }   
  
  
  private class Picture
  {
	  public BufferedImage image;
	  public PictureTemplate parameters;
	  public Picture(ParameterParser f) throws IOException
	  {
		f.startBlockVerify("picture");
		int index = f.readInteger("index");
		parameters = m_picture_template[index-1];
		String filename = f.readString("file",true);
		if (filename.equals(ParameterParser.UNDEFINED_STRING))
		{
			filename = parameters.default_picture;
		}
		if (!new File(m_directory + filename + ".png").exists())
		{
			filename = parameters.default_picture;
		}
		
		filename = m_directory + Localizer.value(filename,true) + ".png";
		
		image = bsLoader.getImage(filename);

		f.endBlockVerify();
	  }
  }
  
  private class Question
  {
	  private Vector<Answer> answers;
	  public Vector<Answer> choices;
	  public String text;
	  public Picture [] pictures = null;
	  
	  public Question(int index)
	  {
		  String qp = m_prefix + "q_";
		  if (index < 10)
		  {
			  qp += "0";
		  }
		  qp += index + "_";

		  this.text = Localizer.value(qp+"question");

		  this.answers = new Vector<Answer>();
		  answers.add(new Answer(qp+"bad",0));
		  answers.add(new Answer(qp+"neutral",1));
		  answers.add(new Answer(qp+"correct",2));
		  

	  }
	  
	  public void render_pictures(Graphics2D g)
	  {
		  if (pictures != null)
		  {
			  for (int i = 0; i < pictures.length; i++)
			  {
				  Picture p = pictures[i];
				  g.drawImage(p.image,p.parameters.x, p.parameters.y, null);
			  }
		  }
	  }
	  public void draw()
	  {
		  choices = new RandomList<Answer>(answers).get_contents();		  
	  }
  }
  
  private Vector<Question> m_questions;
  
  private static final String [] AT_STAKE_STR = { "points", "money", "weapons" };
  
 
  
  PictureTemplate [] m_picture_template = null;
  
  public CommunicationScreen(int player_number,Car c,String directory,GameState repair_screen)
  {
	  m_white = SCGame.NORMAL_BITMAP_FONT;
	  m_gray = SCGame.NORMAL_BITMAP_GRAY_FONT;
	  m_repair_screen = repair_screen;
	  
	  m_directory = DirectoryBase.get_comm_screen_root() + directory + File.separator;
	  	  
	  m_prefix = "cs_"+directory+"_";
	  String player_name = (c==null) ? "nobody" : c.get_driver().get_name();
	  
	  m_player_text = Localizer.value("Player")+" "+player_number+" - "+player_name;
	  m_car = c;
	  
	  m_locale_file = Localizer.load(m_directory);
  }
	protected void p_init()
	{
		m_wood = bsLoader.getImage("images" + File.separator + "wood.jpg");

		int idx = (int)(Math.random() * 3) + 1;
				
		m_intro_text = Localizer.value(m_prefix+"intro_"+idx);		
		
		m_state = INTRO;
		
		m_question_index = -1;
		m_score = 0;
		
		int current_index = 1;
		
		Vector<Question> ql = new Vector<Question>();

		try
		{
			ParameterParser f = ParameterParser.open(m_directory + "questions.sc3");
			
			f.startBlockVerify("COMMUNICATION_SCREEN");
			m_at_stake = f.readEnumerate("at_stake", AT_STAKE_STR);
			m_minimum = f.readInteger("minimum");
			m_maximum = f.readInteger("maximum");
			/*int nb_questions =*/ f.readInteger("nb_questions");
			m_allowed_neutral = f.readInteger("allowed_neutral");
			int extra_pictures = f.readInteger("extra_pictures");
			
			if (extra_pictures > 0)
			{
				m_picture_template = new PictureTemplate[extra_pictures];
				
				for (int i = 0; i < extra_pictures; i++)
				{
					m_picture_template[i] = new PictureTemplate(f);
				}
			}
				
			boolean over = false;
			
			while(!over)
			{
				String block_name = f.readBlockName();
				
				if (block_name.equals("question_end"))
				{
					over = true;
				}
				else if (block_name.equals("question_list"))
				{
					int first = f.readInteger("first");
					int last = f.readInteger("last");

					for (int i = first; i < last; i++)
					{
						ql.add(new Question(i));					
					}

					current_index = last + 1;
				}
				else if (block_name.equals("question"))
				{
					// complex question
					int nb_pictures = f.readInteger("nb_pictures");
					Question q = new Question(current_index++);

					q.pictures = new Picture[nb_pictures];
					for (int i = 0; i < nb_pictures; i++)
					{
						q.pictures[i] = new Picture(f);
					}
					ql.add(q);
				}
				f.endBlockVerify();
			}
			
			f.endBlockVerify();
			
			m_default = bsLoader.getImage(m_directory+"default.png");

			
		}
		catch (IOException e)
		{
			m_error = true;
			fadeout();
			//System.out.println(e);
		}
		
		if (!m_error)
		{
			m_questions = new RandomList<Question>(ql,NB_QUESTIONS).get_contents();
		}
	}
  protected void p_render(Graphics2D g)
  {
	  g.drawImage(m_default,0,0,null);


	  int offset = m_default.getHeight();
	  while (offset < getHeight()) 
	  {
		  int x = 0;
		  while (x < getWidth()) {
			  g.drawImage(m_wood, x, offset, null);
			  x += m_wood.getWidth();
		  }
		  offset += m_wood.getHeight();
	  }
	  
	  
	  switch (m_state)
	  {
	  case INTRO:		  
		  m_white.write(g, m_player_text, 10, m_default.getHeight()+20,-2,false,false,0);		
		  m_gray.write(g, m_intro_text, 10, m_default.getHeight()+70,-2,false,false,0);		
		  break;
		  
	  case QUESTIONS:
		  Question q = m_questions.elementAt(m_question_index);
		  
		  offset = m_default.getHeight()+20;
		  m_white.write(g, q.text, 10, offset,-2,false,false,0);		  
		  offset += 60;
		  
		  boolean gray = get_state_elapsed_time() % 40 > 20;
		  
		  for (int i = 0; i < NB_CHOICES; i++)
		  {
			  GameFont gf = ((!gray) && (i == m_selected_answer)) ? m_white : m_gray;
			  Rectangle r = gf.write_line(g, q.choices.elementAt(i).text, 10, offset, 
					  -2,false,false);
			  
			  offset += r.getHeight();
			  
		  }
		  
		  q.render_pictures(g);

		  break;
		  
	  case VERDICT:
		  offset = m_default.getHeight()+20;
		  m_white.write(g, m_verdict_text, 
				  10, offset, 
				  -2,false,false,0);
		  
	  }
  }

  private boolean handle_questions()
  {
	  boolean rval = false;
  
	  if (m_state == QUESTIONS)
	  {
		  // get last answer
		  Answer last_answer = m_questions.elementAt(m_question_index).choices.elementAt(m_selected_answer);
		  int score = last_answer.value;
		  
		  boolean check_only_bad = (m_question_index < NB_QUESTIONS/2);

		  if (score == BAD) // BAD = out
		  {
			  rval = true;
		  }
		  else
		  {
			  if (score == NEUTRAL)
			  {
				  m_nb_neutral++;
			  }
			  
			  if (!check_only_bad)
			  {
				  if (score == CORRECT)
				  {
					  m_score++;
				  }
			  }
			  else
			  {
				  // correct or neutral is OK for first questions
				  				  				  
				  if (m_nb_neutral > m_allowed_neutral)
				  {
					  // too many neutral answers during the first questions: out
					  
					  rval = true;
				  }
				  else
				  {
					  m_score++;
				  }
			  }
		  }
	  }
	  if (m_question_index < NB_QUESTIONS-1)
	  {
		  Question q = m_questions.elementAt(++m_question_index);
		
		  q.draw();

		  m_selected_answer = 0;
	  }
	  else
	  {
		  rval = true;
	  }
	  return rval;
  }

  private void reward()
  {
	  int value = ((m_maximum - m_minimum) * m_score) / NB_QUESTIONS + m_minimum;
	  m_verdict_text = Localizer.value(m_prefix + "v_" + m_score);
	  
	  if (m_car != null)
	  {
		  switch (m_at_stake)
		  {
		  case POINTS:
			  m_car.add_points(value);
			  break;
		  case MONEY:
			  m_car.get_equipment().add_money(value);
			  break;
		  case WEAPONS:
			  m_car.get_equipment().do_freebie(value);
			  break;
		  }
	  }
  }
  protected void p_update()
  {
	  if (m_error)
	  {
		  set_next(m_repair_screen);
		  fadeout();
	  }
	  if (is_escaped())
	  {
		  set_next(new TitleScreen2());
	  }
	  
	  if (m_game.fire_pressed())
	  {
		  switch(m_state)
		  {
		  case INTRO:
			  handle_questions();
			  m_state++;
			  break;
		  case QUESTIONS:
			  if (handle_questions())
			  {
				  m_state++;
				  reward();
			  }
			  break;
		  case VERDICT:
			  set_next(m_repair_screen);
			  fadeout();
		  }
	  }
	  else
	  {
		  float y_pos = 0;
		  
		  if (m_joystick != null)
		  {
			  y_pos = m_joystick.getYPos();
		  }
		  
		  if (m_game.keyPressed(KeyEvent.VK_UP) || (y_pos < -0.5 && m_old_y_pos > -0.5))
		  {
			  if (m_selected_answer > 0)
			  {
				  m_selected_answer--;
			  }
		  }
		  else if (m_game.keyPressed(KeyEvent.VK_DOWN) || (y_pos > 0.5 && m_old_y_pos < 0.5))
		  {
			  if (m_selected_answer < NB_CHOICES-1)
			  {
				  m_selected_answer++;
			  }
		  }
		  m_old_y_pos = y_pos;
	  }
	  
	  if (is_fadeout_done())
	  {
		  Localizer.unload(m_locale_file);
	  }
  }
}
