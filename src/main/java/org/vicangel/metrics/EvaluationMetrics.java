package org.vicangel.metrics;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public abstract class EvaluationMetrics implements Comparable<EvaluationMetrics> {

  protected String[] parsedEvaluationOutput;
  private static final Pattern decimalNumPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
  private String title;
  private final String classifierOptions;
  private float timeTakenToBuildModel; // 2-decimals seconds
  private float timeTakenToTestModelOnTrainingData; // 2-decimals seconds
  private float timeTakenToPerformCrossValidation; // 2-decimals seconds
  private short correctlyClassifiedInstances;
  private float correctlyClassifiedInstancesPercentage; // 4-decimals
  private short incorrectlyClassifiedInstances;
  private float incorrectlyClassifiedInstancesPercentage; // 4-decimals
  private float kappaStatistic; // 4-decimals

  protected EvaluationMetrics(String evaluationOutput) {
    parsedEvaluationOutput = evaluationOutput.split("\n");
    classifierOptions = parsedEvaluationOutput[1];
    title = parsedEvaluationOutput[5];

    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Time taken to build model")).findFirst()
      .ifPresent(foundLine -> {
        List<String> numValuesList = findDecimalNums(foundLine);
        timeTakenToBuildModel = Float.parseFloat(numValuesList.get(0));
      });

    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Time taken to test model on training data")).findFirst()
      .ifPresent(foundLine -> {
        List<String> numValuesList = findDecimalNums(foundLine);
        timeTakenToPerformCrossValidation = Float.parseFloat(numValuesList.get(0));
      });

    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Time taken to perform cross-validation")).findFirst()
      .ifPresent(foundLine -> {
        List<String> numValuesList = findDecimalNums(foundLine);
        timeTakenToTestModelOnTrainingData = Float.parseFloat(numValuesList.get(0));
      });

    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Correctly Classified Instances"))
      .reduce((first, second) -> second)
      .ifPresent(foundLine -> {
        List<String> numValuesList = findDecimalNums(foundLine);
        correctlyClassifiedInstances = Short.parseShort(numValuesList.get(0));
        correctlyClassifiedInstancesPercentage = Float.parseFloat(numValuesList.get(1));
      });

    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Incorrectly Classified Instances")).
      reduce((first, second) -> second)
      .ifPresent(foundLine -> {
        List<String> numValuesList = findDecimalNums(foundLine);
        incorrectlyClassifiedInstances = Short.parseShort(numValuesList.get(0));
        incorrectlyClassifiedInstancesPercentage = Float.parseFloat(numValuesList.get(1));
      });

    Arrays.stream(parsedEvaluationOutput).filter(line -> line.startsWith("Kappa statistic")).
      reduce((first, second) -> second)
      .ifPresent(foundLine -> {
        List<String> numValuesList = findDecimalNums(foundLine);
        kappaStatistic = Float.parseFloat(numValuesList.get(0));
      });
  }

  protected void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public String getClassifierOptions() {
    return classifierOptions;
  }

  public float getTimeTakenToBuildModel() {
    return timeTakenToBuildModel;
  }

  public float getTimeTakenToTestModelOnTrainingData() {
    return timeTakenToTestModelOnTrainingData;
  }

  public float getTimeTakenToPerformCrossValidation() {
    return timeTakenToPerformCrossValidation;
  }

  public short getCorrectlyClassifiedInstances() {
    return correctlyClassifiedInstances;
  }

  public float getCorrectlyClassifiedInstancesPercentage() {
    return correctlyClassifiedInstancesPercentage;
  }

  public short getIncorrectlyClassifiedInstances() {
    return incorrectlyClassifiedInstances;
  }

  public float getIncorrectlyClassifiedInstancesPercentage() {
    return incorrectlyClassifiedInstancesPercentage;
  }

  public float getKappaStatistic() {
    return kappaStatistic;
  }

  protected static List<String> findDecimalNums(String stringToSearch) { // https://www.baeldung.com/java-find-numbers-in-string
    final var matcher = decimalNumPattern.matcher(stringToSearch);

    final List<String> decimalNumList = new LinkedList<>();
    while (matcher.find()) {
      decimalNumList.add(matcher.group());
    }
    return decimalNumList;
  }

  @Override
  public String toString() {
    return ", title='" + title + '\'' +
           ", classifierOptions='" + classifierOptions + '\'' +
           ", timeTakenToBuildModel=" + timeTakenToBuildModel +
           ", timeTakenToTestModelOnTrainingData=" + timeTakenToTestModelOnTrainingData +
           ", timeTakenToPerformCrossValidation=" + timeTakenToPerformCrossValidation +
           ", correctlyClassifiedInstances=" + correctlyClassifiedInstances +
           ", correctlyClassifiedInstancesPercentage=" + correctlyClassifiedInstancesPercentage +
           ", incorrectlyClassifiedInstances=" + incorrectlyClassifiedInstances +
           ", incorrectlyClassifiedInstancesPercentage=" + incorrectlyClassifiedInstancesPercentage +
           ", kappaStatistic=" + kappaStatistic +
           '}';
  }
}
