package supercars3.sys;

import java.io.*;
import java.util.TreeMap;

import supercars3.base.DirectoryBase;

public class Localizer
{
	public static final String DEFAULT_LANGUAGE = "english";
	public static final String FILE_EXTENSION = ".txt";
	
	private static TreeMap<String,String> m_kv = null;
	private static String m_language = DEFAULT_LANGUAGE;
	
	public static String get_language()
	{
		return m_language;
	}

	
	public static void set_language(String language)
	{
		m_kv = new TreeMap<String,String>();
		m_language = language;
		
		File locale_file = new File(DirectoryBase.get_root() + "locale" +  File.separator + language + FILE_EXTENSION);

		if (!locale_file.exists())
		{
			locale_file = new File(DirectoryBase.get_root() + "locale" +  File.separator + DEFAULT_LANGUAGE + FILE_EXTENSION);
		}
		
		load_any(locale_file);
	}

	public static File load(String locale_directory)
	{
	  File locale_file = new File(locale_directory+File.separator+get_language()+FILE_EXTENSION);
	  if (!locale_file.exists())
	  {
		  locale_file = new File(locale_directory+File.separator+DEFAULT_LANGUAGE+FILE_EXTENSION);
	  }
	  
	  load_any(locale_file);
	  
	  return locale_file;
	}
	
	private static void load_any(File locale_file)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(locale_file));
			try
			{
				while (true)
				{
					String s = br.readLine();
					String [] tokens = s.split("=");

					switch(tokens.length)
					{
					case 2:
					{
						String keyword = tokens[0].trim();
						String value = tokens[1].trim().replaceAll("\\\\n", "\n");
						if (value.length() == 0) // translation empty: same as keyword
						{
							value = keyword;
						}
						m_kv.put(keyword,value);
						break;
					}
					case 1:
					{
						String keyword = tokens[0].trim();
						m_kv.put(keyword,keyword); // old value is replaced if found
					}
					break;
					}
				}
			}
			catch (IOException e)
			{
				br.close();
			}
		}
		catch (Exception e)
		{

		}
	}
	public static void unload(File locale_file)
	{
		if (locale_file != null)
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(locale_file));

				try
				{
					while (true)
					{
						String s = br.readLine();
						String [] tokens = s.split("=");

						switch(tokens.length)
						{
						case 2:
						{
							String keyword = tokens[0].trim();
							m_kv.remove(keyword);
							break;
						}
						case 1:
						{
							String keyword = tokens[0].trim();
							m_kv.remove(keyword);
						}
						break;
						}
					}
				}
				catch (IOException e)
				{
					br.close();
				}
			}
			catch (Exception e)
			{

			}
		}
	}
	public static final String value(String key)
	{
		return value(key,false);
	}

	public static final String value(String key,boolean return_key_if_not_found)
	{
		String rval = null;
		
		if (key != null)
		{
			if (m_kv == null)
			{
				set_language(DEFAULT_LANGUAGE);
			}
			rval = m_kv.get(key);
			if (rval == null)
			{
				if (return_key_if_not_found)
				{
					rval = key;
				}
				else
				{
					rval = "["+key+"]";
				}
			}
		}
		
		return rval;
	}
}
