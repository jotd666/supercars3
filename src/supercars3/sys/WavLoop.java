package supercars3.sys;


public class WavLoop extends WavSound
{
	private byte [] resampled = null;
	private int buffer_length = 0;
	private boolean do_resample = false;
	
	private volatile boolean m_playing = true;
	
	private void handle_resampling()
	{
		if (do_resample)
		{
			do_resample = false;
			// resample the data
			
			float us_ratio = buffer_length / (float)data.length;
			
			// undersample
			for (int i = 0; i < data.length; i+=2)
			{
				int j = (Math.round(i*us_ratio) / 2) * 2;
				resampled[j] = data[i];
				resampled[j+1] = data[i+1];
			}
		}
			
	}
	public WavLoop(String file_prefix)
	{
		super(file_prefix);
		if (data != null)
		{
			buffer_length = data.length;

			resampled = new byte [data.length * 2];
			for (int i = 0; i < data.length; i++)
			{
				resampled[i] = data[i];
			}		
		}
	}

	public void pause()
	{
		synchronized(this)
		{
			m_playing = false;
			notify();
		}
	}
	
	public void end()
	{
		synchronized(this)
		{
			m_playing = false;
			play_thread = null;
			notify();
			
		}
	}
	public void resume()
	{
		synchronized(this)
		{
			m_playing = true;
			notify();
		}
	}
	
	public void set_resampling_ratio(float ratio)
	{
		int frame_size = sdl.getFormat().getFrameSize();
		int new_length = Math.round(data.length / (ratio * frame_size)) * frame_size;
		if (new_length > resampled.length)
		{
			new_length = resampled.length;
		}

		if (buffer_length != new_length)
		{
			buffer_length = new_length;
			do_resample = true;
		}		
	}
	public void run()
	{
		Thread this_thread = Thread.currentThread();

		try
		{			
			synchronized(this)
			{
				wait();
				sdl.flush();
			}

			sdl.start();

			if (data != null)
			{
				while (play_thread == this_thread)
				{		
					if (!m_playing)
					{
						synchronized(this)
						{
							wait();
							sdl.flush();
						}
					}

					handle_resampling();

					sdl.write(resampled,0,buffer_length);

				}	
			}

		}
		catch (Exception ex)
		{

		}
		sdl.stop();
		sdl.close();
	}


}
