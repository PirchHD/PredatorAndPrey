package com.example.predatorandprey;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public  class Animal extends Rectangle
{

    /** Wiek jednoski */
    int age = 0;

    /** Do kiedy ma żyć ? */
    int live = 10;

    protected int x;
    protected int y;

    protected Animal()
    {
        super(HelloController.SIZE_ANIMALS, HelloController.SIZE_ANIMALS);
        setStroke(Color.BLACK);
        setFill(Color.WHITE);
    }

    public void move(GridPane gridPane, int gridSize)
    {

    }

    protected boolean canMoveTo(int newX, int newY, GridPane gridPane, int gridSize)
    {
        return newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize && gridPane.getChildren().stream()
                .filter(node -> node instanceof Animal)
                .map(node -> (Animal) node)
                .noneMatch(animal -> animal.x == newX && animal.y == newY);
    }

    public void setLive(int whenDie)
    {
        live = whenDie;
    }

    public void addOneYear()
    {
        age++;
    }
}

