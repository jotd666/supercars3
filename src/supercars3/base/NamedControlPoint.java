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

public class NamedControlPoint extends ControlPoint implements Nameable {
   private int m_name = 0;

   public NamedControlPoint(ParameterParser fw, double scale) throws IOException
   {
     fw.startBlockVerify("point");
     m_name = fw.readInteger("name");
     m_x = (int)(fw.readInteger("x") * scale);
     m_y = (int)(fw.readInteger("y") * scale);
     set_resume_point(fw.readBoolean("resume"));
     set_car_start_point(fw.readBoolean("car_start"));
     fw.endBlockVerify();
   }

   public NamedControlPoint(ControlPoint cp,int name)
   {
     this(cp.getX(),cp.getY(),name,cp.is_car_start_point(),cp.is_resume_point());
   }
   public NamedControlPoint(int x,int y,int name,boolean car_start_point,boolean resume_point)
   {
     super(x,y);
     m_name = name;
     set_car_start_point(car_start_point);
     set_resume_point(resume_point);
   }
   public int get_name()
   {
     return m_name;
   }

public void set_name(int name) { m_name = name; }

public void serialize(ParameterParser fw) throws IOException
   {
     fw.startBlockWrite("point");
     fw.write("name",get_name());
     fw.write("x",getX());
     fw.write("y",getY());
     fw.write("resume",is_resume_point());
     fw.write("car_start",is_car_start_point());
     fw.endBlockWrite();
   }
}