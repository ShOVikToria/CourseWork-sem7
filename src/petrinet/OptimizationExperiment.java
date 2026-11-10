package petrinet;

import petrinet.config.SimulationConfig;
import petrinet.engine.PetriNetEngine;
import petrinet.model.BusRouteOptimizationModel;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OptimizationExperiment {

    public static void main(String[] args) {
        System.out.println("Запуск дослiдження порогу вiдправлення (k)... Будь ласка, зачекайте.");

        int replications = SimulationConfig.REPLICATIONS;
        
        String directoryPath = "reports/departure_optimization";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        String csvFile = directoryPath + "/data_" + timestamp + ".csv";
        String tableFile = directoryPath + "/table_" + timestamp + ".txt";
        String logFile = directoryPath + "/demo_log_" + timestamp + ".txt";
        
        double bestProfit = -Double.MAX_VALUE;
        int optimalK = -1;

        try (PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile));
             PrintWriter tableWriter = new PrintWriter(new FileWriter(tableFile))) {
             
            csvWriter.println("k;Перевезено;Вiдмов;Прибуток(грн)");
            
            tableWriter.println("Аналiз прибутку для рiзних значень k (вiд 15 до 35):");
            tableWriter.println("-----------------------------------------------------------------------");
            tableWriter.printf("%-5s | %-15s | %-15s | %-15s%n", "k", "Перевезено", "Вiдмов", "Середнiй прибуток (грн)");
            tableWriter.println("-----------------------------------------------------------------------");

            for (int k = 15; k <= 35; k++) {
                double totalProfit = 0;
                long totalTransp = 0;
                long totalLost = 0;

                for (int i = 0; i < replications; i++) {
                    PetriNetEngine engine = BusRouteOptimizationModel.buildModel(k, false);
                    engine.runSimulation(SimulationConfig.SIMULATION_TIME);

                    int nTransp = engine.getPlace("P10").getTokens();
                    int nLost = engine.getPlace("P5").getTokens();
                    double profit = (nTransp * SimulationConfig.TICKET_FARE) - (nLost * SimulationConfig.LOSS_PENALTY);
                    
                    totalProfit += profit;
                    totalTransp += nTransp;
                    totalLost += nLost;
                }

                double avgProfit = totalProfit / replications;
                double avgTransp = (double) totalTransp / replications;
                double avgLost = (double) totalLost / replications;
                
                csvWriter.printf(Locale.US, "%d;%.1f;%.1f;%.2f%n", k, avgTransp, avgLost, avgProfit);
                tableWriter.printf(Locale.US, "%-5d | %-15.1f | %-15.1f | %.2f%n", k, avgTransp, avgLost, avgProfit);

                if (avgProfit > bestProfit) {
                    bestProfit = avgProfit;
                    optimalK = k;
                }
            }
            
            tableWriter.println("-----------------------------------------------------------------------");
            tableWriter.printf(Locale.US, " РЕЗУЛЬТАТ: Найбiльший прибуток (%.2f грн) досягається при k = %d%n", bestProfit, optimalK);

        } catch (IOException e) {
            System.err.println("Помилка запису у файл: " + e.getMessage());
        }

        PrintStream originalConsole = System.out;
        
        try (PrintStream logStream = new PrintStream(new FileOutputStream(logFile))) {
            System.setOut(logStream);
            
            System.out.println("Запуск демонстрацiйного прогону для оптимального k = " + optimalK + " iз виведенням логiв:");
            PetriNetEngine optimalEngine = BusRouteOptimizationModel.buildModel(optimalK, true);
            optimalEngine.runSimulation(SimulationConfig.SIMULATION_TIME);
            
            // Видалено Statistics.printReport(optimalEngine);
            
        } catch (IOException e) {
            System.err.println("Помилка запису логу: " + e.getMessage());
        } finally {
            System.setOut(originalConsole);
        }

        System.out.println("Дослiдження завершено!");
        System.out.println("Оптимальне k = " + optimalK + " (Максимальний прибуток: " + String.format(Locale.US, "%.2f", bestProfit) + " грн)");
        System.out.println("Усi звiти збережено у папку: " + directoryPath);
    }
}