package main.core;

import java.util.*;

public class GameSingleton {

    /**
     * Single instance of the game itself
     */
    private static GameSingleton gameInstance = new GameSingleton();

    /**
     * The list of players that may or may not be active throughout the game.
     *
     * @see Player
     */
    private List<Player> players;

    /**
     * The world that the players will be playing on.
     *
     * @see WorldMap
     */
    private WorldMap world;

    /**
     * Contains the current number of active players.
     */
    private int numActivePlayer;

    /**
     * Scanner for user input.
     */
    private static Scanner myAction;

    /**
     * Private Constructor for Game
     */
    private GameSingleton(){
        //initialize map, player list, and scanner
        players = new ArrayList<>(6);
        world = new WorldMap("Earth");
        myAction = new Scanner(System.in);
        numActivePlayer = 0;
    }

    /** Static method to return the single instance of the game.
     *
     * @return Single instance of RISK game
     */
    public static GameSingleton getGameInstance() {
        if(gameInstance == null){
            gameInstance = new GameSingleton();
        }
        return gameInstance;
    }

    /**
     * Retrieves a territory from the map (or null).
     * {@link WorldMap#getTerritory(String)}
     *
     * @param name The name of the territory to search for
     * @return The requested territory (null if not found)
     */
    public Territory getTerritory(String name) {
        return world.getTerritory(name);
    }

    /**
     * Asks the current world to print itself.
     * {@link WorldMap#printMap}
     */
    public void printMap() {
        world.printMap();
    }

    /**
     * Generates a random order for the players.
     */
    private void shufflePlayers() {
        //need to make a single random field in game class
        Random rand = new Random();

        for (int i = players.size(); i > 0; i--) {
            Player holder = players.get(players.size() - i);
            int chosen = rand.nextInt(i);
            players.set(players.size() - i, players.get(chosen));
            players.set(chosen, holder);
        }
    }

    /**
     * Get the number of people playing the game
     */
    private void getNumPlayers(){
        String input;
        int numOfPlayers = 0;

        boolean validNumEntered = false;

        /*Continuously prompt the user for valid information*/
        while (!validNumEntered) {
            System.out.println("Please input the number of players (max-6 min-2): ");
            input = myAction.nextLine();

            //attempt to parse an integer value from the user's input
            try {
                numOfPlayers = Integer.parseInt(input);
                validNumEntered = true;

                //check if the number parsed is invalid
                if (numOfPlayers > 6 || numOfPlayers < 2) {
                    validNumEntered = false;
                    System.out.println("You input an invalid number, try again.");
                }

                //catch the exception (most commonly thrown when an integer can't be parsed)
            } catch (NumberFormatException e) {
                validNumEntered = false;
                System.out.println("You input an invalid number, try again.");
            }
        }

        setNumActivePlayer(numOfPlayers);
    }

    /** Get all player names and assign random colours for each player.
     */
    private void setColoursAndNames(){
        //six random colors for players
        List<String> randomColors = new LinkedList<>();
        randomColors.add("RED");
        randomColors.add("GREEN");
        randomColors.add("BLUE");
        randomColors.add("YELLOW");
        randomColors.add("WHITE");
        randomColors.add("BLACK");

        Random rand = new Random();

        /*
        We must get all player names and generate colours.
        Loop through players and obtain names through user input.
        Randomly assign colours.
         */
        for (int i = 0; i < this.getNumActivePlayer(); i++) {
            //get this players name
            System.out.print(String.format("main.core.Player %s Name: ", i + 1));
            String playerName = myAction.nextLine();

            int randIndex = rand.nextInt(randomColors.size());
            //generate and assign random colours
            String colour = randomColors.get(randIndex);
            System.out.println(String.format("main.core.Player %s Colour is: %s\n", i + 1, colour));
            randomColors.remove(randIndex);
            players.add(new Player(playerName, colour));
        }
    }

