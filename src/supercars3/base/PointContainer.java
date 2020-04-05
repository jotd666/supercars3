package supercars3.base;

public interface PointContainer
{
	public NamedControlPoint add_point(ControlPoint c);

	public NamedControlPoint get_point(int i);
	
	public int size();
	
	public void remove_point(ControlPoint cp);
	
	public NamedControlPoint lookup_by_name(int point_name);

}
