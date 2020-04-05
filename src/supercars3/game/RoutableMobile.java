package supercars3.game;

import supercars3.base.Route;
import supercars3.base.CircuitData;
import supercars3.game.Mobile.GenericParameters;

public interface RoutableMobile
{
	Route get_route();
	void set_route(Route r);
	GenericParameters get_current();
	GenericParameters get_predicted();
	CircuitData get_circuit_data();
	void handle_brake();
	void die(int damage);

}
