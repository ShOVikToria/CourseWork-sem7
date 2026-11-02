package petrinet;

import petrinet.config.SimulationConfig;
import petrinet.engine.PetriNetEngine;
import petrinet.engine.Statistics;
import petrinet.model.BusRoutePetriNetModel;

public class Main {
    public static void main(String[] args) {
        // 1. Побудувати топологiю мережi Петрi
        PetriNetEngine engine = BusRoutePetriNetModel.buildModel();

        // 2. Запустити симуляцiю
        engine.runSimulation(SimulationConfig.SIMULATION_TIME);

        // 3. Вивести звiт
        Statistics.printReport(engine);
        
        // 4. Вивести звiт аналiзатора логiки
        petrinet.engine.SimulationAnalyzer.printAnalysisReport();
    }
}
