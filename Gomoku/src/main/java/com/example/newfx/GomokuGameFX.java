package com.example.newfx;

//here are all the staff we need to import in the gomoku game , actually the gomoku game can be divided into 3 calss, just like the example on course website , but I prefer to use only one class , it may be a bit complex , but I will put comments on every function in order to make it reader friendly
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;


public class GomokuGameFX extends Application {
    /*My basic idea of creating a Gomoku chess board is quite different . Firstly I will create a 15*15 Cell size square , Then for each cell there contains a circle , which is the area for clicking and a chess will be placed on it.
      Then I create a vertical line and a horizontal line for every circle, so then we will create a chessboard where players can place there chess on the intersection of the square*/
    private final int BOARD_SIZE = 15;
    private final int CELL_SIZE = 40;
    private final Circle[][] circles = new Circle[BOARD_SIZE][BOARD_SIZE];
    private final Line[][] horizontalLines = new Line[BOARD_SIZE][BOARD_SIZE];
    private final Line[][] verticalLines = new Line[BOARD_SIZE][BOARD_SIZE];
    private char [][]board = new char[BOARD_SIZE][BOARD_SIZE];
    private char currentPlayer = '1';
    private Label currentPlayerLabel;
    private Label playerInfoLabel;
    private Text timerLabel;
    private Text WinnerText;
    private boolean gameOver = false;
    private Timer timer;
    private int remainingTime = 30;
    private boolean isPaused = false;
    private long lastTime = 0;


