package org.vicangel.metrics;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class SGDEvaluationMetrics extends EvaluationMetrics {

  public SGDEvaluationMetrics(final String evaluationOutput) {
    super(evaluationOutput);
    setTitle("SGD");
    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Scheme:")).findFirst()
      .ifPresent(this::setClassifierOptions);
  }

  public static Comparator<SGDEvaluationMetrics> getComparator() {
    return Comparator.comparing(SGDEvaluationMetrics::getKappaStatistic)
      .thenComparing(SGDEvaluationMetrics::getCorrectlyClassifiedInstances)
      .thenComparing(SGDEvaluationMetrics::getTimeTakenToBuildModel)
      .thenComparing(SGDEvaluationMetrics::getTimeTakenToPerformCrossValidation)
      .thenComparing(SGDEvaluationMetrics::getIncorrectlyClassifiedInstances);
  }

  @Override
  public int compareTo(EvaluationMetrics sgd) {
    return Double.compare(this.getKappaStatistic(), sgd.getKappaStatistic());
  }
}
