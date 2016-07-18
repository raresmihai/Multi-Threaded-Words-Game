package gui;


import dictionary.DictionaryReader;
import dictionary.Trie;
import game.TilesBag;
import game.Turns;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by rares on 27.03.2016.
 */
public class GameManager {

    DictionaryReader dictionaryReader = new DictionaryReader();
    public static Trie dictionary;
    TilesBag bag = new TilesBag();
    Turns turns;
    public static boolean gameOn = true;
    public static volatile boolean computerTurn;
    public static int noPlayers;
    public static JLabel remainingLetters;
    public static JLabel remLettersLabel;
    public JFrame frame;
    public static JPanel mainPanel;
    public static JTable gameTable;
    JPanel playersPanel;
    public static Timer timer = new Timer();

    public static void runApp() {
        try {
            GameManager window = new GameManager();
            window.frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameManager() {
        dictionary = dictionaryReader.getDictionary();
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(280, 100, 450, 300);
        frame.setSize(800, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        //center-right Component
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));


        JPanel gameView = new JPanel();
        gameView.setLayout(new BorderLayout(0, 0));

        String columnHeadings[] = {"Player", "Word", "Score"};


        DefaultTableModel tableModel = new DefaultTableModel(0, 3);
        tableModel.setColumnIdentifiers(columnHeadings);
        gameTable = new JTable(tableModel);
        gameTable.setForeground(Color.blue);
        gameTable.setEnabled(false);

        gameView.add(new JScrollPane(gameTable), BorderLayout.CENTER);

        JPanel gameInfo = new JPanel(new GridLayout(0, 3));
        remLettersLabel = new JLabel("Remaining letters:", SwingUtilities.CENTER);
        remLettersLabel.setForeground(Color.green);
        remainingLetters = new JLabel("100", SwingUtilities.CENTER);
        remainingLetters.setForeground(Color.orange);
        gameInfo.add(remLettersLabel);
        gameInfo.add(remainingLetters);
        gameInfo.add(timer);


        gameView.add(gameInfo, BorderLayout.SOUTH);

        playersPanel = new JPanel();


        JSplitPane mainSplitPane = new JSplitPane();
        mainSplitPane.setLeftComponent(playersPanel);
        mainSplitPane.setRightComponent(gameView);
        mainSplitPane.setDividerLocation(350);

        mainPanel.add(mainSplitPane, BorderLayout.CENTER);


        JPanel topPanel = new JPanel(new GridLayout(0, 3));
        JLabel label = new JLabel("Enter number of players:", SwingConstants.CENTER);
        final JTextField noOfPLayers = new JTextField();
        JButton start = new JButton("START GAME");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread timerThread = new Thread(timer);
                timerThread.start();

                noPlayers = Integer.parseInt(noOfPLayers.getText());
                playersPanel.setLayout(new GridLayout(noPlayers, 0));
                turns = new Turns(noPlayers);
                Runnable humanPlayer = new HumanPlayer(turns, bag);
                new Thread(humanPlayer).start();
                playersPanel.add((JPanel) humanPlayer);
                for (int i = 1; i < noPlayers; i++) {
                    Runnable computerPlayer = new ComputerPlayer("Player" + i, i, i, turns, bag);
                    new Thread(computerPlayer).start();
                    playersPanel.add((JPanel) computerPlayer);
                    playersPanel.validate();
                    playersPanel.repaint();
                }
            }
        });
        topPanel.add(label);
        topPanel.add(noOfPLayers);
        topPanel.add(start);
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
}
