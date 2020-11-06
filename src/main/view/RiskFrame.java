package main.view;


import main.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.awt.Graphics;
/**
 * This class represents a GUI for the game risk, including the feature of
 *  an interactive map.
 *
 * @author Tony Zeidan
 */
public class RiskFrame extends JFrame implements RiskGameView,ActionListener {
    private GameSingleton riskModel;
    private JLabel playerTurnLbl;
    private double scalingX;
    private double scalingY;
    private RiskEventPane eventPane;
    private Graphics gr;
    private Image finalMapImage;
    /**
     * Stores the territory clicked on by the user.
     * @see RiskController
     */
    private Territory selectedTerritory;

    /**
     * Stores the action selected by the user.
     * @see RiskController
     */
    private int selectedAction;

    /**
     * Stores the points that will be painted on the map.
     * It is altered constantly depending on user inputs.
     */
    private Map<Territory,Point> nodesToPaint;

    /**
     * JPanel containing the game board (the map).
     */
    private JPanel board;

    /**
     * The button for attacking. It is a field as it needs to be
     * altered in other methods.
     */
    private JButton attack;

    /**
     * The button for ending turn. It is a field as it needs to be
     * altered in other methods.
     */
    private JButton endTurn;

    /**
     * Constructor for instances of main.view.RiskFrame, constructs a new GUI.
     *
     * NEEDS ALTERING FOR COMMUNICATION WITH MODEL AND
     * CONTROLLER
     */
    public RiskFrame() {
        super("RISK");
        //TODO: Call GameSingleton.getGameInstance() instead
        riskModel = GameSingleton.getGameInstance(getPlayers(getNumOfPlayers()));
        board=null;
        setLayout(new BorderLayout());
        selectedAction = -1;
        nodesToPaint = null;
        scalingX=1;
        scalingY=1;
        composeFrame();
        riskModel.setUpGame();
        showFrame();
    }

    //TODO
    /**
     *
     * @param sX
     * @param sY
     */
    public void scaleWorld(double sX,double sY) {
//        for (Point p : riskModel.getAllCoordinates().values()) {
//            p.setLocation(p.x*scalingX,p.y*scalingY);
//        }
    }

    /**
     * Generates and places all components on the frame, this should
     * generally only be called once per frame.
     */
    private void composeFrame() {

        RiskController rc = new RiskController(riskModel,this);
        riskModel.addHandler(this);

        //attempt to read the map file
        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(getClass().getResource("/resources/RiskBoard.png"));
        } catch (IOException ioException) {
            System.out.println("RISK Board Load Failed");
            ioException.printStackTrace();
        }

        Dimension og = getSize();
        finalMapImage=mapImage;
        //setPointsToPaint(riskModel.getAllCoordinates());
        board = new JPanel() {

            Dimension previous = null;

            /**
             * Paints the JPanel component with the given graphics.
             * It also uses the given graphics instance to draw a scaled version of
             * the image on the panel, along with the points representing territories and
             * the labels that go with them.
             *
             * @param g The dedicated graphics for this panel
             */
            @Override
            protected void paintComponent(Graphics g) {
                System.out.println("in paintComponent");
                super.paintComponent(g);
                board.removeAll();  //clears the labels off of the board

                Dimension current = getSize();
                if (previous!=null && !(current.equals(previous))) {
                    scalingX = current.getWidth()/previous.getWidth();
                    scalingY = current.getHeight()/previous.getHeight();
                    scaleWorld(scalingX,scalingY);
                }
                previous = current;

                //draws the scaled version of the map image
                g.drawImage(finalMapImage.getScaledInstance(getWidth(),getHeight(),
                        Image.SCALE_SMOOTH), 0, 0, null);
                System.out.println("asd");
                paintPoints(g);     //paint points representing territories
                placePointLabels();     //paint the labels to go with the points
            }
        };
        board.addMouseListener(rc);
        board.setLayout(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);

        JRadioButtonMenuItem fs = new JRadioButtonMenuItem("Fullscreen");
        fs.addActionListener(this);
        menu.add(fs);

        //create a massive separator in the menu bar
        menuBar.add(Box.createHorizontalGlue());
        playerTurnLbl = new JLabel();
        playerTurnLbl.setOpaque(true);
        menuBar.add(playerTurnLbl);    //we must update this with the players turn
        setJMenuBar(menuBar);

