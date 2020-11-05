package logic;

public enum Direction {
    /**
     * Grass.
     */
    UP(1),

    /**
     * Street.
     */
    DOWN(-1),

    /**
     * Light.
     */
    RIGHT(2),

    /**
     * Wall.
     */
    LEFT(-2);
    
    /**
     * my value
     */
    private int myInteger;
    
    /**
     * Constructor of a new Direction.
     * @param theLetter the letter
     */
    Direction(final int theInteger) {
        myInteger = theInteger;
    }
    
    /**
     * Returns the Terrain represented by the given letter.
     * 
     * @param theLetter The letter.
     * @return the Terrain represented by the given letter, or GRASS if no
     *         Terrain is represented by the given letter.
     */
    public static Direction valueOf(final int theInteger) {
        Direction result = DOWN;

        for (final Direction dir : Direction.values()) {
            if (dir.myInteger == theInteger) {
                result = dir;
                break;
            }
        }

        return result;
    }
    
    public static Direction reverseValueOf(final Direction theDirection) {
        Direction result = DOWN;

        for (final Direction dir : Direction.values()) {
            if (dir.myInteger == (-theDirection.myInteger)) {
                result = dir;
                break;
            }
        }

        return result;
    }
}
