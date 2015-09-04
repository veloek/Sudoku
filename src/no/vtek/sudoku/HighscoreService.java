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

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Highscore service to make the game more competative
 * 
 * @author Vegard Løkken
 * @version 0.25
 */
public class HighscoreService {
	private static ArrayList<HighscoreItem> highscoreList = null;
	private static final String FILENAME = "Highscores.dat";
	
	public static HighscoreItem getWorst(int difficultyLevel) {
		loadHighscoreList();
		
		HighscoreItem worst = null;
		for (HighscoreItem item : highscoreList) {
			if (item.getDifficultyLevel() == difficultyLevel) {
				if (worst != null) {
					if (item.getTime() > worst.getTime()) worst = item;
				} else {
					worst = item;
				}
			}
		}
		
		return worst;
	}
	
	public static boolean hasFreeSlots(int difficultyLevel) {
		loadHighscoreList();
		
		int num = 0;
		for (HighscoreItem item : highscoreList) {
			if (item.getDifficultyLevel() == difficultyLevel) {
				num++;
			}
		}
		
		return num < 10;
	}
	
	public static void addScore(String name, int difficultyLevel, int time) {
		loadHighscoreList();
		
		boolean ok = true;
		if (!hasFreeSlots(difficultyLevel)) {
			ok = false;
			
			HighscoreItem worst = getWorst(difficultyLevel);
			if (time > worst.getTime()) {
				highscoreList.remove(worst);
				ok = true;
			}
		}
		
		if (ok) {
			highscoreList.add(new HighscoreItem(
					name, difficultyLevel, time, new Date()));
			saveHighscoreList();
		}
	}
	
	public static void showHighscoreTable(SudokuGame frame, int difficulty) {
		loadHighscoreList();
		
		new HighscoreTable(frame, highscoreList, difficulty);
	}
	
	private static void loadHighscoreList() {
		Object obj = null;
		try {
			FileInputStream fis = new FileInputStream(FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			obj = ois.readObject();
			ois.close();
			
		} catch (Exception e) {
			System.err.println("Error while reading highscore list");
		}
		
		if (obj != null) {
			highscoreList = (ArrayList<HighscoreItem>)obj;
		} else {
			highscoreList = new ArrayList<HighscoreItem>();
		}
	}
	
	private static void saveHighscoreList() {
		try {
			FileOutputStream fos = new FileOutputStream(FILENAME);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(highscoreList);
		} catch (Exception e) {
			System.err.println("Error while saving highscore list");
		}
	}
}

class HighscoreTable extends JDialog {
	

	public HighscoreTable(SudokuGame frame,
			ArrayList<HighscoreItem> highscoreList, int difficulty) {
		super(frame, true);

		JTabbedPane tabbedPane = new JTabbedPane();
		
		// Easy highscores
		ArrayList<HighscoreItem> tableItems = new ArrayList<HighscoreItem>();
		
		for (HighscoreItem item : highscoreList) {
			if (item.getDifficultyLevel() == SudokuFactory.EASY) {
				tableItems.add(item);
			}
		}
		
		// Fill blanks
		for (int i=tableItems.size()-1; i<10; i++) {
			tableItems.add(new HighscoreItem("", 0, 0, null));
		}

		JTable table = new JTable(new HighscoreTableModel(tableItems));
		table.setPreferredScrollableViewportSize(new Dimension(400, 200));
		JScrollPane scrollPane = new JScrollPane(table);
		
		tabbedPane.add("Easy", scrollPane);
		
		// Medium highscores
		tableItems = new ArrayList<HighscoreItem>();
		
		for (HighscoreItem item : highscoreList) {
			if (item.getDifficultyLevel() == SudokuFactory.MEDIUM) {
				tableItems.add(item);
			}
		}
		
		// Fill blanks
		for (int i=tableItems.size()-1; i<10; i++) {
			tableItems.add(new HighscoreItem("", 0, 0, null));
		}

		table = new JTable(new HighscoreTableModel(tableItems));
		table.setPreferredScrollableViewportSize(new Dimension(400, 200));
		scrollPane = new JScrollPane(table);
		
		tabbedPane.add("Medium", scrollPane);
		
		// Hard highscores
		tableItems = new ArrayList<HighscoreItem>();
		
		for (HighscoreItem item : highscoreList) {
			if (item.getDifficultyLevel() == SudokuFactory.HARD) {
				tableItems.add(item);
			}
		}
		
		// Fill blanks
		for (int i=tableItems.size()-1; i<10; i++) {
			tableItems.add(new HighscoreItem("", 0, 0, null));
		}

		table = new JTable(new HighscoreTableModel(tableItems));
		table.setPreferredScrollableViewportSize(new Dimension(400, 200));
		scrollPane = new JScrollPane(table);
		tabbedPane.add("Hard", scrollPane);
		
		switch (difficulty) {
			case SudokuFactory.EASY:
				tabbedPane.setSelectedIndex(0);
				break;
			case SudokuFactory.MEDIUM:
				tabbedPane.setSelectedIndex(1);
				break;
			case SudokuFactory.HARD:
				tabbedPane.setSelectedIndex(2);
				break;
		}
		
		add(tabbedPane);
		pack();

		setVisible(true);
	}

	private class HighscoreTableModel extends AbstractTableModel {

		private String[] columnNames = {"Name",
										"Time",
										"Date"};

		private Object[][] data;

		public HighscoreTableModel(ArrayList<HighscoreItem> items) {
			
			data = new Object[items.size()][columnNames.length];
			
			for (int i=0; i<items.size(); i++) {
				data[i][0] = items.get(i).getName();
				
				data[i][1] = items.get(i).getTime() > 0 ?
						items.get(i).getTime() : "";
				
				if (items.get(i).getDate() != null) {
					SimpleDateFormat formatter =
							new SimpleDateFormat("dd/MM yyyy");
					String date = formatter.format(items.get(i).getDate());
					data[i][2] = date;
				} else {
					data[i][2] = "";
				}
			}
		}


		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

	}
}

