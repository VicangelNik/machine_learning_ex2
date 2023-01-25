package org.vicangel.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.vicangel.ClassifierFactory;
import org.vicangel.metrics.MPEvaluationMetrics;
import org.vicangel.reader.DataReader;

import static org.vicangel.helpers.ThrowingConsumer.throwingConsumerWrapper;
import static org.vicangel.helpers.ThrowingSupplier.throwingSupplierWrapper;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class MultilayerPerceptronExperiments extends AlgorithmExperiments {

  private static final Logger LOGGER = Logger.getLogger(MultilayerPerceptronExperiments.class.getName());

  @Override
  public void performExperiments() {
    LOGGER.info("Starting MultilayerPerceptronExperiments tests");
    final List<MPEvaluationMetrics> metricsList = new ArrayList<>();
    final List<CompletableFuture<Void>> evaluationFutureList = Collections.synchronizedList(new ArrayList<>());

    final String[] learningRates = new String[]{"0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1"}; // -L
    final String[] momentumRates = new String[]{"0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1"}; // -M
    final String[] decay = new String[]{"-D", ""};
    final String[] numHiddenLayers = new String[]{"a", "i", "o", "t"};

    for (String numHiddenLayer : numHiddenLayers) { // The hidden layers to be created for the network.
      for (String lRate : learningRates) { // Learning rate for the backpropagation algorithm.
        for (String mRate : momentumRates) { // Momentum rate for the backpropagation algorithm
          // Number of epochs to train through.
          for (int numberOfEpochs = 100; numberOfEpochs <= 1000; numberOfEpochs += 100) { // -N
            // The value used to seed the random number generator
            for (int seed = 0; seed <= 5; seed++) { // -S
              // The number of consecutive increases of error allowed for validation
              for (int validationThreshold = 5; validationThreshold <= 50; validationThreshold += 5) { // -E
                // Percentage size of validation set to use to terminate training
                for (int validationSet = 0; validationSet <= 10; validationSet++) // -V
                {
                  for (String dec : decay) { // Learning rate decay will occur.
                    final var joiner = new StringJoiner(" ")
                      .add("-H")
                      .add(numHiddenLayer)
                      .add(dec)
                      .add("-V")
                      .add(String.valueOf(validationSet))
                      .add("-E")
                      .add(String.valueOf(validationThreshold))
                      .add("-S")
                      .add(String.valueOf(seed))
                      .add("-N")
                      .add(String.valueOf(numberOfEpochs))
                      .add("-M")
                      .add(mRate)
                      .add("-L")
                      .add(lRate)
                      // options for evaluator
                      .add("-t")
                      .add(DataReader.MUSHROOM_FILE);

                    final CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
                      throwingSupplierWrapper(() -> ClassifierFactory.buildAndEvaluateModel(joiner.toString(), "M")),
                      executor).thenAcceptAsync(evaluationOutput -> {
                      final var mpEvaluationMetrics = new MPEvaluationMetrics(evaluationOutput);
                      metricsList.add(mpEvaluationMetrics);
                      System.out.println(mpEvaluationMetrics);
                    });
                    evaluationFutureList.add(completableFuture);
                  }
                }
              }
            }
          }
        }
      }
      CompletableFuture.allOf(evaluationFutureList.toArray(new CompletableFuture[0]))
        .thenAccept(throwingConsumerWrapper(c -> {
          metricsList.sort(MPEvaluationMetrics.getComparator());
          writeToFile(metricsList);
        }));
    }
  }

  @Override
  public String getDefaultWekaOptionsSet() {
    return "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
  }
}
