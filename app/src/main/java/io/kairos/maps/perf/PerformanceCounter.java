package io.kairos.maps.perf;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerformanceCounter {
    private static Map<String, PerformanceCounter> perfMap;

    static {
        perfMap = new HashMap<String, PerformanceCounter>();
    }

    private long totalTime = 0;
    private long numCounters = 0;
    private long lastStartedTime = 0;

    public synchronized static PerformanceCounter get(String name) {
        if (perfMap.containsKey(name)) return perfMap.get(name);

        PerformanceCounter counter = new PerformanceCounter();
        perfMap.put(name, counter);
        return counter;
    }

    public void start() {
        lastStartedTime = new Date().getTime();
    }

    public void stop() {
        long now = new Date().getTime();
        totalTime += (now - lastStartedTime);
        numCounters++;
    }

    public double getAverageTime() {
        return (double)totalTime / (double)numCounters;
    }

    @Override
    public String toString() {
        return "PerformanceCounter{" +
                "totalTime=" + totalTime +
                ", numCounters=" + numCounters +
                ", lastStartedTime=" + lastStartedTime +
                '}';
    }
}
