package view;

import static model.PropertyChangeEnabledMaze.PROPERTY_SCORED;
import static model.PropertyChangeEnabledMaze.PROPERTY_PLAYER;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
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
    final private static int TIMER_DELAY = 80;
    
    final private static int TILE_SIZE = 30;
    
    final private static int NUM_ROWS = 10;
    
    final private static int NUM_COLS = 10;
    
    /**
     * 
     */
    private static final long serialVersionUID = -9220529195101333347L;
    private static List<JLabel> myPath;
    private static List<JLabel> myItems;
    private static JLabel playerSprite;
    private static JLabel scoreLabel;
    private static List<JLabel> myClouds;
    private static Maze myMaze;
    private int move;
    private Timer myTimer;
    private int myTime;

    public MazeController(Maze theMaze, JFrame theFrame) {
        myMaze = theMaze;
        myClouds = new ArrayList<>();
        myItems = new ArrayList<>();
        myPath = new ArrayList<>();
        playerSprite = new JLabel(new ImageIcon("icons//Player_Standing_1.png"));
        scoreLabel = new JLabel("Score: 0");
        myTimer = new Timer(TIMER_DELAY, this);
        myTimer.start();
        myTime = 0;
        move = 0;
        drawItems(this);
        loadMaze(myMaze, this, theFrame);
    }
    
    public static void createAndShowGUI() {
        //New maze//
        Maze maze = new Maze(NUM_ROWS, NUM_COLS, true);
        
        //Create and set up the window.
        MazeFrame frame = new MazeFrame("Maze Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        //Create and set up the content pane.
        final MazeController pane = new MazeController(maze, frame);
        pane.setOpaque(true); //content panes must be opaque
        pane.setLayout(null);
        frame.pack();
        frame.setVisible(true);
        frame.addKeyListener(frame);
        int x = ((NUM_ROWS*2) + 1) * TILE_SIZE;
        int y = ((NUM_COLS*2) + 1) * TILE_SIZE;

        frame.setSize(y + 15, x + 38);
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
       
        fileMenu.add(newG);
        fileMenu.addSeparator();

        final JMenuItem exitItem = new JMenuItem("Exit");
        
        exitItem.addActionListener(ae -> System.exit(0));
        newG.addActionListener(ae -> {
            myMaze = new Maze(NUM_ROWS, NUM_COLS, true);
            loadMaze(myMaze,this, frame);
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

        final JMenuItem infoItem = new JMenuItem("Race Info...");
        helpMenu.add(infoItem);
        infoItem.setEnabled(false);

        final JMenuItem aboutItem = new JMenuItem("About...");
        helpMenu.add(aboutItem);
        
        aboutItem.addActionListener(ae ->
                        JOptionPane.showMessageDialog(new JFrame(), "Bryce Fujita and Ruvim Radchishin\n"
                                      + "Autumn 2020\n"
                        + "TCSS 360", "About CloudyDaze", JOptionPane.INFORMATION_MESSAGE,
                        null));
        return helpMenu;
    }
    
    private static void reset(JPanel pane) {
        for (JLabel jl  : myClouds) {
            pane.remove(jl);
        }
        for (JLabel jl  : myItems) {
            pane.remove(jl);
        }
        for (JLabel jl  : myPath) {
            pane.remove(jl);
        }
    }
    
    private static void loadMaze(Maze theMaze, MazeController pane, JFrame frame) {
        reset(pane);
        char[][] cMatrix = theMaze.getCharMatrix();
        
        //Draws out path
        for(int i = 0; i < (NUM_ROWS*2) + 1; i++) {
            for(int j = 0; j < (NUM_COLS*2) + 1; j++) { 
                if (cMatrix[i][j] == ' ' || cMatrix[i][j] == '+') {
                    JLabel clpath = new JLabel(new ImageIcon("icons//CloudTile.png"));
                    clpath.setSize(TILE_SIZE,TILE_SIZE);
                    clpath.setLocation(TILE_SIZE*j,TILE_SIZE*i);
                    myPath.add(clpath);
                    pane.add(clpath);
                }
            }
        } 
        
        scoreLabel.setSize(TILE_SIZE*6,TILE_SIZE);
        scoreLabel.setLocation(frame.getWidth()-(TILE_SIZE*5), 0);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(TILE_SIZE - 5.0f));
        scoreLabel.setText("Score : 0");
        playerSprite.setSize(TILE_SIZE,TILE_SIZE);
        playerSprite.setLocation(TILE_SIZE, TILE_SIZE - (TILE_SIZE/4));
        
        pane.add(playerSprite, 0);
        pane.add(scoreLabel, 0);   
        drawItems(pane);
        theMaze.addPropertyChangeListener(pane);
        frame.setContentPane(pane);
    }
    
    private static void drawItems(JPanel pane) {
        //Draws items onto path (Coins or Enemies)
        removeItems(pane);
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
            pane.add(itemlb, 0);
        }
    }
    
    private static void removeItems(JPanel pane) {
        for (JLabel it : myItems) {
            pane.remove(it);
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
            Player player = myMaze.getPlayer();
            player.setMoving(true);
        } else if(PROPERTY_SCORED.equals(theEvent.getPropertyName())) {
            drawItems(this);
            scoreLabel.setText("Score: " + (Integer) theEvent.getNewValue());
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
            int height = rand.nextInt((NUM_COLS*2) * TILE_SIZE);
            int distance = (((NUM_ROWS*2) + 1) * TILE_SIZE);
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
            Player myPlayer = myMaze.getPlayer();
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