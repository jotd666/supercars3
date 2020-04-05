package supercars3.sys;

public class WavSample extends WavSound
{

	public WavSample(String file_prefix, double volume)
	{
		super(file_prefix,volume);
	}

	public void run()
	{
		Thread this_thread = Thread.currentThread();
		int write_block = data.length / 5;		
		int frame_size = sdl.getFormat().getFrameSize();
		
		// must be a multiple of the frame size
		
		write_block = (write_block / frame_size) * frame_size;
		
		try
		{			
			sdl.start();
			
			while (play_thread == this_thread)
			{				
				synchronized(this)
				{
					wait();
					sdl.flush();
				}
				
				if (play_thread != null)
				{					
					while (get_sample_position() < data.length)
					{
						int sp = get_sample_position();
						set_sample_position(sp + write_block);
						
						// other thread can call set_sample_position(0) now
						
						sdl.write(data,sp,Math.min(write_block,data.length - sp));
					
					}
				}	
			}
		}
		catch (Exception ex)
		{

		}
		if (sdl != null)
		{
			sdl.stop();
			sdl.close();
		}
	}

		/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		WavSound sp = new WavSample("mine", 0.5);
		sp.play();

		Thread.sleep(4000);
	}	
	
	}
