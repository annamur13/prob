package com.haulmont.prob.repository.tezis;

import org.springframework.stereotype.Component;

@Component
public class TezisTaskProbability {

    private static final double BASE_PROBABILITY = 0.95; // Базовая вероятность при 0 задач
    private static final double PROBABILITY_DECREASE_FACTOR = 0.05; // Коэффициент уменьшения на задачу

    public double calculateProbability(long taskCount) {
        // Формула: вероятность = базовая * (1 - коэффициент * количество задач)
        double probability = BASE_PROBABILITY * (1 - PROBABILITY_DECREASE_FACTOR * taskCount);

        // Гарантируем, что вероятность не будет меньше 0.1
        return Math.max(0.1, probability);
    }
}