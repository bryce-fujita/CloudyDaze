package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.Menu;

public class MenuController extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Menu myMenu;
	private static boolean clicked = false;
	public MenuController() {
		
	}
	
	public static void createAndShowMenu() {
		JFrame frame = new JFrame("Menu");
		frame.setSize(500,500);
		
		JPanel pane = new JPanel();
		
		
		myMenu = new Menu(300, 300);
		
		loadMenu(myMenu, pane, frame);
			
		
	}
	
	private static void loadMenu(Menu theMenu, JPanel pane, JFrame frame) {
		//frame.setSize(350, 350);
		
		JButton newGame = new JButton("New Game");
		newGame.addActionListener((ActionEvent e) -> {
			newGame();
			frame.dispose();
		});
		JButton loadGame = new JButton("Load Game");
		newGame.setSize(50, 50);
		loadGame.setSize(50, 50);
		pane.add(newGame);
		pane.add(loadGame);
		frame.setVisible(true);
		pane.setSize(20,20);
		frame.add(pane, BorderLayout.CENTER);
		
	}
	
	private static void newGame() {
		javax.swing.SwingUtilities.invokeLater(() -> MazeController.createAndShowGUI());
	}

	
	

}
