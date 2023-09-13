package org.vicangel.experiments;

import java.util.Random;
import java.util.StringJoiner;

import org.vicangel.ClassifierFactory;
import org.vicangel.reader.DataReader;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class NaiveBayesExperiments extends AlgorithmExperiments {

  private static final String USE_CASE = "NaiveBayes";

  public NaiveBayesExperiments() {
    super(USE_CASE);
  }

  @Deprecated
  public static void performNaiveBayesExperiments(final Instances instances) throws Exception {
    // -K or -D have no effect as mushroom.arf as is a nominal dataset. Also, can not be used together.
    final AbstractClassifier classifier = ClassifierFactory.getClassifier("");
    final var eval = new Evaluation(instances);
    eval.crossValidateModel(classifier, instances, 10, new Random(1));
    System.out.println(eval.toSummaryString("\nResults\n======\n", false));
  }

  @Override
  public void performExperiments(final boolean useConcurrency) {
    // -K or -D have no effect as mushroom.arf as is a nominal dataset. Also, can not be used together.
    final var joiner = new StringJoiner(" ")
      // options for evaluator
      .add("-t")
      .add(DataReader.MUSHROOM_FILE);

    final String options = joiner.toString();

    try {
      final String evaluationOutput = ClassifierFactory.buildAndEvaluateModel(options, "");
      System.out.println(evaluationOutput);
      writeToFile(evaluationOutput,false);
    } catch (Exception e) {
      System.out.println(e.getMessage() + "\tOptions when error Occurred: " + options);
    }
  }

  @Override
  public String getDefaultWekaOptionsSet() {
    return "";
  }
}
