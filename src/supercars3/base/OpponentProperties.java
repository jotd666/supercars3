package supercars3.base;

public class OpponentProperties
{
	private static final int SPEED_MULTIPLIER = 32;
	private static final double SPEED_RESOLUTION = 1.0/64.0;
	/*                                            -1 0  1  2  3  4 ... */
	private static final double SPEED_TABLE[] = { 15,24,35,38,40,44,50,55,64}; // fictuous values
	private static final long JUMP_TABLE[] =  
	{ 200, 200, 200, 250, 300, // 0 - 9
	  400, 500, 600, 640, 650, // 10 - 19
	  660, 670, 680, 680, 690, // 20 - 29
	  692, 694, 700, 710, 720, // 30 - 39 
	  730, 730, 750, 800, 830};// 40 - ...
	
	public int car_front_weapon = Equipment.Item.NO_WEAPON.ordinal();
	public int car_rear_weapon = Equipment.Item.NO_WEAPON.ordinal();
	public double train_engine = 1.0;
	public int max_wagons = 1;
	public int min_wagons = 1;
	public int train_wait = 2000;
	public int train_offset = 0; // future use
	public boolean modified = false;
	
	  private CircuitDirectory m_directory;

	  // information inherited from level (circuit set)
	  
	  public int nb_front_weapons = 0;
	  public int nb_rear_weapons = 0;
	  
	  public OpponentProperties(CircuitDirectory dir)
	  {
		  m_directory = dir;
		  
		  if (m_directory != null)
		  {
			  // game context
			  nb_front_weapons = m_directory.nb_front_weapons;
			  nb_rear_weapons = m_directory.nb_rear_weapons;
		  } // else editor context: we don't need the information
	  }
	  
	  public double get_train_linear_speed()
	  {
		  return convert_speed(train_engine);
	  }
	  
	  /**
	   * convert engine value into maximum speed
	   * @param engine_value
	   * @return maximum speed
	   */
	  
	  public static final double convert_speed(double engine_value)
	  {		  
	  	double engine_speed_low = SPEED_TABLE[(int)engine_value+1];
	  	double engine_speed_high = SPEED_TABLE[(int)engine_value+2];
	  	
	  	return SPEED_RESOLUTION * ((engine_value - (int)engine_value) * 
			(engine_speed_high - engine_speed_low) + engine_speed_low);
	  }
	  
	  /**
	   * convert speed value into jump duration in milliseconds
	   * @param speed_value
	   * @return
	   */
	  
	  public static final long convert_jump(double speed_value)
	  {		  
		  int idx = (int)(speed_value * SPEED_MULTIPLIER);
		  
		  if (idx >= JUMP_TABLE.length)
		  {
			  idx = JUMP_TABLE.length-1;
		  }
		  
	  	return (int)(JUMP_TABLE[idx]*0.80);
	  }

}
