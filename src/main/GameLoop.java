import java.io.InputStream;
import java.util.*;

public class GameLoop {

	Main graphics;
	ArrayList<Situation> situations = new ArrayList<>();
	int[] resources = new int[4], oldResources = new int[4];
	int day = 1;
	public HashMap<String, String> placeholders = new HashMap<>();
	Random rand = new Random();
	HashMap<String, LinkedList<Situation>> plotLines = new HashMap<>();
	public  String currentPlot = "NORMAL";
	private Situation situation;

	public GameLoop(Main input, String selectedName) {
		// INITIALISE STARTING VARIABLES
		graphics = input;
		placeholders.put("<name>", selectedName);
		createResources();
		createAllSituations();

		findNewSituation();
	}

	private void findNewSituation() {
		if (currentPlot.equals("NORMAL")) {
			int randNumber = rand.nextInt(situations.size());
			situation = situations.get(randNumber);
		} else {
			LinkedList<Situation> queue = plotLines.get(currentPlot);
			situation = queue.poll();
			plotLines.put(currentPlot, queue);
		}
		graphics.presentSituation(situation, resources, oldResources, placeholders, day);
	}

	public void play(int option) {
		oldResources = resources.clone();

		// SELECT AN OPTION
		if (option == 1) {
			situation.press1(resources, this);
		} else if (option == 2) {
			situation.press2(resources, this);
		}
		day++;

		// CHECK IF PLAYER LOST
		boolean lost = false;
		String lossReason = "";
		if (resources[Resource.HAPPINESS.id()] <= 0) {
			lost = true;
			lossReason = "Your people revolted, broke into your residence, and killed you.";
		}
		if (resources[Resource.MILITARY.id()] <= 0) {
			lost = true;
			lossReason = "Your kingdom was invaded. The military was too weak to hold them back";

		}
		if (resources[Resource.ECONOMY.id()] <= 0) {
			lost = true;
			lossReason = "With no gold left in the treasury, your kingdom went bankrupt.";
		}
		if (resources[Resource.RELIGION.id()] <= 0) {
			lost = true;
			lossReason = "The church excommunicated you, displeased of your actions.";

		}

		// PRESENT NEW SITUATION IF HE IS STILL ALIVE
		if (!lost) {
			findNewSituation();
		} else {
			// PRESENT LOSS
			graphics.deathScreen(lossReason);
		}
	}
	
	private void createResources() {
		// SET EVERY RESOURCE TO 50
		for (int i = 0; i < 4; i++) {
			resources[i] = 50;
			oldResources[i] = 50;
		}
	}
	
	private void createAllSituations() {
		// REGULAR SITUATIONS
		initSubsituations("Basic", 7, 2);
		initSubsituations("Harsh", 1, 4);
		initSubsituations("Luck", 4, 1);
		initSubsituations("Plot", 1, 4);

		// PLOT LINES
		initPlotLine("Invasion", 4);
		initPlotLine("InvasionSurrenderAccepted", 1);
		initPlotLine("InvasionSurrenderDenied", 1);
	}
	
	private void initSubsituations(String name, int amount, int weight) {
		for (int i = 1; i <= amount; i++) {
			InputStream stream = this.getClass().getResourceAsStream("situations/" + name + "/" + name + i + ".txt");
			Situation situation = new Situation(stream);
			for (int x = 0; x < weight; x++) {
				situations.add(situation);
			}
		}
	}

	private void initPlotLine(String line, int amount) {
		LinkedList<Situation> queue = new LinkedList<>();
		for (int i = 1; i <= amount; i++) {
			InputStream stream = this.getClass().getResourceAsStream("situations/" + line + "/" + line + i + ".txt");
			Situation situation = new Situation(stream);
			queue.offer(situation);
		}
		plotLines.put(line, queue);
	}
}
