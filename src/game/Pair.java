package game;

/**
 * Created by rares on 21.04.2016.
 */
public class Pair {
    public Tile tile;
    public int remaining;


    public Pair(Tile tile, int distribution) {
        this.tile = tile;
        this.remaining = distribution;
    }
}
