package view;

import static model.PropertyChangeEnabledMaze.PROPERTY_SCORED;
import static model.PropertyChangeEnabledMaze.PROPERTY_PLAYER;
import static model.PropertyChangeEnabledMaze.PROPERTY_WON;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import logic.Direction;
import logic.Item;
import logic.Player;
import model.Maze;
import model.Vertex;
import model.Edge;

public class MazeController extends JPanel implements PropertyChangeListener, ActionListener{
    /** Set the delay for the timer. */
    private static int TIMER_DELAY = 80;
    
    private static int TILE_SIZE = 30;
    
    private static int EASY_DIFFICULTY = 5;
    
    private static int NORMAL_DIFFICULTY = 10;
    
    private static int HARD_DIFFICULTY = 15;
    
    private static final int TITLE_SIZE = 200;
    
    private static final int TEXT_BOX_WIDTH = 200;
    
    private static final int TEXT_BOX_HEIGHT = 60;
    
    private static final int BOX_OFFSET = 10;
    
    private static final int WINDOW_HEIGHT_OFFSET = 60;
    
    private static final int WINDOW_WIDTH_OFFSET = 16;
    /**
     * 
     */
    private static final long serialVersionUID = -9220529195101333347L;
    private List<JLabel> myPath;
    private List<JLabel> myItems;
    private List<JLabel> myClouds;
    private List<Component> myTitle;
    private static JLabel playerSprite;
    private static JLabel scoreLabel;
    private Maze myMaze;
    private int move;
    private Timer myTimer;
    private int myTime;
    private JFrame myFrame;
    private static Player myPlayer;
    private int numRows;
    private int numCols;
    private int difficulty;

    public MazeController(JFrame theFrame) {
        playerSprite = new JLabel(new ImageIcon("icons//Player_Standing_1.png"));
        scoreLabel = new JLabel();
        myTimer = new Timer(TIMER_DELAY, this);
        myTimer.start();
        myTime = 0;
        move = 0;
        difficulty = NORMAL_DIFFICULTY;
        numRows = NORMAL_DIFFICULTY;
        numCols = NORMAL_DIFFICULTY;
        myMaze = new Maze(numRows, numCols, true);
        myClouds = new ArrayList<>();
        myItems = new ArrayList<>();
        myPath = new ArrayList<>();
        myTitle = new ArrayList<>();
        myFrame = theFrame;
        theFrame.setContentPane(this);
        populateClouds();
        buildTitle(theFrame);
    }
    
