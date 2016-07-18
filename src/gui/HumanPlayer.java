package gui;

import game.Tile;
import game.TilesBag;
import gui.GameManager;
import game.Turns;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by rares on 22.04.2016.
 */
public class HumanPlayer extends JPanel implements Runnable {
    java.util.List<Tile> letters = new ArrayList<>();
    java.util.List<Tile> word = new ArrayList<>();
    java.util.List<Tile> solutionWord = new ArrayList<>();
    boolean[] visited;
    String playerName = "You";
    Turns turns;
    int k = 7;
    TilesBag bag;
    JLabel name;
    JLabel score;
    JLabel lettersAvaialable;
    JLabel lettersLabel = new JLabel("Letters:", SwingUtilities.CENTER);
    JButton drawTiles = new JButton("Get new tiles");
    JTextField wordGetter = new JTextField();
    JButton submitWord = new JButton("Submit word");
    JButton endTurn = new JButton("End turn");
    boolean wait = true;

    @Override
    public void run() {

        play();
    }

    void generateGUI() {
        name = new JLabel(playerName, SwingUtilities.CENTER);
        score = new JLabel("0 points", SwingUtilities.CENTER);
        lettersAvaialable = new JLabel("No letters", SwingUtilities.CENTER);
        wordGetter.setEditable(false);
        submitWord.setEnabled(false);
        submitWord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String word = wordGetter.getText();
                if (word.length() == 0) {
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

                } else {
                    if (wordIsValid(word)) {
                        DefaultTableModel model = (DefaultTableModel) GameManager.gameTable.getModel();
                        model.addRow(new Object[]{playerName, word, getScoreFromString(word)});
                        updateScore(getScoreFromString(word));
                        for (int j = 0; j < word.length(); ++j) {
                            for (int i = 0; i < letters.size(); i++) {
                                if (letters.get(i).getLetter() == word.charAt(j)) {
                                    letters.remove(i);
                                    break;
                                }
                            }
                        }
                        k = word.length();
                    }
                }
                endTurn.doClick();
            }
        });
        endTurn.setEnabled(false);
        endTurn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                wordGetter.setEditable(false);
                submitWord.setEnabled(false);
                endTurn.setEnabled(false);
                drawTiles.setEnabled(false);
                GameManager.mainPanel.validate();
                GameManager.mainPanel.repaint();
                for (int i = 1; i < GameManager.noPlayers; ++i) {
                    turns.setTurn(i, true);
                }
                GameManager.computerTurn = true;
            }
        });
        drawTiles.setEnabled(false);
        drawTiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                letters.clear();
                k = 7;
                letters.addAll(bag.getTilesFromBag(k));
                lettersAvaialable.setText(getStringFromTilesList(letters));
            }
        });
        this.setOpaque(true);
        this.setBackground(Color.magenta);
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        this.setLayout(new GridLayout(4, 2));
        this.add(name);
        this.add(score);
        this.add(lettersLabel);
        this.add(lettersAvaialable);
        this.add(wordGetter);
        this.add(submitWord);
        this.add(drawTiles);
        this.add(endTurn);
    }

    public HumanPlayer(Turns turns, TilesBag bag) {
        this.turns = turns;
        this.bag = bag;
        generateGUI();
    }

    void play() {
        while (GameManager.gameOn) {
            if (turns.isHumanPlayerTurn()) {
                GameManager.computerTurn = false;
                wordGetter.setEditable(true);
                submitWord.setEnabled(true);
                endTurn.setEnabled(true);
                drawTiles.setEnabled(true);
                if (letters.size() < 7) {
                    letters.addAll(bag.getTilesFromBag(k));
                    updateLetters(letters);
                }
                GameManager.mainPanel.validate();
                GameManager.mainPanel.repaint();
            }
        }
        lettersAvaialable.setText("");
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

    String getStringFromTilesList(java.util.List<Tile> tiles) {
        StringBuilder word = new StringBuilder("");
        for (int i = 0; i < tiles.size(); i++) {
            word.append(tiles.get(i).getLetter());
        }
        return String.valueOf(word);
    }

    java.util.List<Tile> hardCopy(java.util.List<Tile> word) {
        java.util.List<Tile> copy = new ArrayList<>();
        for (int i = 0; i < word.size(); ++i) {
            copy.add(word.get(i));
        }
        return copy;
    }

    int getScore(java.util.List<Tile> word) {
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

    void updateLetters(java.util.List<Tile> letters) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < letters.size(); i++) {
            sb.append(letters.get(i).getLetter());
        }
        lettersAvaialable.setText(String.valueOf(sb));
    }

    boolean wordIsValid(String word) {
        java.util.List<Character> availableLetters = new ArrayList<>();
        for (int i = 0; i < letters.size(); ++i) {
            availableLetters.add(letters.get(i).getLetter());
        }
        for (int i = 0; i < word.length(); ++i) {
            if (!availableLetters.contains(word.charAt(i))) {
                return false;
            } else {
                for (int j = 0; j < availableLetters.size(); ++j) {
                    if (availableLetters.get(j) == word.charAt(i)) {
                        availableLetters.remove(j);
                        break;
                    }
                }
            }
        }
        if (GameManager.dictionary.search(word)) {
            return true;
        }
        return false;
    }

    int getScoreFromString(String word) {
        int wordScore = 0;
        for (int i = 0; i < word.length(); i++) {
            for (int j = 0; j < letters.size(); j++) {
                if (letters.get(j).getLetter() == word.charAt(i)) {
                    wordScore += letters.get(j).getValue();
                    break;
                }
            }
        }
        return wordScore;
    }
}
