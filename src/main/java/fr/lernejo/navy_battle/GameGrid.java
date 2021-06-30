package fr.lernejo.navy_battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class GameGrid {
    final private int width;
    final private int height;
    final private Ship[][] grid;

    public GameGrid(int width, int height) {
        this.width = width;
        this.height = height;

        grid = new Ship[this.width][this.height];

        for(int i = 0; i < this.width ; i++) {
            for (int j = 0; j < this.height; j++) {
                this.grid[i][j] = null;
            }
        }

        placeShipsRandomly();
    }

    public void placeHorizontalShip(Random random, Ship ship) {
        int index = 0; List<Integer> locations = new ArrayList<>(); int size = ship.get_size(), randomX = 0, randomY = 0;
        while (index < size) {
            if (index == 0) {
                randomX = random.nextInt(this.width);
                randomY = random.nextInt(this.height - size + 1);
            }
            locations.add(randomY);
            if(randomY >= this.height || grid[randomX][randomY] != null) {
                index = 0; locations.removeAll(locations);
                continue;
            }
            randomY++; index++;
        }
        for(int j = 0; j < locations.size(); j++) {
            grid[randomX][locations.get(j)] = ship;
        }
    }

    public void placeVerticalShip(Random random, Ship ship) {
        int index = 0; List<Integer> locations = new ArrayList<>(); int size = ship.get_size(), randomX = 0, randomY = 0;
        while (index < size) {
            if (index == 0) {
                randomX = random.nextInt(this.width - size + 1);
                randomY = random.nextInt(this.height);
            }
            locations.add(randomX);
            if (randomX >= this.width || grid[randomX][randomY] != null) {
                index = 0; locations.removeAll(locations);
                continue;
            }
            randomX++; index++;
        }
        for (int j = 0; j < locations.size(); j++) {
            grid[locations.get(j)][randomY] = ship;
        }
    }

    public void placeShipsRandomly() {
        CreateShips createShips = new CreateShips();
        Ship[] ships = createShips.createShips();
        int randomDirection, randomX = 0, randomY = 0;
        Random random = new Random();

        for(int i = 0; i < ships.length; i++) {
            randomDirection = random.nextInt(2);

            if (randomDirection == 0) { // horizontal
                placeHorizontalShip(random, ships[i]);
            } else { // vertical
                placeVerticalShip(random, ships[i]);
            }
        }
    }

    public Boolean isShipLeftOnGrid() {
        for(int i = 0; i < this.width ; i++) {
            for (int j = 0; j < this.height; j++) {
                if(this.grid[i][j] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public void hitShip(int x, int y) {
        this.grid[x][y] = new Ship("hit", 0);
    }

    public Ship[][] get_grid() {
        return this.grid;
    }
}
