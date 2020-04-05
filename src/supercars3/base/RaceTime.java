package supercars3.base;

public class RaceTime
{
	private long    milliseconds;
	private boolean default_time;
	
	public long get_milliseconds()
	{
		return milliseconds;
	}
	
	public void set_milliseconds(long m)
	{
		milliseconds = m;
		default_time = false;
		
	}
	
	public boolean is_default_time()
	{
		return default_time;
	}
	
	
	public RaceTime()
	{
		default_time = true;
		milliseconds = 300 * 1000;
	}
	
	public String time()
	{
		String seconds = ""+((milliseconds/1000) % 60);
		if (seconds.length() == 1)
		{
			seconds = "0" + seconds;
		}
		String decis = ""+(milliseconds/100) % 10;

		return (milliseconds / 60000) + "." +
		seconds + "." + decis;
	}

}
