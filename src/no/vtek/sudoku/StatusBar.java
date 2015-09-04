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

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The sudoku game's status bar
 *
 * @author Vegard Løkken
 * @version 0.25
 */
class StatusBar extends JPanel {

	private SudokuGame frame;
	private JLabel statusText = new JLabel("Welcome to Sudoku");
	private JLabel time = new JLabel("Time: 0 ");
	private TimeThread thread = null;

	public StatusBar(SudokuGame frame) {
		this.frame = frame;

		setLayout(new BorderLayout());

		statusText.setForeground(Color.gray);
		time.setForeground(Color.gray);

		add(statusText, BorderLayout.WEST);
		add(time, BorderLayout.EAST);
	}

	public void setStatus(String status) {
		statusText.setText(status);
	}

	public void startTime() {
		if (thread != null && thread.running) stopTime();
		
		thread = new TimeThread();
		thread.start();
	}

	public void stopTime() {
		thread.running = false;
	}

	public void pauseTime() {
		thread.pause = true;
	}

	public void resumeTime() {
		thread.pause = false;
	}

	public int getTime() {
		return thread.timeCount;
	}

	private class TimeThread extends Thread {
		private boolean running = true;
		private boolean pause = false;
		private int timeCount = 0;
		private long last;

		@Override
		public void run() {
			last = System.currentTimeMillis();
			
			while (running) {
				long now = System.currentTimeMillis();
				long diff = now - last;
				
				last = now;
					
				if (!pause) {
					timeCount += diff;
					time.setText("Time: " + timeCount/1000 + " ");
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {
				}
			}
		}
	}
}
