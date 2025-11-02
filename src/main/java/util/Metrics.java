package util;

public class Metrics {
    private long startTime;
    private long endTime;
    private int operationCount;
    private String operationName;

    public Metrics(String operationName) {
        this.operationName = operationName;
        this.operationCount = 0;
    }

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    // count single operation
    public void incrementOperations() {
        operationCount++;
    }

    public double getElapsedTimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }

    @Override
    public String toString() {
        return String.format("%s: %d operations, %.3f ms",
                operationName, operationCount, getElapsedTimeMs());
    }
}
