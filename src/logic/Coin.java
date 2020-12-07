package logic;

import model.Vertex;

public class Coin extends Item {
    
    
    public Coin(Vertex location) {
        super(5, "icons//Coin", "Coin", location);
    }
    
    @Override
    public int itemAction() {
        remove();
        return 5;
    }

}
