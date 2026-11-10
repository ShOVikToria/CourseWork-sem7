package petrinet;

import petrinet.config.SimulationConfig;
import petrinet.engine.PetriNetEngine;
import petrinet.model.BusRouteCapacityModel;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CapacityExperiment {

    public static void main(String[] args) {
        int replications = SimulationConfig.REPLICATIONS;
        
        String directoryPath = "reports/capacity_optimization";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        // Файли для звiтiв
        String csvFile = directoryPath + "/data_" + timestamp + ".csv";
        String tableFile = directoryPath + "/table_" + timestamp + ".txt";
        String logFile = directoryPath + "/demo_log_" + timestamp + ".txt";

        double minWaitTime = Double.MAX_VALUE;
        int optimalN = -1;

        try (PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile));
             PrintWriter tableWriter = new PrintWriter(new FileWriter(tableFile))) {
             
            csvWriter.println("n;Перевезено;Вiдмов;W_avg(хв);Прибуток(грн)");
            
            // Записуємо таблицю у ТЕКСТОВИЙ ФАЙЛ, а не в консоль
            tableWriter.println("Аналiз часу очiкування для рiзних значень мiсткостi n (вiд 15 до 35):");
            tableWriter.println("---------------------------------------------------------------------------------");
            tableWriter.printf("%-5s | %-15s | %-15s | %-20s | %-15s%n", 
                    "n", "Перевезено", "Вiдмов", "Середнiй час очiк.(хв)", "Прибуток (грн)");
            tableWriter.println("---------------------------------------------------------------------------------");

            for (int n = 15; n <= 35; n++) {
                double totalWaitTime = 0.0;
                long totalTransp = 0;
                long totalLost = 0;
                double totalProfit = 0.0;

                for (int i = 0; i < replications; i++) {
                    PetriNetEngine engine = BusRouteCapacityModel.buildModel(n, false);
                    engine.runSimulation(SimulationConfig.SIMULATION_TIME);

                    int nTransp = engine.getPlace("P10").getTokens();
                    int nLost = engine.getPlace("P5").getTokens();
                    double profit = (nTransp * SimulationConfig.TICKET_FARE) - (nLost * SimulationConfig.LOSS_PENALTY);
                    
                    int nQueue1 = engine.getTransitions().stream().filter(t -> t.getId().equals("T2")).findFirst().get().getFiringCount();
                    int nQueue2 = engine.getTransitions().stream().filter(t -> t.getId().equals("T9")).findFirst().get().getFiringCount();
                    
                    double areaP4_total = engine.getPlace("P4").getAreaUnderMarking();
                    
                    double areaP14_total = engine.getPlace("P14").getAreaUnderMarking();
                    
                    double wAvg = 0.0;
                    if ((nQueue1 + nQueue2) > 0) {
                        wAvg = (areaP4_total + areaP14_total) / (nQueue1 + nQueue2);
                    }

                    totalWaitTime += wAvg;
                    totalTransp += nTransp;
                    totalLost += nLost;
                    totalProfit += profit;
                }

                double avgWaitTime = totalWaitTime / replications;
                double avgTransp = (double) totalTransp / replications;
                double avgLost = (double) totalLost / replications;
                double avgProfit = totalProfit / replications;
                        
                csvWriter.printf(Locale.US, "%d;%.1f;%.1f;%.2f;%.2f%n", 
                        n, avgTransp, avgLost, avgWaitTime, avgProfit);
                        
                // Пишемо рядок у файл table.txt
                tableWriter.printf(Locale.US, "%-5d | %-15.1f | %-15.1f | %-20.2f | %.2f%n", 
                        n, avgTransp, avgLost, avgWaitTime, avgProfit);

                if (avgWaitTime < minWaitTime) {
                    minWaitTime = avgWaitTime;
                    optimalN = n;
                }
            }
            
            tableWriter.println("---------------------------------------------------------------------------------");
            tableWriter.printf(Locale.US, " РЕЗУЛЬТАТ: Найменший час очiкування (%.2f хв) досягається при n = %d%n", minWaitTime, optimalN);

        } catch (IOException e) {
            System.err.println("Помилка запису у файл: " + e.getMessage());
        }

        PrintStream originalConsole = System.out; 
        
        try (PrintStream logStream = new PrintStream(new FileOutputStream(logFile))) {
            // Перенаправляємо весь вивiд у файл логу
            System.setOut(logStream);
            
            PetriNetEngine optimalEngine = BusRouteCapacityModel.buildModel(optimalN, true); // true = включити логи руху
            optimalEngine.runSimulation(SimulationConfig.SIMULATION_TIME);         
        } catch (IOException e) {
            System.err.println("Помилка запису логу: " + e.getMessage());
        } finally {
            System.setOut(originalConsole);
        }

        // Фiнальне повiдомлення в чисту консоль
        System.out.println("Оптимальне n = " + optimalN + " (Час очiкування: " + String.format(Locale.US, "%.2f", minWaitTime) + " хв)");
        System.out.println("Усi звiти збережено у папку: " + directoryPath);
    }
}