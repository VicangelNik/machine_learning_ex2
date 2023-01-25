package org.vicangel.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.vicangel.ClassifierFactory;
import org.vicangel.metrics.SGDEvaluationMetrics;
import org.vicangel.reader.DataReader;

import static org.vicangel.helpers.ThrowingConsumer.throwingConsumerWrapper;
import static org.vicangel.helpers.ThrowingSupplier.throwingSupplierWrapper;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class SGDExperiments extends AlgorithmExperiments {

  private static final Logger LOGGER = Logger.getLogger(SGDExperiments.class.getName());

  @Override
  public void performExperiments() {
    LOGGER.info("Starting SGDExperiments tests");
    final List<SGDEvaluationMetrics> metricsList = new ArrayList<>();
    final List<CompletableFuture<Void>> evaluationFutureList = Collections.synchronizedList(new ArrayList<>());

    final String[] learningRates = new String[]{"0.01", "0.02", "0.03", "0.04", "0.05", "0.06", "0.07", "0.08", "0.09", "0.1"};
    final String[] regularizationConstants = new String[]{"1.0E-3", "1.0E-4", "1.0E-5", "0.5E-3", "0.5E-4", "0.5E-5"}; // -R
    // The epsilon threshold for epsilon insensitive and Huber loss.
    final String[] epsilonThresholds = new String[]{"0.0001", "0.005", "0.001", "0.005", "0.01"};
    // Number of epochs to train through.
    for (int numberOfEpochs = 100; numberOfEpochs <= 800; numberOfEpochs += 100) { // -E
      for (String learningRate : learningRates) {
        for (String lambda : regularizationConstants) {
          for (String epsilon : epsilonThresholds) {
            for (int seed = 1; seed <= 4; seed++) {
              final var joiner = new StringJoiner(" ")
                .add("-F")
                .add("0") // Hinge loss (SVM)
                .add("-L")
                .add(learningRate)
                .add("-R")
                .add(lambda)
                .add("-E")
                .add(epsilon)
                .add("-S")
                .add(String.valueOf(seed))
                // options for evaluator
                .add("-t")
                .add(DataReader.MUSHROOM_FILE);

              final CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
                throwingSupplierWrapper(() -> ClassifierFactory.buildAndEvaluateModel(joiner.toString(), "S")),
                executor).thenAcceptAsync(evaluationOutput -> {
                final var sgdEvaluationMetrics = new SGDEvaluationMetrics(evaluationOutput);
                metricsList.add(sgdEvaluationMetrics);
                System.out.println(sgdEvaluationMetrics);
              });
              evaluationFutureList.add(completableFuture);
            }
          }
        }
      }
    }
    CompletableFuture.allOf(evaluationFutureList.toArray(new CompletableFuture[0]))
      .thenAccept(throwingConsumerWrapper(c -> {
        metricsList.sort(SGDEvaluationMetrics.getComparator());
        writeToFile(metricsList);
      }));
  }

  @Override
  public String getDefaultWekaOptionsSet() {
    return "-F 0 -L 0.01 -R 1.0E-4 -E 500 -C 0.001 -S 1";
  }
}
