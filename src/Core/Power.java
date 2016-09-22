package Core;

public abstract class Power extends AbstractDynamicObject {
	int d = 100;
	int[] s;

	public Power(World world, int x, int y, int ray) {
		super(world, x, y, 0, ray);
		s = new int[2];
		s[0] = 1;
		s[1] = -1;
		speedX = (int) ((Math.random() * 6) + 3) * (s[(int) (Math.random() * 2)]);
		speedY = (int) ((Math.random() * 6) + 3) * (s[(int) (Math.random() * 2)]);
	}

	@Override
	public void update() {
		if (d > 0) {
			super.update();
			d--;
		}
	}

}
