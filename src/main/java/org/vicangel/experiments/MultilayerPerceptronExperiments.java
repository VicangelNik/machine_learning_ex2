package org.vicangel.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.vicangel.ClassifierFactory;
import org.vicangel.metrics.MPEvaluationMetrics;
import org.vicangel.reader.DataReader;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Utils;

import static org.vicangel.helpers.ThrowingConsumer.throwingConsumerWrapper;
import static org.vicangel.helpers.ThrowingSupplier.throwingSupplierWrapper;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class MultilayerPerceptronExperiments extends AlgorithmExperiments {

  private static final Logger LOGGER = Logger.getLogger(MultilayerPerceptronExperiments.class.getName());
  private static final String USE_CASE = "MultilayerPerceptron";

  public MultilayerPerceptronExperiments() {
    super(USE_CASE);
  }

  @Override
  public void performExperiments(final boolean useConcurrency) {
    LOGGER.info("Starting MultilayerPerceptronExperiments tests");
    final List<MPEvaluationMetrics> metricsList = new ArrayList<>();
    final List<CompletableFuture<Void>> evaluationFutureList = Collections.synchronizedList(new ArrayList<>());
    final Map<Integer, String> optionsList = new LinkedHashMap<>();

    final String[] learningRates = new String[]{"0.1", "0.3", "0.5"}; // -L
    final String[] momentumRates = new String[]{"0.1", "0.2", "0.3"}; // -M
    // final String[] decay = new String[]{"-D", ""};
    final String[] numHiddenLayers = new String[]{"0", "a", "i", "o", "t"};

    for (String numHiddenLayer : numHiddenLayers) { // The hidden layers to be created for the network.
      for (String lRate : learningRates) { // Learning rate for the backpropagation algorithm.
        for (String mRate : momentumRates) { // Momentum rate for the backpropagation algorithm
          for (int numberOfEpochs = 300; numberOfEpochs <= 700; numberOfEpochs += 200) { // -N Number of epochs to train through.
            // for (int seed = 0; seed <= 5; seed++) { // -S The value used to seed the random number generator
            // The number of consecutive increases of error allowed for validation
            for (int validationThreshold = 10; validationThreshold <= 30; validationThreshold += 10) { // -E
              // Percentage size of validation set to use to terminate training
              for (int validationSet = 0; validationSet <= 6; validationSet += 3) { // -V
                // for (String dec : decay) { // Learning rate decay will occur.
                final var joiner = new StringJoiner(" ")
                  .add("-H")
                  .add(numHiddenLayer)
                  // .add(dec)
                  .add("-V")
                  .add(String.valueOf(validationSet))
                  .add("-E")
                  .add(String.valueOf(validationThreshold))
                  // .add("-S")
                  // .add(String.valueOf(seed))
                  .add("-N")
                  .add(String.valueOf(numberOfEpochs))
                  .add("-M")
                  .add(mRate)
                  .add("-L")
                  .add(lRate)
                  // options for evaluator
                  .add("-t")
                  .add(DataReader.MUSHROOM_FILE);

                final String options = joiner.toString();

                optionsList.put(optionsCounter++, options);
                //  }
              }
            }
          }
          //  }
        }
      }
    }

    printNumberOfExperiments(optionsList.size());

    optionsList.forEach((count, options) -> evaluate(options, metricsList, useConcurrency, evaluationFutureList, count));

    writeInFile(metricsList, useConcurrency, evaluationFutureList);
  }

  private void evaluate(String options,
                        List<MPEvaluationMetrics> metricsList,
                        boolean useConcurrency,
                        List<CompletableFuture<Void>> evaluationFutureList,
                        Integer count) {
    if (useConcurrency) {
      final CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
        throwingSupplierWrapper(() -> ClassifierFactory.buildAndEvaluateModel(options, "M")),
        executor).thenAccept(throwingConsumerWrapper(evaluationOutput -> {
        final var mpEvaluationMetrics = new MPEvaluationMetrics(evaluationOutput);
        metricsList.add(mpEvaluationMetrics);
        System.out.println(count + ": " + mpEvaluationMetrics);
      }));
      evaluationFutureList.add(completableFuture);
    } else {
      try {
        final String evaluationOutput = ClassifierFactory.buildAndEvaluateModel(options, "M");
        final var mpEvaluationMetrics = new MPEvaluationMetrics(evaluationOutput);
        metricsList.add(mpEvaluationMetrics);
        System.out.println(count + ": " + mpEvaluationMetrics);
      } catch (Exception e) {
        // throw new RuntimeException(e);
        System.out.println(count + ": " + e.getMessage() + "\tOptions when error Occurred: " + options);
      }
    }
  }

  private void writeInFile(List<MPEvaluationMetrics> metricsList,
                           boolean useConcurrency,
                           List<CompletableFuture<Void>> evaluationFutureList) {
    if (useConcurrency) {
      CompletableFuture.allOf(evaluationFutureList.toArray(new CompletableFuture[0]))
        .thenAccept(throwingConsumerWrapper(c -> {
          metricsList.sort(MPEvaluationMetrics.getComparator());
          writeToFile(metricsList);
        })).whenComplete((c, throwable) -> System.exit(666)); // for a reason the program does not terminate when in concurrency. I should investigate it more TODO
    } else {
      metricsList.sort(MPEvaluationMetrics.getComparator());
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
    return "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
  }

  /**
   * Perform Experiments for wap.wc.arff.
   *
   * @param instances
   *
   * @throws Exception
   */
  public void performExperimentsForWAP(final Instances instances) throws Exception {
    String[] options = {"-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H 0",
      getDefaultWekaOptionsSet(),
      "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H o",
      "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H 4230, 2115",
      "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H 4230, 2115, 1000"};

    for (String option : options) {
      final AbstractClassifier classifier = ClassifierFactory.getClassifier("M");
      final var eval = new Evaluation(instances);
      classifier.setOptions(Utils.splitOptions(option));
      eval.crossValidateModel(classifier, instances, 10, new Random(1));
      final String evaluationOutput = eval.toSummaryString("\nResults\n======\n", false);
      writeToFile(evaluationOutput, true);
      System.out.println(evaluationOutput);
    }
  }
}
