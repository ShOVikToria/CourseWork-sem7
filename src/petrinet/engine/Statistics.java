package petrinet.engine;

import petrinet.config.SimulationConfig;

public class Statistics {

    public static void printReport(PetriNetEngine engine) {
        double Tmod = engine.getCurrentTime();

        int nTransp = engine.getPlace("P10").getTokens();
        int nLost = engine.getPlace("P5").getTokens();

        int nQueue1 = engine.getTransitions().stream()
                .filter(t -> t.getId().equals("T2")).findFirst().get().getFiringCount();
        int nQueue2 = engine.getTransitions().stream()
                .filter(t -> t.getId().equals("T9")).findFirst().get().getFiringCount();

        double areaP4 = engine.getPlace("P4").getAreaUnderMarking();
        double areaP14 = engine.getPlace("P14").getAreaUnderMarking();

        // Формули (2.5), (2.6), (2.7) з записки
        double wAvg1 = (nQueue1 > 0) ? (areaP4 / nQueue1) : 0.0;
        double wAvg2 = (nQueue2 > 0) ? (areaP14 / nQueue2) : 0.0;
        double wAvgTotal = ((nQueue1 + nQueue2) > 0) ? ((areaP4 + areaP14) / (nQueue1 + nQueue2)) : 0.0;

        // Формула (2.11)
        double lq1Avg = areaP4 / Tmod;
        double lq2Avg = areaP14 / Tmod;

        // Формула (2.10) - Денна виручка
        double rDay = (SimulationConfig.TICKET_FARE * nTransp) - (SimulationConfig.LOSS_PENALTY * nLost);

        System.out.println("   РЕЗУЛЬТАТИ IМIТАЦIЙНОГО ЕКСПЕРИМЕНТУ (СТОХАСТИЧНА МЕРЕЖА ПЕТРI)");
        System.out.printf(" Час симуляцiї (T_mod):                            %.2f хв%n\n", Tmod);
        System.out.printf(" Загальна кiлькiсть перевезених (N_transp):        %d осiб%n", nTransp);
        System.out.printf(" Загальна кiлькiсть втрачених (N_lost):            %d осiб%n\n", nLost);
        System.out.printf(" Середнiй час очiкування на Зупинцi 1 (W_avg1):    %.2f хв%n", wAvg1);
        System.out.printf(" Середнiй час очiкування на Зупинцi 2 (W_avg2):    %.2f хв%n", wAvg2);
        System.out.printf(" iнтегральний середнiй час очiкування (W_avg):     %.2f хв%n\n", wAvgTotal);
        System.out.printf(" Середня довжина черги на Зупинцi 1 (L_q1,avg):    %.2f осiб%n", lq1Avg);
        System.out.printf(" Середня довжина черги на Зупинцi 2 (L_q2,avg):    %.2f осiб%n\n", lq2Avg);
        System.out.printf(" Денний фiнансовий результат (R_day):              %.2f грн%n\n", rDay);
    }
}