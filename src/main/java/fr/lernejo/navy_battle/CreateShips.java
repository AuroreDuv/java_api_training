package fr.lernejo.navy_battle;

class CreateShips {
    public Ship[] createShips() {
        Ship aircraftCarrier = new Ship("aircraft-carrier", 5);
        Ship cruiser = new Ship("cruiser", 4);
        Ship destroyer1 = new Ship("destroyer-1", 3);
        Ship destroyer2 = new Ship("destroyer-2", 3);
        Ship torpedoBoat = new Ship("torpedo-boat", 2);

        Ship[] ships = {torpedoBoat, destroyer1, destroyer2, cruiser, aircraftCarrier};

        return ships;
    }
}
