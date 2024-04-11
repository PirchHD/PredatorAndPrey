package com.example.predatorandprey;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloController
{

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
    private TextField tfPredatorRate;
    @FXML
    private CheckBox cbPredatorRate;

    @FXML
    private TextField tfPreyRate;
    @FXML
    private CheckBox cbPreyRate;

    @FXML
    private TextField tfPredatorReproduceRate;
    @FXML
    private CheckBox cbPredatorReproduceRate;

    @FXML
    private TextField tfPreyReproduceRate;
    @FXML
    private CheckBox cbPreyReproduceRate;

    @FXML
    private TextField tfPredatorDie;
    @FXML
    private CheckBox cbPredatorDie;

    @FXML
    private TextField tfPreyDie;
    @FXML
    private CheckBox cbPreyDie;

    @FXML
    private Button btnStartStop;
    @FXML
    private Button btnReset;

    @FXML
    private GridPane gridPane;

    @FXML
    private LineChart<String, Integer> populationChart;
    @FXML
    private NumberAxis xAxis;
    private final ObservableList<XYChart.Data<String, Integer>> preySeriesData = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<String, Integer>> predatorSeriesData = FXCollections.observableArrayList();


    private AnimationTimer simulationTimer;


    /** Czy symulacja trwa ? */
    private boolean isSimulationRunning = false;

    /** Kroki w symulacji **/
    private int simulationSteps = 0;


    /** Wielkośc grida */
    public static int GRID_SIZE = 10;
    /** Wielkośc kwadratów */
    public static int SIZE_ANIMALS = 50;

    /** Ilość drapieżników */
    private int PREDATOR_SIZE = 10;
    /** Ilość ofiar */
    private int PREYS_SIZE = 10;


    /** Współczynniki przeżywalności dla drapieżników */
    private double PREDATOR_SURVIVAL_RATE = 0.2;
    /** Czy bierzemy pod uwage "Współczynniki przeżywalności" */
    private static boolean IS_PREDATOR_SURVIVAL_RATE = false;

    /** Współczynniki reprodukcji dla drapieżników */
    private double PREDATOR_REPRODUCE_RATE = 0.2;
    /** Czy bierzemy pod uwage "Współczynniki reprodukcji" dla drapiezników */
    private static boolean IS_PREDATOR_REPRODUCE_RATE = false;

    /** Do którego kroku drapiezniki mają żyć */
    private int PREDATOR_DIE = 10;
    /** Czy drapiezniki maja umierac z starości */
    private static boolean IS_PREDATOR_DIE = false;



    /** Współczynniki przeżywalności dla ofiar */
    private double PREY_SURVIVAL_RATE = 0.2;
    /** Czy bierzemy pod uwage "Współczynniki przeżywalności" */
    private static boolean IS_PREY_SURVIVAL_RATE = false;

    /** Współczynniki reprodukcji dla ofiar */
    private double PREY_REPRODUCE_RATE = 0.2;
    /** Czy bierzemy pod uwage "Współczynniki reprodukcji" dla ofiar */
    private static boolean IS_PREY_REPRODUCE_RATE = false;

    /** Do którego kroku drapiezniki mają żyć */
    private int PREY_DIE = 10;
    /** Czy drapiezniki maja umierac z starości */
    private static boolean IS_PREY_DIE = false;

    /**
     * Inicjalizacja ekranu
     * */
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

    /**
     * Tworzenie planszy
     * */
    private void createGrid()
    {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        for (int i = 0; i < GRID_SIZE; i++)
        {
            for (int j = 0; j < GRID_SIZE; j++)
            {
                Animal rectangle = new Animal();
                gridPane.add(rectangle, i, j);
            }
        }
    }

    /**
     * Ustawienie drapieżników oraz ofiar w randomowych miejscach na gridzie
     *
     * TODO dodać sprawdzenie czy miejsce jest zajete
     * */
    private void placePredatorsAndPreys()
    {
        Random random = new Random();

        for (int i = 0; i < PREDATOR_SIZE; i++)
        {
            Predator predator = new Predator();
            predator.setLive(PREDATOR_DIE);
            gridPane.add(predator, random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        }

        for (int i = 0; i < PREYS_SIZE; i++)
        {
            Prey prey = new Prey();
            prey.setLive(PREY_DIE);
            gridPane.add(prey, random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        }
    }

    private void setupChart()
    {
        ObservableList<XYChart.Series<String, Integer>> data = FXCollections.observableArrayList();
        data.add(new XYChart.Series<>("PREY", preySeriesData));
        data.add(new XYChart.Series<>("PREDATOR", predatorSeriesData));

        populationChart.setData(data);
    }


    /**
     * Klikniecie przycisku "Start/stop"
     *
     * Rozpoczyna symulacje albo ją stopuje
     * */
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

    /**
     * Reset symulacji oraz ustawienie nowych wartości
     * */
    @FXML
    private void resetSimulation()
    {
        preySeriesData.clear();
        predatorSeriesData.clear();
        
        GRID_SIZE =  Integer.valueOf(gridSizeTextField.getText());
        SIZE_ANIMALS =  Integer.valueOf(tfSizeAnimals.getText());



        /** Ustawienia drapiezników:  */

        PREDATOR_SIZE =  Integer.valueOf(predatorCountTextField.getText());

        PREDATOR_SURVIVAL_RATE = Double.valueOf(tfPredatorRate.getText());
        IS_PREDATOR_SURVIVAL_RATE = cbPredatorRate.isSelected();

        PREDATOR_REPRODUCE_RATE = Double.valueOf(tfPredatorReproduceRate.getText());
        IS_PREDATOR_REPRODUCE_RATE = cbPredatorReproduceRate.isSelected();

        PREDATOR_DIE = Integer.valueOf(tfPredatorDie.getText());
        IS_PREDATOR_DIE = cbPredatorDie.isSelected();


        /** Ustawienia ofiar:  */

        PREYS_SIZE =  Integer.valueOf(preyCountTextField.getText());

        PREY_SURVIVAL_RATE = Double.valueOf(tfPreyRate.getText());
        IS_PREY_SURVIVAL_RATE = cbPredatorRate.isSelected();

        PREY_REPRODUCE_RATE = Double.valueOf(tfPreyReproduceRate.getText());
        IS_PREY_REPRODUCE_RATE = cbPreyReproduceRate.isSelected();

        PREY_DIE = Integer.valueOf(tfPreyDie.getText());
        IS_PREY_DIE = cbPreyDie.isSelected();

        simulationSteps = 0;
        predatorCountLabel.setText("0");
        preyCountLabel.setText("0" );
        simulationStepLabel.setText(Integer.toString(simulationSteps));
        createGrid();
        placePredatorsAndPreys();
    }


    /** Symulacja */
    private void startSimulation()
    {
        simulationTimer = new AnimationTimer()
        {
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

    /**
     * Zatrzymuje symulacje
     * */
    private void stopSimulation()
    {
        simulationTimer.stop();
    }

    private void updateChart()
    {
        int predatorCount = Integer.parseInt(predatorCountLabel.getText());
        int preyCount = Integer.parseInt(preyCountLabel.getText());
        preySeriesData.add(new XYChart.Data<>(Integer.toString(simulationSteps), preyCount));
        predatorSeriesData.add(new XYChart.Data<>(Integer.toString(simulationSteps), predatorCount));
    }



    private void moveEntities()
    {
        Random random = new Random();
        // Lista do przechowywania nowych osobników
        List<Animal> newEntities = new ArrayList<>();

        for (int i = 0; i < gridPane.getChildren().size(); i++)
        {
            Rectangle rectangle = (Rectangle) gridPane.getChildren().get(i);
            if (!(rectangle instanceof Animal))
                continue;

            Animal animal = (Animal) rectangle;
            int x = GridPane.getColumnIndex(animal);
            int y = GridPane.getRowIndex(animal);

            animal.addOneYear();

            /** Jeśli któryś z zwierząt osiągnał swój limit życia to umiera */
            if (animal instanceof Predator)
            {
                int dx = random.nextInt(3) - 1;
                int dy = random.nextInt(3) - 1;
                int newX = Math.max(0, Math.min(GRID_SIZE - 1, x + dx));
                int newY = Math.max(0, Math.min(GRID_SIZE - 1, y + dy));
                if (IS_PREDATOR_SURVIVAL_RATE && random.nextDouble() > PREDATOR_SURVIVAL_RATE)
                {
                    gridPane.getChildren().remove(animal);
                    continue;
                }

                if (IS_PREDATOR_DIE && animal.live <= animal.age)
                {
                    gridPane.getChildren().remove(animal);
                    continue;
                }

                if (IS_PREDATOR_REPRODUCE_RATE && random.nextDouble() < PREDATOR_REPRODUCE_RATE)
                {
                    Predator newPredator = new Predator();
                    newPredator.setLive(PREDATOR_DIE);
                    newEntities.add(newPredator);
                }

                if (tryToEatPrey(newX, newY))
                    continue;

                GridPane.setConstraints(rectangle, newX, newY);
            }
            else if (animal instanceof Prey)
            {
                int dx = random.nextInt(3) - 1;
                int dy = random.nextInt(3) - 1;
                int newX = Math.max(0, Math.min(GRID_SIZE - 1, x + dx));
                int newY = Math.max(0, Math.min(GRID_SIZE - 1, y + dy));
                if (isPreyEaten(newX, newY))
                {
                    gridPane.getChildren().remove(animal);
                    continue;
                }

                if (IS_PREY_SURVIVAL_RATE && random.nextDouble() > PREY_SURVIVAL_RATE)
                {
                    gridPane.getChildren().remove(rectangle);
                    continue;
                }

                if (IS_PREY_DIE && animal.live <= animal.age)
                {
                    gridPane.getChildren().remove(animal);
                    continue;
                }

                if (IS_PREY_REPRODUCE_RATE && random.nextDouble() < PREY_REPRODUCE_RATE)
                {
                    Prey newPrey = new Prey();
                    newPrey.setLive(PREY_DIE);
                    newEntities.add(newPrey);
                }


                GridPane.setConstraints(rectangle, newX, newY);
            }

        }

        for (Rectangle entity : newEntities)
        {
            gridPane.getChildren().add(entity);
            int newX = Math.max(0, Math.min(GRID_SIZE - 1, random.nextInt(GRID_SIZE)));
            int newY = Math.max(0, Math.min(GRID_SIZE - 1, random.nextInt(GRID_SIZE)));
            GridPane.setConstraints(entity, newX, newY);
        }

    }

    /**
     * Próbuje zjeśc ofiare
     *
     * @return true/false - udało sie zjeść albo nie
     * */
    private boolean tryToEatPrey(int x, int y)
    {
        for (int i = 0; i < gridPane.getChildren().size(); i++)
        {
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

    /**
     * Czy ofiara została zjedzona
     *
     * @return true/false - została zjedzona (czy ofiara sama na weszla)
     * */
    private boolean isPreyEaten(int x, int y)
    {
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Rectangle entity = (Rectangle) gridPane.getChildren().get(i);
            Color fill = (Color) entity.getFill();
            if (fill.equals(Color.RED))
            {
                int predatorX = GridPane.getColumnIndex(entity);
                int predatorY = GridPane.getRowIndex(entity);
                if (predatorX == x && predatorY == y)
                    return true;

            }
        }
        return false;
    }


    /**
     * Aktualizacja informacji w kontrolkach
     * */
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

    @FXML
    private void cbPredatorRateOnAction()
    {
        PREDATOR_SURVIVAL_RATE = Double.valueOf(tfPredatorRate.getText());
        IS_PREDATOR_SURVIVAL_RATE = cbPredatorRate.isSelected();
        tfPredatorRate.setDisable(!cbPredatorRate.isSelected());
    }

    @FXML
    private void cbPreyRateOnAction()
    {
        PREY_SURVIVAL_RATE = Double.valueOf(tfPreyRate.getText());
        IS_PREY_SURVIVAL_RATE = cbPreyRate.isSelected();
        tfPreyRate.setDisable(!cbPreyRate.isSelected());
    }

    @FXML
    private void cbPredatorReproduceOnAction()
    {
        PREDATOR_REPRODUCE_RATE = Double.valueOf(tfPredatorReproduceRate.getText());
        IS_PREDATOR_REPRODUCE_RATE = cbPredatorReproduceRate.isSelected();
        tfPredatorReproduceRate.setDisable(!cbPredatorReproduceRate.isSelected());
    }

    @FXML
    private void cbPreyReproduceOnAction()
    {
        PREY_REPRODUCE_RATE = Double.valueOf(tfPreyReproduceRate.getText());
        IS_PREY_REPRODUCE_RATE = cbPreyReproduceRate.isSelected();
        tfPreyReproduceRate.setDisable(!cbPreyReproduceRate.isSelected());
    }


    @FXML
    private void cbPredatorDieOnAction()
    {
        PREDATOR_DIE = Integer.valueOf(tfPredatorDie.getText());
        IS_PREDATOR_DIE = cbPredatorDie.isSelected();
        tfPredatorDie.setDisable(!cbPredatorDie.isSelected());
    }

    @FXML
    private void cbPreyDieOnAction()
    {
        PREY_DIE = Integer.valueOf(tfPreyDie.getText());
        IS_PREY_DIE = cbPreyDie.isSelected();
        tfPreyDie.setDisable(!cbPreyDie.isSelected());
    }


}