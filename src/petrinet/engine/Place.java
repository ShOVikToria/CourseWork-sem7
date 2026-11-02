package petrinet.engine;

public class Place {
    private final String id;
    private final String name;
    private int tokens;
    private double areaUnderMarking; // Для розрахунку середньої довжини черги
    private double lastUpdateTime;

    public Place(String id, String name, int initialTokens) {
        this.id = id;
        this.name = name;
        this.tokens = initialTokens;
        this.areaUnderMarking = 0.0;
        this.lastUpdateTime = 0.0;
    }

    public void updateTime(double currentTime) {
        double dt = currentTime - lastUpdateTime;
        this.areaUnderMarking += this.tokens * dt;
        this.lastUpdateTime = currentTime;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getTokens() { return tokens; }
    
    public void addTokens(int count) { this.tokens += count; }
    public void removeTokens(int count) { this.tokens -= count; }

    public double getAreaUnderMarking() { return areaUnderMarking; }
}
