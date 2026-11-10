package petrinet.engine;

public class SimulationAnalyzer {
    
    // Лiчильник знайдених помилок
    private static int errorCount = 0;

    /**
     * Метод викликається безпосередньо перед спрацюванням переходу 
     * для перевiрки логiчних iнварiантiв системи.
     */
    public static void validateTransitionFiring(Transition t, PetriNetEngine engine) {
        String tId = t.getId();

        // ---------------------------------------------------------
        // ПРАВИЛО 1: Не повинно бути вiдмови, якщо в черзi є мiсця
        // ---------------------------------------------------------
        if (tId.equals("T3")) { // Вiдмова на Зупинцi 1
            Place p3 = engine.getPlace("P3");
            if (p3 != null && p3.getTokens() > 0) {
                errorCount++;
            }
        }
        if (tId.equals("T10")) { // Вiдмова на Зупинцi 2
            Place p13 = engine.getPlace("P13");
            if (p13 != null && p13.getTokens() > 0) {
                errorCount++;
            }
        }

        // ---------------------------------------------------------
        // ПРАВИЛО 2: Не можна сiдати в автобус В, якщо є мiсця в А
        // ---------------------------------------------------------
        if (tId.equals("T5")) { // Посадка в мiкроавтобус В (Зупинка 1)
            Place p8 = engine.getPlace("P8");
            if (p8 != null && p8.getTokens() > 0) {
                errorCount++;
            }
        }
        if (tId.equals("T12")) { // Посадка в мiкроавтобус В (Зупинка 2)
            Place p11 = engine.getPlace("P11");
            if (p11 != null && p11.getTokens() > 0) {
                errorCount++;
            }
        }
    }

    public static void printAnalysisReport() {
        if (errorCount == 0) {
            System.out.println(" [OK] Помилок логiки не виявлено. Усi правила маршруту дотримано.");
        } else {
            System.out.println(" [!] Виявлено " + errorCount + " порушень логiки пiд час моделювання.");
        }
    }
}