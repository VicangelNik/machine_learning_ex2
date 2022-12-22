
package org.vicangel;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SGD;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Utils;

import static weka.classifiers.evaluation.Evaluation.evaluateModel;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class ClassifierFactory {

  private ClassifierFactory() {
  }

  public static AbstractClassifier getClassifier(String classifier) {
    return switch (classifier) {
      case "T" -> new J48();
      case "M" -> new MultilayerPerceptron();
      case "I" -> new IBk();
      case "S" -> new SGD();
      default -> new NaiveBayes();
    };
  }

  public static String buildAndEvaluateModel(final String options, String classifierId) throws Exception {
    final String[] optionsArray = Utils.splitOptions(options);

    final AbstractClassifier classifier = ClassifierFactory.getClassifier(classifierId);
    // cross-validation (-x) == 10, random seed (-s) == 1
    // like Evaluation eval = new Evaluation(instances);
    // eval.crossValidateModel(classifier, instances, 10, new Random(1), output, stringBuilder);
    // System.out.println(eval.toSummaryString("\nResults\n======\n", true));
    // but the returned output has more information
    // https://weka.sourceforge.io/doc.dev/weka/classifiers/Evaluation.html#evaluateModel-weka.classifiers.Classifier-java.lang.String:A-
    return evaluateModel(classifier, optionsArray);
  }
}
