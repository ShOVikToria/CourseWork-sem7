package petrinet.engine;

public class Arc {
    private final Place place;
    private final int weight;

    public Arc(Place place, int weight) {
        this.place = place;
        this.weight = weight;
    }

    public Place getPlace() { return place; }
    public int getWeight() { return weight; }
}