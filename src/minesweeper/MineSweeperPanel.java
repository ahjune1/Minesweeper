package minesweeper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MineSweeperPanel extends JPanel {

	private static final int SQ = 25, H_PADDING = 10, TOP_PADDING = 70, BOTTOM_PADDING = 10;

	Image unClickedSquare, flaggedSquare, one, two, three, four, five, six, seven, eight, clickedSquare, redmine, mine, nomine, smiley, sunglasses, deadsmiley, displayzero, displayone, displaytwo, displaythree, displayfour, displayfive, displaysix, displayseven, displayeight, displaynine;
	Image[] imageArr;
	Image[] displayArr;

	int[][] mineGrid; // 0 if no mine, 1 if mine

	int[][] clickState; // 0 if unclicked, 1 if clicked, 2 if flagged

	int[][] numbers; // -1 if mine, else n if n mines around the position

	int flags; // current number of flags placed

	int mineNum; // total number of mines

	int gameState = 3; // 1 if ongoing, 2 if won, 0 if gameover, 3 if not started

	int time = 0; // time in seconds elapsed from start

	int rows, columns, mines;

	Timer t;

	public MineSweeperPanel(int rows, int columns, int mines) {
		openImages();
		imageArr = new Image[]{redmine, clickedSquare, one, two, three, four, five, six, seven, eight};
		displayArr = new Image[]{displayzero, displayone, displaytwo, displaythree, displayfour, displayfive, displaysix, displayseven, displayeight, displaynine};
		this.rows = rows;
		this.columns = columns;
		this.mines = mines;
		createGrid(Math.max(Math.min(16, rows), 10), Math.max(Math.min(30, columns), 10), Math.max(Math.min(20, mines), 10));
		setPreferredSize(new Dimension(mineGrid[0].length * SQ + H_PADDING * 2,mineGrid.length * SQ + TOP_PADDING + BOTTOM_PADDING));
		setUpClickListener();
	}

	private void setUpClickListener() {
		this.requestFocusInWindow();

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent click) {
				clickedAt(click);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});

	}

	private void setUpTimer(){
		t = new Timer(1000, arg0 -> {
			if (time != 999)
				time++;
			repaint();
		});
		t.start();
	}

	public void resetGame() {
		createGrid(Math.max(Math.min(16, rows), 10), Math.max(Math.min(30, columns), 10), Math.max(Math.min(20, mines), 10));
		t.stop();
		time = 0;
		flags = 0;
		gameState = 3;
	}


	protected void clickedAt(MouseEvent click) {
		if (click.getX() > (this.getWidth() / 2) - 20 &&
				click.getY() > 15 &&
				click.getX() < (this.getWidth() / 2) + 20 &&
				click.getY() < 55) {
			resetGame();
			repaint();
		}
		if (gameState != 1 && gameState != 3) {
			return;
		}
		if (click.getX() < H_PADDING || click.getY()<TOP_PADDING ||
			click.getX() > this.getWidth()-H_PADDING ||
			click.getY() > this.getHeight()-BOTTOM_PADDING)
			return;
		if (gameState == 3) {
			gameState = 1;
			setUpTimer();
		}
		int col = (click.getX()-H_PADDING)/SQ;
		int row = (click.getY()-TOP_PADDING)/SQ;
		System.out.println("row: "+row+" col: "+col);
		System.out.println(mineGrid[row][col]);
		if(click.getButton()==1) {
			leftClick(row, col);
		}
		else if(click.getButton()==3){
			rightClick(row,col);
		}
		repaint();
	}

	private void rightClick(int row, int col) {
		if(clickState[row][col]==0 && flags < mineNum) {
			clickState[row][col] = 2;
			flags++;
		}
		else if(clickState[row][col]==2) {
			clickState[row][col] = 0;
			flags--;
		}
	}

	private void leftClick(int row, int col) {
		if (clickState[row][col] != 0) {
			return;
		}
		if (mineGrid[row][col] == 1) {
			blowUp();
		}
		if (numbers[row][col] == 0) {
			expand(row, col);
		}
		clickState[row][col] = 1;
		checkFinish();
	}

	private void expand(int row, int col) {
		clickState[row][col] = 1;
		if (numbers[row][col] == 0) {
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					if (row + a >= 0 &&
							row + a < mineGrid.length &&
							col + b >= 0 &&
							col + b < mineGrid[row + a].length &&
							clickState[row + a][col + b] == 0) {
							expand(row + a, col + b);
					}
				}
			}
		}
	}

	private void blowUp() {
		gameState = 0;
		t.stop();
	}

	private void checkFinish() {
		for (int r = 0; r < mineGrid.length; r++) {
			for (int c = 0; c < mineGrid[0].length; c++) {
				if ((clickState[r][c] == 0 && mineGrid[r][c] == 0) ||
						(clickState[r][c] == 2 && mineGrid[r][c] == 0)) {
					return;
				}
			}
		}
		gameState = 2;
		t.stop();
	}

	private void createGrid(int rows, int columns, int mines) {
		mineGrid = new int[rows][columns];
		
		clickState = new int[rows][columns];
		
		numbers= new int[rows][columns];

		mineNum = ((mines * ( rows*columns)) / 100);

		placeMines(mineNum);

	}
	private void placeMines(int mines) {

		int rows = mineGrid.length, cols = mineGrid[0].length;
		for(int i = 0; i<mines; i++) {
			int r = (int) (Math.random()*rows);
			int c = (int) (Math.random()*cols);
			if(mineGrid[r][c] == 0) {
				mineGrid[r][c] = 1;
			} else {
				i--;
			}
		}
		createNumberGrid();
	}
	private void createNumberGrid() {
		for(int row=0; row < mineGrid.length; row++){
			for(int column = 0; column < mineGrid[row].length; column++){
				if(mineGrid[row][column] == 1){
					numbers[row][column] = -1;
				} else {
					for (int a = -1; a <= 1; a++) {
						for (int b = -1; b <= 1; b++) {
							if (row + a >= 0 &&
									row + a < mineGrid.length &&
									column + b >= 0 &&
									column + b < mineGrid[row + a].length &&
									mineGrid[row + a][column + b] == 1) {
								numbers[row][column]++;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (gameState == 1 || gameState == 3) {
			g.drawImage(this.smiley, (this.getWidth() / 2) - 20, 15, 40 ,40, null);
		} else if (gameState == 2) {
			g.drawImage(this.sunglasses, (this.getWidth() / 2) - 20, 15, 40 ,40, null);
		} else {
			g.drawImage(this.deadsmiley, (this.getWidth() / 2) - 20, 15, 40 ,40, null);
		}
		int displayLeft = mineNum - flags;
		int hundreds = (displayLeft / 100) % 10;
		int tens = (displayLeft / 10) % 10;
		int ones = displayLeft % 10;
		g.drawImage(this.displayArr[hundreds], 15, 15, 20 ,40, null);
		g.drawImage(this.displayArr[tens], 35, 15, 20 ,40, null);
		g.drawImage(this.displayArr[ones], 55, 15, 20 ,40, null);
		hundreds = (time / 100) % 10;
		tens = (time / 10) % 10;
		ones = time % 10;
		g.drawImage(this.displayArr[hundreds], this.getWidth() - 75, 15, 20 ,40, null);
		g.drawImage(this.displayArr[tens], this.getWidth() - 55, 15, 20 ,40, null);
		g.drawImage(this.displayArr[ones], this.getWidth() - 35, 15, 20 ,40, null);
		for (int r = 0; r < mineGrid.length; r++) {
			for (int c = 0; c < mineGrid[0].length; c++) {
				if (clickState[r][c] == 0)
					g.drawImage(this.unClickedSquare, H_PADDING + c * SQ, TOP_PADDING + SQ * r, SQ, SQ, null);
				else if (clickState[r][c] == 2)
					g.drawImage(this.flaggedSquare, H_PADDING + c * SQ, TOP_PADDING + SQ * r, SQ, SQ, null);
				else if (clickState[r][c] == 1) {
					g.drawImage(this.imageArr[numbers[r][c] + 1], H_PADDING + c * SQ, TOP_PADDING + SQ * r, SQ, SQ, null);
				}
				if ((gameState == 0 && mineGrid[r][c] == 1) && clickState[r][c] == 0) {
					g.drawImage(this.mine, H_PADDING + c * SQ, TOP_PADDING + SQ * r, SQ, SQ, null);
				} else if ((gameState == 0 && mineGrid[r][c] == 0) && clickState[r][c] == 2) {
					g.drawImage(this.nomine, H_PADDING + c * SQ, TOP_PADDING + SQ * r, SQ, SQ, null);
				}
				if ((gameState == 2 && mineGrid[r][c] == 1)) {
					g.drawImage(this.flaggedSquare, H_PADDING + c * SQ, TOP_PADDING + SQ * r, SQ, SQ, null);
				}
			}
		}
	}

	private void openImages() {
		
			ImageIcon Icon = new ImageIcon(getClass().getClassLoader().getResource("unclicked.png"));
			unClickedSquare = Icon.getImage();
			
			Icon = new ImageIcon(getClass().getClassLoader().getResource("flagged.png"));
			flaggedSquare= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("Number 1.PNG"));
			one= Icon.getImage();
			
			Icon = new ImageIcon(getClass().getClassLoader().getResource("Number 2.PNG"));
			two= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("Number 3.PNG"));
			three= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("Number 4.PNG"));
			four= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("Number 5.PNG"));
			five= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("Number 6.PNG"));
			six= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("Number 7.PNG"));
			seven= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("Number 8.PNG"));
			eight= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("clicked.png"));
			clickedSquare= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("redmine.png"));
			redmine= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("mine.png"));
			mine= Icon.getImage();
			
			Icon = new ImageIcon(getClass().getClassLoader().getResource("nomine.png"));
			nomine= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("smiley.png"));
			smiley= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("deadsmiley.png"));
			deadsmiley= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("sunglassessmiley.png"));
			sunglasses= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displayone.png"));
			displayone= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displaytwo.png"));
			displaytwo = Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displaythree.png"));
			displaythree = Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displayfour.png"));
			displayfour = Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displayfive.png"));
			displayfive = Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displaysix.png"));
			displaysix = Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displayseven.png"));
			displayseven= Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displayeight.png"));
			displayeight = Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displaynine.png"));
			displaynine = Icon.getImage();
			Icon = new ImageIcon(getClass().getClassLoader().getResource("displayzero.png"));
			displayzero = Icon.getImage();
			
	}

}
