package supercars3.sys;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Mp3Play implements Runnable
{
	 private String m_mp3_name;
	 private Player m_player;
	 private Thread m_play_thread;
	 
	 public Mp3Play(String mp3_name)
	 {
		 m_mp3_name = mp3_name;
	 }
	 
	 public void play()
	 {
		 m_play_thread = new Thread(this);
		 m_play_thread.start();
	 }

	 public boolean is_playing()
	 {
		  return m_play_thread.isAlive();
	 }
	 public synchronized void stop()
	 {
		 if (m_player != null)
		 {
			 m_player.close();
		 }
	 }
	 
	public void run() 
	{
		
		 try 
		 {
			FileInputStream is = new FileInputStream(m_mp3_name+".mp3");
			 BufferedInputStream bis = new BufferedInputStream(is);
			 m_player = new Player(bis);
			 
			 m_player.play();
		} 
		 catch (FileNotFoundException e) {
			
		} catch (JavaLayerException e) {
			
		}
		
	}
}