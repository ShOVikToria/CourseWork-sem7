package petrinet;

import petrinet.config.SimulationConfig;
import petrinet.engine.PetriNetEngine;
import petrinet.model.BusRoutePetriNetModel;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        int replications = SimulationConfig.REPLICATIONS;

        String directoryPath = "reports/base_simulation";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Створює папку reports та пiдпапку base_simulation
        }

        // 1. Генерацiя мiтки часу та унiкальної назви файлу
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String csvFile = directoryPath + "/base_results_" + timestamp + ".csv";
        
        double totalTransp = 0, totalLost = 0;
        double totalW1 = 0, totalW2 = 0, totalWAvg = 0;
        double totalLq1 = 0, totalLq2 = 0, totalProfit = 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            // Заголовок CSV (використовуємо крапку з комою для зручного iмпорту в Excel)
            writer.println("Прогiн;Перевезено;Вiдмов;W_avg1;W_avg2;W_avg_total;L_q1;L_q2;Прибуток");

            for (int i = 1; i <= replications; i++) {
                PetriNetEngine engine = BusRoutePetriNetModel.buildModel();
                engine.runSimulation(SimulationConfig.SIMULATION_TIME);

                // Збiр базової статистики
                int nTransp = engine.getPlace("P10").getTokens();
                int nLost = engine.getPlace("P5").getTokens();

                int nQueue1 = engine.getTransitions().stream().filter(t -> t.getId().equals("T2")).findFirst().get().getFiringCount();
                int nQueue2 = engine.getTransitions().stream().filter(t -> t.getId().equals("T9")).findFirst().get().getFiringCount();

                double areaP4 = engine.getPlace("P4").getAreaUnderMarking();
                double areaP14 = engine.getPlace("P14").getAreaUnderMarking();
                double tMod = engine.getCurrentTime();

                // Розрахунки за формулами
                double wAvg1 = (nQueue1 > 0) ? (areaP4 / nQueue1) : 0.0;
                double wAvg2 = (nQueue2 > 0) ? (areaP14 / nQueue2) : 0.0;
                double wAvgTotal = ((nQueue1 + nQueue2) > 0) ? ((areaP4 + areaP14) / (nQueue1 + nQueue2)) : 0.0;

                double lq1Avg = areaP4 / tMod;
                double lq2Avg = areaP14 / tMod;

                double rDay = (SimulationConfig.TICKET_FARE * nTransp) - (SimulationConfig.LOSS_PENALTY * nLost);

                // Запис поточного прогону у файл (Locale.US гарантує крапку у десяткових дробах)
                writer.printf(Locale.US, "%d;%d;%d;%.4f;%.4f;%.4f;%.4f;%.4f;%.2f%n",
                        i, nTransp, nLost, wAvg1, wAvg2, wAvgTotal, lq1Avg, lq2Avg, rDay);

                // Накопичення сум для середнього значення
                totalTransp += nTransp;
                totalLost += nLost;
                totalW1 += wAvg1;
                totalW2 += wAvg2;
                totalWAvg += wAvgTotal;
                totalLq1 += lq1Avg;
                totalLq2 += lq2Avg;
                totalProfit += rDay;
            }
        } catch (IOException e) {
            System.err.println("Помилка запису у файл: " + e.getMessage());
        }

        // Виведення фiнальних усереднених результатiв у консоль
        System.out.println("   СЕРЕДНi РЕЗУЛЬТАТИ БАЗОВОЇ СИМУЛЯЦiЇ (За " + replications + " прогонiв)");
        System.out.printf(" Середня кiлькiсть перевезених (N_transp):        %.1f осiб%n", totalTransp / replications);
        System.out.printf(" Середня кiлькiсть втрачених (N_lost):            %.1f осiб%n\n", totalLost / replications);
        System.out.printf(" Середнiй час очiкування на Зупинцi 1 (W_avg1):    %.2f хв%n", totalW1 / replications);
        System.out.printf(" Середнiй час очiкування на Зупинцi 2 (W_avg2):    %.2f хв%n", totalW2 / replications);
        System.out.printf(" iнтегральний середнiй час очiкування (W_avg):     %.2f хв%n\n", totalWAvg / replications);
        System.out.printf(" Середня довжина черги на Зупинцi 1 (L_q1,avg):    %.2f осiб%n", totalLq1 / replications);
        System.out.printf(" Середня довжина черги на Зупинцi 2 (L_q2,avg):    %.2f осiб%n\n", totalLq2 / replications);
        System.out.printf(" Середнiй денний фiнансовий результат (R_day):     %.2f грн%n\n", totalProfit / replications);
        System.out.println(" [i] Детальнi результати кожного прогону збережено у файл:" + csvFile);
        
        // Виклик фiнального звiту аналiзатора
        petrinet.engine.SimulationAnalyzer.printAnalysisReport();
    }
}