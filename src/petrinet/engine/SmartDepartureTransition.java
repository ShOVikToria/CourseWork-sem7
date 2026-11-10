package petrinet.engine;

import java.util.LinkedList;
import java.util.Queue;

public class SmartDepartureTransition extends Transition {
    private final Place seatedPlace;     // Пасажири в салонi (напр. P6)
    private final Place queuePlace;      // Пасажири в черзi на зупинцi (напр. P4)
    private final Place destQueuePlace;  // Вiльнi мiсця автобуса на НАСТУПНiЙ зупинцi (напр. P11)
    private final Place statsPlace;      // Лiчильник перевезених (P10)
    private final Place freeSeatsPlace;  // ДОДАНО: Вiльнi мiсця на ПОТОЧНiЙ зупинцi (напр. P8)
    
    private final int busCapacity;       
    private final int minK;              
    private final boolean debugLog;      
    
    private final Queue<Integer> passengersInTransit = new LinkedList<>();

    public SmartDepartureTransition(String id, String name, int priority, double meanDelay, double devDelay,
                                    Place seatedPlace, Place queuePlace, Place destQueuePlace, Place statsPlace, 
                                    Place freeSeatsPlace, int busCapacity, int minK, boolean debugLog) {
        super(id, name, priority, meanDelay, devDelay);
        this.seatedPlace = seatedPlace;
        this.queuePlace = queuePlace;
        this.destQueuePlace = destQueuePlace;
        this.statsPlace = statsPlace;
        this.freeSeatsPlace = freeSeatsPlace;
        this.busCapacity = busCapacity;
        this.minK = minK;
        this.debugLog = debugLog;
    }

    @Override
    public boolean isEnabled() {
        int seated = seatedPlace.getTokens();
        int waiting = queuePlace.getTokens();
        
        if (seated >= busCapacity) return true;
        if (seated >= minK && waiting == 0) return true;

        return false;
    }

    @Override
    public void consumeInputTokens() {
        int passengersToDepart = seatedPlace.getTokens();
        if (passengersToDepart > busCapacity) passengersToDepart = busCapacity;
        
        // Забираємо пасажирiв iз салону
        seatedPlace.removeTokens(passengersToDepart);
        passengersInTransit.add(passengersToDepart);
        
        // ДОДАНО: Знищуємо всi вiльнi мiсця, що залишилися, щоб автобус не "роздвоювався"
        int leftoverSeats = freeSeatsPlace.getTokens();
        if (leftoverSeats > 0) {
            freeSeatsPlace.removeTokens(leftoverSeats);
        }
        
        if (debugLog) {
            int emptySeats = busCapacity - passengersToDepart;
            System.out.printf("[ЛОГ РУХУ] %s. Зайнято: %d/%d (Вiльних мiсць: %d).%n", 
                              getName(), passengersToDepart, busCapacity, emptySeats);
        }
        
        super.consumeInputTokens(); 
    }

    @Override
    public void produceOutputTokens() {
        if (!passengersInTransit.isEmpty()) {
            int arrivedPassengers = passengersInTransit.poll();
            statsPlace.addTokens(arrivedPassengers);
            
            // Автобус з'являється на iншiй зупинцi повнiстю порожнiм (всi n мiсць доступнi)
            destQueuePlace.addTokens(busCapacity);
        }
        super.produceOutputTokens();
    }
}