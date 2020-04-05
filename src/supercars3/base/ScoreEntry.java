package supercars3.base;

import java.io.IOException;

import supercars3.sys.ParameterParser;

public class ScoreEntry implements Comparable<ScoreEntry>
{
	public int compareTo(ScoreEntry other)
	{
		return other.score - score;
	}
	
	public String driver;
	public int score;
	public boolean human;
	
	public ScoreEntry()
	{
		this("",0,false);
	}
	public ScoreEntry(String driver, int score, boolean human)
	{
		this.driver = driver;
		this.score = score;
		this.human = human;
	}
	public void parse(ParameterParser fr) throws IOException
	{
		fr.startBlockVerify("entry");
		driver = fr.readString("driver");
		human = fr.readBoolean("human");		
		score = fr.readInteger("score");
		fr.endBlockVerify();
	}
	public void serialize(ParameterParser fw) throws IOException
	{
		fw.startBlockWrite("entry");
		fw.write("driver",driver);
		fw.write("human",human);
		fw.write("score",score);
		fw.endBlockWrite();
	}
}
