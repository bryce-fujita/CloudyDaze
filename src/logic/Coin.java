package logic;

import model.Vertex;

public class Coin extends Item {
    
    /** The number of points to reward a player. */
    private static final int REWARD = 5;
    
    /**
     * Constructor for coin.
     * @param location Vertex of where the coin is located
     */
    public Coin(Vertex location) {
        super(REWARD, "icons//Coin", "Coin", location);
    }
    
    /**
     * Used to reward player points.
     */
    @Override
    public int itemAction() {
        remove();
        return REWARD;
    }

}
