package petrinet.config; 
/** 
 * Конфiгурацiйнi параметри iмiтацiйного експерименту. 
 * Винесенi в окремий блок для зручного варiювання пiд час дослiджень. 
 */ 
public class SimulationConfig { 
    // --- Параметри модельного часу --- 
    public static double SIMULATION_TIME = 600.0; // Тривалiсть змiни (хвилин) 
    
    public static int REPLICATIONS = 80; // Кiлькiсть повторiв для пошуку середнього
    // --- Обмеження та мiсткостi системи --- 
    public static int QUEUE_CAPACITY_STOP_1 = 31; // Максимальна черга на Зупинцi 1 (Lmax1) 
    public static int QUEUE_CAPACITY_STOP_2 = 31; // Максимальна черга на Зупинцi 2 (Lmax2) 
    public static int BUS_CAPACITY_A = 35; // Мiсткiсть мiкроавтобуса А (n_A) 
    public static int BUS_CAPACITY_B = 35; // Мiсткiсть мiкроавтобуса В (n_B) 
    
    // --- Часовi параметри надходження пасажирiв (Рiвномiрний розподiл, хв) --- 
    public static double PASSENGER_ARRIVAL_MEAN_1 = 0.5; // Зупинка 1 (Δt1) 
    public static double PASSENGER_ARRIVAL_DEV_1 = 0.2; 
    public static double PASSENGER_ARRIVAL_MEAN_2 = 0.5; // Зупинка 2 (Δt2) 
    public static double PASSENGER_ARRIVAL_DEV_2 = 0.2; 
    
    // --- Часовi параметри руху мiкроавтобусiв (Рiвномiрний розподiл, хв) --- 
    public static double ROUTE_TIME_BUS_A_MEAN = 25.0; // Рейс A в один бiк 
    public static double ROUTE_TIME_BUS_A_DEV = 6.0; 
    public static double ROUTE_TIME_BUS_B_MEAN = 35.0; // Рейс В в один бiк
    public static double ROUTE_TIME_BUS_B_DEV = 6.0; 
   
    // --- Прiоритети посадки в транспорт --- 
    public static int PRIORITY_BUS_A = 10; // Вищий прiоритет посадки в А 
    public static int PRIORITY_BUS_B = 5; // Нижчий прiоритет посадки в В 
     
    // --- Економiчнi параметри --- 
    public static double TICKET_FARE = 2.0; // Вартiсть квитка (C_fare, грн) 
    public static double LOSS_PENALTY = 2.0; // Збиток вiд вiдмови пасажиру (C_loss, грн) 
}