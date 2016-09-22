package Core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Circle extends AbstractDynamicObject {
	int points;
	Color color;
	Circle toFollow;
	ArrayList<Circle> splitted;

	public Circle(World world) {
		super(world, (int) (Math.random() * world.getWidth()), (int) (Math.random() * world.getHeight()), 0, 10);
		Random rand = new Random();
		color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
		splitted = new ArrayList<>();
	}

	public Circle(int x, int y, int r, World world) {
		super(world, x, y, 0, r);
		Random rand = new Random();
		color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
		splitted = new ArrayList<>();
	}

	public ArrayList<Circle> getSplitted() {
		return splitted;
	}

	public int getPoints() {
		return points;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void setSpeedX(int speedX) {
		super.setSpeedX(speedX);
		splitted.forEach(e -> e.setSpeedX(speedX));
	}

	@Override
	public void setSpeedY(int speedY) {
		super.setSpeedY(speedY);
		splitted.forEach(e -> e.setSpeedY(speedY));
	}

	public Circle split() {
		ray /= 2;

		return new Circle(x + ray, y + ray, ray, world);
	}

	public void eat(AbstractDynamicObject object) {
		if (object instanceof Circle) {
			if (object.ray == 10) {
				this.ray += object.ray / 10;
				this.points += object.ray;
			} else {
				this.ray += object.ray / 2;
				this.points += object.ray;
			}
		}
	}

	public void addPower(Power p) {
		if (p instanceof SpeedUp) {
			speed = SpeedUp.val;
		} else if (p instanceof Expand) {
			if (!expanded) {
				ray *= Expand.val;
				expanded = true;
			}
		}
	}

	@Override
	public void update() {
		super.update();
		splitted.forEach(e -> e.update());
	}

}
