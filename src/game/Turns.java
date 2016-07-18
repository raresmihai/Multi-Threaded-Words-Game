package game;

/**
 * Created by rares on 21.04.2016.
 */
public class Turns {
    private volatile boolean available = true;
    volatile boolean turns[];

    public Turns(int noPlayers) {
        turns = new boolean[noPlayers];
    }

    public synchronized void setTurn(int player, boolean value) {
        turns[player] = value;
    }

    public synchronized boolean isHumanPlayerTurn() {
        for (int i = 0; i < turns.length; ++i) {
            if (turns[i]) {//there is a computer player turn
                return false;
            }
        }
        return true;
    }

    public boolean isMyTurn(int id) {
        return turns[id];
    }


}
