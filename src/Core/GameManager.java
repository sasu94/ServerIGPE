package Core;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameManager {
	World world;
	Map<String, Circle> players;
	Set<Circle> enemies;
	private CopyOnWriteArrayList<Circle> plancton;
	private Set<Circle> splitted;
	Set<PowerSpawn> spawns;
	Set<Power> powers;
	private Set<Circle> toRemove;
	public boolean difficulty;
	Runnable runnable;
	public boolean started = true;
	boolean gameOver;

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean b) {
		gameOver = b;
	}

	public Set<Power> getPowers() {
		return powers;
	}

	public Set<PowerSpawn> getSpawns() {
		return spawns;
	}

	public World getWorld() {
		return world;
	}

	public CopyOnWriteArrayList<Circle> getPlancton() {
		return plancton;
	}

	public void setPlancton(CopyOnWriteArrayList<Circle> plancton) {
		this.plancton = plancton;
	}

	public Set<Circle> getEnemies() {
		return enemies;
	}

	public Set<Circle> getSplitted() {
		return splitted;
	}

	public void setSplitted(Set<Circle> splitted) {
		this.splitted = splitted;
	}

	public GameManager() {
	}

	public void start(Runnable runnable, List<String> names) {
		this.runnable = runnable;
		gameOver = false;
		world = new World(5000, 5000);
		players = new HashMap<String, Circle>();

		splitted = new HashSet<>();
		enemies = new HashSet<>();
		plancton = new CopyOnWriteArrayList<>();
		spawns = new HashSet<>();
		powers = new HashSet<>();
		toRemove = new HashSet<>();

		boolean b = false;

		for (int i = 0; i < 20; i++) {
			Circle c = new Circle(world);
			c.ray = (int) (Math.random() * 250 + 10);
			for (Circle circle : enemies) {
				while (b != true) {
					if (Point2D.distance(c.x, c.y, circle.x, circle.y) < c.ray + circle.ray)
						c = new Circle(world);
					else
						b = true;
				}
			}
			enemies.add(c);

		}

		for (int i = 0; i < 1000; i++) {
			plancton.add(new Circle(world));

		}

		for (int i = 0; i < 10; i++) {
			spawns.add(new PowerSpawn(world));
		}

		for (String s : names) {
			Circle player = new Circle(world);
			player.ray = 50;
			while (!createPlayer(enemies, player))
				player = new Circle(world);
			player.ray = 50;
			players.put(s, player);
		}

	}

	public void update() {
		if (started && !gameOver) {
			complexAI();
			players.forEach((s, p) -> p.update());
			powers.forEach(p -> {
				p.update();
			});

			for (Iterator<Circle> iterator = enemies.iterator(); iterator.hasNext();) {
				Circle circle = iterator.next();
				circle.update();
				checkEat(plancton, circle);
				checkEatPower(powers, circle);
				checkEatSpawns(spawns, circle);
				toRemove.addAll(checkEatBeetween(enemies, circle));
			}
			enemies.removeAll(toRemove);
			toRemove.clear();

			players.forEach((s, p) -> {
				checkEatPlayer(p, s);
				p.getSplitted().forEach(e -> checkEatPlayer(e, s));

			});

			for (PowerSpawn s : spawns) {
				if (Math.random() < 0.0001) {
					Power p = s.ejectPower();
					if (p instanceof SpeedUp)
						powers.add(new SpeedUp(world, s.x, s.y, s.ray / 4));
					else if (p instanceof Expand)
						powers.add(new Expand(world, s.x, s.y, s.ray / 4));
				}
			}

			if (runnable != null) {
				runnable.run();
			}
		}
	}

	private boolean createPlayer(Set<Circle> e, Circle p) {
		for (Circle enemy : e)
			if (Point2D.distance(p.x, p.y, enemy.x, enemy.y) < (p.ray + enemy.ray + 100)) {
				return false;
			}
		return true;
	}

	private void checkEatPlayer(Circle c, String s) {

		gameOver = gameOver || checkEat(enemies, c);
		checkEat(plancton, c);
		checkEatPower(powers, c);
		checkEatSpawns(spawns, c);
		for (String name : players.keySet())
			if (s != name)
				if (players.get(s).intersect(players.get(name))) {
					if (players.get(s).ray > players.get(name).ray) {
						players.get(s).eat(players.get(name));
					} else {
						players.get(name).eat(players.get(s));
					}
					gameOver = gameOver || true;
				}
	}

	private void checkEatSpawns(Set<PowerSpawn> spawns, Circle p) {
		for (Iterator<PowerSpawn> iteratorCircle = spawns.iterator(); iteratorCircle.hasNext();) {
			PowerSpawn circle = iteratorCircle.next();
			if (p.intersect(circle) || circle.intersect(p)) {
				p.ray = p.ray / 2;
				iteratorCircle.remove();
				p.expanded = false;
			}
		}

	}

	private void checkEatPower(Set<Power> s, Circle p) {
		for (Iterator<Power> iteratorCircle = s.iterator(); iteratorCircle.hasNext();) {
			Power circle = iteratorCircle.next();
			if (p.intersect(circle) || circle.intersect(p)) {
				p.addPower(circle);
				iteratorCircle.remove();
			}
		}
	}

	private boolean checkEat(CopyOnWriteArrayList<Circle> s, Circle p) {
		for (Circle circle : s) {
			if (p.intersect(circle) || circle.intersect(p)) {
				if (p.ray > circle.ray) {
					p.eat(circle);
					s.remove(circle);
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkEat(Set<Circle> s, Circle p) {
		for (Iterator<Circle> iteratorCircle = s.iterator(); iteratorCircle.hasNext();) {
			Circle circle = iteratorCircle.next();
			if (p.intersect(circle) || circle.intersect(p)) {
				if (p.ray > circle.ray) {
					p.eat(circle);
					iteratorCircle.remove();
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private Set<Circle> checkEatBeetween(Set<Circle> s, Circle p) {
		Set<Circle> set = new HashSet<>();
		for (Iterator<Circle> iteratorCircle = s.iterator(); iteratorCircle.hasNext();) {
			Circle circle = iteratorCircle.next();
			if (p.intersect(circle) || circle.intersect(p)) {
				if (p.ray > circle.ray) {
					p.eat(circle);
					set.add(circle);
				}
			}
		}
		return set;
	}

	private void complexAI() {
		for (Circle c1 : enemies) {
			c1.toFollow = players.values().iterator().next();
			players.forEach((s, p) -> {
				if (p.ray < c1.toFollow.ray)
					c1.toFollow = p;
			});
			for (Circle c2 : enemies) {
				if (c2.ray < c1.ray) {
					if (c2.ray > c1.toFollow.ray) {
						c1.toFollow = c2;
					}
				}
			}
		}

		for (Circle circle : enemies) {
			if (((circle.toFollow.x - circle.x) / circle.ray) > 0)
				circle.setSpeedX((circle.toFollow.x - circle.x) / circle.ray);
			else
				circle.setSpeedX((circle.toFollow.x - circle.x) / 150);
			if (((circle.toFollow.y - circle.y) / circle.ray) > 0)
				circle.setSpeedY((circle.toFollow.y - circle.y) / circle.ray);
			else
				circle.setSpeedY((circle.toFollow.y - circle.y) / 150);
		}
	}

	public void SimpleIA() {
		int minRay, xmin, ymin;
		Circle first = players.values().iterator().next();
		minRay = first.ray;
		xmin = first.x;
		ymin = first.y;

		for (Circle c : players.values()) {
			if (c.ray < minRay) {
				minRay = c.ray;
				xmin = c.x;
				ymin = c.y;
			}
		}
		for (Circle circle : enemies) {
			if (circle.ray < minRay) {
				minRay = circle.ray;
				xmin = circle.x;
				ymin = circle.y;
			}
		}

		for (Circle circle : enemies) {
			if (!(circle.ray == minRay)) {
				if (((xmin - circle.x) / circle.ray) > 0)
					circle.setSpeedX((xmin - circle.x) / circle.ray);
				else
					circle.setSpeedX((xmin - circle.x) / 150);
				if (((ymin - circle.y) / circle.ray) > 0)
					circle.setSpeedY((ymin - circle.y) / circle.ray);
				else
					circle.setSpeedY((ymin - circle.y) / 150);
			}
		}
	}

	public void startNetworkGame(Runnable runnable, List<String> names) {
		start(null, names);
	}

	public String statusToString() {
		StringBuilder sb = new StringBuilder();
		sb.append(isGameOver() + ";");
		players.forEach((s, p) -> {
			sb.append(s + " " + p.x + " " + p.y + " " + p.ray + " " + p.color.getRed() + " " + p.color.getGreen() + " "
					+ p.color.getBlue() + " " + p.points + " " + p.expanded + " " + p.speed + ";");
		});
		if (enemies.size() == 0)
			sb.append("_");
		else
			enemies.forEach(e -> {
				sb.append(e.x + " " + e.y + " " + e.ray + " " + e.color.getRed() + " " + e.color.getGreen() + " "
						+ e.color.getBlue() + ",");
			});
		sb.append(";");
		if (spawns.size() == 0)
			sb.append("_");
		else
			spawns.forEach(s -> {
				sb.append(s.x + " " + s.y + " " + s.ray + ",");
			});
		sb.append(";");
		if (powers.size() == 0)
			sb.append("_");
		else
			powers.forEach(p -> {
				if (p instanceof SpeedUp)
					sb.append("s " + p.x + " " + p.y + " " + p.ray + ",");
				else
					sb.append("e " + p.x + " " + p.y + " " + p.ray + ",");

			});
		sb.append(";");
		if (plancton.size() == 0)
			sb.append("_");
		else
			plancton.forEach(p -> {
				sb.append(p.x + " " + p.y + " " + p.ray + " " + p.color.getRed() + " " + p.color.getGreen() + " "
						+ p.color.getBlue() + ",");
			});
		sb.append(";");
		return sb.toString();

	}

	public void statusFromString(String message) {
		String[] p = message.split(" ");
		getPlayer(p[0]).setSpeedX(Integer.parseInt(p[1]));
		getPlayer(p[0]).setSpeedX(Integer.parseInt(p[2]));
	}

	public Circle getPlayer(String nick) {
		return players.get(nick);

	}

}
