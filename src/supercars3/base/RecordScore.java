package supercars3.base;

import java.io.IOException;
import java.util.Arrays;

import supercars3.sys.ParameterParser;


public class RecordScore 
{
	public String level;
	public final static int NB_SCORES = 10;

	private ScoreEntry [] m_score_entry = new ScoreEntry[NB_SCORES];
	
	public RecordScore(ParameterParser fr) throws IOException
	{
		parse(fr);
	}
	public ScoreEntry [] get_entries()
	{
		return m_score_entry;
	}
	
	public RecordScore(String level)
	{
		this.level = level;
		for (int i = 0; i < m_score_entry.length; i++)
		{
			m_score_entry[i] = new ScoreEntry();
		}
	}
	
	public void parse(ParameterParser fr) throws IOException
	{
		fr.startBlockVerify("record");
		level = fr.readString("level");
		for (int i = 0; i < m_score_entry.length; i++)
		{
			m_score_entry[i] = new ScoreEntry();
		}
		for (ScoreEntry se : m_score_entry)
		{
			se.parse(fr);
		}
		fr.endBlockVerify();
		
		sort();
	}
	public void serialize(ParameterParser fw) throws IOException
	{
		fw.startBlockWrite("record");
		fw.write("level",level);
		for (ScoreEntry se : m_score_entry)
		{
			se.serialize(fw);
		}
		fw.endBlockWrite();	

	}

	private void sort()
	{
		Arrays.sort(m_score_entry);
	}
	
	public boolean set(String driver,int challenging_score)
	{
		sort();

		ScoreEntry se = m_score_entry[NB_SCORES-1];
				
		boolean rval = (se.score < challenging_score);
		
		if (rval)
		{
			se.score = challenging_score;
			se.driver = driver;

			// sort required
			
			sort();
		}
		return rval;
	}
	public boolean matches(String lev)
	{
		return (level.equals(lev));
	}

	
}