    public static void createAndShowGUI() {
        
        //Create and set up the window.
        MazeFrame frame = new MazeFrame("Cloudy Days");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setIconImage(new ImageIcon("icons//CloudTile.png").getImage());
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addKeyListener(frame);
        
        int size = ((NORMAL_DIFFICULTY*2) + 1) * TILE_SIZE;
        frame.setSize(size + WINDOW_WIDTH_OFFSET, size + WINDOW_HEIGHT_OFFSET);
        
        //Create and set up the content pane.
        final MazeController pane = new MazeController(frame);
        pane.setOpaque(true); //content panes must be opaque
        pane.setLayout(null);
        frame.setJMenuBar(pane.buildMenuBar(frame));
//        File soundFile = new File("music//CloudyDaze.wav");
//        try { AudioInputStream in
//            = AudioSystem.getAudioInputStream(soundFile); Clip clip =
//            AudioSystem.getClip(); clip.open(in); clip.start();
//            clip.loop(Clip.LOOP_CONTINUOUSLY);
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) { 
//            e.printStackTrace();
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        }
    }
    
    private void buildTitle(JFrame frame) {
        reset(this);
        int size = ((NORMAL_DIFFICULTY*2) + 1) * TILE_SIZE;
        frame.setSize(size + WINDOW_WIDTH_OFFSET, size + WINDOW_HEIGHT_OFFSET);
        myTitle.clear();
        JLabel title = new JLabel(new ImageIcon("icons//Title.png"));
        title.setSize(TITLE_SIZE*2, TITLE_SIZE);
        title.setLocation((((numCols*2) + 1) * TILE_SIZE)/2 - TITLE_SIZE, TITLE_SIZE/2);
        
        int buttonX = ((((numCols*2) + 1) * TILE_SIZE)/2) - (TEXT_BOX_WIDTH/2);
        
        JButton newGame = new JButton(new ImageIcon("icons//New_Game.png"));
        newGame.setSize(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);
        newGame.setLocation(buttonX, title.getLocation().y + TITLE_SIZE + BOX_OFFSET);
        newGame.setOpaque(false);
        newGame.setBorder(null);
        newGame.addActionListener(ae -> {
            myMaze.removePropertyChangeListener(this);
            myMaze = new Maze(difficulty, difficulty, true);
            loadMaze(myMaze, this, frame);
        });
        
        JButton loadGame = new JButton(new ImageIcon("icons//Load_Game.png"));
        loadGame.setSize(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);
        loadGame.setLocation(buttonX, newGame.getLocation().y + TEXT_BOX_HEIGHT + BOX_OFFSET);
        loadGame.setOpaque(false);
        loadGame.setBorder(null);
        loadGame.addActionListener(ae -> {
            myMaze.removePropertyChangeListener(this);
            loadGame(frame);
        });
        
        /*JButton diff = new JButton(new ImageIcon("icons//Difficulty.png"));
        diff.setSize(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);
        diff.setLocation(buttonX, loadGame.getLocation().y + TEXT_BOX_HEIGHT + BOX_OFFSET);
        diff.setOpaque(false);
        diff.setBorder(null);
        String [] options = {"Easy", "Normal", "Hard"};
        diff.addActionListener(ae -> {
            String s = (String) JOptionPane.showInputDialog(null, "Choose a difficulty:", "Difficulty", JOptionPane.PLAIN_MESSAGE, null, options,options[1]);
            if (s.equals(options[0])) {
                difficulty = EASY_DIFFICULTY;
            } else if (s.equals(options[2])) {
                difficulty = HARD_DIFFICULTY;
            } else {
                difficulty = NORMAL_DIFFICULTY;
            }
        });*/
        
        JButton help = new JButton(new ImageIcon("icons//Help.png"));
        help.setSize(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT);
        help.setLocation(buttonX, loadGame.getLocation().y + TEXT_BOX_HEIGHT + BOX_OFFSET);
        help.setOpaque(false);
        help.setBorder(null);
        help.addActionListener(ae -> Instructions.createAndShowGUI());
        
        myTitle.add(title);
        myTitle.add(newGame);
        myTitle.add(loadGame);
        //myTitle.add(diff);
        myTitle.add(help);
        
        this.add(title,1);
        this.add(newGame,1);
        this.add(loadGame,1);
        //this.add(diff);
        this.add(help,1);
    }
    
    /**
     * Constructs a JMenuBar for the Frame.
     * @return the Menu Bar
     */
    private JMenuBar buildMenuBar(JFrame frame) {
        final JMenuBar bar = new JMenuBar();
        bar.add(buildFileMenu(frame));
        bar.add(buildHelpMenu());
        return bar;
    }
    
    /**
     * Builds the file menu for the menu bar.
     * 
     * @return the File menu
     */
    private JMenu buildFileMenu(JFrame frame) {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem newG = new JMenuItem("New Game");
        final JMenuItem saveG = new JMenuItem("Save Game");
        final JMenuItem loadG = new JMenuItem("Load Game");
       
        fileMenu.add(newG);
        fileMenu.add(saveG);
        fileMenu.add(loadG);
        fileMenu.addSeparator();

        final JMenuItem exitItem = new JMenuItem("Exit");
        
        exitItem.addActionListener(ae -> buildTitle(frame));
        newG.addActionListener(ae -> {
            myMaze.removePropertyChangeListener(this);
            myMaze = new Maze(difficulty, difficulty, true);
            loadMaze(myMaze, this, frame);
        });
        saveG.addActionListener(ae -> {
            saveGame();
        });
        loadG.addActionListener(ae -> {
            myMaze.removePropertyChangeListener(this);
            loadGame(frame);
        });
        
        fileMenu.add(exitItem);
        return fileMenu;
    }
    
    /**
     * Build the Help JMenu.
     * 
     * @return the Help JMenu
     */
    private JMenu buildHelpMenu() {
        final JMenu helpMenu = new JMenu("Help");
        
        final JMenuItem howtoItem = new JMenuItem("How to Play");
        helpMenu.add(howtoItem);

        final JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        
        howtoItem.addActionListener(ae -> Instructions.createAndShowGUI());
        aboutItem.addActionListener(ae ->
                        JOptionPane.showMessageDialog(new JFrame(), "Bryce Fujita and Ruvim Radchishin\n"
                                      + "Autumn 2020\n"
                        + "TCSS 360", "About Cloudy Days", JOptionPane.INFORMATION_MESSAGE,
                        null));
        return helpMenu;
    }
    
    private void reset(JPanel pane) {
        for (Component c : myTitle) {
            pane.remove(c);
        }
        for (JLabel jl : myItems) {
            pane.remove(jl);
        }
        for (JLabel jl : myPath) {
            pane.remove(jl);
        }
        pane.remove(playerSprite);
        pane.remove(scoreLabel);
    }
    
    private void loadMaze(Maze theMaze, MazeController pane, JFrame frame) {
        reset(pane);
        int size = ((NORMAL_DIFFICULTY*2) + 1) * TILE_SIZE;
        frame.setSize(size + WINDOW_WIDTH_OFFSET, size + WINDOW_HEIGHT_OFFSET);
        frame.repaint();
        myPlayer = theMaze.getPlayer();
        myItems.clear();
        myPath.clear();
        char[][] cMatrix = theMaze.getCharMatrix();
        
        //Draws out path
        for(int i = 0; i < (numCols*2) + 1; i++) {
            for(int j = 0; j < (numRows*2) + 1; j++) { 
                if (cMatrix[i][j] == ' ' || cMatrix[i][j] == '+') {
                    JLabel clpath = new JLabel(new ImageIcon("icons//CloudTile.png"));
                    clpath.setSize(TILE_SIZE,TILE_SIZE);
                    clpath.setLocation(TILE_SIZE*j,TILE_SIZE*i);
                    myPath.add(clpath);
                    pane.add(clpath,5);
                }
            }
        } 
        
        playerSprite.setSize(TILE_SIZE,TILE_SIZE);
        playerSprite.setLocation(TILE_SIZE, TILE_SIZE - (TILE_SIZE/4));
        
        pane.add(playerSprite, 0);
        pane.add(scoreLabel, 0);   
        drawItems();
        theMaze.addPropertyChangeListener(pane);
        scoreLabel.setSize(TILE_SIZE*6,TILE_SIZE);
        scoreLabel.setLocation((int) (TILE_SIZE*numCols*1.5), 0); 
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(TILE_SIZE - 5.0f));
        scoreLabel.setText("Score: 0");
        myMaze = theMaze;
        frame.requestFocusInWindow();
    }
    
    private void drawItems() {
        //Draws items onto path (Coins or Enemies)
        removeItems();
        myItems.clear();
        List<Vertex> items = myMaze.getItemLocations();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i).getItem();
            int row = items.get(i).getRow();
            int col = items.get(i).getCol();
            JLabel itemlb = new JLabel(new ImageIcon(item.getIcon() + ".png"));
            itemlb.setSize(TILE_SIZE,TILE_SIZE);
            itemlb.setLocation(TILE_SIZE*2*(col+1)-TILE_SIZE,TILE_SIZE*2*(row+1)-TILE_SIZE);
            myItems.add(itemlb);
            this.add(itemlb, 10);
        }
    }
    
    private void populateClouds() {
        Random rand = new Random();
        int randI = rand.nextInt(10);
        randI += 10;
        for (int i = 0; i < randI; i++) {
            JLabel cloud = new JLabel(new ImageIcon("icons//BackgroundCloud.png"));
            cloud.setSize(100,50);
            int height = rand.nextInt((numCols*2) * TILE_SIZE);
            int distance = rand.nextInt(((numRows*2) + 1) * TILE_SIZE);
            cloud.setLocation(distance, height);
            myClouds.add(cloud);
            this.add(cloud, 0);
        }
    }
    
    private void removeItems() {
        for (JLabel it : myItems) {
            this.remove(it);
        }
    }
    
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        this.setBackground(new Color(89, 205, 238));
    }
    
    private void moveBClouds() {
        for(JLabel cloud : myClouds) {
            cloud.setLocation(cloud.getLocation().x-(2), cloud.getLocation().y);
        }
    }
    
    private void updatePClouds(int theTime) {
        int mod = (theTime % 10);
        if ((mod < 5) && (mod >= 3)) {
            for(JLabel path : myPath) {
                path.setIcon(new ImageIcon("icons//CloudTile.png"));
                path.setLocation(path.getLocation().x, path.getLocation().y-1);
            }
            for(JLabel item : myItems) {
                item.setLocation(item.getLocation().x, item.getLocation().y-1);
            }
        } else if ((mod >= 5) && (mod >=8)) {
            for (JLabel path : myPath) {
                path.setIcon(new ImageIcon("icons//CloudTile2.png"));
                path.setLocation(path.getLocation().x, path.getLocation().y + 1);
            }
            for(JLabel item : myItems) {
                item.setLocation(item.getLocation().x, item.getLocation().y + 1);
            }
        }
    }
    
    private void updatePlayer(int theTime) {
        Player player = myMaze.getPlayer();
        String cdir = "";
        if (player.getDirection() == Direction.RIGHT) {
            cdir = "R";
        } else if (player.getDirection() == Direction.LEFT) {
            cdir = "L";
        } else if (player.getDirection() == Direction.UP) {
            cdir = "U";
        }
        if (player.isMoving() && move < (TILE_SIZE*2)) {
            int mod = (theTime % 20);
            if (mod < 5) {
                playerSprite.setIcon(new ImageIcon("icons//Player_Standing_1" + cdir + ".png"));
            } else if ((mod >= 5) && (mod < 10)) {
                playerSprite.setIcon(new ImageIcon("icons//Player_Running_1" + cdir + ".png"));
            } else if ((mod >= 10) && (mod < 15)) {
                playerSprite.setIcon(new ImageIcon("icons//Player_Standing_2" + cdir + ".png"));
            } else if ((mod >= 15) && (mod < 20)) {
                playerSprite.setIcon(new ImageIcon("icons//Player_Running_2" + cdir + ".png"));
            }
            move++;
            movePlayer();
            if (move >= TILE_SIZE) {
                move = 0;
                player.setMoving(false);
                Vertex myTru = player.getVertex();
                playerSprite.setLocation((myTru.getCol()+1)*(TILE_SIZE*2) - TILE_SIZE, ((myTru.getRow()+1)*TILE_SIZE*2) - (TILE_SIZE/4)- TILE_SIZE);
            }
        }else {
            move = 0;
            int mod = (theTime % 10);
            if (mod < 5) {
                playerSprite.setIcon(new ImageIcon("icons//Player_Standing_1" + cdir + ".png"));
            } else {
                playerSprite.setIcon(new ImageIcon("icons//Player_Standing_2" + cdir + ".png"));
            }
        }
    }
    
    public void saveGame() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(null);  //Opens directory to the system's default
            fc.setDialogTitle("Save Game");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {  //If selected path is chosen and accepted.
                String fileloc = (""+fc.getCurrentDirectory()); //cheat to turn file location into a string.
                System.out.println("File Location: " + fileloc);
                File directory = fc.getSelectedFile();
                String filePath = directory.getAbsolutePath();
                String fileName = JOptionPane.showInputDialog("Please enter a name for your file:") + ".bin";  //sets name for file.
                String newFilePath = filePath + "\\" + fileName;  //saves into the location.
                System.out.println(newFilePath);
                FileOutputStream newF = new FileOutputStream(new File(newFilePath));
                ObjectOutputStream out = new ObjectOutputStream(newF);
                out.writeObject(myMaze);
                out.close();
                newF.close();
                System.out.println("Object has been serialized.");
            } else { //No acceptable locations chosen
                System.out.println("No Selection ");
            }
        } catch (Exception e) {
            String s = "Something went wrong.";
            s += "\nIf I knew more about Java, maybe I would have done something";
            s += "\nBut for now, nothing.";
            JOptionPane.showMessageDialog(null,s);

        }
    }
    
    
    public void loadGame(JFrame frame) {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(null);  //Opens directory to the system's default
            fc.setDialogTitle("Load Game");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {  //If selected path is chosen and accepted.
                File game = fc.getSelectedFile();
                FileInputStream oldF = new FileInputStream(game);
                ObjectInputStream in = new ObjectInputStream(oldF);
                myMaze = (Maze) in.readObject();
                oldF.close();
                in.close();
                System.out.println("Object has been serialized.");
                loadMaze(myMaze, this, frame);
                System.out.println("Maze Loaded");
            } else { //No acceptable locations chosen
                System.out.println("No Selection ");
            }
        } catch (Exception e) {
            String s = "Something went wrong.";
            s += "\nIf I knew more about Java, maybe I would have done something";
            s += "\nBut for now, nothing.";
            JOptionPane.showMessageDialog(null,s);

        }
    }
    private void movePlayer() {
        Player player = myMaze.getPlayer();
        if (player.getDirection().equals(Direction.UP)) {
            Point pos = playerSprite.getLocation();
            playerSprite.setLocation(pos.x, pos.y-2);
        } else if (player.getDirection().equals(Direction.DOWN)) {
            Point pos = playerSprite.getLocation();
            playerSprite.setLocation(pos.x, pos.y+2);
        } else if (player.getDirection().equals(Direction.RIGHT)) {
            Point pos = playerSprite.getLocation();
            playerSprite.setLocation(pos.x+2, pos.y);
        } else if (player.getDirection().equals(Direction.LEFT)) {
            Point pos = playerSprite.getLocation();
            playerSprite.setLocation(pos.x-2, pos.y);
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent theEvent) {
        if(PROPERTY_PLAYER.equals(theEvent.getPropertyName())) {
            myPlayer.setMoving(true);
        } else if(PROPERTY_SCORED.equals(theEvent.getPropertyName())) {
            drawItems();
            int score = (Integer) theEvent.getNewValue();
            scoreLabel.setText("Score: " + score);
            if (score < 0) {
                JOptionPane.showMessageDialog(null, "GAME OVER!!! \n Score: " + (Integer) theEvent.getNewValue());
                myMaze.removePropertyChangeListener(this);
                myMaze = new Maze(numRows, numCols, true);
                loadMaze(myMaze, this, myFrame);
            }
        } else if(PROPERTY_WON.equals(theEvent.getPropertyName())) {
            JOptionPane.showMessageDialog(null, "CONGRATS YOU WIN!!! \n Score: " + (Integer) theEvent.getNewValue());
            myMaze.removePropertyChangeListener(this);
            myMaze = new Maze(numRows, numCols, true);
            loadMaze(myMaze, this, myFrame);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        myTime++;
        Random rand = new Random();
        int randI = rand.nextInt(350);
        if (randI <= 10) {
            JLabel cloud = new JLabel(new ImageIcon("icons//BackgroundCloud.png"));
            cloud.setSize(100,50);
            int height = rand.nextInt((numRows*2) * TILE_SIZE);
            int distance = (((numCols*2) + 1) * TILE_SIZE);
            cloud.setLocation(distance, height);
            myClouds.add(cloud);
            this.add(cloud);
        }
        moveBClouds();
        updatePClouds(myTime);
        updatePlayer(myTime);
        repaint();
    }

    private static class MazeFrame extends JFrame implements KeyListener {
        public MazeFrame(String theTitle) {
            super(theTitle);
        }
        
        public Vertex findVert(HashSet<Edge> edges, Direction theDir) {
            Vertex returnMe = null;
            for (Edge c : edges) {
                if (c.myDir == theDir) return c.end;
            }
            return returnMe;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(!myPlayer.isMoving()) {
                Vertex curLocation = myPlayer.getVertex();
                HashSet<Edge> curEdges = curLocation.getEdges();
                HashSet<Direction> validDirections = new HashSet<Direction>();
                for (Edge c : curEdges) {
                    validDirections.add(c.myDir);
                }
                int keyCode = e.getKeyCode();
                if ((keyCode == 38) && (validDirections.contains(Direction.UP))) {
                    myPlayer.setDirection(Direction.UP);
                    Vertex next = findVert(curEdges, Direction.UP);
                    myPlayer.setMove(next);
                } else if ((keyCode == 40) && (validDirections.contains(Direction.DOWN))) {
                    myPlayer.setDirection(Direction.DOWN);
                    Vertex next = findVert(curEdges, Direction.DOWN);
                    myPlayer.setMove(next);
                } else if ((keyCode == 39) && (validDirections.contains(Direction.RIGHT))) {
                    myPlayer.setDirection(Direction.RIGHT);
                    Vertex next = findVert(curEdges, Direction.RIGHT);
                    myPlayer.setMove(next);
                } else if ((keyCode == 37) && (validDirections.contains(Direction.LEFT))) {
                    myPlayer.setDirection(Direction.LEFT);
                    Vertex next = findVert(curEdges, Direction.LEFT);
                    myPlayer.setMove(next);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
        
        
        @Override
        public void keyTyped(KeyEvent e) {
        }
    }
}