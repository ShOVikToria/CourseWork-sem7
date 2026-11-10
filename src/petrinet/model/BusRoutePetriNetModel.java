package petrinet.model;

import petrinet.config.SimulationConfig;
import petrinet.engine.Place;
import petrinet.engine.PetriNetEngine;
import petrinet.engine.Transition;

public class BusRoutePetriNetModel {

    public static PetriNetEngine buildModel() {
        PetriNetEngine engine = new PetriNetEngine();

        // ---------------------------------------------------------------------
        // 1. СТВОРЕННЯ ПОЗИЦiЙ (P1 ... P18)
        // ---------------------------------------------------------------------
        Place p1  = new Place("P1",  "Готовнiсть генератора 1", 1);
        Place p2  = new Place("P2",  "Прибуття пасажира 1", 0);
        Place p3  = new Place("P3",  "Вiльнi мiсця в черзi 1", SimulationConfig.QUEUE_CAPACITY_STOP_1);
        Place p4  = new Place("P4",  "Черга пасажирiв 1", 0);
        Place p5  = new Place("P5",  "Втраченi пасажири", 0);
        Place p6  = new Place("P6",  "Сiли в бус А на зупинцi 1", 0);
        Place p7  = new Place("P7",  "Сiли в бус В на зупинцi 1", 0);
        Place p8  = new Place("P8",  "Вiльнi мiсця бус А на зупинцi 1", SimulationConfig.BUS_CAPACITY_A);
        Place p9  = new Place("P9",  "Вiльнi мiсця бус В на зупинцi 1", SimulationConfig.BUS_CAPACITY_B);
        Place p10 = new Place("P10", "Перевезено всього", 0);
        Place p11 = new Place("P11", "Вiльнi мiсця бус А на зупинцi 2", 0);
        Place p12 = new Place("P12", "Вiльнi мiсця бус В на зупинцi 2", 0);
        Place p13 = new Place("P13", "Вiльнi мiсця в черзi 2", SimulationConfig.QUEUE_CAPACITY_STOP_2);
        Place p14 = new Place("P14", "Черга пасажирiв 2", 0);
        Place p15 = new Place("P15", "Сiли в бус А на зупинцi 2", 0);
        Place p16 = new Place("P16", "Сiли в бус В на зупинцi 2", 0);
        Place p17 = new Place("P17", "Готовнiсть генератора 2", 1);
        Place p18 = new Place("P18", "Прибуття пасажира 2", 0);

        engine.addPlace(p1);  engine.addPlace(p2);  engine.addPlace(p3);
        engine.addPlace(p4);  engine.addPlace(p5);  engine.addPlace(p6);
        engine.addPlace(p7);  engine.addPlace(p8);  engine.addPlace(p9);
        engine.addPlace(p10); engine.addPlace(p11); engine.addPlace(p12);
        engine.addPlace(p13); engine.addPlace(p14); engine.addPlace(p15);
        engine.addPlace(p16); engine.addPlace(p17); engine.addPlace(p18);

        // ---------------------------------------------------------------------
        // 2. СТВОРЕННЯ ПЕРЕХОДiВ ТА ДУГ (T1 ... T14)
        // ---------------------------------------------------------------------
        // T1: Надходження на зупинку 1
        Transition t1 = new Transition("T1", "Надходження 1", 1, SimulationConfig.PASSENGER_ARRIVAL_MEAN_1, SimulationConfig.PASSENGER_ARRIVAL_DEV_1);
        t1.addInArc(p1, 1); t1.addOutArc(p1, 1); t1.addOutArc(p2, 1);

        // T2: Постановка в чергу 1
        Transition t2 = new Transition("T2", "Стає в чергу 1", 2, 0.0, 0.0);
        t2.addInArc(p2, 1); t2.addInArc(p3, 1); t2.addOutArc(p4, 1);

        // T3: Вiдмова на зупинцi 1
        Transition t3 = new Transition("T3", "Вiдмова 1", 1, 0.0, 0.0);
        t3.addInArc(p2, 1); t3.addOutArc(p5, 1);

        // T4: Посадка в бус А1 (Прiоритет r=10)
        Transition t4 = new Transition("T4", "Посадка А1", SimulationConfig.PRIORITY_BUS_A, 0.0, 0.0);
        t4.addInArc(p4, 1); t4.addInArc(p8, 1); t4.addOutArc(p6, 1); t4.addOutArc(p3, 1);

        // T5: Посадка в бус В1 (Прiоритет r=5)
        Transition t5 = new Transition("T5", "Посадка В1", SimulationConfig.PRIORITY_BUS_B, 0.0, 0.0);
        t5.addInArc(p4, 1); t5.addInArc(p9, 1); t5.addOutArc(p7, 1); t5.addOutArc(p3, 1);

        // T6: Прямий рейс А (20 хв, кратнiсть 35)
        Transition t6 = new Transition("T6", "Прямий рейс А", 1, SimulationConfig.ROUTE_TIME_BUS_A_MEAN, SimulationConfig.ROUTE_TIME_BUS_A_DEV);
        t6.addInArc(p6, SimulationConfig.BUS_CAPACITY_A);
        t6.addOutArc(p11, SimulationConfig.BUS_CAPACITY_A);
        t6.addOutArc(p10, SimulationConfig.BUS_CAPACITY_A);

        // T7: Прямий рейс В (30 хв, кратнiсть 35)
        Transition t7 = new Transition("T7", "Прямий рейс В", 1, SimulationConfig.ROUTE_TIME_BUS_B_MEAN, SimulationConfig.ROUTE_TIME_BUS_B_DEV);
        t7.addInArc(p7, SimulationConfig.BUS_CAPACITY_B);
        t7.addOutArc(p12, SimulationConfig.BUS_CAPACITY_B);
        t7.addOutArc(p10, SimulationConfig.BUS_CAPACITY_B);

        // T8: Надходження на зупинку 2
        Transition t8 = new Transition("T8", "Надходження 2", 1, SimulationConfig.PASSENGER_ARRIVAL_MEAN_2, SimulationConfig.PASSENGER_ARRIVAL_DEV_2);
        t8.addInArc(p17, 1); t8.addOutArc(p17, 1); t8.addOutArc(p18, 1);

        // T9: Постановка в чергу 2
        Transition t9 = new Transition("T9", "Стає в чергу 2", 2, 0.0, 0.0);
        t9.addInArc(p18, 1); t9.addInArc(p13, 1); t9.addOutArc(p14, 1);

        // T10: Вiдмова на зупинцi 2
        Transition t10 = new Transition("T10", "Вiдмова 2", 1, 0.0, 0.0);
        t10.addInArc(p18, 1); t10.addOutArc(p5, 1);

        // T11: Посадка в бус А2 (r=10)
        Transition t11 = new Transition("T11", "Посадка А2", SimulationConfig.PRIORITY_BUS_A, 0.0, 0.0);
        t11.addInArc(p14, 1); t11.addInArc(p11, 1); t11.addOutArc(p15, 1); t11.addOutArc(p13, 1);

        // T12: Посадка в бус В2 (r=5)
        Transition t12 = new Transition("T12", "Посадка В2", SimulationConfig.PRIORITY_BUS_B, 0.0, 0.0);
        t12.addInArc(p14, 1); t12.addInArc(p12, 1); t12.addOutArc(p16, 1); t12.addOutArc(p13, 1);

        // T13: Зворотний рейс А (20 хв, кратнiсть 35)
        Transition t13 = new Transition("T13", "Зворотний рейс А", 1, SimulationConfig.ROUTE_TIME_BUS_A_MEAN, SimulationConfig.ROUTE_TIME_BUS_A_DEV);
        t13.addInArc(p15, SimulationConfig.BUS_CAPACITY_A);
        t13.addOutArc(p8, SimulationConfig.BUS_CAPACITY_A);
        t13.addOutArc(p10, SimulationConfig.BUS_CAPACITY_A);

        // T14: Зворотний рейс В (30 хв, кратнiсть 35)
        Transition t14 = new Transition("T14", "Зворотний рейс В", 1, SimulationConfig.ROUTE_TIME_BUS_B_MEAN, SimulationConfig.ROUTE_TIME_BUS_B_DEV);
        t14.addInArc(p16, SimulationConfig.BUS_CAPACITY_B);
        t14.addOutArc(p9, SimulationConfig.BUS_CAPACITY_B);
        t14.addOutArc(p10, SimulationConfig.BUS_CAPACITY_B);

        engine.addTransition(t1);  engine.addTransition(t2);  engine.addTransition(t3);
        engine.addTransition(t4);  engine.addTransition(t5);  engine.addTransition(t6);
        engine.addTransition(t7);  engine.addTransition(t8);  engine.addTransition(t9);
        engine.addTransition(t10); engine.addTransition(t11); engine.addTransition(t12);
        engine.addTransition(t13); engine.addTransition(t14);

        return engine;
    }
}