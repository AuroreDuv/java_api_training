package fr.lernejo.navy_battle;

public class Ship {
    final private String slug;
    final private int size;

    protected Ship(String slug, int size) {
        this.slug = slug;
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public String getSlug() {
        return this.slug;
    }

    public Boolean isAlive(GameGrid gameGrid) {
        for(int i = 0; i < gameGrid.get_grid()[0].length ; i++) {
            for (int j = 0; j < gameGrid.get_grid().length; j++) {
                Ship ship = gameGrid.get_grid()[i][j];
                if(ship != null && ship.getSlug().equals(this.getSlug())) {
                    return true;
                }
            }
        }
        return false;
    }
}
