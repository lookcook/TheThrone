import java.io.InputStream;
import java.util.Collections;
import java.util.Scanner;

public class Situation {

	Scanner in;
	String name, messenger, text, option1, option2, effect1, effect2;
	
	public Situation(InputStream stream) {
		in = new Scanner(stream);
		name = in.nextLine();
		messenger = in.nextLine();
		text = in.nextLine();
		option1 = in.nextLine();
		option2 = in.nextLine();
		effect1 = in.nextLine().replace("NONE", "");
		effect2 = in.nextLine().replace("NONE", "");
	}
	
	public int[] press1(int[] resources, GameLoop gameLoop) {
		return selectOption(resources, effect1, gameLoop);
	}
	
	public int[] press2(int[] resources, GameLoop gameLoop) {
		return selectOption(resources, effect2, gameLoop);
	}
	
	public int[] selectOption(int[] resources, String effect, GameLoop gameLoop) {
		int[] newResources = resources;
		for (String string : effect.split(", ")) {
			if (!string.equals("")) {
				String[] list = string.split(" ");
				if (list[0].equals("PLOTLINE")) {
					gameLoop.currentPlot = list[1];
					if (gameLoop.currentPlot.equals("NORMAL")) {
						gameLoop.situations.removeAll(Collections.singleton(this));
					}
				} else if (list[0].equals("DEATH")) {
					gameLoop.graphics.deathScreen(list[1].replace("_", " "));
				} else {
					int resourceNumber = Resource.valueOf(list[0]).id();
					int value = newResources[resourceNumber] + Integer.parseInt(list[1]);
					if (value > 100) {
						value = 100;
					}
					newResources[resourceNumber] = value;
				}
			}
		}
		return newResources;
	}
}
