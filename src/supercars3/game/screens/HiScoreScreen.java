package supercars3.game.screens;

import java.awt.Graphics2D;

import supercars3.base.*;
import supercars3.game.GameState;
import supercars3.game.SCGame;
import supercars3.sys.GameFont;
import supercars3.sys.Localizer;

public class HiScoreScreen extends DemoGameState
{
	private RecordScore m_record_score;
	private String m_title;
	private	GameState m_next_screen;
	private String [] m_positions;
	private ScoreEntry [] m_score_entries;
	private WoodBackground m_wood;
	
	public HiScoreScreen(CourseRecords cr, Levels levels, int level_index)
	{
		set_maximum_duration(7000);
		set_fadeinout_time(0, 1000);
		CircuitDirectory current = levels.get_level(level_index);
		m_record_score = cr.lookup_score(current.name);
		
		if (level_index+1 == levels.size())
		{
			m_next_screen = new SecondNancyScreen();
		}
		else
		{
			m_next_screen = new HiScoreScreen(cr,levels,level_index+1);
		}
	}
	
	protected GameState default_next_screen()
	{
		return m_next_screen;
	}
	
	protected void p_init()
	{
		m_wood = new WoodBackground(getWidth(),getHeight());
		
		m_title = Localizer.value("LEVEL_X").replaceFirst("%LEVEL%", Localizer.value(m_record_score.level).toUpperCase());
		m_score_entries = m_record_score.get_entries();
		m_positions = new String[m_score_entries.length];
		for (int i = 0; i < m_positions.length; i++)
		{
			m_positions[i] = new String(""+(i+1)+CourseRecords.get_suffix(i+1, true));
		}
	}
	
	protected void p_render(Graphics2D g)
	{
		m_wood.render(g);
		
		if (m_record_score != null)
		{
			GameFont gf = SCGame.NORMAL_BITMAP_GRAY_FONT;
			
			draw_string(g,m_title,PIXEL_TO_RATIO*22);

			int i = 98;
			int idx = 0;
			for (ScoreEntry se : m_record_score.get_entries())
			{
				gf.write_line(g,m_positions[idx++],62,i,0,false,false);
				gf.write_line(g,se.driver,148,i,0,false,false);
				gf.write_line(g,se.score+"",580,i,0,false,false);

				i += 36;
			}
		}
		
	}
}
