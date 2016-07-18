package gui;

import gui.GameManager;
import game.Tile;
import game.TilesBag;
import game.Turns;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by rares on 20.04.2016.
 */
public class ComputerPlayer extends JPanel implements Runnable {
    List<Tile> letters = new ArrayList<>();
    List<Tile> word = new ArrayList<>();
    List<Tile> solutionWord = new ArrayList<>();
    boolean[] visited;
    String playerName;
    int color;
    int id;
    int k = 7;
    Turns turns;
    TilesBag bag;

    JLabel name;
    JLabel score;
    JLabel lettersAvaialable;
    JLabel action;
    Color[] colors = {Color.red, Color.yellow, Color.green, Color.cyan, Color.red, Color.orange, Color.white,
            Color.yellow, Color.green, Color.cyan, Color.red, Color.darkGray, Color.orange, Color.white,
    };

    public ComputerPlayer(String playerName, int color, int id, Turns turns, TilesBag bag) {
        this.playerName = playerName;
        this.color = color;
        this.id = id;
        this.turns = turns;
        this.bag = bag;
        generateGUI();
    }

    @Override
    public void run() {

        play();
    }

    void generateGUI() {
        name = new JLabel(playerName, SwingUtilities.CENTER);
        score = new JLabel("0 points", SwingUtilities.CENTER);
        lettersAvaialable = new JLabel("No letters", SwingUtilities.CENTER);
        action = new JLabel("Waiting for player", SwingUtilities.CENTER);
        this.setOpaque(true);
        this.setBackground(colors[color]);
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        this.setLayout(new GridLayout(2, 2));
        this.add(name);
        this.add(score);
        this.add(lettersAvaialable);
        this.add(action);
    }

    void play() {
        while (GameManager.gameOn) {
            if (GameManager.computerTurn) {
                if (turns.isMyTurn(id)) {
                    letters.addAll(bag.getTilesFromBag(k));
                    updateLetters(letters);
                    solutionWord.clear();
                    visited = new boolean[letters.size()];
                    seachForWord(0);
                    if (solutionWord.size() > 0) {
                        DefaultTableModel model = (DefaultTableModel) GameManager.gameTable.getModel();
                        model.addRow(new Object[]{getStringFromTilesList(solutionWord), playerName, String.valueOf(getScore(solutionWord))});
                        for (int i = 0; i < solutionWord.size(); i++) {
                            for (int j = 0; j < letters.size(); j++) {
                                if (solutionWord.get(i).getLetter() == letters.get(j).getLetter()) {
                                    letters.remove(j);
                                    break;
                                }
                            }
                        }
                        updateScore(getScore(solutionWord));
                        k = solutionWord.size();// update k (number of tiles to draw from the bag)
                    } else {
                        letters.clear();
                        k = 7;
                    }
                    turns.setTurn(id, false);
                }
            }
        }
        GameManager.timer.stop();
        GameManager.remLettersLabel.setText("");
        GameManager.remainingLetters.setText("Game finished");
        GameManager.mainPanel.validate();
        GameManager.mainPanel.repaint();
    }

    void seachForWord(int p) {
        if (p < letters.size()) {
            for (int i = 0; i < letters.size(); ++i) {
                if (!visited[i]) {
                    visited[i] = true;
                    word.add(letters.get(i));
                    String currentWord = getStringFromTilesList(word);
                    if (GameManager.dictionary.startsWith(currentWord)) { //valid prefix
                        if (GameManager.dictionary.search(currentWord)) { //solution
                            solutionWord = hardCopy(word);
                        }
                        seachForWord(p + 1);
                    }
                    word.remove(word.size() - 1);
                    visited[i] = false;
                }
            }
        }
    }

    String getStringFromTilesList(List<Tile> tiles) {
        StringBuilder word = new StringBuilder("");
        for (int i = 0; i < tiles.size(); i++) {
            word.append(tiles.get(i).getLetter());
        }
        return String.valueOf(word);
    }

    List<Tile> hardCopy(List<Tile> word) {
        List<Tile> copy = new ArrayList<>();
        for (int i = 0; i < word.size(); ++i) {
            copy.add(word.get(i));
        }
        return copy;
    }

    int getScore(List<Tile> word) {
        int scoreValue = 0;
        for (int i = 0; i < word.size(); i++) {
            scoreValue += word.get(i).getValue();
        }
        return scoreValue;
    }

    void updateScore(int wordScore) {
        int newScore = Integer.parseInt(score.getText().split(" ")[0]) + wordScore;
        score.setText(String.valueOf(newScore) + " points");
    }

    void updateLetters(List<Tile> letters) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < letters.size(); i++) {
            sb.append(letters.get(i).getLetter());
        }
        lettersAvaialable.setText(String.valueOf(sb));
    }
}
