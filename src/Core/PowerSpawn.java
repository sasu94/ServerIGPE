package Core;

import java.util.Random;

public class PowerSpawn extends AbstractStaticObject {

	Power[] p;

	public PowerSpawn(World w) {
		super((int) (Math.random() * w.getWidth()), (int) (Math.random() * w.getHeight()), 100, w);
		p = new Power[2];
		p[0] = new SpeedUp(w, x, y, 100);
		p[1] = new Expand(w, x, y, 100);
	}

	public Power ejectPower() {
		Random r = new Random();
		return p[r.nextInt(2)];

	}

}
