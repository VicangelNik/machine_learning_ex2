package org.vicangel.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.vicangel.ClassifierFactory;
import org.vicangel.metrics.MPEvaluationMetrics;
import org.vicangel.reader.DataReader;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public final class MultilayerPerceptronExperiments extends AlgorithmExperiments {

  @Override
  public void performExperiments() throws Exception {
    final List<MPEvaluationMetrics> metricsList = new ArrayList<>();

    //default -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a

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

                    final String evaluationOutput = ClassifierFactory.buildAndEvaluateModel(joiner.toString(), "M");
                    final MPEvaluationMetrics mp = new MPEvaluationMetrics(evaluationOutput);
                    metricsList.add(mp);
                    System.out.println(mp);
                  }
                }
              }
            }
          }
        }
      }
      metricsList.sort(MPEvaluationMetrics.getComparator());
      writeToFile(metricsList);
    }

//    newVector.addElement(new Option("\tA NominalToBinary filter will NOT automatically be used.\n\t(Set this to not use a NominalToBinary filter).", "B", 0, "-B"));
//    newVector.addElement(new Option("\tNormalizing the attributes will NOT be done.\n\t(Set this to not normalize the attributes).", "I", 0, "-I"));
//    newVector.addElement(new Option("\tReseting the network will NOT be allowed.\n\t(Set this to not allow the network to reset).", "R", 0, "-R"));
  }
}
