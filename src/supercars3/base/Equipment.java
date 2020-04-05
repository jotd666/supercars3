package supercars3.base;

import supercars3.game.GameOptions;
import supercars3.sys.Localizer;


/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class Equipment
{
	protected static final GameOptions.ReadOnly ro_settings = GameOptions.instance().read_only;
		public final static String [] REPAIR_PARTS = {
		"SPARK PLUGS/RETUNE",
		"COMPLETE EXHAUST",
		"REPLACEMENT GEARBOX",
		"BRAKE DISCS x 4",
		"BODY PANELS/RESPRAY",
		"TYRES x 4",
		"STEERING ADJUSTMENT",
		"SUSPENSION x 4"
	};
	public static final int MAX_HEALTH = ro_settings.car_power;
		
	//private static final int [] MAX_DAMAGE ={ 400,800,600,400,1000,600,800,1000};
	
	private int m_money = 0;
	private boolean m_turbocharged = false;
	private int m_health;
	
	public boolean is_turbocharged()
	{
		return m_turbocharged;
	}
	
	public void reset_health()
	{
		m_health = MAX_HEALTH;
	}
	public void set_health(int h)
	{
		m_health = h;
	}
	
	public void add_health(int h)
	{
		m_health += h;
		if (m_health < 0)
		{
			m_health = 0;
		}
	}
	
	public int get_health()
	{
		return m_health;
	}
	
  public class Accessory
  {
    public Accessory(String name,int max_price, int max_items)
    {
      m_name = Localizer.value(name);
      m_max_price = max_price;
      m_max_items = max_items;
    }
    public Accessory(String name, int max_price)
    {
        this(name,max_price,99);
    }

    
    void set_initial_count(double c)
    {
      m_count = Math.min(c,m_max_items);
    }

    
    void copy_price(Accessory other)
    {
       	m_buy_price = other.m_buy_price;
       	m_sell_price = other.m_sell_price;
    }
    
    void cast_prices()
    {
      double r = Math.random();
      int min_price = m_max_price / 4;
      m_buy_price = (int)Math.round((r * (m_max_price - min_price) + min_price)/10) * 10;
      m_sell_price = (int)Math.round((m_buy_price * 0.75)/10) * 10;
    }

 
    public void freebie(int count)
    {
    	m_count = Math.min(m_max_items, count + m_count);
    }
    
    public void buy()
    {
      if ((m_money >= m_buy_price) && (m_count < m_max_items))
      {
        m_count+=1.0;
        m_money -= m_buy_price;
      }
    }
    public void sell()
    {
      if (m_count>0)
      {
        m_count-=1.0;
        m_money += m_sell_price;
      }
    }

    public void one_less()
    {
    	if (m_count>0)
    	{
    		m_count -= 1.0;
    	}
    }
    public void set_count(double c)
    {
    	m_count = c;
    }
    
    public double get_count()
    {
      return m_count;
    }
    public int get_buy_price()
    {
      return m_buy_price;
    }
    public int get_sell_price()
    {
      return m_sell_price;
    }

    public String get_name()
    {
    	return m_name;
    }
    
    public int get_max_items()
    {
    	return m_max_items;
    }
    
    private String m_name;
    private double m_count = 0;
    private int m_buy_price;
    private int m_sell_price;
    private int m_max_price;
    private int m_max_items;
  }
  
  /* warning: do not change order or values of the weapons/accessories: 
   * that would affect shop */
  
  public enum Item {FRONT_MISSILE ,REAR_MISSILE,HOMER_MISSILE,SUPER_MISSILE,
	  MINE ,ARMOUR ,RAM , ENGINE ,NITRO,NO_WEAPON
  }

  public static final int NB_EQUIPMENTS = Item.values().length;
  public static final int NB_WEAPONS = 6;
   
  private Accessory [] accessory = new Accessory[NB_EQUIPMENTS];
  
 
  public void do_freebie(int level)
  {
	  switch (level)
	  {
	  case 0:
		  break;
	  case 1:
		  accessory[Item.FRONT_MISSILE.ordinal()].freebie(5);
		  break;
	  case 2:
		  accessory[Item.NITRO.ordinal()].freebie(3);		  
		  break;
	  case 3:
		  accessory[Item.SUPER_MISSILE.ordinal()].freebie(5);		  
		  break;
	  case 4:
		  accessory[Item.HOMER_MISSILE.ordinal()].freebie(5);		  
		  break;
	  case 5:
		  accessory[Item.ARMOUR.ordinal()].freebie(2);		  
		  accessory[Item.RAM.ordinal()].freebie(1);		  
		  break;
	  case 6:
		  accessory[Item.REAR_MISSILE.ordinal()].freebie(15);		  
		  break;
	  case 7:
		  accessory[Item.NITRO.ordinal()].freebie(12);		  
		  break;
	  case 8:
		  accessory[Item.HOMER_MISSILE.ordinal()].freebie(8);		  
		  break;
	  case 9:
		  accessory[Item.MINE.ordinal()].freebie(20);		  
		  break;
	  case 10:
		  accessory[Item.HOMER_MISSILE.ordinal()].freebie(10);		  
		  accessory[Item.REAR_MISSILE.ordinal()].freebie(5);		  
		  break;
		  default:
			  break;
		  
	  }
  }
  public Equipment(boolean turbocharged)
  {
	  m_turbocharged = turbocharged;
	  //maxcosts	dc.w	400,600,1500,1000,800,500,3000,1000,5000

	  accessory[Item.FRONT_MISSILE.ordinal()] = new Accessory("F.MISS",400); //220
	  accessory[Item.REAR_MISSILE.ordinal()] = new Accessory("R.MISS",600); //220
	  accessory[Item.HOMER_MISSILE.ordinal()] = new Accessory("HOMER",1500);//1200
	  accessory[Item.SUPER_MISSILE.ordinal()] = new Accessory("SUPER",1000);//900
	  accessory[Item.MINE.ordinal()] = new Accessory("MINE",800); // 500
	  accessory[Item.ARMOUR.ordinal()] = new Accessory("ARMOUR",3000,3); // 2500
	  accessory[Item.RAM.ordinal()] = new Accessory("RAM",1000,3); //900 
	  accessory[Item.ENGINE.ordinal()] = new Accessory("ENGINE",5000,3);
	  accessory[Item.NITRO.ordinal()] = new Accessory("NITRO",500);
	  accessory[Item.NO_WEAPON.ordinal()] = new Accessory("NO_WEAPON",0,0);
	  
	  accessory[Item.ENGINE.ordinal()].set_initial_count(0.0); /* overridden by set_engine */
	  
	  set_health(10000);

	  if (!turbocharged)
	  {
		  // normal
	  
		  accessory[Item.FRONT_MISSILE.ordinal()].set_initial_count(10);
		  accessory[Item.REAR_MISSILE.ordinal()].set_initial_count(10);
	  }
	  else
	  {
		  accessory[Item.FRONT_MISSILE.ordinal()].set_initial_count(99);
		  accessory[Item.REAR_MISSILE.ordinal()].set_initial_count(99);
		  accessory[Item.HOMER_MISSILE.ordinal()].set_initial_count(99);
		  accessory[Item.SUPER_MISSILE.ordinal()].set_initial_count(99);
		  accessory[Item.MINE.ordinal()].set_initial_count(99);
		  accessory[Item.ARMOUR.ordinal()].set_initial_count(3);
		  accessory[Item.RAM.ordinal()].set_initial_count(3);
		  accessory[Item.ENGINE.ordinal()].set_initial_count(3);
		  accessory[Item.NITRO.ordinal()].set_initial_count(99);
		  
	  }
  }

  public Item mounted_front = Item.FRONT_MISSILE;
  public Item mounted_rear = Item.REAR_MISSILE;
  
  public Accessory get_accessory(Item item)
  {
	  return accessory[item.ordinal()];
  }
  // int array: even: damage percent, odd: damage price
  
  public int [] cast_repairs()
  {
  	int [] rval = new int[REPAIR_PARTS.length * 2];
  	
  	// damage assessment
  	
  	int total_damage = ro_settings.car_power - m_health;
  	
  	int damage = total_damage;
  	
  	// distribute all damage to the various repairable parts of the car
  	
  	int nb_parts = Equipment.REPAIR_PARTS.length;
  	
  	for (int i = 0; i < nb_parts; i++)
  	{
  		int local_damage = damage / (nb_parts - i);
  		
  		if (i < nb_parts - 1)
  		{
  			// add an offset

  			local_damage += (MAX_HEALTH/2) * (Math.random() - 0.5) / nb_parts;

  		}
  		else
  		{
  		  	// last one: remainder
			local_damage = damage;
  		}
  		
  		if (local_damage < 0)
  		{
  			local_damage = 0;
  		}
  		
 		rval[i*2] = local_damage;
  		
  		rval[i*2 + 1] = (rval[i*2] / 20) * 10; // TODO randomize
  		
  		// substract assigned damage
  		damage -= local_damage;
  	}
  	
  	return rval;
  }
  public void add_money(int m)
  {
	  if (m_money + m >= 0)
	  {
		  m_money += m;
	  }
  }
  public int get_money()
  {
	  return m_money;
  }

  public double get_engine()
  {
	  return  accessory[Item.ENGINE.ordinal()].m_count;
  }
  /**
   * set at car creation or change (CPU)
   */
  public void set_engine(double value)
  {
	  accessory[Item.ENGINE.ordinal()].m_count = value;
  }

  public void cast_prices()
  {
	  for (Item it : Item.values())
	  {
		  accessory[it.ordinal()].cast_prices();
	  }
  }
  public void copy_prices(Equipment other)
  {
	  for (Item it : Item.values())
	  {
		  accessory[it.ordinal()].copy_price(other.accessory[it.ordinal()]);
	  }
  }

}