    public void start(Stage primaryStage ){
        BorderPane root = new BorderPane();
        //first we create the chessboard
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(1);
        gridPane.setVgap(1);

        initializeBoard();

        for (int row = 0; row < BOARD_SIZE;row++){
            for (int col = 0 ; col< BOARD_SIZE;col++){
                StackPane cell = new StackPane();
                //The border of the chess area will be set as transparent for using friendly
                cell.setStyle("-fx-border-color: transparent;");
                cell.setPrefSize(CELL_SIZE,CELL_SIZE);

                //The circle is for the chess
                Circle circle = new Circle(CELL_SIZE / 2);
                circle.setFill(Color.TRANSPARENT);
                circle.setStroke(Color.WHITE);
                circles[row][col] = circle;

                //create the following chessboard
                Line horizontalLine = new Line(0, CELL_SIZE / 2, CELL_SIZE, CELL_SIZE / 2);
                Line verticalLine = new Line(CELL_SIZE / 2, 0, CELL_SIZE / 2, CELL_SIZE);
                horizontalLines[row][col] = horizontalLine;
                verticalLines[row][col] = verticalLine;

                //set the event handler after clicking the circle area
                int finalRow = row;
                int finalCol = col;
                cell.setOnMouseClicked(e -> handleCellClick(finalRow,finalCol));

                cell.getChildren().addAll(circle, horizontalLine, verticalLine);
                gridPane.add(cell,col,row);
            }
        }

        //We place a menu and add some function for Gomoku, Reset for start again the gomoku game , Exit for exit the game . pause for stop timecounting , continue for continue timecounting
        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("Game");
        MenuItem resetMenuItem = new MenuItem("Reset");
        MenuItem exitMenuItem = new MenuItem("Exit");

        MenuItem pauseMenuItem = new MenuItem("Pause");
        MenuItem continueMenuItem = new MenuItem("Continue");
        pauseMenuItem.setOnAction(e -> pauseTimer());
        continueMenuItem.setOnAction(e -> continueTimer());
        gameMenu.getItems().addAll(resetMenuItem, new SeparatorMenuItem(), pauseMenuItem, continueMenuItem);



        //set the event handler
        resetMenuItem.setOnAction(e -> resetGame());
        exitMenuItem.setOnAction(e -> primaryStage.close());

        gameMenu.getItems().addAll(resetMenuItem, new SeparatorMenuItem(), exitMenuItem);
        menuBar.getMenus().add(gameMenu);

        //display the currentPlayer and current Boardsize
        currentPlayerLabel = new Label("CurrentPlayer: " + currentPlayer + " | Current Board Size : "+ BOARD_SIZE + " * "+ BOARD_SIZE);
        playerInfoLabel = new Label("Player 1 : Black | Player 2: Red");
        playerInfoLabel.setAlignment(Pos.CENTER_RIGHT);
        WinnerText = new Text();
        WinnerText.setFont(Font.font("Arial", FontWeight.BOLD,20));
        WinnerText.setFill(Color.BLUE);

        //display the remaining thinking time for the player
        timerLabel = new Text("Time remaining" + remainingTime + "s");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        timerLabel.setFill(Color.RED);


        VBox playerInfoBox = new VBox(10);
        playerInfoBox.setAlignment(Pos.CENTER);
        playerInfoBox.getChildren().addAll(currentPlayerLabel, playerInfoLabel, timerLabel);

        //we set the position of each displacement
        root.setCenter(gridPane);
        root.setTop(menuBar);
        root.setRight(playerInfoBox);
        root.setLeft(WinnerText);

        //basic show process
        Scene scene = new Scene(root, CELL_SIZE * BOARD_SIZE, CELL_SIZE*BOARD_SIZE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gomoku game");
        primaryStage.show();

        startTimer();

    }

    //used for the pause function
    public void pauseTimer(){
        isPaused = true;
    }

    //used for the continue function
    public void continueTimer(){
        isPaused = false;
        lastTime = System.nanoTime();
    }

    //update the remaining time for each player and show out , if one player thinks for more than 30s , then the game will switch to the next player independently
    public void  updateTimer(){
        if(lastTime == 0){
            lastTime = System.nanoTime();
        }
        if(remainingTime >= 0 && !gameOver && !isPaused){
            long now = System.nanoTime();
            long elaspedTime = now - lastTime;
            if(elaspedTime >= 1_000_000_000){
                lastTime = now;
                remainingTime--;
                updateTimerLabel();
                if(remainingTime <= 0){
                    switchPlayer();
                }
            }
        }
    }

    //startTimer function is used for start  remaining time calculating
    public void startTimer(){
        new AnimationTimer(){
            public void handle(long now){
                updateTimer();
            }
        }.start();
    }

    //used to show the remaining time for the player
    private void updateTimerLabel(){
        timerLabel.setText("Time remaining: " + remainingTime + "s");
    }

    //when the thinking time exceeds 30s or a player has made his movement , then switch to another player
    private void switchPlayer(){
        currentPlayer = currentPlayer == '1' ? '2': '1';
        currentPlayerLabel.setText("CurrentPlayer: " + currentPlayer + " | Current Board Size : "+ BOARD_SIZE + " * "+ BOARD_SIZE);
        remainingTime = 30;
        updateTimerLabel();
    }

    //for the game bagin or clicking reset , then we initialize the chess board
    private void initializeBoard(){
        for(int row = 0; row < BOARD_SIZE; row++){
            for(int col = 0; col < BOARD_SIZE; col++){
                board[row][col] = '-';
            }
        }
    }

    // we will not stop the game till one winner has show up , and while one player made their movement the Text will display
    private void handleCellClick(int row, int col){
        if(gameOver){
            return;
        }
        if(board[row][col]=='-'){
            Color color = currentPlayer == '1' ? Color.BLACK : Color.RED;
            circles[row][col].setFill(color);
            board[row][col] = currentPlayer;
            currentPlayerLabel.setText("CurrentPlayer: " + currentPlayer + " | Current Board Size : "+ BOARD_SIZE + " * "+ BOARD_SIZE);
            if(checkWin(row,col)){
                WinnerText.setText("The winner is Player : " + currentPlayer);
                disableAllCells();
            }else if(isBoardFull()){
                WinnerText.setText("Draw!");
                gameOver = true;
            }else{
                switchPlayer();
            }
        }
    }

    private boolean isBoardFull(){
        for(int row = 0; row < BOARD_SIZE;row++){
            for(int col = 0; col < BOARD_SIZE;col++){
                if(board[row][col] == '-'){
                    return false;
                }
            }
        }
        return true;
    }

    //this function will be inplemented to test whether the winner has show out , we test from eight directions , and once we found a five together , them we print out the winner
    private boolean checkWin(int x, int y) {
        int[][]directions = {{0,1},{1,0},{1,1},{1,-1},{0,-1},{-1,0},{-1,1},{-1,-1}};
        for(int[] direction : directions){
            int count = 1;
            int dx = direction[0];
            int dy = direction[1];
            for(int i = 1 ; i < 5; i ++){
                int newX = x + i * dx;
                int newY = y + i * dy;
                if(!isValidPosition(newX,newY)){
                    break;
                }
                if(board[newX][newY] == board[x][y]){
                    count++;
                }else{
                    break;
                }
            }
            if(count>=5){
                disableAllCells();
                return true;
            }
        }
        return false;
    }

    //prevent the user clicking the same chess again
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    //resetGame function , as it seems, will reset the game once you click it
    private void resetGame(){
        gameOver = false;
        isPaused = false;
        remainingTime = 30;
        lastTime = 0;
        for (int row = 0 ; row < BOARD_SIZE; row++){
            for (int col = 0; col < BOARD_SIZE;col++){
                circles[row][col].setFill(Color.TRANSPARENT);
                board[row][col]='-';
            }
        }
        currentPlayer = '1';
        currentPlayerLabel.setText("CurrentPlayer: " + currentPlayer + " | Current Board Size : "+ BOARD_SIZE + " * "+ BOARD_SIZE);
        WinnerText.setText("");
        remainingTime = 30;
        updateTimerLabel();
    }

    //This is used for refreshing the board and delete all the chess which be signed
    private void disableAllCells() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                circles[row][col].setDisable(true);
            }
        }
        gameOver = true;
    }

    //the main function 
    public static void main(String[] args){
        launch(args);
    }
}