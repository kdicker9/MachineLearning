package gridworld;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GridUI extends JFrame{
	private static final long serialVersionUID = 1L;
	Grid g;
	int r, c;
	Container container;
	ArrayList<JPanel> components;
	
	public GridUI(Grid grid, int rows, int cols) {
		g = grid;
		r = rows;
		c = cols;
	}
	
	public void run() throws IOException {
		this.setPreferredSize(new Dimension(500, 500));
		this.setResizable(true);
		this.setLocation(200, 100);
		this.setLayout(new GridLayout(this.r, this.c));
		container = this.getContentPane();
		components = new ArrayList<JPanel>();
		JPanel temp = null;
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Populates gridui with squares
		for (int i=0; i<r; i++) {
			for (int j=0; j<c; j++) {
				temp = new JPanel();
				
				components.add(temp);
				container.add(temp);
				temp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				if (g.getGrid()[i][j].type == Grid.Type.EMPTY) {
					temp.setBackground(Color.WHITE);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.PLAYER) {
					temp.setBackground(Color.BLUE);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.WALL) {
					temp.setBackground(Color.BLACK);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.REWARD) {
					temp.setBackground(Color.YELLOW);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.VICTORY) {
					temp.setBackground(Color.GREEN);
				}
			}
		}
		
		this.pack();
		this.setVisible(true);
	}
	
	// re-colors squares that have changed
	public void refresh(Grid grid) throws IOException {
		JPanel temp = null;
		ArrayList<JPanel> newComps = new ArrayList<JPanel>();
		for (int i=0; i<r; i++) {
			for (int j=0; j<c; j++) {
				temp = new JPanel();
				//temp.setSize(20, 20);
				
				newComps.add(temp);
				temp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				if (g.getGrid()[i][j].type == Grid.Type.EMPTY) {
					temp.setBackground(Color.WHITE);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.PLAYER) {
					temp.setBackground(Color.BLUE);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.WALL) {
					temp.setBackground(Color.BLACK);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.REWARD) {
					temp.setBackground(Color.YELLOW);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.VICTORY) {
					temp.setBackground(Color.GREEN);
				}
			}
		}
		for (int k=0; k<components.size(); k++) {
			if (components.get(k).getBackground() != newComps.get(k).getBackground()) {
				components.get(k).setBackground(newComps.get(k).getBackground());
			}
		}
		this.revalidate();
		this.repaint();
	}
	
	@SuppressWarnings("deprecation")
	public void refreshWithArrows(Grid grid) throws IOException {
		JPanel temp = null;
		ArrayList<JPanel> newComps = new ArrayList<JPanel>();
		for (int i=0; i<r; i++) {
			for (int j=0; j<c; j++) {
				temp = new JPanel();
				//temp.setSize(20, 20);
				
//				// image
				//BufferedImage upPic = ImageIO.read(new File("uparrowtrans.png"));
				if (g.getGrid()[i][j].type != Grid.Type.WALL && g.getGrid()[i][j].type != Grid.Type.REWARD && g.getGrid()[i][j].type != Grid.Type.VICTORY) {
					
					// set up down right or left
					int highest = 0;
					for (int k=1; k<4; k++) {
						if (g.getGrid()[i][j].qtable[k] > g.getGrid()[i][j].qtable[highest]) {
							highest = k;
						}
					}
					BufferedImage myPicture;
					if (highest == 0)
						myPicture = ImageIO.read(new File("uparrowtrans.png"));
					else if (highest == 0)
						myPicture = ImageIO.read(new File("downarrowtrans.png"));
					else if (highest == 0)
						myPicture = ImageIO.read(new File("leftarrowtrans.png"));
					else
						myPicture = ImageIO.read(new File("rightarrowtrans.png"));
					JLabel picLabel = new JLabel(new ImageIcon(myPicture));
					temp.add(picLabel);
				}
				
				newComps.add(temp);
				temp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				if (g.getGrid()[i][j].type == Grid.Type.EMPTY) {
					temp.setBackground(Color.WHITE);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.PLAYER) {
					temp.setBackground(Color.BLUE);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.WALL) {
					temp.setBackground(Color.BLACK);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.REWARD) {
					temp.setBackground(Color.YELLOW);
				}
				else if (g.getGrid()[i][j].type == Grid.Type.VICTORY) {
					temp.setBackground(Color.GREEN);
				}
			}
		}
		for (int k=0; k<components.size(); k++) {
			if (newComps.get(k).countComponents() > 0) {
				components.get(k).removeAll();
				components.get(k).add(newComps.get(k).getComponent(0));
			}
			components.get(k).setBackground(newComps.get(k).getBackground());
		}
		this.revalidate();
		this.repaint();
	}
}
