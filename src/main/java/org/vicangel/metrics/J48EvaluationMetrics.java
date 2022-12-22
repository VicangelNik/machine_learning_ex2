package org.vicangel.metrics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class J48EvaluationMetrics extends EvaluationMetrics {

  private short numberOfLeaves;
  private short sizeOfTree;

  public J48EvaluationMetrics(final String evaluationOutput) {
    super(evaluationOutput);
    final String[] parsedEvaluationOutput = super.parsedEvaluationOutput;

    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Number of Leaves")).
      reduce((first, second) -> second)
      .ifPresent(foundLine -> {
        List<String> numValuesList = findDecimalNums(foundLine);
        numberOfLeaves = Short.parseShort(numValuesList.get(0));
      });

    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Size of the tree")).
      reduce((first, second) -> second)
      .ifPresent(foundLine -> {
        List<String> numValuesList = findDecimalNums(foundLine);
        sizeOfTree = Short.parseShort(numValuesList.get(0));
      });
  }

  public static Comparator<J48EvaluationMetrics> getComparator() {
    return Comparator.comparing(J48EvaluationMetrics::getNumberOfLeaves)
      .thenComparing(J48EvaluationMetrics::getSizeOfTree)
      .thenComparing(J48EvaluationMetrics::getKappaStatistic)
      .thenComparing(J48EvaluationMetrics::getCorrectlyClassifiedInstances)
      .thenComparing(J48EvaluationMetrics::getIncorrectlyClassifiedInstances);
  }

  public short getNumberOfLeaves() {
    return numberOfLeaves;
  }

  public short getSizeOfTree() {
    return sizeOfTree;
  }

  @Override
  public int compareTo(EvaluationMetrics j48) {
    return Double.compare(this.getKappaStatistic(), j48.getKappaStatistic());
  }

  @Override
  public String toString() {
    return "J48EvaluationMetrics{" +
           "numberOfLeaves=" + numberOfLeaves +
           ", sizeOfTree=" + sizeOfTree + ", " + super.toString();
  }
}
