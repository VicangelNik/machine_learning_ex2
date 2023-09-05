package org.vicangel.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vicangel.ClassifierFactory;
import org.vicangel.metrics.EvaluationMetrics;
import org.vicangel.metrics.IBKEvaluationMetrics;
import org.vicangel.reader.DataReader;

import static org.vicangel.helpers.ThrowingConsumer.throwingConsumerWrapper;
import static org.vicangel.helpers.ThrowingSupplier.throwingSupplierWrapper;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class IBKExperiments extends AlgorithmExperiments {

  private static final String USE_CASE = "IBK";
  private static final Logger LOGGER = Logger.getLogger(IBKExperiments.class.getName());

  public IBKExperiments() {
    super(USE_CASE);
  }

  @Override
  public void performExperiments(final boolean useConcurrency) {
    LOGGER.info("Starting IBK tests");
    LOGGER.log(Level.INFO, "Experiments are running in concurrency mode: {0}", useConcurrency);
    final List<EvaluationMetrics> metricsList = new ArrayList<>();
    final List<CompletableFuture<Void>> evaluationFutureList = Collections.synchronizedList(new ArrayList<>());
    final Map<Integer, String> optionsList = new LinkedHashMap<>();

    // Select the number of nearest neighbours between 1 and the k value specified using hold-one-out evaluation on the training data (use when k > 1)
    final String[] crossValidate = new String[]{"-X", ""};
    // Weight neighbours by 1 - their distance (-F)
    // Weight neighbours by the inverse of their distance (-I)
    final String[] weightNeighbours = new String[]{"", "-I", "-F"};
    // Whether to skip identical instances (with distance 0 to the target)
    final String[] skipIdentical = new String[]{"-S ", ""};
    final String[] nearestNeighbourSearchAlgorithm = new String[]{"weka.core.neighboursearch.LinearNNSearch "}; // -A
    final String[] distanceFunction = new String[]{"weka.core.EuclideanDistance -R first-last ",
      "weka.core.ManhattanDistance -R first-last ", "weka.core.ChebyshevDistance -R first-last ",
      "weka.core.MinkowskiDistance -P 2.0 -R first-last "}; // -A
    final String[] invertSelection = new String[]{"-V", ""};

    for (int knn = 1; knn <= 30; knn++) { // Number of nearest neighbours (k) used in classification
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
                    // options for distance function
                    .add(knn > 1 ? "" : invert) //throws java.lang.Error -> Couldn't optimize by cross-validation for knn >1
                    .add("\\\"")
                    // options for nearest Neighbour Search Algorithm
                    .add("-P") // Always calculate performance statistics for the NN search.
                    .add(sIdentical)
                    .add("\"")
                    // options for evaluator
                    .add("-t")
                    .add(DataReader.MUSHROOM_FILE);

                  final String options = joiner.toString();

                  if (!optionsList.containsValue(options)) { // because of line 69
                    optionsList.put(optionsCounter++, options);
                  }
                }
              }
            }
          }
        }
      }
    }

    printNumberOfExperiments(optionsList.size());

    optionsList.forEach((count, options) -> evaluate(options, metricsList, useConcurrency, evaluationFutureList, count)
    );

    writeInFile(metricsList, useConcurrency, evaluationFutureList);
  }

  private void evaluate(String options, List<EvaluationMetrics> metricsList, boolean useConcurrency,
                        List<CompletableFuture<Void>> evaluationFutureList, int count) {
    if (useConcurrency) {
      final CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
          throwingSupplierWrapper(() -> ClassifierFactory.buildAndEvaluateModel(options, "I")),
          executor)
        .thenAcceptAsync(throwingConsumerWrapper(evaluationOutput -> {
          final var ibkEvaluationMetrics = new IBKEvaluationMetrics(evaluationOutput);
          metricsList.add(ibkEvaluationMetrics);
          System.out.println(count + ": " + ibkEvaluationMetrics);
        }));
      evaluationFutureList.add(completableFuture);
    } else {
      try {
        final String evaluationOutput = ClassifierFactory.buildAndEvaluateModel(options, "I");
        final var ibkEvaluationMetrics = new IBKEvaluationMetrics(evaluationOutput);
        metricsList.add(ibkEvaluationMetrics);
        System.out.println(count + ": " + ibkEvaluationMetrics);
      } catch (Exception e) {
        // throw new RuntimeException(e);
        System.out.println(count + ": " + e.getMessage() + "\tOptions when error Occurred: " + options);
      }
    }
  }

  private void writeInFile(List<EvaluationMetrics> metricsList, boolean useConcurrency,
                           List<CompletableFuture<Void>> evaluationFutureList) {
    if (useConcurrency) {
      CompletableFuture.allOf(evaluationFutureList.toArray(new CompletableFuture[0]))
        .thenAccept(throwingConsumerWrapper(c -> {
          metricsList.sort(IBKEvaluationMetrics.getComparator());
          writeToFile(metricsList);
        })).whenComplete((c, throwable) -> System.exit(666));
    } else {
      metricsList.sort(IBKEvaluationMetrics.getComparator());
      try {
        writeToFile(metricsList);
      } catch (IOException e) {
        System.out.println(e.getMessage());
        // throw new RuntimeException(e);
      }
    }
  }

  @Override
  public String getDefaultWekaOptionsSet() {
    return "-K 1 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"";
  }
}
