package petrinet;

import petrinet.config.SimulationConfig;
import petrinet.engine.PetriNetEngine;
import petrinet.model.BusRoutePetriNetModel;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WarmUpExperiment {
    public static void main(String[] args) {
        String directoryPath = "reports/warmup_analysis";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String csvFile = directoryPath + "/warmup_dynamics_" + timestamp + ".csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            // Заголовок для Excel
            writer.println("Time(min);Lq1;Lq2;Wavg1;Wavg2");
            
            // Будуємо базову модель (n=35)
            PetriNetEngine engine = BusRoutePetriNetModel.buildModel();
            
            // Запускаємо симуляцiю з кроком логування 5.0 хвилин
            double logStep = 5.0; 
            engine.runSimulation(SimulationConfig.SIMULATION_TIME, logStep, writer);

            System.out.println(" [i] Симуляцiю завершено успiшно.");
            System.out.println(" [i] Звiт динамiки (кожнi 5 хв) збережено у файл: " + csvFile);
        } catch (IOException e) {
            System.err.println("Помилка запису у файл: " + e.getMessage());
        }
    }
}