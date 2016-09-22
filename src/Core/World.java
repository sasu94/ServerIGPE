package Core;

public class World {
	private final int width;
	private final int height;

	public World() {
		width = 5000;
		height = 5000;
	}

	public World(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	@Override
	public String toString() {
		return "World [width=" + width + ", height=" + height + "]";
	}
}
