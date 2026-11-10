package petrinet.model;

import petrinet.config.SimulationConfig;
import petrinet.engine.Place;
import petrinet.engine.PetriNetEngine;
import petrinet.engine.Transition;
import petrinet.engine.SmartDepartureTransition;

public class BusRouteCapacityModel {

    // Передаємо параметр мiсткостi N
    public static PetriNetEngine buildModel(int capacityN, boolean debugLog) {
        PetriNetEngine engine = new PetriNetEngine();

        Place p1 = new Place("P1", "Ген. 1", 1); Place p2 = new Place("P2", "Пас. 1", 0);
        Place p3 = new Place("P3", "Вiльнi в черзi 1", SimulationConfig.QUEUE_CAPACITY_STOP_1);
        Place p4 = new Place("P4", "Черга 1", 0); Place p5 = new Place("P5", "Втрати", 0);
        Place p6 = new Place("P6", "Бус А1", 0); Place p7 = new Place("P7", "Бус В1", 0);
        
        // ПОЧАТКОВА МiСТКiСТЬ АВТОБУСiВ ДОРiВНЮЄ N
        Place p8 = new Place("P8", "Мiсця А1", capacityN);
        Place p9 = new Place("P9", "Мiсця В1", capacityN);
        
        Place p10 = new Place("P10", "Перевезено", 0);
        Place p11 = new Place("P11", "Мiсця А2", 0); Place p12 = new Place("P12", "Мiсця В2", 0);
        Place p13 = new Place("P13", "Вiльнi в черзi 2", SimulationConfig.QUEUE_CAPACITY_STOP_2);
        Place p14 = new Place("P14", "Черга 2", 0);
        Place p15 = new Place("P15", "Бус А2", 0); Place p16 = new Place("P16", "Бус В2", 0);
        Place p17 = new Place("P17", "Ген. 2", 1); Place p18 = new Place("P18", "Пас. 2", 0);

        engine.addPlace(p1); engine.addPlace(p2); engine.addPlace(p3); engine.addPlace(p4);
        engine.addPlace(p5); engine.addPlace(p6); engine.addPlace(p7); engine.addPlace(p8);
        engine.addPlace(p9); engine.addPlace(p10); engine.addPlace(p11); engine.addPlace(p12);
        engine.addPlace(p13); engine.addPlace(p14); engine.addPlace(p15); engine.addPlace(p16);
        engine.addPlace(p17); engine.addPlace(p18);

        // Стандартнi переходи генерацiї та посадки (з виправленими прiоритетами)
        Transition t1 = new Transition("T1", "Надходження 1", 1, SimulationConfig.PASSENGER_ARRIVAL_MEAN_1, SimulationConfig.PASSENGER_ARRIVAL_DEV_1);
        t1.addInArc(p1, 1); t1.addOutArc(p1, 1); t1.addOutArc(p2, 1);

        Transition t2 = new Transition("T2", "В чергу 1", 2, 0.0, 0.0);
        t2.addInArc(p2, 1); t2.addInArc(p3, 1); t2.addOutArc(p4, 1);

        Transition t3 = new Transition("T3", "Вiдмова 1", 1, 0.0, 0.0);
        t3.addInArc(p2, 1); t3.addOutArc(p5, 1);

        Transition t4 = new Transition("T4", "Посадка А1", SimulationConfig.PRIORITY_BUS_A, 0.0, 0.0);
        t4.addInArc(p4, 1); t4.addInArc(p8, 1); t4.addOutArc(p6, 1); t4.addOutArc(p3, 1);

        Transition t5 = new Transition("T5", "Посадка В1", SimulationConfig.PRIORITY_BUS_B, 0.0, 0.0);
        t5.addInArc(p4, 1); t5.addInArc(p9, 1); t5.addOutArc(p7, 1); t5.addOutArc(p3, 1);

        // ПЕРЕХОДИ РУХУ: Вiдправка ТiЛЬКИ ПРИ ПОВНОМУ ЗАПОВНЕННi (busCapacity = n, minK = n)
        SmartDepartureTransition t6 = new SmartDepartureTransition("T6", "Прямий рейс А", 1, 
                SimulationConfig.ROUTE_TIME_BUS_A_MEAN, SimulationConfig.ROUTE_TIME_BUS_A_DEV, 
                p6, p4, p11, p10, p8, capacityN, capacityN, debugLog);

        SmartDepartureTransition t7 = new SmartDepartureTransition("T7", "Прямий рейс В", 1, 
                SimulationConfig.ROUTE_TIME_BUS_B_MEAN, SimulationConfig.ROUTE_TIME_BUS_B_DEV, 
                p7, p4, p12, p10, p9, capacityN, capacityN, debugLog);

        Transition t8 = new Transition("T8", "Надходження 2", 1, SimulationConfig.PASSENGER_ARRIVAL_MEAN_2, SimulationConfig.PASSENGER_ARRIVAL_DEV_2);
        t8.addInArc(p17, 1); t8.addOutArc(p17, 1); t8.addOutArc(p18, 1);

        Transition t9 = new Transition("T9", "В чергу 2", 2, 0.0, 0.0);
        t9.addInArc(p18, 1); t9.addInArc(p13, 1); t9.addOutArc(p14, 1);

        Transition t10 = new Transition("T10", "Вiдмова 2", 1, 0.0, 0.0);
        t10.addInArc(p18, 1); t10.addOutArc(p5, 1);

        Transition t11 = new Transition("T11", "Посадка А2", SimulationConfig.PRIORITY_BUS_A, 0.0, 0.0);
        t11.addInArc(p14, 1); t11.addInArc(p11, 1); t11.addOutArc(p15, 1); t11.addOutArc(p13, 1);

        Transition t12 = new Transition("T12", "Посадка В2", SimulationConfig.PRIORITY_BUS_B, 0.0, 0.0);
        t12.addInArc(p14, 1); t12.addInArc(p12, 1); t12.addOutArc(p16, 1); t12.addOutArc(p13, 1);

        SmartDepartureTransition t13 = new SmartDepartureTransition("T13", "Зворотний рейс А", 1, 
                SimulationConfig.ROUTE_TIME_BUS_A_MEAN, SimulationConfig.ROUTE_TIME_BUS_A_DEV, 
                p15, p14, p8, p10, p11, capacityN, capacityN, debugLog);

        SmartDepartureTransition t14 = new SmartDepartureTransition("T14", "Зворотний рейс В", 1, 
                SimulationConfig.ROUTE_TIME_BUS_B_MEAN, SimulationConfig.ROUTE_TIME_BUS_B_DEV, 
                p16, p14, p9, p10, p12, capacityN, capacityN, debugLog);

        engine.addTransition(t1); engine.addTransition(t2); engine.addTransition(t3);
        engine.addTransition(t4); engine.addTransition(t5); engine.addTransition(t6);
        engine.addTransition(t7); engine.addTransition(t8); engine.addTransition(t9);
        engine.addTransition(t10); engine.addTransition(t11); engine.addTransition(t12);
        engine.addTransition(t13); engine.addTransition(t14);

        return engine;
    }
}