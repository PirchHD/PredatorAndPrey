package com.example.predatorandprey;

import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;

import java.util.Random;

public class Prey extends Animal {
    public Prey(int size) {
        super(size);
        setFill(Color.GREEN);
    }

    @Override
    public void move(GridPane gridPane, int gridSize)
    {
        Random random = new Random();
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;
        int newX = Math.max(0, Math.min(gridSize - 1, x + dx));
        int newY = Math.max(0, Math.min(gridSize - 1, y + dy));
        if (canMoveTo(newX, newY, gridPane, gridSize)) {
            gridPane.getChildren().remove(this);
            x = newX;
            y = newY;
            gridPane.add(this, x, y);
        }
    }
}
