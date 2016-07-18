package game;

import gui.GameManager;

import java.util.*;

/**
 * Created by rares on 21.04.2016.
 */
public class TilesBag {
    private boolean available;
    char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    int[] values = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
    int[] distribution = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
    List<Pair> bag = new ArrayList<>();
    private Random randomGenerator = new Random();
    int remainingLetters = 100;
    private void initialize() {
        for (int i = 0; i < distribution.length; ++i) {
            Tile tile = new Tile(letters[i], values[i]);
            bag.add(new Pair(tile, distribution[i]));
        }
        available = true; //the bag containing the tiles was produced
    }


    public TilesBag() {
        initialize();
    }

    public synchronized List<Tile> getTilesFromBag(int k) {

        while(!available) { //while some other player is getting tiles from the bag - wait
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        available = false; //start using the bag
        notifyAll();
        if(k>remainingLetters) {
            k = remainingLetters;
        }
        int count = 0;
        List<Tile> tiles = new ArrayList<>();
        while (count < k) { //get k tiles from the bag randomly
            try {
                int index = randomGenerator.nextInt(bag.size());
                tiles.add(bag.get(index).tile);
                bag.get(index).remaining--;
                remainingLetters--;
                if (bag.get(index).remaining == 0) {
                    bag.remove(index);
                }
                count++;
            } catch(IllegalArgumentException e) {
                GameManager.gameOn = false;
            }

        }
        GameManager.remainingLetters.setText(String.valueOf(remainingLetters));
        GameManager.mainPanel.validate();
        GameManager.mainPanel.repaint();
        if(remainingLetters == 0) {
            GameManager.gameOn = false;
        }
        available = true; //free the bag
        notifyAll();
        return tiles;
    }


}
