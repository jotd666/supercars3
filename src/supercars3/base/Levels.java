package supercars3.base;

import java.io.File;
import java.util.Vector;

public class Levels
{
	private Vector<CircuitDirectory> m_list = new Vector<CircuitDirectory>();
	
	public Levels() throws Exception
	{
	    File f = new File(DirectoryBase.get_circuit_path());
	    String [] items = f.list();
		
	    for (int i = 0; i < items.length; i++)
	    {
	    	CircuitDirectory cd = CircuitDirectory.build(items[i]);
	    	
	    	if (cd != null)
	    	{
	    		m_list.add(cd);
	    	}
	    }
	    
	    if (m_list.size() == 0)
	    {
	    	throw new Exception("no valid circuit directory found in "+DirectoryBase.get_circuit_path());
	    }
	}
	
	/**
	 * 
	 * @return number of available circuit directories
	 */
	public int size()
	{
		return m_list.size();
	}
	
	public CircuitDirectory get_level(int i)
	{
		return m_list.elementAt(i);
	}
	
	public String [] get_names()
	{
		String [] rval = new String[m_list.size()];
		
		for (int i = 0; i < rval.length; i++)
		{
			CircuitDirectory cd = m_list.elementAt(i);
			// clone
			rval[i] = new String(cd.name);
		}
		
		return rval;
	}
}
