package gridworld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import gridworld.Grid.Square;
import gridworld.Grid.Type;

public class Runner {
	final static int ROWS = 20;
	final static int COLS = 20;
	final static int WALLS = 20;
	
	static int generation = 0;
	static boolean hitWall = false;
	
	// true: shows every step
	// false: shows grid at beginning and end of simulation. much faster!
	static boolean useGUI = true;
	// show arrows during simulation
	static boolean showArrows = true;
	// number of times to simulate each time
	final static int runs = 100;
	// number of times to simulate "simulations" times
	final static int iterations = 5;
	
	static double lastActionValue = 0;
	// greek letters
	static double epsilon = .9; // greedy chance
	static double gamma = .8; // discount factor
	static double alpha = .8; // learning rate
	static double lambda = .8; // head value for decay rate
	
	// keeps track of moves to get reward
	static ArrayList<Integer> moveCounts = new ArrayList<Integer>();
	
	public static void main(String[] args) throws IOException {
		Grid g = new Grid(ROWS, COLS, WALLS);
		GridUI gui = new GridUI(g, ROWS, COLS);
		gui.run();
		simulate(g, runs, gui);
		gui.refresh(g);
//		g.printValueGrid();
		gui.refreshWithArrows(g);
		moveCounterInfo();
		
		for (int i=0; i<iterations; i++) {
			reset(g);
			simulate(g, runs, gui);
			gui.refresh(g);
			gui.refreshWithArrows(g);
			//moveCounterInfo();
		}
		
		for (int j=0; j<moveCounts.size(); j+=50) {
			if (j+49 > moveCounts.size()) {
				moveAverageBetween(j, moveCounts.size());
			}
			else
				moveAverageBetween(j, j+49);
		}
		
//		useGUI = true;
//		
//		for (int i=0; i<iterations; i++) {
//			reset(g);
//			simulate(g, runs, gui);
//			gui.refresh(g);
//			gui.refreshWithArrows(g);
//			moveCounterInfo();
//		}
	}
	
	static void moveCounterInfo() {
		System.out.println();
		// average
		int sum = 0;
		for (Integer num : moveCounts)
			sum += num;
		System.out.println("Average moves: " + sum/moveCounts.size());
		//moveCounts.clear();
	}
	
	static void moveAverageBetween(int first, int last) {
		System.out.println();
		// average
		int sum = 0;
		for (int i=first; i<last; i++) {
			sum += moveCounts.get(i);
		}
		System.out.println("Average moves " + first + "-" + last + ": " + sum/(last-first));
		//moveCounts.clear();
	}
	
	static void simulate(Grid g, int iterations, GridUI gui) throws IOException {
		for (int i=0; i<iterations; i++) {
			move(g, gui);
			if (hitWall == true)
				System.out.println("Generation " + generation + ", " + moveCounts.get(moveCounts.size()-1) + " moves, hit wall");
			else
				System.out.println("Generation " + generation + ", " + moveCounts.get(moveCounts.size()-1) + " moves, found reward");
			generation++;
			if (i != iterations-1)
				reset(g);
		}
	}
	
