package petrinet.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Transition {
    private final String id;
    private final String name;
    private final int priority;
    private final double meanDelay;
    private final double devDelay;
    private final List<Arc> inArcs = new ArrayList<>();
    private final List<Arc> outArcs = new ArrayList<>();

    private int firingCount = 0; // Лiчильник спрацьовувань

    public Transition(String id, String name, int priority, double meanDelay, double devDelay) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.meanDelay = meanDelay;
        this.devDelay = devDelay;
    }

    public void addInArc(Place place, int weight) { inArcs.add(new Arc(place, weight)); }
    public void addOutArc(Place place, int weight) { outArcs.add(new Arc(place, weight)); }

    // Перевiрка умови активацiї переходу (наявнiсть маркерiв на вхiдних дугах)
    public boolean isEnabled() {
        for (Arc arc : inArcs) {
            if (arc.getPlace().getTokens() < arc.getWeight()) {
                return false;
            }
        }
        return true;
    }

    // Забiр маркерiв iз вхiдних позицiй пiд час початку спрацьовування
    public void consumeInputTokens() {
        for (Arc arc : inArcs) {
            arc.getPlace().removeTokens(arc.getWeight());
        }
        firingCount++;
    }

    // Передача маркерiв у вихiднi позицiї пiд час завершення спрацьовування
    public void produceOutputTokens() {
        for (Arc arc : outArcs) {
            arc.getPlace().addTokens(arc.getWeight());
        }
    }

    // Генерацiя затримки згiдно з рiвномiрним розподiлом Unif[mean-dev, mean+dev]
    public double generateDelay(Random random) {
        if (meanDelay <= 0.0) return 0.0;
        double min = meanDelay - devDelay;
        double max = meanDelay + devDelay;
        return min + (max - min) * random.nextDouble();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getPriority() { return priority; }
    public double getMeanDelay() { return meanDelay; }
    public int getFiringCount() { return firingCount; }
}