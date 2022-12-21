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

  public static Comparator<? super MPEvaluationMetrics> getComparator() {
    return Comparator.comparing(MPEvaluationMetrics::getKappaStatistic)
      .thenComparing(MPEvaluationMetrics::getCorrectlyClassifiedInstances)
      .thenComparing(MPEvaluationMetrics::getTimeTakenToBuildModel)
      .thenComparing(MPEvaluationMetrics::getTimeTakenToPerformCrossValidation)
      .thenComparing(MPEvaluationMetrics::getIncorrectlyClassifiedInstances);
  }

  @Override
  public int compareTo(EvaluationMetrics mp) {
    return Double.compare(this.getKappaStatistic(), mp.getKappaStatistic());
  }
}
