package supercars3.base;

import java.io.IOException;

import supercars3.game.players.Driver;
import supercars3.sys.ParameterParser;


public class RecordTime extends RaceTime
{
	public String level;
	public int circuit;
	public String driver;
	
	public RecordTime(ParameterParser fr) throws IOException
	{
		fr.startBlockVerify("record");
		level = fr.readString("level");
		circuit = fr.readInteger("circuit");
		driver = fr.readString("driver");
		set_milliseconds(fr.readLong("millseconds"));
		fr.endBlockVerify();
		
	}
	public RecordTime()
	{
		driver = "anon";
	}
	

	public void serialize(ParameterParser fw) throws IOException
	{
		fw.startBlockWrite("record");
		fw.write("level",level);
		fw.write("circuit",circuit);
		fw.write("driver",driver);
		fw.write("millseconds",get_milliseconds());
		fw.endBlockWrite();	

	}

	public boolean set(Driver driver,long ms)
	{
		boolean rval = (ms < this.get_milliseconds());
		
		if (rval)
		{
			// new record
			this.driver = driver.get_name();
			this.set_milliseconds(ms);
		}
		
		return rval;
	}
	public boolean matches(int circ, String lev)
	{
		return ((circuit == circ) &&
				level.equals(lev));
	}

	
}