        /*
        This panel contains the buttons for the players turn.
            - we must enable them and disable them accordingly
         */
        JPanel buttonPane = new JPanel(new GridLayout(2,1));
        attack = new JButton("Attack");
        endTurn = new JButton("End Turn");
        attack.addActionListener(rc);
        endTurn.addActionListener(rc);
        buttonPane.add(attack);
        buttonPane.add(endTurn);

        eventPane = new RiskEventPane();

        //add everything to the main content pane
        getContentPane().add(BorderLayout.CENTER,board);
        getContentPane().add(BorderLayout.SOUTH,buttonPane);
        getContentPane().add(BorderLayout.WEST,eventPane);

        //make the program terminate when frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //just to make sure everything has been reset for the start of the game
        restoreGUI();

        //set size of frame
        setSize(new Dimension(1200,800));

        rc.gameStart();
        //prepare
        //pack();
    }

    //TODO
    /**
     *
     * @return
     */
    public Territory getSelectedTerritory() {
        return selectedTerritory;
    }

    //TODO
    /**
     *
     * @return
     */
    public int getSelectedAction() {
        return selectedAction;
    }

    //TODO
    /**
     *
     * @param selectedAction
     */
    public void setSelectedAction(int selectedAction) {
        this.selectedAction = selectedAction;
    }

    /**
     * Enables or disables the Attack Button
     * @param enabled
     */
    public void setAttackable(boolean enabled) {
        attack.setEnabled(enabled);
    }

    /**
     * Enables or disables the End Turn Button
     * @param enabled
     */
    public void setEndable(boolean enabled) {
        endTurn.setEnabled(enabled);
    }

    //TODO
    /**
     *
     * @return
     */
    public Map<Territory,Point> getPointsToPaint() {
        return nodesToPaint;
    }

    //TODO
    /**
     *
     * @param pointsToPaint
     */
    public void setPointsToPaint(Map<Territory,Point> pointsToPaint) {
       // this.pointsToPaint=pointsToPaint;
    }

    //TODO
    /**
     *
     * @param color
     * @return
     */
    private static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    //TODO
    /**
     *
     * @param
     */
    private void paintPoints(Graphics g) {

        /*if (pointsToPaint==null) return;
        for (Territory t : pointsToPaint.keySet()) {
            Point p = pointsToPaint.get(t);

//            Map<Territory,Point> neighbourNodes = riskModel.getNeighbouringNodes(t);
//            for (Point p2 : neighbourNodes.values()) {
//                g.drawLine(p2.x+6,p2.y+6,p.x+6,p.y+6);
//            }
        }*/

        for (Territory t : nodesToPaint.keySet()) {
            Point p = nodesToPaint.get(t);
            g.setColor(Color.BLACK);

            int x = (int) (p.getX());
            int y = (int) (p.getY());
            g.fillOval(x-2,y-2,16,16);
            Player player = t.getOwner();
            g.setColor(player.getColour().getValue());
            g.fillOval(x,  y, 12, 12);
        }
        System.out.println("1");
    }

    //TODO
    /**
     *
     */
    public void placePointLabels() {
        if (nodesToPaint==null) return;

        for (Territory t : nodesToPaint.keySet()) {
            Point p = nodesToPaint.get(t);
//            int x = (int) (p.getX());
//            int y = (int) (p.getY());
//            JLabel lbl = new JLabel(t.getName());
//            JLabel lbl2 = new JLabel(String.valueOf(t.getUnits()));
//
//            lbl.setFont(new Font("Segoe UI",Font.BOLD,9));
//            lbl2.setFont(new Font("Segoe UI",Font.BOLD,11));
//
//            Insets insets = board.getInsets();
//            Dimension lblSize = lbl.getPreferredSize();
//            Dimension lblSize2 = lbl2.getPreferredSize();
//            lbl.setBounds(25 + insets.left, 5 + insets.top,
//                    lblSize.width, lblSize.height);
//            board.add(lbl);
//            lbl2.setBounds(30 + insets.left, 5 + insets.top,
//                    lblSize2.width, lblSize2.height);
//            board.add(lbl);
//            board.add(lbl2);
//
//            //Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
//
//            lbl.setLocation(p.x-(lbl.getWidth()/2)+2,p.y-15);
//            lbl2.setLocation(p.x+15,p.y);
//            lbl.setForeground(Color.BLACK);
//            RiskColour playerColour = t.getOwner().getColour();
//            lbl2.setForeground(playerColour.getValue());
//            lbl.setBackground(Color.WHITE);
//            lbl2.setBackground(Color.WHITE);
//            //lbl.setBorder(raisedEtched);
//            //lbl2.setBorder(raisedEtched);
//            lbl.setOpaque(true);
//            lbl2.setOpaque(true);
//            System.out.println("a");
        }
    }

    //TODO
    /**
     *
     */
    public void showFrame() {
        setResizable(true);
        setVisible(true);
    }

    //TODO
    /**
     *
     * @return
     */
    private int getNumOfPlayers()
    {
        String input;
        int numOfPlayers = 0;
        Object[] options = {"2", "3", "4", "5", "6"};
        input  = (String) JOptionPane.showInputDialog(this,"How many players?","Number of Players",
                JOptionPane.QUESTION_MESSAGE,null,options,"2");
        //User pressed close or cancel
        if(input == null){
            System.exit(0);
        }
        return numOfPlayers = Integer.parseInt(input);
    }

    //TODO
    /**
     *
     * @param numPlayers
     * @return
     */
    private List<Player> getPlayers(int numPlayers)
    {
        ArrayList<Player> players = new ArrayList<>();
        for(int i= 0; i<numPlayers;i++) {
            String input = null;
            while(input == null || input.length() == 0){
                input = JRiskOptionPane.showPlayerNameDialog(this,i+1);
            }
            players.add(new Player(input));
        }
        return players;
    }

    /**
     * We need some sort of updating methods that will do the following.
     *
     * 1) update the lists when a territory is selected
     */

    public static void main(String[] args) {
        RiskFrame rf = new RiskFrame();
    }

    //TODO
    /**
     *
     * @param info
     */
    public void setCurrentInstruction(String info) {
        eventPane.setCurrentInstruction(info);
    }

    //TODO
    /**
     *
     * @param territory
     */
    public void setInfoDisplay(Territory territory) {
        Player p = territory.getOwner();
        eventPane.clearSelectedTerritoryDisplay();
        eventPane.setInfoDisplay(p,territory);
    }

    //TODO
    /**
     *
     */
    public void restoreGUI() {
        selectedAction = -1;
        selectedTerritory = null;
        //setPointsToPaint(riskModel.getAllCoordinates());
        attack.setText("Attack");
        attack.setEnabled(false);
        endTurn.setEnabled(true);
        eventPane.clearSelectedTerritoryDisplay();
        eventPane.addEvent(riskModel.getCurrentPlayer().getName()+
                ", please select a territory or end your turn.");
    }

    //TODO
    /**
     *
     * @param selectedTerritory
     */
    public void setSelectedTerritory(Territory selectedTerritory) {
        this.selectedTerritory = selectedTerritory;
    }

    //TODO
    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JRadioButtonMenuItem fs = (JRadioButtonMenuItem) e.getSource();
        dispose();
        if (fs.isSelected()) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setPreferredSize(new Dimension(1200,800));
        }
        setUndecorated(fs.isSelected());
        setVisible(true);
    }

    //TODO
    /**
     *
     * @param e
     */
    @Override
    public void handleRiskUpdate(RiskEvent e) {
        RiskEventType eventType = e.getType();
        Object trigger = e.getTrigger();
        //if (eventDescriptions.getSize()==25) eventDescriptions.clear();

        System.out.println(eventType);
        //TODO: only tell game board to repaint when necessary
        switch (eventType) {
            case UPDATE_MAP:
                //for selecting on our map we need a reference
                nodesToPaint = (HashMap<Territory,Point>)trigger;
                board.repaint();
            case GAME_STARTED:
            case GAME_OVER:
            case ATTACK_COMMENCED:
                //eventPane.addEvent((String) trigger);
                break;
            case TURN_BEGAN:
                Player beganPlayer = (Player) trigger;
                Color playerColour = beganPlayer.getColour().getValue();
                eventPane.addEvent(String.format("%s's turn has began", beganPlayer.getName()));
                playerTurnLbl.setText("it is : "+beganPlayer.getName()+"'s turn.        ");
                playerTurnLbl.setBackground(playerColour);
                playerTurnLbl.setForeground(getContrastColor(playerColour));
                break;
            case TURN_ENDED:
                //TODO: trigger this event when the next turn method is called
                //TODO: but before the player is actually switched
                Player endedPlayer = (Player) trigger;
                eventPane.addEvent(String.format("%s's turn had ended", endedPlayer.getName()));
                break;
            case DIE_ROLLED:
                eventPane.addEvent("Rolled: " + trigger);
                break;
            case ATTACK_COMPLETED:
            case UNITS_MOVED:
            case TERRITORY_DEFENDED:
            case TERRITORY_DOMINATION:
                eventPane.addEvent((String) trigger);
                board.revalidate();
                break;
            default:
                return;
        }
    }
}