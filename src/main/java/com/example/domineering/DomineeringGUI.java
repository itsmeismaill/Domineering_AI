package com.example.domineering;

import com.example.domineering.State.DomineeringGameState;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class DomineeringGUI extends Application {

    private static final int BOX_SIZE = 140; // Size of the box
    private static final int NUM_SQUARES = 5; // Number of squares in each dimension

    // the state of the game
    private final DomineeringGameState gameState = new DomineeringGameState(Player.HUMAN);


    //? first Player is horizontal Player
    private static final Color FIRST_PLAYER_COLOR = Color.RED;
    private static final Color FIRST_PLAYER_HOVER_COLOR = Color.web("#ffc9c9");
    private static final Color FIRST_PLAYER_HOVER_FILL_COLOR = Color.web("#e03131");

    //? second Player is vertical Player
    private static final Color SECOND_PLAYER_COLOR = Color.web("#1864ab");
    private static final Color SECOND_PLAYER_HOVER_COLOR = Color.web("#1864ab");
    private static final Color SECOND_PLAYER_HOVER_FILL_COLOR = Color.web("74c0fc");

    private static final Color DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final Color DEFAULT_FILL_COLOR_2 = Color.GRAY;
    private static final Color DEFAULT_STROKE_COLOR = Color.BLACK;

    private Label movesPlayer1Label;
    private Label maxPossibleMovesPlayer1Label;
    private Label movesPlayer2Label;
    private Label maxPossibleMovesPlayer2Label;

    private final Label adversaryLabel = new Label(gameState.getCurrentPlayerType().toString());
    private final Rectangle rectanglePlayer1 = new Rectangle((double) (BOX_SIZE * NUM_SQUARES) / 13, (double) (BOX_SIZE * NUM_SQUARES) / 13);
    private final Rectangle rectanglePlayer2 = new Rectangle((double) (BOX_SIZE * NUM_SQUARES) / 13, (double) (BOX_SIZE * NUM_SQUARES) / 13);
    private final GridPane gridPane = new GridPane();

    @Override
    public void start(Stage stage) {
        // Set the title of the window
        stage.setTitle("Welcome to Domineering Game!");

        int squareSize = (BOX_SIZE / NUM_SQUARES) * 3;

        for (int row = 0; row < NUM_SQUARES; row++) {
            for (int col = 0; col < NUM_SQUARES; col++) {
                Rectangle square = new Rectangle(squareSize, squareSize);
                square.setFill((row + col) % 2 == 0 ? DEFAULT_FILL_COLOR : DEFAULT_FILL_COLOR_2);
                square.setStroke(DEFAULT_STROKE_COLOR);
                square.setStrokeWidth(2);
                // Add the square to the grid
                gridPane.add(square, col, row);
                if (gameState.isDebugMode()) {
                    gridPane.add(new Text(col + "," + row), col, row);
                }

                square.getProperties().put("col", col);
                square.getProperties().put("row", row);

                square.setOnMouseClicked(this::onSquareClicked);

                square.setOnMouseEntered(event -> {
                    if (gameState.isCurrentPlayerType(2) && gameState.isCurrentPlayerType(Player.HUMAN)) return;
                    Rectangle hoveredSquare = (Rectangle) event.getSource();
                    Rectangle neighbourSquare = getNeighbourSquare((Rectangle) event.getSource(), gameState.getCurrentPlayer());
                    if (neighbourSquare == null) return;

                    Paint currentPlayerHoverColor = gameState.isCurrentPlayerType(1) ? FIRST_PLAYER_HOVER_COLOR : SECOND_PLAYER_HOVER_COLOR;
                    Paint currentPlayerFillColor = gameState.isCurrentPlayerType(1) ? FIRST_PLAYER_HOVER_FILL_COLOR : SECOND_PLAYER_HOVER_FILL_COLOR;

                    hoveredSquare.setStroke(currentPlayerHoverColor);
                    hoveredSquare.setFill(currentPlayerFillColor);
                    neighbourSquare.setStroke(currentPlayerHoverColor);
                    neighbourSquare.setFill(currentPlayerFillColor);
                });

                square.setOnMouseExited(event -> {
                    Rectangle hoveredSquare = (Rectangle) event.getSource();
                    Rectangle neighbourSquare = getNeighbourSquare((Rectangle) event.getSource(), gameState.getCurrentPlayer());
                    resetRectangleColors(hoveredSquare, neighbourSquare);
                });
            }
        }
        //add a title to the game
        Text title = new Text("Domineering Game");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-font-family: Monospaced;");
        //add a color black to the title
        title.setFill(Color.BLACK);
        title.setStroke(Color.GRAY);
        title.setStrokeWidth(2);
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), title);
        // Define the animation properties (move up and down)
        translateTransition.setFromY(0);
        translateTransition.setToY(-20);
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE);
        translateTransition.setAutoReverse(true);
        // Start the animation
        translateTransition.play();
        //add a rectangle
        Rectangle rectangle = new Rectangle((BOX_SIZE * NUM_SQUARES) / 1.3, (BOX_SIZE * NUM_SQUARES) / 1.5);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);
        rectanglePlayer1.setStroke(Color.BLACK);
        // set the position of the text
        rectanglePlayer1.setFill(FIRST_PLAYER_COLOR);
        rectanglePlayer2.setStroke(Color.BLACK);
        rectanglePlayer2.setFill(DEFAULT_FILL_COLOR);
        if (gameState.isCurrentPlayerType(1)) {
            rectanglePlayer1.setFill(FIRST_PLAYER_COLOR);
            rectanglePlayer2.setFill(DEFAULT_FILL_COLOR);
        } else {
            rectanglePlayer2.setFill(SECOND_PLAYER_COLOR);
            rectanglePlayer1.setFill(DEFAULT_FILL_COLOR);
        }
        GridPane table = new GridPane();
        table.setHgap(10);  // horizontal gap between the columns
        table.setVgap(10); // vertical gap between the rows
        table.setPadding(new Insets(10)); // the margin of the grid pane
        //style du tableau
        table.setStyle("-fx-background-color: #bfbaba; -fx-font-size: 15px; -fx-font-weight: bold; -fx-font-family: Monospaced;");
        // add titles to the table
        table.add(new Label("Player"), 0, 0);
        table.add(new Label("Moves"), 0, 1);
        movesPlayer1Label = new Label("0");
        table.add(movesPlayer1Label, 1, 1);
        table.add(new Label("Maximal Moves"), 0, 2);
        movesPlayer2Label = new Label("0");
        table.add(movesPlayer2Label, 2, 1);
        table.add(new Label("Human<you>"), 1, 0);
        table.add(adversaryLabel, 2, 0);

        maxPossibleMovesPlayer1Label = new Label(String.valueOf(countMaxPossibleMoves(1)));
        table.add(maxPossibleMovesPlayer1Label, 1, 2);
        maxPossibleMovesPlayer2Label = new Label(String.valueOf(countMaxPossibleMoves(2)));
        table.add(maxPossibleMovesPlayer2Label, 2, 2);


        // Create a MenuBar
        MenuBar menuBar = new MenuBar();

        // Create a Option menu
        Menu optionsMenu = new Menu("Options");

        // Create menu items
        MenuItem newGameMenuItem = new MenuItem("New game");
        MenuItem saveGameMenuItem = new MenuItem("Save game");
        MenuItem loadGameMenuItem = new MenuItem("Load game");
        MenuItem exitMenuItem = new MenuItem("Exit");


        // create game setting menu
        Menu playerSettingMenu = new Menu("Player mode");
        // create menu items
        MenuItem humanPlayerMenuItem = new MenuItem("Human");
        MenuItem randomPlayerMenuItem = new MenuItem("Random agent");
        MenuItem minMaxPlayerMenuItem = new MenuItem("Minimax agent");
        MenuItem alphaBetaPlaeryMenuItem = new MenuItem("Alpha-beta agent");

        // add menu items to the Player setting menu
        playerSettingMenu.getItems().addAll(humanPlayerMenuItem, randomPlayerMenuItem, minMaxPlayerMenuItem, alphaBetaPlaeryMenuItem);

        // set event handlers for menu items
        humanPlayerMenuItem.setOnAction(e -> {
            restartGame();
            gameState.setCurrentPlayerType(Player.HUMAN);
            adversaryLabel.setText(gameState.getCurrentPlayerType().toString());
        });
        randomPlayerMenuItem.setOnAction(e -> {
            restartGame();
            gameState.setCurrentPlayerType(Player.RANDOM);
            adversaryLabel.setText(gameState.getCurrentPlayerType().toString());
        });
        minMaxPlayerMenuItem.setOnAction(e -> {
            restartGame();
            gameState.setCurrentPlayerType(Player.MINIMAX);
            adversaryLabel.setText(gameState.getCurrentPlayerType().toString());
        });
        alphaBetaPlaeryMenuItem.setOnAction(e -> {
            restartGame();
            gameState.setCurrentPlayerType(Player.ALPHA_BETA);
            adversaryLabel.setText(gameState.getCurrentPlayerType().toString());
        });


        // Set event handlers for menu items
        newGameMenuItem.setOnAction(e -> restartGame());
        exitMenuItem.setOnAction(e -> exitGame());
        saveGameMenuItem.setOnAction(e -> saveGame());
        loadGameMenuItem.setOnAction(e -> loadGame());

        // Add menu items to the Options menu
        optionsMenu.getItems().addAll(newGameMenuItem, saveGameMenuItem, loadGameMenuItem, exitMenuItem);

        // Add the File menu to the MenuBar
        menuBar.getMenus().add(optionsMenu);
        menuBar.getMenus().add(playerSettingMenu);
        menuBar.setStyle("-fx-background-color: #bfbaba; -fx-font-size: 15px; -fx-font-weight: bold; -fx-font-family: Monospaced;");

        // creat a new scene with the vbox and Menubar as the root
        Pane root = new Pane();
        title.setLayoutY(120);
        title.setLayoutX(450);
        gridPane.setLayoutX(100);
        gridPane.setLayoutY(200);
        rectangle.setLayoutX(730);
        rectangle.setLayoutY(185);
        table.setLayoutX(870);
        table.setLayoutY(520);
        rectanglePlayer1.setLayoutX(800);
        rectanglePlayer1.setLayoutY(200);
        rectanglePlayer2.setLayoutX(1150);
        rectanglePlayer2.setLayoutY(200);
        root.getChildren().add(title);
        root.getChildren().add(gridPane);
        root.getChildren().add(menuBar);
        root.getChildren().add(rectangle);
        root.getChildren().add(table);
        root.getChildren().add(rectanglePlayer1);
        root.getChildren().add(rectanglePlayer2);


        Scene scene = new Scene(root, BOX_SIZE * NUM_SQUARES, BOX_SIZE * NUM_SQUARES);

        // Set the scene to the stage
        stage.setScene(scene);

        // Set the minimum size of the stage to create a square window
        stage.setMinWidth(BOX_SIZE * NUM_SQUARES + 20); // Consider the margin
        stage.setMinHeight(BOX_SIZE * NUM_SQUARES);
        stage.setFullScreen(true);

        // Show the stage
        stage.show();

    }


    private void restartGame() {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof Rectangle square) {
                square.setDisable(false);
                resetRectangleColor(square);
            }
        });
        maxPossibleMovesPlayer1Label.setText(String.valueOf(countMaxPossibleMoves(1)));
        maxPossibleMovesPlayer2Label.setText(String.valueOf(countMaxPossibleMoves(2)));
        updateUI();
    }

    private void exitGame() {
        // Display a confirmation dialog before exiting the game
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.initModality(Modality.APPLICATION_MODAL);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Exit Game");
        confirmationDialog.setContentText("Are you sure you want to exit the game?");

        // Customize the buttons in the confirmation dialog
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        // Show the confirmation dialog and handle the result
        confirmationDialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                // User clicked "Yes," so exit the application
                System.exit(0);
            }

        });
    }


    // Event handler for square click
    private void onSquareClicked(MouseEvent event) {
        // the human player can only play when it's his turn
        if (gameState.isCurrentPlayerType(Player.HUMAN) || gameState.isCurrentPlayerType(1)) {
            onSquareClickedHuman(event);
            if (!gameState.isCurrentPlayerType(Player.HUMAN)) {
                onSquareClickedComputer();
            }
        }
    }

    private void onSquareClickedComputer() {
        if (gameState.isCurrentPlayerType(Player.RANDOM)) {
            onSquareClickedComputerRandom();
        } else if (gameState.isCurrentPlayerType(Player.MINIMAX)) {
            onSquareClickedComputerMinMax();
        } else if (gameState.isCurrentPlayerType(Player.ALPHA_BETA)) {
            onSquareClickedComputerAlphaBeta();
        }
    }

    private void onSquareClickedComputerAlphaBeta() {
    }

    private void onSquareClickedComputerMinMax() {

    }

    private void onSquareClickedComputerRandom() {

        List<Rectangle> unPlayedSquares = gridPane.getChildren().stream().filter(node -> {
            Rectangle neighbourSquare = getNeighbourSquare((Rectangle) node, gameState.getCurrentPlayer());
            return !node.isDisable() && neighbourSquare != null && !neighbourSquare.isDisable();
        }).map(node -> (Rectangle) node).toList();

        if (!unPlayedSquares.isEmpty()) {
            Rectangle randomSquare = unPlayedSquares.get((int) (Math.random() * unPlayedSquares.size()));
            onSquareClickedHuman(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, null, 0, false, false, false, false, false, false, false, false, false, false, null) {
                @Override
                public Object getSource() {
                    return randomSquare;
                }
            });

        }


    }

    private void onSquareClickedHuman(MouseEvent event) {
        Rectangle neighbourSquare = getNeighbourSquare((Rectangle) event.getSource(), gameState.getCurrentPlayer());
        if (neighbourSquare != null) {
            Rectangle clickedSquare = (Rectangle) event.getSource();

            if (gameState.isCurrentPlayerType(1)) {
                gameState.incrementMovesPlayer(2);
                movesPlayer1Label.setText(String.valueOf(gameState.getMovesPlayer(1)));
                rectanglePlayer2.setFill(SECOND_PLAYER_COLOR);
                rectanglePlayer2.setStroke(Color.BLACK);
                rectanglePlayer1.setFill(DEFAULT_FILL_COLOR);
                rectanglePlayer1.setStroke(Color.BLACK);
            } else {
                gameState.incrementMovesPlayer(2);
                movesPlayer2Label.setText(String.valueOf(gameState.getMovesPlayer(2)));
                rectanglePlayer1.setFill(FIRST_PLAYER_COLOR);
                rectanglePlayer1.setStroke(Color.BLACK);
                rectanglePlayer2.setFill(DEFAULT_FILL_COLOR);
                rectanglePlayer2.setStroke(Color.BLACK);
            }
            Paint currentPlayerColor = gameState.isCurrentPlayerType(1) ? FIRST_PLAYER_COLOR : SECOND_PLAYER_COLOR;
            Paint currentPlayerStorkColor = DEFAULT_STROKE_COLOR;
            // filling the bottom square with the color of the current Player
            // filling the square with the color of the current Player
            clickedSquare.setFill(currentPlayerColor);
            clickedSquare.setStroke(currentPlayerStorkColor);
            // disable the square
            clickedSquare.setDisable(true);
            // filling the bottom square with the color of the current Player
            neighbourSquare.setFill(currentPlayerColor);
            neighbourSquare.setStroke(currentPlayerStorkColor);
            // disable the square
            neighbourSquare.setDisable(true);

            //switch Player
            gameState.switchPlayer();

            // check if the game is over
            checkIfGameIsOver();
            // update the max possible moves
            maxPossibleMovesPlayer1Label.setText(String.valueOf(countMaxPossibleMoves(1)));
            maxPossibleMovesPlayer2Label.setText(String.valueOf(countMaxPossibleMoves(2)));
        }

    }


    private void checkIfGameIsOver() {
        // enhance this function
        for (int row = 0; row < NUM_SQUARES; row++) {
            for (int col = 0; col < NUM_SQUARES; col++) {
                Rectangle square = (Rectangle) gridPane.getChildren().get(row * NUM_SQUARES + col);
                Rectangle neighbourSquare = getNeighbourSquare(square, gameState.getCurrentPlayer());
                if (!square.isDisable() && neighbourSquare != null && !neighbourSquare.isDisable()) return;
            }
        }
        // announce that the game is over
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        Text text = new Text("Winner is Player " + (gameState.isCurrentPlayerType(1) ? 2 : 1));
        text.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: Monospaced;");
        alert.setContentText(text.getText());
        alert.show();
    }


    public static void main(String[] args) {
        launch();
    }


    // saveGame function (using a file.txt)
    private void saveGame() {
        // Create a FileChooser object
        FileChooser fileChooser = new FileChooser();

        // Set the title of the FileChooser
        fileChooser.setTitle("Save Game");

        // Set the initial directory
        fileChooser.setInitialDirectory(new File("."));

        // Set the extension filter
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"), new FileChooser.ExtensionFilter("All Files", "*.*"));

        // Display the save file dialog
        File file = fileChooser.showSaveDialog(null);

        // Check if the user clicked the Save button
        if (file != null) {
            // Save the game to the file
            try (FileWriter fileWriter = new FileWriter(file)) {
                // Write the current player
                fileWriter.write(gameState.getCurrentPlayer() + "\n");

                // Write the number of moves for each player
                fileWriter.write(gameState.getMovesPlayer(1) + "\n");
                fileWriter.write(gameState.getMovesPlayer(2) + "\n");

                // Write the state of each square
                for (int row = 0; row < NUM_SQUARES; row++) {
                    for (int col = 0; col < NUM_SQUARES; col++) {
                        Rectangle square = (Rectangle) gridPane.getChildren().get(row * NUM_SQUARES + col);
                        fileWriter.write(square.isDisable() + "\n");
                        fileWriter.write(square.getFill().equals(FIRST_PLAYER_COLOR) + "\n");
                    }
                }

                // Display a confirmation dialog
                Alert confirmationDialog = new Alert(Alert.AlertType.INFORMATION);
                confirmationDialog.setTitle("Confirmation");
                confirmationDialog.setHeaderText("Game Saved");
                confirmationDialog.setContentText("The game has been saved successfully.");
                confirmationDialog.show();
            } catch (IOException e) {
                // Display an error dialog
                Alert errorDialog = new Alert(Alert.AlertType.ERROR);
                errorDialog.setTitle("Error");
                errorDialog.setHeaderText("Error Saving Game");
                errorDialog.setContentText("An error occurred while saving the game.");
                errorDialog.show();
            }
        }
    }

    //Add Load Game function (loading from a text file) // sohaib hani kancommenter b anglais
    private void loadGame() {
        // Create a FileChooser object
        FileChooser fileChooser = new FileChooser();

        // Set the title of the FileChooser
        fileChooser.setTitle("Load Game");

        // Set the initial directory
        fileChooser.setInitialDirectory(new File("."));

        // Set the extension filter
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"), new FileChooser.ExtensionFilter("All Files", "*.*"));

        // Display the open file dialog
        File file = fileChooser.showOpenDialog(null);

        // Check if the user clicked the Open button
        if (file != null) {
            // Load the game from the file
            try (Scanner scanner = new Scanner(file)) {
                // Read the current player
                gameState.setCurrentPlayer(scanner.nextInt());

                // Read the number of moves for each player
                gameState.setMovesPlayer(1, scanner.nextInt());
                gameState.setMovesPlayer(2, scanner.nextInt());


                // Read the state of each square
                for (int row = 0; row < NUM_SQUARES; row++) {
                    for (int col = 0; col < NUM_SQUARES; col++) {
                        Rectangle square = (Rectangle) gridPane.getChildren().get(row * NUM_SQUARES + col);
                        boolean isSquareDisabled = scanner.nextBoolean();
                        square.setDisable(isSquareDisabled);

                        // Read and set the color of the square
                        boolean isFirstPlayerColor = scanner.nextBoolean();
                        square.setFill(isFirstPlayerColor ? FIRST_PLAYER_COLOR : SECOND_PLAYER_COLOR);

                        if (isSquareDisabled) {
                            square.setStroke(DEFAULT_STROKE_COLOR);
                        } else {
                            square.setStroke(DEFAULT_STROKE_COLOR);
                        }
                    }
                }

                // Update the UI
                updateUI();

                // Display a confirmation dialog
                Alert confirmationDialog = new Alert(Alert.AlertType.INFORMATION);
                confirmationDialog.setTitle("Confirmation");
                confirmationDialog.setHeaderText("Game Loaded");
                confirmationDialog.setContentText("The game has been loaded successfully.");
                confirmationDialog.show();
            } catch (IOException e) {
                // Display an error dialog
                Alert errorDialog = new Alert(Alert.AlertType.ERROR);
                errorDialog.setTitle("Error");
                errorDialog.setHeaderText("Error Loading Game");
                errorDialog.setContentText("An error occurred while loading the game.");
                errorDialog.show();
            }
        }
    }

    //update ui (restore the UI to the last saved state)
    private void updateUI() {
        // Update the number of moves for each player
        movesPlayer1Label.setText(String.valueOf(gameState.getMovesPlayer(1)));
        movesPlayer2Label.setText(String.valueOf(gameState.getMovesPlayer(2)));

        // Update the color of the current player
        if (gameState.isCurrentPlayerType(1)) {
            rectanglePlayer1.setFill(FIRST_PLAYER_COLOR);
            rectanglePlayer1.setStroke(Color.BLACK);
            rectanglePlayer2.setFill(DEFAULT_FILL_COLOR);
            rectanglePlayer2.setStroke(Color.BLACK);
        } else {
            rectanglePlayer1.setFill(DEFAULT_FILL_COLOR);
            rectanglePlayer1.setStroke(Color.BLACK);
            rectanglePlayer2.setFill(SECOND_PLAYER_COLOR);
            rectanglePlayer2.setStroke(Color.BLACK);
        }

        // Update the color of each square
        for (int row = 0; row < NUM_SQUARES; row++) {
            for (int col = 0; col < NUM_SQUARES; col++) {
                Rectangle square = (Rectangle) gridPane.getChildren().get(row * NUM_SQUARES + col);
                if (square.isDisable()) {
                    square.setFill(square.getFill().equals(FIRST_PLAYER_COLOR) ? FIRST_PLAYER_COLOR : SECOND_PLAYER_COLOR);
                    square.setStroke(DEFAULT_STROKE_COLOR);
                } else {
                    square.setFill((row + col) % 2 == 0 ? DEFAULT_FILL_COLOR : DEFAULT_FILL_COLOR_2);
                    square.setStroke(DEFAULT_STROKE_COLOR);
                }
            }
        }
    }

    static private void resetRectangleColors(Rectangle... rectangles) {
        // call resetRectangleColor for each rectangle in the list
        Arrays.stream(rectangles).forEach(DomineeringGUI::resetRectangleColor);
    }

    private static void resetRectangleColor(Rectangle rectangle) {
        if (rectangle == null || rectangle.isDisable()) return;
        int row = (int) rectangle.getProperties().get("row");
        int col = (int) rectangle.getProperties().get("col");
        // set the previous color of the rectangle
        // set the default color of the rectangle
        rectangle.setFill((row + col) % 2 == 0 ? DEFAULT_FILL_COLOR : DEFAULT_FILL_COLOR_2); // Set the fill color to transparent
        rectangle.setStroke(DEFAULT_STROKE_COLOR); // Set the border color
    }


    private Rectangle getNeighbourSquare(Rectangle rectangle, int currentPlayer) {
        int clickedSquareRow = (int) rectangle.getProperties().get("row");
        int clickedSquareCol = (int) rectangle.getProperties().get("col");
        // check the current Player
        if (currentPlayer == 1)
            return this.gridPane.getChildren().stream().filter(node -> GridPane.getRowIndex(node) == clickedSquareRow + 1 && GridPane.getColumnIndex(node) == clickedSquareCol && !node.isDisable()).map(node -> (Rectangle) node).findFirst().orElse(null);
        else
            return this.gridPane.getChildren().stream().filter(node -> GridPane.getRowIndex(node) == clickedSquareRow && GridPane.getColumnIndex(node) == clickedSquareCol + 1 && !node.isDisable()).map(node -> (Rectangle) node).findFirst().orElse(null);
    }

    private int countMaxPossibleMoves(int currentPlayer) {
        int maxPossibleMoves = 0;
        for (int row = 0; row < NUM_SQUARES; row++) {
            for (int col = 0; col < NUM_SQUARES; col++) {
                Rectangle square = (Rectangle) gridPane.getChildren().get(row * NUM_SQUARES + col);
                Rectangle neighbourSquare = getNeighbourSquare(square, currentPlayer);
                if (!square.isDisable() && neighbourSquare != null && !neighbourSquare.isDisable()) maxPossibleMoves++;
            }
        }
        return maxPossibleMoves;
    }

}
