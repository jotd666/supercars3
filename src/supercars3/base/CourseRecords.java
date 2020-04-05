package supercars3.base;

import java.io.*;
import java.util.*;

import supercars3.sys.Localizer;
import supercars3.sys.ParameterParser;

public class CourseRecords
{
	private static final int NB_OPPONENTS = 10;
	
	public static final int [] PRIZES = {
		  5000,
		  4000,
		  3000,
		  2500,
		  2200,
		  2000,
		  1800,
		  1700,
		  1600,
		  1500
		};
	public static final int [] POINTS = {
		  5,
		  4,
		  3,
		  2,
		  1,
		  0,
		  0,
		  0,
		  0,
		  0
		};
	
	private HashSet<RecordTime> m_record_times = new HashSet<RecordTime>();
	private HashSet<RecordScore> m_record_score = new HashSet<RecordScore>();
		
	public static String get_opponent_name(int i)
	{
		return Localizer.value("opponent_"+(i+1));
	}
	 private final static String [] THS = { "ST","ND","RD","TH" };
	 public static final String get_suffix(int position, boolean lowercase)
	   {
	     int idx = position;
	     if (idx >= THS.length)
	     {
	       idx = THS.length-1;
	     }
	     else
	     {
	       idx = position-1;
	     }
	     String v = THS[idx];
	     
	     return Localizer.value(lowercase ? v.toLowerCase() : v);

	   }
	
	public RecordScore lookup_score(String levels_name)
	{
		RecordScore rval = null;
		Iterator<RecordScore> it = m_record_score.iterator();
		while (it.hasNext() && rval == null)
		{
			RecordScore r = it.next();
			if (r.matches(levels_name))
			{
				rval = r;
			}
		}
	
		return rval;
	}

	public RecordTime lookup_time(int circuit, String levels_name)
	{
		return lookup_time(circuit,levels_name,true);
	}
	
	public void save() throws IOException
	{
		ParameterParser fw = ParameterParser.create(DirectoryBase.get_user_path()+".sc3_records");
		
		fw.startBlockWrite("SC3_RECORDS");
		int nb_records = 0;
		for (RecordTime r : m_record_times)
		{
			if (!r.is_default_time())
			{
				nb_records++;
			}
		}
		fw.write("nb_records",nb_records);

		for (RecordTime r : m_record_times)
		{
			if (!r.is_default_time())
			{
				r.serialize(fw);
			}
		}

		fw.endBlockWrite();
		
		fw.startBlockWrite("SC3_SCORES");
		
		fw.write("nb_levels",m_record_score.size());
		
		for (RecordScore rs : m_record_score)
		{
			rs.serialize(fw);
		}
		
		fw.endBlockWrite();
		
		fw.close();
	}
	public CourseRecords(Levels levels)
	{
		int nb_levels = levels.size();
		
		for (int lidx = 0; lidx < nb_levels; lidx++)
		{
			CircuitDirectory cd = levels.get_level(lidx);
			
			RecordScore rs = new RecordScore(cd.name);
			
			int max_score = (cd.nb_circuits - 1) * POINTS[0];
			
			for (int i = 0; i < RecordScore.NB_SCORES; i++)
			{
				rs.set(get_opponent_name(i % NB_OPPONENTS),Math.max(max_score - (max_score/RecordScore.NB_SCORES)*i,0));
			}
			
			m_record_score.add(rs);
		}
		
		ParameterParser fr = null;
		try
		{
			fr = ParameterParser.open(DirectoryBase.get_user_path()+".sc3_records");
			fr.startBlockVerify("SC3_RECORDS");
			int nb_records = fr.readInteger("nb_records");
			m_record_times.clear();
			for (int i = 0; i < nb_records; i++)
			{
				m_record_times.add(new RecordTime(fr));
			}
			
			fr.endBlockVerify();
			fr.startBlockVerify("SC3_SCORES");
			
			int nb_levels_in_file = fr.readInteger("nb_levels");
			
			for (int i = 0; i < nb_levels_in_file; i++)
			{
				RecordScore rs = new RecordScore(fr);
				
				RecordScore rs_old = lookup_score(rs.level);
				
				if (rs_old != null)
				{
					m_record_score.remove(rs_old);
				}
				m_record_score.add(rs);
			}
			fr.endBlockVerify();
			
			fr.close();
		}
		catch (IOException e)
		{
			// do nothing
		}
		catch (Exception e)
		{
			if (fr != null)
			{
				fr.close();
			}
		}
		int opidx = 0;
		for (int i = 0; i < levels.size(); i++)
		{
			CircuitDirectory cd = levels.get_level(i);
			for (int j = 0; j < cd.nb_circuits; j++)
			{
				if (lookup_time(j+1,cd.name,false) == null)
				{
					// not in file: create our own
					RecordTime r = new RecordTime();
					r.circuit = j+1;
					r.driver = get_opponent_name(opidx);
					r.level = cd.name;
					
					m_record_times.add(r);
				}
				opidx++;
				if (opidx == NB_OPPONENTS)
				{
					opidx = 0;
				}
			}
		}
	}

	private RecordTime lookup_time(int circuit, String levels_name, boolean avoid_null)
	{
		RecordTime rval = null;
		Iterator<RecordTime> it = m_record_times.iterator();
		while (it.hasNext() && rval == null)
		{
			RecordTime r = it.next();
			if (r.matches(circuit,levels_name))
			{
				rval = r;
			}
		}
		if ((rval == null) && (avoid_null))
		{
			  
			rval = new RecordTime();
			
		}
		return rval;
	}

}
