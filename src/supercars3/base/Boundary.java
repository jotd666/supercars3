package supercars3.base;

import java.io.IOException;

import supercars3.sys.ParameterParser;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class Boundary extends Segment {
  // slope type
  public static final int SLOPE_NORMAL = 0;
  public static final int SLOPE_BOTTOM = 1;
  public static final int SLOPE_TOP = 2;

  public static final String[] SLOPE_TYPE_STR = {
      "normal", "slope_bottom", "slope_top"};

  private boolean m_fence = false;
  private boolean m_closing_gates = false;
  private int m_closing_percent = 50;
  private int m_closing_period = 2;

  private int m_slope_type = SLOPE_NORMAL;

  public Boundary(Segment s)
  {
    super(s.get_start_point(),s.get_end_point());
  }
public Boundary(ControlPoint a, ControlPoint b)
  {
    super(a,b);
  }
public int get_closing_period() { return m_closing_period; }
  public void set_closing_period(int cp) { m_closing_period = cp; }
  public int get_closing_percent() { return m_closing_percent; }
  public void set_closing_percent(int cp) { m_closing_percent = cp; }
  public boolean is_closing_gates() { return m_closing_gates; }
  public void set_closing_gates(boolean f) { m_closing_gates = f; }
  public boolean is_fence() { return m_fence; }
  public void set_fence(boolean f) { m_fence = f; }

  public int get_slope_type() { return m_slope_type; }
  void set_slope_type(int t) { m_slope_type = t; }
void fill(ParameterParser fr) throws IOException
  {
    fr.startBlockVerify("boundary");
    set_fence(fr.readBoolean("fence"));

    if (!is_fence())
    {
     m_slope_type = fr.readEnumerate("type",SLOPE_TYPE_STR);


     set_closing_gates(fr.readBoolean("closing_gates"));
     if (is_closing_gates())
     {
       set_closing_percent(fr.readInteger("closing_percent"));
       set_closing_period(fr.readInteger("closing_period"));
     }
   }
   fr.endBlockVerify();
 }



  void serialize(ParameterParser fw) throws IOException
  {
    fw.startBlockWrite("boundary");
    fw.write("fence",is_fence());
    if (!is_fence())
    {
      fw.write("type",SLOPE_TYPE_STR[m_slope_type]);
      fw.write("closing_gates",is_closing_gates());
      if (is_closing_gates())
      {
        fw.write("closing_percent",get_closing_percent());
        fw.write("closing_period",get_closing_period());
      }
    }
    fw.endBlockWrite();
  }
}