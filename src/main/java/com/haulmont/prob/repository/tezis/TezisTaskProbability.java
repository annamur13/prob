package com.haulmont.prob.repository.tezis;

import org.springframework.stereotype.Component;

@Component
public class TezisTaskProbability {
    private static final double BASE_PROBABILITY = 0.95; // Базовая вероятность при 0 задач
    private static final double TASK_COUNT_DECREASE_FACTOR = 0.05; // Коэффициент уменьшения на задачу
    private static final double TEXT_LENGTH_DECREASE_FACTOR = 0.0001; // Коэффициент уменьшения на символ
    private static final double MIN_PROBABILITY = 0.1; // Минимальная вероятность

    /**
     * Рассчитывает вероятность с учетом количества задач и средней длины их описаний
     * @param taskCount количество задач
     * @param totalTextLength общая длина текста всех задач (в символах)
     * @return рассчитанная вероятность
     */
    public double calculateProbability(long taskCount, long totalTextLength) {
        if (taskCount == 0) {
            return BASE_PROBABILITY;
        }

        // Рассчитываем среднюю длину описания задачи
        double avgTextLength = (double) totalTextLength / taskCount;

        // Уменьшаем вероятность на основе количества задач
        double probability = BASE_PROBABILITY * (1 - TASK_COUNT_DECREASE_FACTOR * taskCount);

        // Дополнительно уменьшаем вероятность на основе средней длины текста
        probability *= (1 - Math.min(TEXT_LENGTH_DECREASE_FACTOR * avgTextLength, 0.5));

        // Гарантируем, что вероятность не выйдет за допустимые пределы
        return Math.max(MIN_PROBABILITY, probability);
    }

    /**
     * Упрощенный метод для обратной совместимости
     */
    public double calculateProbability(long taskCount) {
        return calculateProbability(taskCount, 0);
    }
}