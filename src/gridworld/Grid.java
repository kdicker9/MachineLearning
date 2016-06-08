package gridworld;

import java.util.Random;

public class Grid {
	Square[][] grid;
	int r;
	int c;
	int playerRow = 0;
	int playerCol = 0;
	int rewardRow = 0;
	int rewardCol = 0;
	boolean foundReward = false;
	
	public Grid(int rows, int cols, int numWalls) {
		this.r = rows; this.c = cols;
		this.grid = generateGrid(rows, cols, numWalls);
		
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				if (grid[i][j].getType() == Type.PLAYER) {
					playerRow = i;
					playerCol = j;
				}
				if (grid[i][j].getType() == Type.REWARD) {
					rewardRow = i;
					rewardCol = j;
				}
			}
		}
	}
	
	private static Square[][] generateGrid(int rows, int cols, int numWalls) {
		Square[][] newGrid = new Square[rows][cols];
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				newGrid[i][j] = new Square();
				newGrid[i][j].sr = i;
				newGrid[i][j].sc = j;
			}
		}
		Random random = new Random();
		
		// places reward randomly
		int rewardRow = random.nextInt(rows);
		int rewardCol = random.nextInt(cols);
		
		newGrid[rewardRow][rewardCol].setType(Type.REWARD);
		newGrid[rewardRow][rewardCol].setReward(1);
		
		// places walls randomly
		for (int i=0; i<numWalls; i++) {
			int wallRow = random.nextInt(rows);
			int wallCol = random.nextInt(rows);
			while (newGrid[wallRow][wallCol].getType() != Type.EMPTY) {
				wallRow = random.nextInt(rows);
				wallCol = random.nextInt(rows);
			}
			newGrid[wallRow][wallCol].setType(Type.WALL);
			newGrid[wallRow][wallCol].setReward(-1);
		}
		
		// places player randomly not where reward or walls are
		int playerRow = random.nextInt(rows);
		int playerCol = random.nextInt(cols);
		while (newGrid[playerRow][playerCol].getType() != Type.EMPTY) {
			playerRow = random.nextInt(rows);
			playerCol = random.nextInt(cols);
		}
		newGrid[playerRow][playerCol].setType(Type.PLAYER);
		return newGrid;
	}
	
	public void printGrid() {
		System.out.println();
		for (int i=0; i<this.r; i++) {
			for (int j=0; j<this.c; j++) {
				switch (grid[i][j].type) {
					case EMPTY:		System.out.print("[ ]");
									break;
					case WALL:		System.out.print("[W]");
									break;
					case PLAYER:	System.out.print("[P]");
									break;
					case REWARD:	System.out.print("[R]");
									break;
					case VICTORY:	System.out.print("[V]");
									break;
				}
				System.out.print(" ");
			}
			System.out.print("\n");
		}
	}
	
	public void printValueGrid() {
		System.out.println();
		for (int i=0; i<this.r; i++) {
			for (int j=0; j<this.c; j++) {
				int highestAction = 0;
				for (int k=0; k<4; k++) {
					if (this.getGrid()[i][j].qtable[k] > this.getGrid()[i][j].qtable[highestAction]) {
						highestAction = k;
					}
				}
				if (highestAction == 0) {
					System.out.print("[ U ] ");
				}
				else if (highestAction == 1) {
					System.out.print("[ D ] ");
				}
				else if (highestAction == 2) {
					System.out.print("[ L ] ");
				}
				else if (highestAction == 3) {
					System.out.print("[ R ] ");
				}
			}
			System.out.print("\n");
		}
	}
	
	public Square[][] getGrid() {
		return grid;
	}

	public void setGrid(Square[][] grid) {
		this.grid = grid;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public int getPlayerRow() {
		return playerRow;
	}

	public void setPlayerRow(int playerRow) {
		this.playerRow = playerRow;
	}

	public int getPlayerCol() {
		return playerCol;
	}

	public void setPlayerCol(int playerCol) {
		this.playerCol = playerCol;
	}

	public boolean isFoundReward() {
		return foundReward;
	}

	public void setFoundReward(boolean foundReward) {
		this.foundReward = foundReward;
	}



	public static class Square {
		Type type;
		int sr, sc;
		double alpha, delta, gamma;
		int reward = 0;
		
		// Q table, start with random value between 0 and 1
		double[] qtable = new double[4];
		
		// E table, starts with all zeros
		double[] etable = new double[4];
		
		public Square() {
			this.type = Type.EMPTY;
			
			// initialize qtable with random values
			for (int a=0; a<qtable.length; a++) {
				qtable[a] = Math.random();
			}
		}
		
		public Square(Type type) {
			this.type = type;
			
			// initialize qtable with random values
			for (int a=0; a<qtable.length; a++) {
				qtable[a] = Math.random();
			}
		}
		
		public Type getType() {
			return type;
		}
		
		public void setType(Type type) {
			this.type = type;
		}
		
		public int getReward() {
			return this.reward;
		}
		
		public void setReward(int reward) {
			this.reward = reward;
		}
	}
	
	public enum Type {
		EMPTY, WALL, PLAYER, REWARD, VICTORY
	}
}
