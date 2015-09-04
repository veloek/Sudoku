/*
 * The MIT License
 *
 * Copyright 2015 Vegard Løkken
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package no.vtek.sudoku;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 * The sudoku game board
 * 
 * @author Vegard Løkken
 * @version 0.25
 */
public class SudokuBoard extends JPanel implements MouseListener {
	private SudokuGame frame;
	private int[][] game;
	private int[][] gameOriginal;
	private boolean[][] networkCheck;
	private JLabel[][] tiles = new JLabel[9][9];
	private int chosenNumber = 0;
	private NumberChooser numberChooserDialog = new NumberChooser();
	JLabel activeTile = null;

	public SudokuBoard(SudokuGame frame, int[][] game) {
		this.frame = frame;
		this.game = game;
		
		/* Making a copy of the original so that we can remember which
		 * fields are editable */
		gameOriginal = new int[game.length][game[0].length];
		for (int i=0; i<game.length; i++) {
			for (int j=0; j<game[0].length; j++) {
				gameOriginal[i][j] = game[i][j];
			}
		}
		
		networkCheck = new boolean[game.length][game[0].length];
		
		setPreferredSize(new Dimension(500, 500));
		setLayout(null);
		
		for (int i=0; i<game.length; i++) {
			for (int j=0; j<game[0].length; j++) {
				tiles[i][j] = new JLabel();
				tiles[i][j].setBackground(Color.white);
				tiles[i][j].setOpaque(true);
				tiles[i][j].addMouseListener(this);
				tiles[i][j].setFont(new Font("Arial", Font.BOLD, 18));
				
				if (game[i][j] != 0) {
					tiles[i][j].setText(new Integer(game[i][j]).toString());
				} else {
					//tiles[i][j].setText("?");
					tiles[i][j].setForeground(Color.red);
					//tiles[i][j].setToolTipText("Click to change");
				}
				
				tiles[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				tiles[i][j].setBorder(
						BorderFactory.createLineBorder(Color.black));
				tiles[i][j].setBounds(j*(50+5)+5, i*(50+5)+5, 50, 50);
				add(tiles[i][j]);
			}
		}
		
		JPanel squareFrame;
		for (int i=2; i<497; i+=165) {
			for (int j=2; j<497; j+=165) {
				squareFrame = new JPanel();
				squareFrame.setBounds(j, i, 166, 166);
				squareFrame.setBorder(BorderFactory.createLineBorder(Color.black, 2));
				add(squareFrame);
			}
		}
		
	}
	
	private int[] findIndex(JLabel button) {
		int[] ret = new int[]{-1, -1};
		
		for (int i=0; i<tiles.length; i++) {
			for (int j=0; j<tiles[0].length; j++) {
				if (tiles[i][j] == button) {
					ret = new int[]{j, i};
				}
			}
		}
		
		return ret;
	}
	
	private boolean boardCompleted() {
		boolean completed = true;
		
		outer:
		for (int i=0; i<game.length; i++) {
			for (int j=0; j<game[0].length; j++) {
				if (game[i][j] == 0) {
					completed = false;
					break outer;
				}
			}
		}
		
		return completed;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (frame.inGame && e.getSource() instanceof JLabel) {
			int[] index = findIndex((JLabel)e.getSource());

			int x = index[0];
			int y = index[1];

			int clickedNumber = frame.redo ? gameOriginal[y][x] : game[y][x];

			if (clickedNumber == 0) {
				
					numberChooserDialog.showDialog(x, y);
					
					setNumber(x, y, chosenNumber);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (frame.inGame && e.getSource() instanceof JLabel) {
			
			// Color those who are affected
			if (frame.lineAssistance) {
				activeTile = (JLabel) e.getSource();

				for (JLabel tile : getNeighbours(activeTile)) {
					tile.setBackground(new Color(200, 225, 250));
				}

				activeTile.setBackground(new Color(100, 150, 225));
			}
			
			
			int[] index = findIndex((JLabel)e.getSource());
			int x = index[0];
			int y = index[1];
			if (gameOriginal[y][x] == 0) {
				frame.statusBar.setStatus("Click to change");
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		for (int i=0; i<tiles.length; i++) {
			for (int j=0; j<tiles[0].length; j++) {
			tiles[i][j].setBackground(Color.white);
			}
		}
		
		frame.statusBar.setStatus(null);
	}
	
	private void setNumber(int x, int y, int num) {
		game[y][x] = num;

		if (num > 0) {

			tiles[y][x].setText(new Integer(num).toString());

			/* Check for win */
			if (boardCompleted()) {
				if (SudokuFactory.checkValid(game)) {
					frame.statusBar.stopTime();
					frame.gameOver();

				} else {
					JOptionPane.showMessageDialog(frame,
						"You have completed the board, but it " +
						"doesn't seem to fulfill sudoku rules. " +
						"Take one more look at it and try again.");
				}
			}
		} else {

			tiles[y][x].setText(null);
		}
	}
	
	private ArrayList<JLabel> getNeighbours(JLabel activeTile) {
		ArrayList<JLabel> neighbours = new ArrayList<JLabel>();
		
		int[] index = findIndex(activeTile);
		int x = index[0], y = index[1];
		
		// Rows
		for (int i=0; i<tiles.length; i++) {
			neighbours.add(tiles[i][x]);
		}
		
		// Columns
		for (int i=0; i<tiles[0].length; i++) {
			neighbours.add(tiles[y][i]);
		}
		
		// Square
		int startCol = (x / 3) * 3;
		int startRow = (y / 3) * 3;

		for (int i=startRow; i<startRow+3; i++) {
			for (int j=startCol; j<startCol+3; j++) {
				neighbours.add(tiles[i][j]);
			}
		}
		
		return neighbours;
	}
	
	public void finishGame() {
		
		int[][] finished = SudokuFactory.finishGame(gameOriginal);
		
		if (finished != null) {
			frame.inGame = false;
			frame.statusBar.stopTime();
			
			game = finished;

			for (int i=0; i<tiles.length; i++) {
				for (int j=0; j<tiles[0].length; j++) {
					tiles[i][j].setText(new Integer(game[i][j]).toString());
				}
			}
			
		} else {
			System.out.println("ERROR: Couldn't finish game.");
		}
	}
	
	public void retryGame() {
		
		game = new int[gameOriginal.length][gameOriginal[0].length];
		for (int i=0; i<gameOriginal.length; i++) {
			for (int j=0; j<gameOriginal[0].length; j++) {
				game[i][j] = gameOriginal[i][j];
				
				if (game[i][j] > 0) {
					tiles[i][j].setText(
						new Integer(gameOriginal[i][j]).toString());
				} else {
					tiles[i][j].setText(null);
				}
			}
		}
		
		frame.inGame = true;
		frame.statusBar.startTime();
		
	}
	
	private class NumberChooser extends JDialog implements MouseListener {
		
		private ArrayList<JLabel> buttons = new ArrayList<JLabel>();
		private JLabel xButton;
		
		public NumberChooser() {
			super(frame, true);
			setUndecorated(true);
			setResizable(false);
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			
			xButton = new JLabel("clear");
			xButton.addMouseListener(this);
			xButton.setPreferredSize(new Dimension(100, 15));
			xButton.setBorder(BorderFactory.createLineBorder(Color.black));
			xButton.setBackground(Color.black);
			xButton.setForeground(Color.white);
			xButton.setOpaque(true);
			xButton.setHorizontalAlignment(SwingConstants.CENTER);
			xButton.setFont(new Font("Arial", Font.BOLD, 14));
			add(xButton);
			
			for (int i=1; i<10; i++) {
				JLabel button = new JLabel(""+i);
				button.addMouseListener(this);
				button.setPreferredSize(new Dimension(30, 30));
				button.setBorder(BorderFactory.createLineBorder(Color.black));
				button.setBackground(Color.white);
				button.setOpaque(true);
				button.setHorizontalAlignment(SwingConstants.CENTER);
				button.setFont(new Font("Arial", Font.BOLD, 14));
				add(button);
				
				buttons.add(button);
			}
			
		}
		
		public void showDialog(int x, int y) {

			setBounds(frame.getBounds().x + (x*55),
				frame.getBounds().y + ((y+1)*55), 110, 130);
			
			changeButtonBackground(frame.numberAssistance, x, y);
			
			setVisible(true);
		}
		
		private void changeButtonBackground(boolean help, int x, int y) {
			if (help) {
				int[] validNumbers = SudokuFactory.getPossibleNumbers(
					game, y, x);
				
				for (JLabel button : buttons) {
					int num = Integer.parseInt(button.getText());
					if (validNumbers[num] == 1) { // Not valid
						button.setBackground(Color.lightGray);
					} else {
						button.setBackground(Color.white);
					}
				}
			} else {
				for(JLabel button : buttons) {
					button.setBackground(Color.white);
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel src = (JLabel) e.getSource();

			if (src == xButton) {
				chosenNumber = 0;
			} else {
				chosenNumber = Integer.parseInt(src.getText());
			}

			setVisible(false);
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}
}
