package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class Menu implements PropertyChangeListener {
	
	private int myWidth;
	
	private int myHeight;
	
	private PropertyChangeSupport myPcs;
	
	public Menu(int width, int height) {
		myWidth = width;
		myHeight = height;
		myPcs = new PropertyChangeSupport(this);
	}

	private void constructMenu() {
		
		// load game -- needs implementation
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
}
