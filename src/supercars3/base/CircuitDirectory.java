package supercars3.base;

import java.io.*;
import java.util.Vector;

import supercars3.sys.ParameterParser;

public class CircuitDirectory
{
	public String name = "unknown";
	public String directory = "unknown";
	public int nb_circuits = 1;
	public int initial_human_engine = 0;
	public float initial_cpu_engine = 0;
	public float final_cpu_engine = 0;
	public static final int MAX_ENGINE = 3;
	public int nb_front_weapons = 10;
	public int nb_rear_weapons = 10;
	public int [] aggressivity;
	
	private void check_engine_range(float v,String value_type) throws Exception
	{
		if ((v < 0) || (v > MAX_ENGINE))
		{
			throw new Exception("engine value out of range for "+value_type);
		}
	}
	private void check_weapon_range(float v,String value_type) throws Exception
	{
		if (v < 0)
		{
			throw new Exception("weapon count value out of range for "+value_type);
		}
	}
	
	public double get_cpu_engine(int circuit_number)
	{
		return ((final_cpu_engine - initial_cpu_engine) * (circuit_number-1) / (double)nb_circuits) + 
		initial_cpu_engine;
	}
	public void set_initial_human_engine(int v) throws Exception
	{
		check_engine_range(v,"initial human engine");
		initial_human_engine = v;
	}
	public void set_initial_cpu_engine(float v) throws Exception
	{
		check_engine_range(v,"initial CPU engine");
		initial_cpu_engine = v;
	}
	public void set_final_cpu_engine(float v) throws Exception
	{
		check_engine_range(v,"final CPU engine");
		final_cpu_engine = v;
	}
	public void set_cpu_nb_front_weapons(int v) throws Exception
	{
		check_weapon_range(v,"front weapons");
		nb_front_weapons = v;
	}
	public void set_cpu_nb_rear_weapons(int v) throws Exception
	{
		check_weapon_range(v,"rear weapons");
		nb_rear_weapons = v;
	}
	
	
	public CircuitDirectory()
	{
		
	}
	
	private CircuitDirectory(File candidate) throws IOException
	{
		directory = candidate.getAbsolutePath();

		ParameterParser fr = ParameterParser.open
		(candidate + File.separator + "info.sc3");
		
		fr.startBlockVerify("LEVEL_INFORMATION");
		name = fr.readString("name");
		nb_circuits = fr.readInteger("nb_circuits");
		fr.startBlockVerify("human");
		initial_human_engine = fr.readInteger("initial_engine");
		fr.endBlockVerify();
		
		fr.startBlockVerify("cpu");
		initial_cpu_engine = fr.readFloat("initial_engine");
		final_cpu_engine = fr.readFloat("final_engine");
		nb_front_weapons = fr.readInteger("nb_front_weapons");
		nb_rear_weapons = fr.readInteger("nb_rear_weapons");
		aggressivity = new int[nb_circuits];
		fr.readVector("aggressivity", aggressivity);
		fr.endBlockVerify();
	}
	
	public void serialize() throws IOException
	{
		File d = new File(directory);
		if (!d.isAbsolute())
		{
			directory = DirectoryBase.get_circuit_path() + directory;
		}
		
		ParameterParser fw = ParameterParser.create
		(directory + File.separator + "info.sc3");

		fw.startBlockWrite("LEVEL_INFORMATION");
		fw.write("name",name);
		fw.write("nb_circuits",nb_circuits);
		fw.startBlockWrite("human");
		fw.write("initial_engine",initial_human_engine);
		fw.endBlockWrite();
		
		fw.startBlockWrite("cpu");
		fw.write("initial_engine",initial_cpu_engine);
		fw.write("final_engine",final_cpu_engine);
		fw.write("nb_front_weapons",nb_front_weapons);
		fw.write("nb_rear_weapons",nb_rear_weapons);
		fw.write("aggressivity",aggressivity);
		fw.endBlockWrite();

		fw.close();
	}
	public static CircuitDirectory build_for_audit(String filename) throws Exception
	{
		CircuitDirectory rval = null;

		File candidate = new File(DirectoryBase.get_circuit_path() +
				filename);
		if (candidate.isDirectory())
		{    		
			rval = new CircuitDirectory(candidate);    			
		}


    	return rval;
    }	
	public static CircuitDirectory build(String filename)
	{
		CircuitDirectory rval = null;
		try
		{
			rval = build_for_audit(filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
    	
    	return rval;
    }
	
	public Vector<String> audit()
	{
		Vector<String> v = new Vector<String>();
		CircuitData data = new CircuitData(this);
		
		for (int i = 0; i < nb_circuits;i++)
		{
			String circuit_name = directory + File.separator+(i+1)+".sc3";
			v.add("--> Loading "+circuit_name);
			
			try
			{
				data.unchecked_load(circuit_name, false);
				v.addAll(data.report_errors());
			}
			catch (IOException e)
			{
				v.add(e.getMessage());
			}
		}
		return v;
	}
}
