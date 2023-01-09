package org.vicangel.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.vicangel.ClassifierFactory;
import org.vicangel.metrics.J48EvaluationMetrics;
import org.vicangel.reader.DataReader;

import static org.vicangel.helpers.ThrowingConsumer.throwingConsumerWrapper;
import static org.vicangel.helpers.ThrowingSupplier.throwingSupplierWrapper;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class J48Experiments extends AlgorithmExperiments {

  private static final Logger LOGGER = Logger.getLogger(J48Experiments.class.getName());

  // Whether parts are removed that do not reduce training error.
  private final String[] collapseTree = new String[]{"-O", ""}; // -O indicates false default is true
  // Whether to use binary splits on nominal attributes when building the trees
  private final String[] binarySplits = new String[]{"-B", ""}; // -B indicates true default is false
  // Whether MDL correction is used when finding splits on numeric attributes.
  private final String[] useMDLCorrection = new String[]{"-J", ""}; // -J indicates false default is true
  // Whether counts at leaves are smoothed based on Laplace.
  private final String[] useLaplace = new String[]{"-A", ""}; // -A indicates true default is false
  // Do not perform subtree raising.
  private final String[] subtreeRaising = new String[]{"-S", ""};

  /**
   * Perform for C45 algorithm experiments
   */
  public void performExperiments() {

    final List<J48EvaluationMetrics> metricsList = new ArrayList<>();
    final List<CompletableFuture<Void>> evaluationFutureList = Collections.synchronizedList(new ArrayList<>());

    final String[] confidenceFactors = new String[]{"0.05", "0.1", "0.15", "0.2", "0.25", "0.3", "0.35", "0.4", "0.45", "0.5"};
    // Whether pruning is performed.
    final String[] unPruned = new String[]{"-U", ""};

    // confidenceFactor must be greater than 0 and less than 1
    for (String confidenceFactor : confidenceFactors) { // -C pruning confidence
      for (int minNumObj = 1; minNumObj <= 5; minNumObj++) { // -M minimum number of instances
        for (String ct : collapseTree) {
          for (String bs : binarySplits) {
            for (String mdl : useMDLCorrection) {
              for (String laplace : useLaplace) {
                for (String treeRaising : subtreeRaising) {
                  for (String pr : unPruned) {
                    final var joiner = new StringJoiner(" ")
                      .add(ct)
                      .add(bs)
                      .add(mdl)
                      .add(laplace)
                      .add(treeRaising)
                      .add((isUnPrunedValueAppropriate(pr, treeRaising, confidenceFactor)))
                      .add("-M")
                      .add(String.valueOf(minNumObj))
                      .add("-C")
                      .add(confidenceFactor)
                      // options for evaluator
                      .add("-t")
                      .add(DataReader.MUSHROOM_FILE);

                    final CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
                      throwingSupplierWrapper(() -> ClassifierFactory.buildAndEvaluateModel(joiner.toString(), "T")),
                      executor).thenAcceptAsync(evaluationOutput -> {
                      final var j48EvaluationMetrics = new J48EvaluationMetrics(evaluationOutput);
                      metricsList.add(j48EvaluationMetrics);
                      LOGGER.info(j48EvaluationMetrics::toString);
                    });
                    evaluationFutureList.add(completableFuture);
                  }
                }
              }
            }
          }
        }
      }
    }
    CompletableFuture.allOf(evaluationFutureList.toArray(new CompletableFuture[0]))
      .thenAccept(throwingConsumerWrapper(c -> {
        metricsList.sort(J48EvaluationMetrics.getComparator());
        writeToFile(metricsList);
      }));
  }

  private static String isUnPrunedValueAppropriate(String unPruned, String treeRaising, String confidenceFactor) {
    return treeRaising.isEmpty() && "0.25".equals(confidenceFactor) ? unPruned : "";
  }

  public void performReducedErrorPruningExperiments() throws Exception {

    final List<J48EvaluationMetrics> metricsList = new ArrayList<>();

    //default -R -N 3 -Q 1 -M 2

    for (int seed = 1; seed <= 5; seed++) { // seed -- The seed used for randomizing the data when reduced-error pruning is used.
      for (int minNumObj = 1; minNumObj <= 5; minNumObj++) { // -M minimum number of instances per leaf
        // Determines the amount of data used for reduced-error pruning. One fold is used for pruning, the rest for growing the tree.
        for (int numFolds = 2; numFolds <= 6; numFolds++) {
          for (String ct : collapseTree) {
            for (String bs : binarySplits) {
              for (String mdl : useMDLCorrection) {
                for (String laplace : useLaplace) {
                  for (String treeRaising : subtreeRaising) {
                    final var joiner = new StringJoiner(" ", "-R ", "") // reduced error pruning
                      .add(ct)
                      .add(bs)
                      .add(mdl)
                      .add(laplace)
                      .add(treeRaising)
                      .add("-N")
                      .add(String.valueOf(numFolds))
                      .add("-M")
                      .add(String.valueOf(minNumObj))
                      .add("-Q")
                      .add(String.valueOf(seed))
                      // options for evaluator
                      .add("-t")
                      .add(DataReader.MUSHROOM_FILE);

                    final String evaluationOutput = ClassifierFactory.buildAndEvaluateModel(joiner.toString(), "T");

                    metricsList.add(new J48EvaluationMetrics(evaluationOutput));
                  }
                }
              }
            }
          }
        }
      }
    }
    metricsList.sort(J48EvaluationMetrics.getComparator());
    writeToFile(metricsList);
  }

  @Override
  public String getDefaultWekaOptionsSet() {
    return "-M 2 -C 0.25";
  }
}
