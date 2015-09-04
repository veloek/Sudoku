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

import java.util.Random;

/**
 * Sudoku game generator
 *
 * @author Vegard Løkken
 * @version 0.25
 */
public class SudokuFactory {
	public static final int EASY = 50;
	public static final int MEDIUM = 65;
	public static final int HARD = 75;
	
	private static Random rand = new Random();
	
	/**
	 * Creates a 9 x 9 sudoku table fully filled.
	 * 
	 * @return The game
	 */
	public static int[][] createGame() {
		int[][] board = new int[9][9];

		int counter = 0;
		
		outer:
		for (int i=0; i<board.length; i++) {

			for (int j=0; j<board[0].length; j++) {
				counter++;

				int[] triedNumbers = new int[9];
				
				board[i][j] = randInt(1, 9);
				while (!checkValid(board, i, j)) {

					if (addToTriedNumbers(triedNumbers, board[i][j])) {
						break; // DO SOMETHING; GO BACK
					}
					
					board[i][j] = randInt(1, 9);
				}
					
				if (!checkValid(board, i, j)) {
					if (i>0) i--;
					if (j>0) j--;
				}
				
				if (counter > 1000) break outer;
			}
		}
		
		/* If counter > 1000, something is fishy and we try again.
		 * This is a bugfix for a problem with createGame(difficulty)
		 * getting trapped in an endless loop.
		 */
		if (counter > 1000) {
			return createGame();
		} else {
			return board;
		}
	}
	
	/**
	 * Creates a game board with a given difficulty.
	 * 
	 * @param difficulty Integer pointing to how many
	 *		tiles to remove
	 * @return The game board
	 */
	public static int[][] createGame(int difficulty) {
		
		int[][] game;
		
		do {
			
			game = createGame();
			
			for (int i=0; i<difficulty; i++) {

				/* TODO: This should check if the position has already
				 * been cleared out, and if so find a new position.
				 */
				game[randInt(0,8)][randInt(0,8)] = 0;

			}
			
		} while (!isPossible(game));
		
		return game;
	}
	
	/**
	 * Finishes a given game. Does not touch given game.
	 * Returns a finished copy.
	 * 
	 * @param game The game to finish
	 * @return The finished game
	 */
	public static int[][] finishGame(int[][] game) {
		
		int[][] copy = new int[game.length][game[0].length];
		for (int i=0; i<game.length; i++) {
			for (int j=0; j<game[0].length; j++) {
				copy[i][j] = game[i][j];
			}
		}
		
		boolean someLeft;
		int counter = 0;
		
		do {
			someLeft = false;
			counter++;
			
			for (int i=0; i<copy.length; i++) {
				for (int j=0; j<copy[0].length; j++) {
					
					if (copy[i][j] == 0) {

						int[] validNumbers = getPossibleNumbers(copy, i, j);
						int sum = 0;
						for (int num : validNumbers) sum += num;

						if (sum == 8) { // Only one possible number
						
							// Find the one and use it
							for (int k=1; k<validNumbers.length; k++) {
								if (validNumbers[k] == 0) copy[i][j] = k;
							}
							
						} else {
							someLeft = true;	
						}
					}
				}
			}
		} while (someLeft && counter < 100);
		
		return counter < 100 ? copy : null;
	}
	
	/**
	 * Checks to see if a game is possible to finish. Used under
	 * game creation.
	 * 
	 * @param game The game board to check
	 * @return True if possible, else false
	 */
	public static boolean isPossible(int[][] game) {
		return finishGame(game) != null;
	}
	
	/**
	 * Fetches all possible values for that position on the board
	 * 
	 * @param game The sudoku game board
	 * @param col The column
	 * @param row The row
	 * @return An array with indexes from 0-9. Those indexes that
	 *		returns 0 is the numbers that are free, 1 means already used.
	 */
	public static int[] getPossibleNumbers(int[][] game, int row, int col) {
		
		int[] validNumbers = new int[10];
		
		// Rows
		for (int i=0; i<game.length; i++) {
			if (game[i][col] != 0) {
				validNumbers[game[i][col]] = 1;
			}
		}
		
		// Columns
		for (int i=0; i<game[0].length; i++) {
			if (game[row][i] != 0) {
				validNumbers[game[row][i]] = 1;
			}
		}
		
		// Square
		int startCol = (col / 3) * 3;
		int startRow = (row / 3) * 3;

		for (int i=startRow; i<startRow+3; i++) {
			for (int j=startCol; j<startCol+3; j++) {
				if (game[i][j] != 0) {
					validNumbers[game[i][j]] = 1;
				}
			}
		}
		
		return validNumbers;
	}
	
	/**
	 * Returns a random integer between given integers
	 * 
	 * @param start Lowest possible return
	 * @param end Highest possible return
	 * @return The number generated
	 */
	private static int randInt(int start, int end) {
		end++;
		if (start < end) {
			return rand.nextInt(end-start) + start;
		} else {
			return -1;
		}
	}
	
	/**
	 * Checks if a given board position number is valid according to
	 * sudoku rules
	 * 
	 * @param game The game board to use
	 * @param row Row position
	 * @param col Column position
	 * @return The valid status
	 */
	private static boolean checkValid(int[][] game, int row, int col) {
		boolean ok = true;
		
		int num = game[row][col];

		
		// Check rows
		if (ok) {
			for (int i=0; i<row; i++) {
				if (game[i][col] == num) {
					ok = false;
					break;
				}
			}
		}
		
		// Check columns
		if (ok) {
			for (int i=0; i<col; i++) {
				if (game[row][i] == num) {
					ok = false;
					break;
				}
			}
		}
		
		// Check square
		if (ok) {
			int startCol = (col / 3) * 3;
			int startRow = (row / 3) * 3;

			outer:
			for (int i=startRow; i<startRow+3; i++) {
				for (int j=startCol; j<startCol+3; j++) {
					if (i != row && j != col) { // Don't check this number
						if (game[i][j] == num) {
							ok = false;
							break outer;
						}
					}
				}
			}
		}
		
		return ok;
	}
	
	/**
	 * Checks the entire game board for errors.
	 * Handy when in doubt of createGame method's integrity.
	 * 
	 * @param game The game board to check
	 * @return Valid status
	 */
	public static boolean checkValid(int[][] game) {
		boolean ok = true;
		
		outer:
		for (int i=0; i<game.length; i++) {
			for (int j=0; j<game[0].length; j++) {
				if (!checkValid(game, i, j)) {
					ok = false;
					break outer;
				}
			}
		}
		
		return ok;
	}
	
	/**
	 * Helper method that adds a number to an array if not already
	 * in there.
	 * 
	 * @param array The array to use
	 * @param num The number to add if not present
	 * @return True if the array is full, else false
	 */
	private static boolean addToTriedNumbers(int[] array, int num) {
		boolean present = false;
		int endIndex = 0;
		for (int i=0; i<array.length; i++) {
			if (array[i] == num) {
				present = true;
				break;
			} else if (array[i] == 0) {
				endIndex = i;
				break;
			}
		}
		
		if (!present) {
			array[endIndex] = num;
		}
		
		if (endIndex == array.length-1) return true;
		else return false;
	}
}
