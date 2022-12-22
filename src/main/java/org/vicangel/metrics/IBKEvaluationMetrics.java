package org.vicangel.metrics;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class IBKEvaluationMetrics extends EvaluationMetrics {

  public IBKEvaluationMetrics(final String evaluationOutput) {
    super(evaluationOutput);
    setTitle("IBK");
    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Scheme:")).findFirst()
      .ifPresent(this::setClassifierOptions);
  }

  public static Comparator<IBKEvaluationMetrics> getComparator() {
    return Comparator.comparing(IBKEvaluationMetrics::getKappaStatistic)
      .thenComparing(IBKEvaluationMetrics::getCorrectlyClassifiedInstances)
      .thenComparing(IBKEvaluationMetrics::getTimeTakenToBuildModel)
      .thenComparing(IBKEvaluationMetrics::getTimeTakenToPerformCrossValidation)
      .thenComparing(IBKEvaluationMetrics::getIncorrectlyClassifiedInstances);
  }

  @Override
  public int compareTo(EvaluationMetrics ibk) {
    return Double.compare(this.getKappaStatistic(), ibk.getKappaStatistic());
  }
}
