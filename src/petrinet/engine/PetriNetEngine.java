package petrinet.engine;

import java.util.*;

public class PetriNetEngine {
    private final Map<String, Place> places = new LinkedHashMap<>();
    private final List<Transition> transitions = new ArrayList<>();
    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();
    private final Random random = new Random();
    private double currentTime = 0.0;

    // Клас Подiї дискретного часу
    public static class Event implements Comparable<Event> {
        double time;
        Transition transition;

        public Event(double time, Transition transition) {
            this.time = time;
            this.transition = transition;
        }

        @Override
        public int compareTo(Event o) {
            return Double.compare(this.time, o.time);
        }
    }

    public void addPlace(Place p) { places.put(p.getId(), p); }
    public void addTransition(Transition t) { transitions.add(t); }

    public Place getPlace(String id) { return places.get(id); }

    // Запуск симулятора
    public void runSimulation(double maxTime) {
        // Вiдсортувати переходи за спаданням прiоритету
        transitions.sort((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()));

        // Спочатку запустити миттєвi або початково активованi переходи
        fireEnabledTransitions();

        while (!eventQueue.isEmpty()) {
            Event nextEvent = eventQueue.poll();
            if (nextEvent.time > maxTime) {
                updateAllPlacesTime(maxTime);
                currentTime = maxTime;
                break;
            }

            updateAllPlacesTime(nextEvent.time);
            currentTime = nextEvent.time;

            // Завершити спрацьовування часового переходу
            nextEvent.transition.produceOutputTokens();

            // Перевiрити й активувати iншi переходи, якi стали доступними
            fireEnabledTransitions();
        }
    }

    private void fireEnabledTransitions() {
        boolean fired;
        do {
            fired = false;
            for (Transition t : transitions) {
                if (t.isEnabled()) {
                    SimulationAnalyzer.validateTransitionFiring(t, this);
                    
                    t.consumeInputTokens();
                    double delay = t.generateDelay(random);
                    if (delay <= 0.0) {
                        // Миттєвий перехiд: одразу видає маркери
                        t.produceOutputTokens();
                        fired = true;
                        break; // Повторити цикл iз вищим прiоритетом
                    } else {
                        // Часовий перехiд: додати подiю в календарний планувальник
                        eventQueue.add(new Event(currentTime + delay, t));
                    }
                }
            }
        } while (fired);
    }

    private void updateAllPlacesTime(double time) {
        for (Place p : places.values()) {
            p.updateTime(time);
        }
    }

    public double getCurrentTime() { return currentTime; }
    public Collection<Place> getPlaces() { return places.values(); }
    public List<Transition> getTransitions() { return transitions; }
}