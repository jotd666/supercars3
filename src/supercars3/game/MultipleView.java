package supercars3.game;

public interface MultipleView extends View
{
	MobileImageSet get_image_set(int idx);
	
	int get_nb_image_sets();
}
