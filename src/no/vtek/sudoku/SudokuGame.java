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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

/**
 * Sudoku game
 *
 * @author Vegard Løkken
 * @version 0.25
 */
public class SudokuGame extends JFrame implements ActionListener, ItemListener {

	private MenuItem newGameItem;
	private MenuItem retryGameItem;
	private MenuItem highscoreGameItem;
	private MenuItem exitGameItem;
	private CheckboxMenuItem lineAssistanceMenuItem;
	private CheckboxMenuItem numberAssistanceMenuItem;
	private CheckboxMenuItem redoMenuItem;
	private MenuItem finishMenuItem;
	private MenuItem aboutMenuItem;
	private MenuItem helpMenuItem;
	protected SudokuBoard gamePanel;
	protected StatusBar statusBar;
	protected boolean inGame = false;
	protected boolean lineAssistance = true;
	protected boolean numberAssistance = false;
	protected boolean redo = true;
	protected int[][] game = null;
	private int difficultyLevel = -1;

	public SudokuGame() {
		super("Sudoku puzzle");
		setSize(500, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		// Try to use system look and feel
		try {
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }

		statusBar = new StatusBar(this);
		add(statusBar, BorderLayout.SOUTH);

		MenuBar menuBar = new MenuBar();
		setMenuBar(menuBar);

		Menu gameMenu = new Menu("Game");
		menuBar.add(gameMenu);

		newGameItem = new MenuItem("New game");
		newGameItem.addActionListener(this);

		retryGameItem = new MenuItem("Retry game");
		retryGameItem.addActionListener(this);

		highscoreGameItem = new MenuItem("Highscore list");
		highscoreGameItem.addActionListener(this);

		exitGameItem = new MenuItem("Exit");
		exitGameItem.addActionListener(this);

		gameMenu.add(newGameItem);
		gameMenu.add(retryGameItem);
		gameMenu.add(highscoreGameItem);
		gameMenu.add(exitGameItem);

		Menu optionsMenu = new Menu("Options");
		menuBar.add(optionsMenu);

		Menu assistance = new Menu("Assistance");
		optionsMenu.add(assistance);

		lineAssistanceMenuItem = new CheckboxMenuItem("Line Assistance");
		lineAssistanceMenuItem.addItemListener(this);
		lineAssistanceMenuItem.setState(true);

		numberAssistanceMenuItem = new CheckboxMenuItem("Number Assistance");
		numberAssistanceMenuItem.addItemListener(this);
		numberAssistanceMenuItem.setState(false);

		assistance.add(lineAssistanceMenuItem);
		assistance.add(numberAssistanceMenuItem);

		redoMenuItem = new CheckboxMenuItem("Redo possible");
		redoMenuItem.addItemListener(this);
		redoMenuItem.setState(true);

		optionsMenu.add(redoMenuItem);

		finishMenuItem = new MenuItem("Finish game");
		finishMenuItem.addActionListener(this);

		optionsMenu.add(finishMenuItem);

		Menu helpMenu = new Menu("Help");
		menuBar.add(helpMenu);

		aboutMenuItem = new MenuItem("About");
		aboutMenuItem.addActionListener(this);

		helpMenuItem = new MenuItem("Help");
		helpMenuItem.addActionListener(this);

		helpMenu.add(aboutMenuItem);
		helpMenu.add(helpMenuItem);

		setVisible(true);

		// Center
		this.setLocationRelativeTo(null);

		startNewGame();
	}

	protected void startNewGame() {

		boolean ok = true;

		if (inGame) {
			int ans = JOptionPane.showConfirmDialog(this,
							"A game is already in progress. "
							+ "Are you sure you want to start a new one?", "Abort",
							JOptionPane.YES_NO_OPTION);

			if (ans == JOptionPane.NO_OPTION) {
				ok = false;
			} else {
				remove(gamePanel);
				gamePanel = null;
				game = null;
			}
		} else if (gamePanel != null) {
			remove(gamePanel);
			gamePanel = null;
			game = null;
		}

		if (ok) {
			
			WelcomeDialog welcomeDialog = showWelcomeDialog();
			
			JButton dif = welcomeDialog.difficulty;
			if (dif == welcomeDialog.easyButton) {
				difficultyLevel = SudokuFactory.EASY;
			} else if (dif == welcomeDialog.mediumButton) {
				difficultyLevel = SudokuFactory.MEDIUM;
			} else if (dif == welcomeDialog.hardButton) {
				difficultyLevel = SudokuFactory.HARD;
			} else {
				System.exit(0);
			}
		
			game = SudokuFactory.createGame(difficultyLevel);
			
			gamePanel = new SudokuBoard(this, game);

			inGame = true;

			add(gamePanel, BorderLayout.CENTER);

			statusBar.startTime();

			pack();
				
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == newGameItem) {
			startNewGame();
		} else if (source == exitGameItem) {
			System.exit(0);
		} else if (source == aboutMenuItem) {
			JOptionPane.showMessageDialog(this, "About Sudoku:\n\n"
							+ "Another clone of the Japanese puzzle.\n\n"
							+ "Vegard Løkken (2012).\nVersion 0.25\n\n"
							+ "Feel free to share this game.");
		} else if (source == helpMenuItem) {
			JOptionPane.showMessageDialog(this, "The objective is to fill a "
							+ "9×9 grid with digits so that each column,\n"
							+ "each row, and each of the nine 3×3 sub-grids that "
							+ "compose the grid\n (also called \"boxes\", \"blocks\","
							+ "\"regions\", or \"sub-squares\")\n"
							+ "contains all of the digits from 1 to 9.");
		} else if (source == finishMenuItem) {
			int ans = JOptionPane.showConfirmDialog(this,
							"Are you sure you want me to finish the game? "
							+ "This can not be undone.", "Sure?",
							JOptionPane.YES_NO_OPTION);

			if (ans == JOptionPane.YES_OPTION) {
				gamePanel.finishGame();
			}
		} else if (source == retryGameItem) {
			gamePanel.retryGame();
		} else if (source == highscoreGameItem) {
			HighscoreService.showHighscoreTable(this, difficultyLevel);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();
		if (obj instanceof CheckboxMenuItem) {

			CheckboxMenuItem source = (CheckboxMenuItem) e.getSource();

			if (source == lineAssistanceMenuItem) {
				lineAssistance = source.getState();
			} else if (source == numberAssistanceMenuItem) {
				numberAssistance = source.getState();
			} else if (source == redoMenuItem) {
				redo = source.getState();
			}
		}
	}

	public void gameOver() {
		inGame = false;

		HighscoreItem worst = HighscoreService.getWorst(difficultyLevel);

		if (HighscoreService.hasFreeSlots(difficultyLevel)
						|| statusBar.getTime() > worst.getTime()) {

			String name = JOptionPane.showInputDialog("Congratulations,\n"
							+ "you have completed the game and you've\nreached "
							+ "the highscore list.\n\nEnter your name:");
			while (name != null && name.length() > 30) {
				name = JOptionPane.showInputDialog("The name you entered "
								+ "is too long.\nPlease try again.");
			}

			if (name != null) {
				HighscoreService.addScore(name, difficultyLevel,
								statusBar.getTime() / 1000);

				HighscoreService.showHighscoreTable(this, difficultyLevel);
			}

			int ans = JOptionPane.showConfirmDialog(this,
							"Would you like to start a new game?",
							"New game?", JOptionPane.YES_NO_OPTION);

			if (ans == JOptionPane.YES_OPTION) {
				startNewGame();
			} else {
				System.exit(0);
			}
		} else {

			int ans = JOptionPane.showConfirmDialog(this,
							"Congratulations, you have completed the "
							+ "game. Would you like to start a new one?",
							"Congrats!", JOptionPane.YES_NO_OPTION);

			if (ans == JOptionPane.YES_OPTION) {
				startNewGame();
			} else {
				System.exit(0);
			}
		}
	}

	private WelcomeDialog showWelcomeDialog() {
		WelcomeDialog welcomeDialog = new WelcomeDialog();
		
		return welcomeDialog;
	}

	private class WelcomeDialog extends JDialog implements ActionListener {

		protected JButton easyButton, mediumButton, hardButton;
		protected JButton difficulty = null;

		public WelcomeDialog() {
			super(SudokuGame.this, "Welcome to Sudoku puzzle", true);

			easyButton = new JButton("Easy");
			easyButton.addActionListener(this);

			mediumButton = new JButton("Medium");
			mediumButton.addActionListener(this);

			hardButton = new JButton("Hard");
			hardButton.addActionListener(this);

			setLayout(new GridLayout(3, 1, 50, 5));

			add(new JLabel("Welcome to Sudoku puzzle!"));
			add(new JLabel("Please select a difficulty level:"));

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 3));
			buttonPanel.add(easyButton);
			buttonPanel.add(mediumButton);
			buttonPanel.add(hardButton);

			add(buttonPanel);

			pack();
			
			// Center
			setLocationRelativeTo(null);
			
			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();

			if (source == easyButton || source == mediumButton ||
							source == hardButton) {
				difficulty = (JButton) source;
			}

			setVisible(false);
		}
		
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new SudokuGame();
			}
		});
	}
}
