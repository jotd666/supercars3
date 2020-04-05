package supercars3.sys;

import javax.sound.sampled.*;
import java.io.File;

public abstract class WavSound implements Runnable
{
	protected byte [] data = null;
	protected volatile Thread play_thread;
	protected SourceDataLine sdl;
	protected double m_sound_length;
	private volatile int sample_position;
	
	synchronized int get_sample_position()
	{
		return sample_position;
	}
	
	synchronized void set_sample_position(int sp)
	{
		sample_position = sp;
	}
	
	public WavSound(String file_prefix)
	{
		this(file_prefix,1.0);
	}
	
	private void apply_gain(int i, double volume)
	{
		int shift = 8;
		int value = 0;

		for (int j = 0; j < 2; j++)
		{
			value += (data[i+1-j]) << shift;
			shift -= 8;
		}


		value *= volume;

		// little endian WAV
		shift = 8;

		for (int j = 0; j < 2; j++)
		{
			data[i+1-j] = (byte)(((value >> shift) & 0xFF));
			shift -= 8;
		}
	}
	
	public WavSound(String file_prefix, double volume)
	{
		try
		{
			File fileIn = new File(file_prefix+".wav");
			if (!fileIn.exists()) { System.out.println("error: "+fileIn); }
			AudioInputStream audioInputStream = 
				AudioSystem.getAudioInputStream(fileIn);
			long frameLength = audioInputStream.getFrameLength();
			AudioFormat audioFormat = audioInputStream.getFormat();
			sdl = AudioSystem.getSourceDataLine(audioFormat);
			// in a .wav file, this is the length 
			// of the audio data in bytes

			data = new byte[(int)frameLength * audioFormat.getFrameSize()];
			sdl.open(audioFormat,data.length);

			audioInputStream.read(data);
			
			m_sound_length = audioInputStream.getFrameLength() / audioFormat.getSampleRate();
			/*AudioFormat af = audioInputStream.getFormat();
			 
			int nb_channels = af.getChannels();
			AudioFormat.Encoding enc = af.getEncoding();
			enc.equals(AudioFormat.Encoding.PCM_SIGNED)*/
			
			// sorry about this piece of code to change volume
			// it only works with Windows 16 bit WAV files (mono & stereo)
			
			if (volume != 1.0)
			{
				int frame_size = audioFormat.getFrameSize();
				switch (frame_size)
				{
				case 1:
					break;
				case 2:

					for (int i = 0; i < data.length; i += frame_size)
					{
						apply_gain(i,volume);
					}
					break;
				case 4:
					for (int i = 0; i < data.length; i += 4)
					{
						apply_gain(i,volume);
						apply_gain(i+2,volume);
					}

					break;
				}
			}
		}
		
		catch (Exception ex)
		{
			data = null;
		}
		play_thread = new Thread(this);

		play_thread.start();

		Thread.yield();

	}
	
	public double duration()
	{
		return m_sound_length;
	}
	
	public void end()
	{
		play_thread = null;
		synchronized(this)
		{
			notify();
		}
	}
	
	public void play()
	{	
		set_sample_position(0);
		
		synchronized(this)
		{
			notify();
		}
	}
	


}