    /** Go through the current player's turn if they have not yet been eliminated
     * from the game.
     *
     * @param endStart String displayed at the start of the turn.
     * @param i The index of the current player in the list of players.
     * @return True if only one player remains, false otherwise
     */
    private boolean playerTurn(String endStart, int i) {
        if (players.get(i).isActive()) {
            Player currentPlayer = players.get(i);

            //print out number of remaining players and whose turn it is
            System.out.println(String.format("Remaining Players: %s\n", numActivePlayer));

            //beginning of turn print
            System.out.println(String.format(endStart, currentPlayer.getName(), "Begins!"));

            //While loop for current players turn
            boolean playerTurn = false;
            while (!playerTurn) {
                System.out.println(String.format("\nIt is %s of %s's turn.", players.get(i).getName(), players.get(i).getColour()));
                //Print out the available commands and asks for a command
                System.out.println("Commands: attack, worldstate, end");
                System.out.println("What do you want to do?");
                String command = myAction.nextLine().toLowerCase();
                System.out.println(String.format("Selected command: %s\n", command));
                switch (command) {
                    //Current player selected 'attack' : Begin attack protocol
                    case "attack":
                        /*
                           Attacking Conditions:
                           1) Defending territory must be a neighbour to Attacking territory
                           2) There are 2 units on the attacking territory (taken care of in implementation)
                           3) Attacking owner does not own Defending territory
                           */
                        Territory attacking = null;

                        //keep asking for a territory until we get a valid one
                        while (attacking == null) {
                            players.get(i).printOwned();
                            System.out.print("\nattack from where? ");
                            attacking = getTerritory(myAction.nextLine());
                            if (attacking == null) {
                                System.out.println("\nThat territory is not valid, try again.");
                                attacking = null;
                            } else if (world.getTerritoryOwner(attacking) != players.get(i)) {
                                System.out.println("\nYou do not own that territory, try again.");
                                attacking = null;
                            }
                        }

                        //we can use this method if we have conquered all neighbouring territories
                        if (!world.ownsAllTerritoryNeighbours(currentPlayer, attacking)) {
                            System.out.println("");

                            Territory defending = null;
                            while (defending == null) {
                                world.printTerritoryNeighbours(attacking);
                                System.out.print("\nwho to attack? ");
                                defending = getTerritory(myAction.nextLine());
                                if (defending == null) {
                                    System.out.println("\nThat territory is not valid, try again.");
                                    defending = null;
                                } else if (!world.areNeighbours(attacking, defending)) {
                                    System.out.println("\nThat territory is not a neighbour, try again.");
                                    defending = null;
                                } else if (currentPlayer.ownsTerritory(attacking) && currentPlayer.ownsTerritory(defending)) {
                                    System.out.println("\nYou can not attack yourself!");
                                    defending = null;
                                }
                            }
                            battle(attacking, defending);
                            System.out.println("");
                        } else {
                            System.out.println("You can not attack as you have conquered all neighbouring territories.\n");
                        }
                        break;
                        /*
                      The current player had ended their turn.
                      1) Print a turn ended message.
                      2) Break the current turn loop and move on to the next player.
                       */
                    case "end":
                        System.out.println("you typed end");
                        playerTurn = true;

                        //end of turn print
                        System.out.println(String.format(endStart, currentPlayer.getName(), "Ends!"));
                        break;
                    case "worldstate":
                        checkWorld();
                        break;
                    //Current player selected an invalid command, lets player know this and asks for a command again.
                    default:
                        System.out.println("Not a valid command");
                        break;
                }
                setPlayersEliminated();
                //Only one player remains, end the game.
                if (numActivePlayer == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Game has ended. Print the name and colour of the player who won the game.
     */
    private void endGame(){
        Player winner = null;

        for (Player p : players) {
            if (p.isActive()) winner = p;
        }

        System.out.println("|*----------------------------------------{GAME OVER}----------------------------------------*|");
        System.out.println(String.format("%s of %s has conquered all of %s! Hooray!", winner.getName(), winner.getColour(), world.getName()));
    }

    /**
     * Runs the current session corresponding to all the setup that has been done.
     * (Main main.core.Game Loop)
     */
    public void runGame() {

        getNumPlayers();

        setColoursAndNames();

        shufflePlayers();

        //print player order at the start of the game.
        System.out.println("The order of players: ");
        for (int i = 0; i < players.size(); i++) {
            System.out.println((i + 1) + " : " + players.get(i).getName() + " ; " + players.get(i).getColour());
        }

        String endStart = "|*----------------------------------------{%s's Turn %s}----------------------------------------*|";

        //main game loop
        boolean finished = false;
        while (!finished) {
            //loop through each player (Turns), until the game is over.
            for (int i = 0; i < players.size(); i++) {
                finished = playerTurn(endStart, i);
            }
        }

        endGame();
    }

    /**
     * Prints the current state of the world.
     */
    private void checkWorld() {
        System.out.println(String.format("|--------------------(World State: %s)--------------------|", world.getName()));
        printMap();
    }


    /*private int getPlayerDieCount(Player player, int lowerBound, int upperBound) {
        if (lowerBound==upperBound) return lowerBound;

        boolean validInput = false;
        int dieCount = 0;
        while (!validInput) {
            System.out.println(String.format("%s how many die would you like to roll with? (%s to %s)",player.getName(),lowerBound,upperBound));
            String attInput = myAction.nextLine();
            try {
                dieCount = Integer.parseInt(attInput);
                validInput = true;
                if (dieCount>upperBound||dieCount<lowerBound) {
                    validInput = false;
                }
            } catch (NumberFormatException e) {
                validInput = false;
            }
        }
        return dieCount;
    }*/

    private int getPlayerDice(Player player, Boolean isAttacking, int units){
        String attInput, defInput;

        int dieCount;

        String pName = player.getName();

        if (units == 1){
            System.out.println("You are defending with 1 defense dice!");
            return 1;
        } else if ((units == 2) && isAttacking) {
            System.out.println("You are attacking with 1 attack dice!");
            //attack with one die if the attacking territory contains exactly two units
            return 1;
        } else if ((units == 3) && isAttacking) {
            System.out.println(pName + ", would you like to attack with 1 or 2 dice?");
            attInput = myAction.nextLine();

            //check for invalid input (default to 2 dice if invalid)
            try {
                dieCount = Integer.parseInt(attInput);
                if(dieCount > 3 || dieCount < 1){
                    return 2;
                }
            } catch (NumberFormatException e) {
                //default choice is two attacking dice if input is invalid
                return 2;
            }
        } else {
            if(isAttacking){
                System.out.println(pName + ", would you like to attack with 1, 2 or 3 dice?");
                attInput = myAction.nextLine();

                //check for invalid input (default to 3 dice)
                try {
                    dieCount = Integer.parseInt(attInput);
                    if(dieCount > 4 || dieCount < 1){
                        return 3;
                    }
                } catch (NumberFormatException e) {
                    return 3;
                }
            }else{
                //defender chooses to roll one die or two dice
                System.out.println(pName + ", would you like to defend with 1 or 2 dice?");
                defInput = myAction.nextLine();

                //check for invalid input (default to 2 dice)
                try {
                    dieCount = Integer.parseInt(defInput);
                    if(dieCount > 2 || dieCount < 1){
                        return 2;
                    }
                } catch (NumberFormatException e) {
                    return 2;
                }
            }
        }

        //This line should never execute
        return 1;
    }

    /** Returns true if the battle is over via the defender fending off the attacker
     * or the attacker destroying all of the defender's units.
     *
     * @param attacking The territory that is attacking.
     * @param defending The territory that is being attacked.
     * @param attDice The number of dice the attacker previously rolled.
     * @return True if the battle is over, false otherwise
     */
    private boolean checkBattle(Territory attacking, Territory defending, int attDice){
        String end = "|------------------(Battle %s - %s)------------------|";
        if (defending.getUnits() == 0) {
            System.out.println(String.format("%s dominates over %s!", attacking.getName(), defending.getName()));
            System.out.println(String.format(end, "Ended", attacking.getName() + " Wins!"));
            world.addPlayerOwned(world.getTerritoryOwner(attacking),defending);
            fortifyPosition(attacking, defending, attDice);
            return true;
        } else if (attacking.getUnits() == 1) {
            System.out.println(String.format("%s drives off the attacker!", attacking.getName()));
            System.out.println(String.format(end, "Ended", defending.getName() + " Wins!"));
            return true;
        }
        return false;
    }

    /** Get the inputted command from the player who is attacking,
     * which is to either attack or retreat.
     *
     * @param attName The name of the player who is attacking.
     * @return The command that the player has entered
     */
    private String getBattleCommand(String attName){
        String battleCommand = null;
        while (battleCommand == null) {
            System.out.println(String.format("\n%s is attacking. %s, would you like to attack or retreat?", attName));
            battleCommand = myAction.nextLine().toLowerCase();
            if (!battleCommand.equals("attack") && !battleCommand.equals("retreat")) {
                System.out.println("You need to select either attack or retreat, try again.");
                battleCommand = null;
            }
        }
        return battleCommand;
    }

    /**
     * Simulates the battle sequence between a territory attacking an adjacent territory. The attacker
     * is required to select a number of dice to attack with provided he/she meets the minimum unit requirements
     *
     * @param attacking The territory containing units that will be used in the attack
     * @param defending The territory being attacked
     */
    private void battle(Territory attacking, Territory defending) {

        System.out.println(String.format("|------------------(Battle Commenced - %s vs. %s)------------------|", attacking.getName(), defending.getName()));

        String attName = world.getTerritoryOwner(attacking).getName();
        String defName = world.getTerritoryOwner(defending).getName();

        int attDice = 0;
        int defDice;

        boolean retreat = false;

        //Continue the attack step until either side loses all of its units or the attacker decides to retreat
        while (!retreat) {

            //display the units on both sides of the battle
            System.out.println(String.format("%s's Units: %s\n%s's Units: %s", attName, attacking.getUnits(), defName, defending.getUnits()));

            if (checkBattle(attacking,defending,attDice)){
                break;
            }

            String battleCommand = getBattleCommand(attName);

            //if the command is to attack or retreat
            if (battleCommand.equals("attack")) {

                attDice = getPlayerDice(world.getTerritoryOwner(attacking),true,attacking.getUnits());
                defDice = getPlayerDice(world.getTerritoryOwner(defending),false,defending.getUnits());

                //Proceed to the attack phase
                int[] lost = attack(attDice, defDice);
                attacking.removeUnits(lost[0]);
                defending.removeUnits(lost[1]);
                System.out.println("");

            } else if (battleCommand.equals("retreat")) {
                //Attacker chooses to retreat from the battle
                retreat = true;
                System.out.println(String.format("|------------------(Battle %s - %s)------------------|"+"Over", attacking.getName() + " Retreated"));
            }
        }
    }

    /**
     * Represents one smaller conflict between two territories.
     * This method represents the rolling of dice on both parties and the outcome of those rolls.
     *
     * @param attackRolls The number of dice the attacker is using for this attack
     * @param defendRolls The number of dice the defender is using for this defence
     * @return A pair of integers (position 0: how many units attacker lost, position 1: how many units defender lost)
     */
    private static int[] attack(int attackRolls, int defendRolls) {

        //random acts as die
        Random rand = new Random();

        //two primitive integer arrays to store random rolls
        int[] attackDice = new int[attackRolls];
        int[] defendDice = new int[defendRolls];

        //roll dice (random integer) for both parties and display simultaneously
        System.out.print("Attacking Rolls:   |");
        for (int i = 0; i < attackRolls; i++) {
            attackDice[i] = rand.nextInt(6) + 1;
            System.out.print(" " + attackDice[i] + " |");
        }
        System.out.print("\nDefending Rolls:  |");
        for (int i = 0; i < defendRolls; i++) {
            defendDice[i] = rand.nextInt(6) + 1;
            System.out.print(" " + defendDice[i] + " |");
        }

        //sort both rolls in descending order
        Arrays.sort(attackDice);
        Arrays.sort(defendDice);

        //Set counter variables for lost units in the attack
        int attackLost = 0;
        int defendLost = 0;

        /*
        Logic:
        If both attacking rolls are greater than both defending rolls, then defender loses two units.
        If the top defender roll is equal/greater than the top attacking roll while the second defending roll
            is less than the second attacking roll, then both players lose one unit.
        If both defender rolls are equal to or greater than both attacking rolls, then the attacker loses two
            units.
        If the attacker rolls one dice, then check if that roll is greater than the top defender roll or not
            and remove unit accordingly.
         */
        for (int i = attackRolls - 1; i >= 0; i--) {
            for (int j = defendRolls - (attackRolls - i); j >= 0; j--) {
                if (attackDice[i] > defendDice[j]) {
                    defendLost += 1;
                    break;
                } else {
                    attackLost += 1;
                    break;
                }
            }
        }
        //Return the result of the attack via units lost
        return new int[]{attackLost, defendLost};
    }

    /**
     * Fortify more units into one territory from an adjacent territory, such
     * that the current player owns both territories. At least one unit must
     * be left behind in the initial territory. Used after an attack once the
     * territory has been claimed.
     *
     * @param initialT The territory that will move units out
     * @param finalT The territory that will add units
     * @param attDice The number of dice that the attacker used (if applicable)
     */
    private static void fortifyPosition(Territory initialT, Territory finalT, int attDice) {

        String input;

        //Number of units to fortify
        int numUnits = 0;

        //True if valid number of units is provided by the player, false otherwise
        boolean fortifyCommand = false;

        //Keep looping until player enters a valid number of units to fortify
        while (!fortifyCommand) {
            System.out.println("How many troops would you like to move from " + initialT.getName() + " to " + finalT.getName() + "?");
            input = myAction.nextLine();

            //Check if player provides a number, not text
            try {
                numUnits = Integer.parseInt(input);
                fortifyCommand = true;
                //Check if number inputted is valid
                if (numUnits > initialT.getUnits() - 1 || numUnits < attDice) {
                    fortifyCommand = false;
                    System.out.println("Invalid number of units! Please enter a valid number of units" +
                            "(remember, after winning an attack, you must move units at least the number of attack dice you rolled");
                }
            } catch (NumberFormatException e) {
                //Input provided is not a number
                fortifyCommand = false;
                System.out.println("Invalid number of units! Please enter a valid number of units");
            }
        }

        //Move the units from the fortifying territory to the fortified territory
        initialT.removeUnits(numUnits);
        finalT.addUnits(numUnits);
    }

    /**
     * Update the number of players active.
     */
    public void setPlayersEliminated() {
        int numActive = 0;
        List<Territory> territories = world.getTerritories();
        for (Player player : players) {
            if (player.isActive()) {
                if (player.getOwnedTerritories().size() > 0) {
                    numActive += 1;
                } else {
                    //update
                    player.setActive(false);
                }
            }
        }
        numActivePlayer = numActive;
    }

    /** Get the number of players in the game who have not yet been eliminated.
     *
     * @return The number of players active in the game
     */
    public int getNumActivePlayer() {
        return numActivePlayer;
    }

    /** Set the number of players in the game who have not yet been eliminated.
     *
     * @param numActivePlayer The number of players in the game not yet eliminated.
     */
    public void setNumActivePlayer(int numActivePlayer) {
        this.numActivePlayer = numActivePlayer;
    }

}
