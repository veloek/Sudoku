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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Generic modal dialog
 * 
 * @author Vegard Løkken
 * @version 0.25
 */
public class SudokuDialog extends JDialog implements ActionListener {
	private ArrayList<JComponent> comps;
	private JPanel compsPanel, buttonPanel;
	private JButton okButton, abortButton;
	private boolean ok = false;

	public SudokuDialog(Frame owner) {
		super(owner, true);
		setLayout(new BorderLayout());
		
		comps = new ArrayList<JComponent>();
		compsPanel = new JPanel();
		compsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		buttonPanel = new JPanel();
		okButton = new JButton("OK");
		abortButton = new JButton("Abort");
		
		buttonPanel.add(okButton);
		buttonPanel.add(abortButton);
		
		okButton.addActionListener(this);
		abortButton.addActionListener(this);
		
	}
	
	public void addComp(JComponent comp) {
		comps.add(comp);
		compsPanel.add(comp);
	}
	
	public void open() {
		add(compsPanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
		this.setSize(getWidth()+50, getHeight());
		setVisible(true);
	}

	public ArrayList<JComponent> getComps() {
		return comps;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == okButton) {
			ok = true;
		}
		
		setVisible(false);
	}
	
}
