package org.vicangel.experiments;

import java.util.Random;

import org.vicangel.ClassifierFactory;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class NaiveBayesExperiments extends AlgorithmExperiments {

  public static void performNaiveBayesExperiments(final Instances instances) throws Exception {
    // -K or -D have no effect as mushroom.arf is a nominal dataset. Also, can not be used together.
    final AbstractClassifier classifier = ClassifierFactory.getClassifier("");
    final var eval = new Evaluation(instances);
    eval.crossValidateModel(classifier, instances, 10, new Random(1));
    System.out.println(eval.toSummaryString("\nResults\n======\n", false));
  }

  @Override
  public void performExperiments() {

  }

  @Override
  public String getDefaultWekaOptionsSet() {
    return "";
  }
}
