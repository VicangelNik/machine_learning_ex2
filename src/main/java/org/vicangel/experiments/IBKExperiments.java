package org.vicangel.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.vicangel.ClassifierFactory;
import org.vicangel.metrics.IBKEvaluationMetrics;
import org.vicangel.reader.DataReader;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class IBKExperiments extends AlgorithmExperiments {

  @Override
  public void performExperiments() throws Exception {
    final List<IBKEvaluationMetrics> metricsList = new ArrayList<>();

    // Select the number of nearest neighbours between 1 and the k value specified using hold-one-out evaluation on the training data (use when k > 1)
    final String[] crossValidate = new String[]{"-X", ""};
    // Weight neighbours by 1 - their distance (-F)
    // Weight neighbours by the inverse of their distance (-I)
    final String[] weightNeighbours = new String[]{"", "-I", "-F"};
    // Whether to skip identical instances (with distance 0 to the target)
    final String[] skipIdentical = new String[]{"-S ", ""};
    final String[] nearestNeighbourSearchAlgorithm = new String[]{"weka.core.neighboursearch.LinearNNSearch "}; // -A
    final String[] distanceFunction = new String[]{"weka.core.EuclideanDistance -R first-last ",
      "weka.core.ManhattanDistance -R first-last ", "weka.core.ChebyshevDistance -R first-last "}; // -A
    final String[] invertSelection = new String[]{"-V", ""};

    for (int knn = 1; knn <= 22; knn++) // Number of nearest neighbours (k) used in classification
    {
      for (String cv : crossValidate) {
        for (String wn : weightNeighbours) {
          for (String nNAlgorithm : nearestNeighbourSearchAlgorithm) {
            for (String sIdentical : skipIdentical) {
              for (String dFunction : distanceFunction) {
                for (String invert : invertSelection) {
                  final var joiner = new StringJoiner(" ")
                    .add(cv)
                    .add(wn)
                    .add("-K")
                    .add(String.valueOf(knn))
                    .add("-A")
                    .add("\"")
                    .add(nNAlgorithm)
                    .add("-A")
                    .add("\\\"")
                    .add(dFunction)
                    .add("\\\"")
                    // options for distance function
                    .add(invert)
                    .add("\"")
                    // options for nearest Neighbour Search Algorithm
                    .add("-P") // Always calculate performance statistics for the NN search.
                    .add(sIdentical)
                    // options for evaluator
                    .add("-t")
                    .add(DataReader.MUSHROOM_FILE);

                  final String evaluationOutput = ClassifierFactory.buildAndEvaluateModel(joiner.toString(), "I");

                  metricsList.add(new IBKEvaluationMetrics(evaluationOutput));
                }
              }
            }
          }
        }
      }
    }
    metricsList.sort(IBKEvaluationMetrics.getComparator());
    writeToFile(metricsList);
  }

  @Override
  public String getDefaultWekaOptionsSet() {
    return "-K 1 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"";
  }
}
