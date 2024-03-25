package com.example.predatorandprey;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloController
{

    private boolean isSimulationRunning = false;


    @FXML
    private Slider speedSlider;


    @FXML
    private TextField preyCountTextField;

    @FXML
    private TextField predatorCountTextField;

    @FXML
    private TextField gridSizeTextField;

    @FXML
    private TextField tfSizeAnimals;

    @FXML
    private Label simulationStepLabel;

    @FXML
    private Label predatorCountLabel;

    @FXML
    private Label preyCountLabel;




    @FXML
    private Button btnStartStop;
    @FXML
    private Button btnReset;

    @FXML
    private GridPane gridPane;

//    @FXML
//    private LineChart<Integer, Integer> populationChart;
//    private XYChart.Series<Integer, Integer> predatorSeries;
//    private XYChart.Series<Integer, Integer> preySeries;


    private AnimationTimer simulationTimer;



    private int simulationSteps = 0;


    /** Wielkośc grida */
    private int GRID_SIZE = 10;
    /** Wielkośc grida */
    private int SIZE_ANIMALS = 50;

    /** Ilość drapieżników */
    private int PREDATOR_SIZE = 10;
    /** Ilość ofiar */
    private int PREYS_SIZE = 10;



    // Metoda inicjalizująca
    @FXML
    private void initialize()
    {
        createGrid();
        placePredatorsAndPreys();
        setupChart();

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isSimulationRunning)
            {
                stopSimulation();
                startSimulation();
            }
        });

        simulationStepLabel.setText("0");
    }

    private void createGrid()
    {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        for (int i = 0; i < GRID_SIZE; i++)
        {
            for (int j = 0; j < GRID_SIZE; j++)
            {
                Rectangle rectangle = new Rectangle(SIZE_ANIMALS, SIZE_ANIMALS);
                rectangle.setStroke(Color.BLACK);
                rectangle.setFill(Color.WHITE);
                gridPane.add(rectangle, i, j);
            }
        }
    }

    private void placePredatorsAndPreys()
    {
        Random random = new Random();

        for (int i = 0; i < PREDATOR_SIZE; i++)
        {
            int x = random.nextInt(GRID_SIZE);
            int y = random.nextInt(GRID_SIZE);
            Rectangle predator = new Rectangle(SIZE_ANIMALS, SIZE_ANIMALS);
            predator.setStroke(Color.BLACK);
            predator.setFill(Color.RED);
            gridPane.add(predator, x, y);
        }

        for (int i = 0; i < PREYS_SIZE; i++)
        {
            int x = random.nextInt(GRID_SIZE);
            int y = random.nextInt(GRID_SIZE);
            Rectangle prey = new Rectangle(SIZE_ANIMALS, SIZE_ANIMALS);
            prey.setStroke(Color.BLACK);
            prey.setFill(Color.GREEN);
            gridPane.add(prey, x, y);
        }
    }

    private void setupChart()
    {
//        NumberAxis xAxis = new NumberAxis();
//        NumberAxis yAxis = new NumberAxis();
//        populationChart.setTitle("Population Simulation");
//        xAxis.setLabel("Simulation Steps");
//        yAxis.setLabel("Population Count");
//
//        predatorSeries = new XYChart.Series<>();
//        predatorSeries.setName("Predators");
//        preySeries = new XYChart.Series<>();
//        preySeries.setName("Preys");
//
//        populationChart.getData().addAll(predatorSeries, preySeries);
    }

    @FXML
    private void startOrStopSimulation()
    {
        if (isSimulationRunning)
        {
            stopSimulation();
            setDisableComponents(false);
        }
        else
        {
            startSimulation();
            setDisableComponents(true);
        }

        isSimulationRunning = !isSimulationRunning;
    }

    @FXML
    private void resetSimulation()
    {
        GRID_SIZE =  Integer.valueOf(gridSizeTextField.getText());
        PREDATOR_SIZE =  Integer.valueOf(predatorCountTextField.getText());
        PREYS_SIZE =  Integer.valueOf(preyCountTextField.getText());
        SIZE_ANIMALS =  Integer.valueOf(tfSizeAnimals.getText());

        simulationSteps = 0;
        predatorCountLabel.setText("0");
        preyCountLabel.setText("0" );
        simulationStepLabel.setText(Integer.toString(simulationSteps));
        createGrid();
        placePredatorsAndPreys();
    }


    private void startSimulation()
    {
        simulationTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            double speed = speedSlider.getValue();
            double interval = (1.0 / speed * 1_000_000_000);

            @Override
            public void handle(long now) {

                if (now - lastUpdate >= interval)
                {
                    lastUpdate = now;
                    moveEntities();
                    simulationSteps++;
                    updateCountLabels();
                    updateChart();
                }
            }
        };
        simulationTimer.start();
    }

    private void stopSimulation()
    {
        simulationTimer.stop();
    }

    private void updateChart()
    {
//        predatorSeries.getData().add(new XYChart.Data<>(simulationSteps, Integer.parseInt(predatorCountLabel.getText())));
//        preySeries.getData().add(new XYChart.Data<>(simulationSteps, Integer.parseInt(preyCountLabel.getText())));
    }

    private void moveEntities()
    {
        Random random = new Random();
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Rectangle entity = (Rectangle) gridPane.getChildren().get(i);
            Color fill = (Color) entity.getFill();
            int x = GridPane.getColumnIndex(entity);
            int y = GridPane.getRowIndex(entity);

            if (fill.equals(Color.RED))
            {
                int dx = random.nextInt(3) - 1;
                int dy = random.nextInt(3) - 1;
                int newX = Math.max(0, Math.min(GRID_SIZE - 1, x + dx));
                int newY = Math.max(0, Math.min(GRID_SIZE - 1, y + dy));
                if (tryToEatPrey(newX, newY))
                    continue;

                GridPane.setConstraints(entity, newX, newY);
            }
            else if (fill.equals(Color.GREEN))
            {

                int dx = random.nextInt(3) - 1;
                int dy = random.nextInt(3) - 1;
                int newX = Math.max(0, Math.min(GRID_SIZE - 1, x + dx));
                int newY = Math.max(0, Math.min(GRID_SIZE - 1, y + dy));
                if (!isPreyEaten(newX, newY))
                    GridPane.setConstraints(entity, newX, newY);
                else
                    gridPane.getChildren().remove(entity);

            }
        }

    }


    private boolean tryToEatPrey(int x, int y)
    {
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Rectangle entity = (Rectangle) gridPane.getChildren().get(i);
            Color fill = (Color) entity.getFill();
            if (fill.equals(Color.GREEN))
            {
                int preyX = GridPane.getColumnIndex(entity);
                int preyY = GridPane.getRowIndex(entity);
                if (preyX == x && preyY == y)
                {
                    gridPane.getChildren().remove(entity);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPreyEaten(int x, int y)
    {
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Rectangle entity = (Rectangle) gridPane.getChildren().get(i);
            Color fill = (Color) entity.getFill();
            if (fill.equals(Color.RED)) {
                int predatorX = GridPane.getColumnIndex(entity);
                int predatorY = GridPane.getRowIndex(entity);
                if (predatorX == x && predatorY == y)
                    return true;

            }
        }
        return false;
    }


    private void updateCountLabels()
    {
        int predatorCount = 0;
        int preyCount = 0;
        for (int i = 0; i < gridPane.getChildren().size(); i++)
        {
            Rectangle entity = (Rectangle) gridPane.getChildren().get(i);
            Color fill = (Color) entity.getFill();
            if (fill.equals(Color.RED))
                predatorCount++;
            else if (fill.equals(Color.GREEN))
                preyCount++;

        }

        predatorCountLabel.setText("" + predatorCount);
        preyCountLabel.setText("" + preyCount);
        simulationStepLabel.setText(Integer.toString(simulationSteps));
    }

    private void setDisableComponents(boolean isDisable)
    {
        preyCountTextField.setDisable(isDisable);
        predatorCountTextField.setDisable(isDisable);
        gridSizeTextField.setDisable(isDisable);
        btnReset.setDisable(isDisable);
    }

}