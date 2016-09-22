package Core;

import java.awt.geom.Point2D;

public abstract class AbstractStaticObject implements StaticObject {
	int x, y, ray;
	World world;

	public AbstractStaticObject(int x, int y, int r, World w) {
		this.x = x;
		this.y = y;
		ray = r;
		world = w;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public void setX(int x) {
		if (x < 0)
			this.x = 0;
		else if (x > world.getWidth())
			this.x = world.getWidth();
		else
			this.x = x;
	}

	public void setY(int y) {
		if (y < 0)
			this.y = 0;
		else if (y > world.getHeight())
			this.y = world.getHeight();
		else
			this.y = y;
	}

	@Override
	public int getRay() {
		return ray;
	}

	@Override
	public boolean intersect(StaticObject other) {
		return (Point2D.distance(x, y, other.getX(), other.getY()) < ray);
	}

}
