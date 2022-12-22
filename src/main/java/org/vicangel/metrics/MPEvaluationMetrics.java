package org.vicangel.metrics;

import java.util.Comparator;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class MPEvaluationMetrics extends EvaluationMetrics {

  public MPEvaluationMetrics(String evaluationOutput) {
    super(evaluationOutput);
    setTitle("MultilayerPerceptron");
  }

  public static Comparator<EvaluationMetrics> getComparator() {
    return Comparator.comparing(EvaluationMetrics::getKappaStatistic)
      .thenComparing(EvaluationMetrics::getCorrectlyClassifiedInstances)
      .thenComparing(EvaluationMetrics::getTimeTakenToBuildModel)
      .thenComparing(EvaluationMetrics::getTimeTakenToPerformCrossValidation)
      .thenComparing(EvaluationMetrics::getIncorrectlyClassifiedInstances);
  }

  @Override
  public int compareTo(EvaluationMetrics mp) {
    return Double.compare(this.getKappaStatistic(), mp.getKappaStatistic());
  }
}
