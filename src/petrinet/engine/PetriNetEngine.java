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

    public void runSimulation(double maxTime, double logStep, java.io.PrintWriter logWriter) {
        // Відсортувати переходи за спаданням пріоритету
        transitions.sort((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()));

        // Спочатку запустити миттєві або початково активовані переходи
        fireEnabledTransitions();

        double nextLogTime = logStep;

        while (!eventQueue.isEmpty()) {
            Event nextEvent = eventQueue.poll();

            // --- БЛОК ЛОГУВАННЯ (ТЕЛЕМЕТРІЯ) ---
            if (logStep > 0 && logWriter != null) {
                // Якщо наступна подія відбувається ПІСЛЯ нашого часу логування,
                // робимо зупинку, обчислюємо інтеграли і записуємо зріз.
                while (nextLogTime <= nextEvent.time && nextLogTime <= maxTime) {
                    updateAllPlacesTime(nextLogTime); // Оновлюємо площі до моменту зрізу
                    currentTime = nextLogTime;        // Просуваємо системний годинник

                    double lq1 = getPlace("P4").getAreaUnderMarking() / currentTime;
                    double lq2 = getPlace("P14").getAreaUnderMarking() / currentTime;

                    int nQ1 = transitions.stream().filter(t -> t.getId().equals("T2")).findFirst().get().getFiringCount();
                    int nQ2 = transitions.stream().filter(t -> t.getId().equals("T9")).findFirst().get().getFiringCount();

                    double w1 = (nQ1 > 0) ? (getPlace("P4").getAreaUnderMarking() / nQ1) : 0.0;
                    double w2 = (nQ2 > 0) ? (getPlace("P14").getAreaUnderMarking() / nQ2) : 0.0;

                    // Запис у CSV (Час; Lq1; Lq2; Wavg1; Wavg2)
                    logWriter.printf(java.util.Locale.US, "%.1f;%.4f;%.4f;%.4f;%.4f%n", currentTime, lq1, lq2, w1, w2);
                    
                    nextLogTime += logStep;
                }
            }

            if (nextEvent.time > maxTime) {
                updateAllPlacesTime(maxTime);
                currentTime = maxTime;
                break;
            }

            updateAllPlacesTime(nextEvent.time);
            currentTime = nextEvent.time;

            // Завершити спрацьовування часового переходу
            nextEvent.transition.produceOutputTokens();

            // Перевірити й активувати інші переходи
            fireEnabledTransitions();
        }
    }

    private void fireEnabledTransitions() {
        boolean fired;
        do {
            fired = false;
            int maxPriority = -1;
            List<Transition> enabledCandidates = new ArrayList<>();

            for (Transition t : transitions) {
                if (t.isEnabled()) {
                    if (enabledCandidates.isEmpty()) {
                        maxPriority = t.getPriority();
                        enabledCandidates.add(t);
                    } else if (t.getPriority() == maxPriority) {
                        enabledCandidates.add(t);
                    } else if (t.getPriority() < maxPriority) {
                        break;
                    }
                }
            }

            if (!enabledCandidates.isEmpty()) {
                Transition tToFire = enabledCandidates.get(random.nextInt(enabledCandidates.size()));
                
                SimulationAnalyzer.validateTransitionFiring(tToFire, this);
                tToFire.consumeInputTokens();
                double delay = tToFire.generateDelay(random);
                
                if (delay <= 0.0) {
                    tToFire.produceOutputTokens();
                    fired = true;
                } else {
                    eventQueue.add(new Event(currentTime + delay, tToFire));
                    fired = true; // <--- САМЕ ЦЕЙ РЯДОК ВІДНОВЛЮЄ ПАРАЛЕЛЬНІСТЬ!
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