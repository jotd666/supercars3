package supercars3.game;

import java.io.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;

import javax.imageio.ImageIO;

import supercars3.game.cars.Car;
import supercars3.sys.GameFont;
import supercars3.sys.Localizer;
import supercars3.base.CourseRecords;
import supercars3.base.DirectoryBase;
import supercars3.base.Equipment;


public class DashBoard 
{
  private BufferedImage m_back;
  private Car m_car;
  private Rectangle m_health;
  private Rectangle m_speed;
  
  private boolean m_dual_player;
  private GameFont m_letter_font = SCGame.NORMAL_BITMAP_FONT;
  private GameFont m_number_font = SCGame.NUMBER_BITMAP_FONT;

  private AffineTransform m_translation;

  private int m_x,m_y;
  
  private static final int [] Y_ITEMS_1P = {14,40,/*speed+damage*/
      8,36/*pos+laps*/
};
  private static final int [] X_ITEMS_1P = { 68,40,504,478,/*speed, damage*/
	  										 222,200,374,388,/*pos, laps*/
};
  private static final int [] Y_ITEMS_2P = {-2,20,/*speed+damage*/
      32,40 /*pos+laps*/
};
  private static final int [] X_ITEMS_2P = { 48,16,184,160,/*speed, damage*/
	  										 66-50,38+20,196-50,212+20,/*pos, laps*/
};
  private int [] x_items = X_ITEMS_1P;
  private int [] y_items = Y_ITEMS_1P;

  public void render(Graphics2D g)
  {
    AffineTransform at = g.getTransform();

    g.setTransform(m_translation);


    g.drawImage(m_back, 0, 0, null);

    int position = m_car.get_position();

    m_letter_font.write_line(g,Localizer.value("SPEED"),x_items[0],y_items[0],-1,false,false);
    m_letter_font.write_line(g,Localizer.value("POS"),x_items[4],y_items[2],-1,false,false);
    m_letter_font.write_line(g,Localizer.value("LAPS"),x_items[6],y_items[2],-1,false,false);
    Rectangle r1 = m_letter_font.write_line(g,Localizer.value("DAMAGE"),x_items[2],y_items[0],-1,false,false);

    m_number_font.write_line(g,m_car.get_laps_to_go()+"",x_items[7],y_items[3],0,false,false);

    String text = "" + position;
    Rectangle r2 = m_number_font.text_position(text, x_items[5],y_items[3], 0, false, false);
    m_number_font.write_line(g,text,0,r2);

    m_letter_font.write_line(g, CourseRecords.get_suffix(position,false),r2.x+r2.width,
    		r2.y+r2.height-r1.height+4,0,false,false);
      
      // fill health & speed
      g.setColor(Color.BLACK);    
      g.fill(m_speed);
      g.fill(m_health);


    Color bar_color = Color.BLUE;
    
    if (GameOptions.instance().debug_mode &&  m_car.get_current().skid)
    {
    	bar_color = Color.CYAN;
    }
    
    g.setColor(bar_color);
    fill_bar(g,m_speed,0,m_car.get_current().get_speed(),1.0);
    g.setColor(Color.RED);
    double hv = ((double)m_car.get_health())/Equipment.MAX_HEALTH;
    fill_bar(g,m_health,0.0,hv,0.08);
    g.setColor(Color.GREEN);
    fill_bar(g,m_health,0.08,hv,0.24);
    g.setColor(Color.YELLOW);
    fill_bar(g,m_health,0.24,hv,1.0);

    g.setTransform(at);
  }
  
	  									
  public void set_car(Car c)
  {
	  m_car = c;
  }
public DashBoard(Car car,Rectangle rect, boolean dual_player)
  {
   	m_back = new BufferedImage((int)rect.getWidth(),(int)rect.getHeight(),
			BufferedImage.TYPE_INT_RGB);
   	
    try
    {
    	BufferedImage wood = ImageIO.read(new File(DirectoryBase.get_images_path() + "wood.jpg"));

 
    	m_car = car;

    	m_dual_player = dual_player;
    	if (m_dual_player)
    	{
    		x_items = X_ITEMS_2P;
    		y_items = Y_ITEMS_2P;
    	}

    	Graphics g = m_back.getGraphics();

    	int x=0;
    	int step = wood.getWidth();

    	g.setColor(Color.BLACK);
    	g.drawRect(0,0,m_back.getWidth(),2);

    	while (x<m_back.getWidth())
    	{
    		g.drawImage(wood, x, 2, null);
    		x+=step;
    	}
    }
    catch (IOException e)
    {
    }

    m_x = (int)rect.getX();
    m_y = (int)rect.getY();
      
    m_speed = new Rectangle(x_items[1],y_items[1],128,14); // width = 64*2
  
    m_health = new Rectangle(x_items[3],
    		y_items[1],(int)m_speed.getWidth(),(int)m_speed.getHeight());
    
    m_translation = AffineTransform.getTranslateInstance(m_x,m_y);
  }
  static private void fill_bar(Graphics g,Rectangle2D r,double min, double max,double limit)
  {
	  if (min<max)
	  {
    g.fillRect((int)(r.getX()+2 + (r.getWidth()-4) * min),(int)r.getY()+2,
                 (int)((r.getWidth()-4) * (Math.min(max,limit) - min)),
                 (int)r.getHeight()-4);
	  }

  }
 
}