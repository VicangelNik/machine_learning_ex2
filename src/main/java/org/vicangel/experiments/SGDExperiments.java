package org.vicangel.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
  private static final String USE_CASE = "SGD";

  public SGDExperiments() {
    super(USE_CASE);
  }

  @Override
  public void performExperiments(final boolean useConcurrency) {
    LOGGER.info("Starting SGDExperiments tests");
    final List<SGDEvaluationMetrics> metricsList = new ArrayList<>();
    final List<CompletableFuture<Void>> evaluationFutureList = Collections.synchronizedList(new ArrayList<>());
    final Map<Integer, String> optionsList = new LinkedHashMap<>();

    final String[] learningRates = new String[]{"0.005", "0.01", "0.02"};
    final String[] regularizationConstants = new String[]{"1.0E-3", "1.0E-4", "1.0E-5"}; // -R
    // The epsilon threshold for epsilon insensitive and Huber loss. Does not make any difference here.
    final String[] epsilonThresholds = new String[]{"0.001"};
    // Number of epochs to train through.
    for (int numberOfEpochs = 300; numberOfEpochs <= 700; numberOfEpochs += 200) { // -E
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
                .add(String.valueOf(numberOfEpochs))
                .add("-C")
                .add(epsilon)
                .add("-S")
                .add(String.valueOf(seed))
                // options for evaluator
                .add("-t")
                .add(DataReader.MUSHROOM_FILE);

              final String options = joiner.toString();

              if (!optionsList.containsValue(options)) {
                optionsList.put(optionsCounter++, options);
              }
            }
          }
        }
      }
    }

    printNumberOfExperiments(optionsList.size());

    optionsList.forEach((count, options) -> evaluate(options, metricsList, useConcurrency, evaluationFutureList, count));

    writeInFile(metricsList, useConcurrency, evaluationFutureList);
  }

  private void evaluate(String options,
                        List<SGDEvaluationMetrics> metricsList,
                        boolean useConcurrency,
                        List<CompletableFuture<Void>> evaluationFutureList,
                        Integer count) {

    if (useConcurrency) {
      final CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
        throwingSupplierWrapper(() -> ClassifierFactory.buildAndEvaluateModel(options, "S")),
        executor).thenAcceptAsync(throwingConsumerWrapper(evaluationOutput -> {
        final var sgdEvaluationMetrics = new SGDEvaluationMetrics(evaluationOutput);
        metricsList.add(sgdEvaluationMetrics);
        System.out.println(count + ": " + sgdEvaluationMetrics);
      }));
      evaluationFutureList.add(completableFuture);
    } else {
      try {
        final String evaluationOutput = ClassifierFactory.buildAndEvaluateModel(options, "S");
        final var sgdEvaluationMetrics = new SGDEvaluationMetrics(evaluationOutput);
        metricsList.add(sgdEvaluationMetrics);
        System.out.println(count + ": " + sgdEvaluationMetrics);
      } catch (Exception e) {
        // throw new RuntimeException(e);
        System.out.println(count + ": " + e.getMessage() + "\tOptions when error Occurred: " + options);
      }
    }
  }

  private void writeInFile(List<SGDEvaluationMetrics> metricsList, boolean useConcurrency,
                           List<CompletableFuture<Void>> evaluationFutureList) {
    if (useConcurrency) {
      CompletableFuture.allOf(evaluationFutureList.toArray(new CompletableFuture[0]))
        .thenAccept(throwingConsumerWrapper(c -> {
          metricsList.sort(SGDEvaluationMetrics.getComparator());
          writeToFile(metricsList);
        })).whenComplete((c, throwable) -> System.exit(666));
    } else {
      metricsList.sort(SGDEvaluationMetrics.getComparator());
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
    return "-F 0 -L 0.01 -R 1.0E-4 -E 500 -C 0.001 -S 1";
  }
}
