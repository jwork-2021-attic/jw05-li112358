/*
 * Copyright (C) 2015 Aeranythe Echosong
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import screen.RestartScreen;
import screen.Screen;
import screen.StartScreen;
import screen.WinScreen;

/**
 *
 * @author Aeranythe Echosong
 */
public class ApplicationMain extends JFrame implements KeyListener {

    static private AsciiPanel terminal = null;
    static private RestartScreen screen = null;

    public ApplicationMain() {
        super();
        if(terminal == null)
        	terminal = new AsciiPanel(50, 70, AsciiFont.TALRYTH_15_15);
        add(terminal);
        pack();
        if(screen == null) {
        	screen = new StartScreen(terminal, this);
        	Thread tscreen = new Thread((StartScreen)screen);
        	tscreen.start();
        }
        addKeyListener(this);
        
        repaint();
    }

    public void setScreen() {
    	screen = screen.Link();
    }
    

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		screen.pressKey(e);	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		screen.releaseKey(e);
	}

    public static void main(String[] args) {
        ApplicationMain app = new ApplicationMain();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
        while(true) {
        	try {
        		Thread.sleep(50);
        		app.setScreen();
        	}catch(InterruptedException e) {
        		e.printStackTrace();
        	}
        }
    }

}