	static void move(Grid g, GridUI gui) throws IOException {
		
		int counter = 0;
		// do per episode
		do {
			// do per step
			counter++;
			hitWall = false;
			Square[] moves = new Square[4];
			Random random = new Random();
			Square action;
			int actionNum;
			
			// up 0
			if (g.playerRow + 1 < g.getR()){
				moves[0] = (g.getGrid()[g.playerRow+1][g.playerCol]);
			}
			else {
				moves[0] = g.getGrid()[g.playerRow][g.playerCol];
			}
			
			// down 1
			if (g.playerRow -1 >= 0){
				moves[1] = (g.getGrid()[g.playerRow-1][g.playerCol]);
			}
			else {
				moves[1] = g.getGrid()[g.playerRow][g.playerCol];
			}
			
			// left 2
			if (g.playerCol - 1 >= 0){
				moves[2] = (g.getGrid()[g.playerRow][g.playerCol-1]);
			}
			else {
				moves[2] = g.getGrid()[g.playerRow][g.playerCol];
			}
			
			// right 3
			if (g.playerCol + 1 < g.getC()){
				moves[3] = (g.getGrid()[g.playerRow][g.playerCol+1]);
			}
			else {
				moves[3] = g.getGrid()[g.playerRow][g.playerCol];
			}
			
			double actionValue;
			// if we decide to be greedy, take best action. if no best action, take first action
			if (random.nextDouble() > epsilon) {
				action = moves[0];
				actionValue = g.getGrid()[g.playerRow][g.playerCol].qtable[0];
				actionNum = 0;
				for (int i=1; i<4; i++) {
					if (g.getGrid()[g.playerRow][g.playerCol].qtable[i] > actionValue) {
						action = moves[i];
						actionValue = g.getGrid()[g.playerRow][g.playerCol].qtable[i];
						actionNum = i;
					}
				}
			}
			// else take random of the four actions
			else {
				int randomMove = random.nextInt(4);
				action = moves[randomMove];
				actionValue = g.getGrid()[g.playerRow][g.playerCol].qtable[randomMove];
				actionNum = randomMove;
			}
			
			// update delta
			double delta = 0;
			if (action.type == Type.REWARD) {
				delta += 1;
			}
			if (action.type == Type.WALL) {
				delta -= 1;
			}
			delta += gamma*actionValue;
			delta -= lastActionValue;
			lastActionValue = actionValue;
			
			// add one to eligibility trace
			g.getGrid()[g.playerRow][g.playerCol].etable[actionNum] += 1;
			
			// for all states and actions
			for (int i=0; i<g.getR(); i++) {
				for (int j=0; j<g.getC(); j++) {
					for (int k=0; k<4; k++) {
						g.getGrid()[i][j].qtable[k] += alpha*delta*g.getGrid()[i][j].etable[k];
						g.getGrid()[i][j].etable[k] = gamma*lambda*g.getGrid()[i][j].etable[k];
					}
				}
			}
			
			// take action
			// if reward
			if (action.type == Type.REWARD) {
				g.getGrid()[g.playerRow][g.playerCol].setType(Type.EMPTY);
				g.foundReward = true;
				action.setType(Type.VICTORY);
			}
			
			// if wall
			if (action.type == Type.WALL) {
//				g.getGrid()[g.playerRow][g.playerCol].setType(Type.EMPTY);
//				hitWall = true; // forces it to reset
			}
			
			// if empty
			if (action.type == Type.EMPTY) {
				g.getGrid()[g.playerRow][g.playerCol].setType(Type.EMPTY);
				g.playerRow = action.sr;
				g.playerCol = action.sc;
				g.getGrid()[g.playerRow][g.playerCol].setType(Type.PLAYER);
			}
			if (useGUI)
				gui.refresh(g);
			//if (showArrows)
				//gui.refreshWithArrows(g);
		} while (g.foundReward == false && hitWall == false);
		//System.out.println(counter + " moves");
		if (g.foundReward == true)
			moveCounts.add(counter);
		if (showArrows)
			gui.refreshWithArrows(g);
		if (counter > 10000)
			gui.refreshWithArrows(g);

		epsilon -= .0001;
		if (epsilon < .1)
			epsilon = .1;
	}
	
	// resets player position
	static void reset(Grid g) {
		g.foundReward = false;
		g.getGrid()[g.rewardRow][g.rewardCol].setType(Type.REWARD);
		
		// reset player
		Random random = new Random();
		g.playerRow = random.nextInt(g.getR());
		g.playerCol = random.nextInt(g.getC());
		while (g.getGrid()[g.playerRow][g.playerCol].getType() != Type.EMPTY) {
			g.setPlayerRow(random.nextInt(g.getR()));
			g.setPlayerCol(random.nextInt(g.getC()));
		}
		g.getGrid()[g.playerRow][g.playerCol].setType(Type.PLAYER);
	}
}
