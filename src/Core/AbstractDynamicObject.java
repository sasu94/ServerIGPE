package Core;

public abstract class AbstractDynamicObject extends AbstractStaticObject implements DynamicObject {

	int speedX, speedY;
	int turnsSpeed = 500;
	int turnsExpand = 500;
	boolean expanded = false;
	int speed = 1;

	public AbstractDynamicObject(final World world, final int x, final int y, final int speed, int ray) {
		super(x, y, ray, world);
	}

	@Override
	public int getSpeed() {
		return speed;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public int getSpeedX() {
		return speedX;
	}

	public int getSpeedY() {
		return speedY;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	@Override
	public void update() {
		if (turnsSpeed > 0) {
			this.setX(this.getX() + speedX * speed);
			this.setY(this.getY() + speedY * speed);
			if (speed != 1)
				turnsSpeed--;
		} else {
			speed = 1;
			turnsSpeed = 500;
		}

		if (turnsExpand > 0 && expanded) {
			turnsExpand--;
		} else if (turnsExpand == 0 && expanded) {
			turnsExpand = 500;
			ray /= 2;
			expanded = false;
		}
	}
}
