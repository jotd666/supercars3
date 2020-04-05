package supercars3.game;

import java.io.File;
import java.io.IOException;

import supercars3.base.DirectoryBase;
import supercars3.sys.*;
import java.util.*;

public class RandomInGameMusic
{
	private MusicModule m_module = new MusicModule();
	private File m_music_dir;
	private Collection<String> m_module_file_list = new LinkedList<String>();
	private Collection<String> m_shuffled_module_list;
	private Iterator<String> m_module_list_iterator;
	private boolean m_loop = true;
	
	private void append_music_file(File directory)
	{
		String user_dir = directory.getPath();
		
		String [] files = new File(user_dir).list();
		for (String f : files)
		{
			if (f.contains(".mod") || f.contains("mod."))
			{
				m_module_file_list.add(user_dir + File.separator + f);
			}					
		}
	}
	public RandomInGameMusic()
	{
		m_music_dir = new File(DirectoryBase.get_music_path() + "user" + File.separator);
		File [] files = m_music_dir.listFiles();
		for (File f : files)
		{
			if (f.isDirectory())
			{
				append_music_file(f);
			}
		}
	}
	
	private void shuffle()
	{
		m_shuffled_module_list = new RandomList<String>(m_module_file_list).get_contents();
		m_module_list_iterator = m_shuffled_module_list.iterator();
	}
	
	public void play_next()
	{
		if ((m_module_list_iterator == null) || (!m_module_list_iterator.hasNext()))
		{
			shuffle();
		}
		
		if (m_module_list_iterator.hasNext())
		{
			String f = m_module_list_iterator.next();
			try
			{
				m_module.load(f, m_loop);				
			}
			catch (IOException e)
			{
				
			}
		}
	}
	
	public void stop()
	{
		m_module.stop();
	}
}
