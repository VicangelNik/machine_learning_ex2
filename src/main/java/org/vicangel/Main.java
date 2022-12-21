package org.vicangel;

import org.vicangel.experiments.MultilayerPerceptronExperiments;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class Main {

  public static void main(String[] args) throws Exception {

//    IReader reader = new DataReader();
//    final var instances = reader.getInstancesFromDataSource(null);

    // performNaiveBayesExperiments(instances);
    // new J48Experiments().performExperiments();
    // new J48Experiments().performReducedErrorPruningExperiments();
    new MultilayerPerceptronExperiments().performExperiments();
  }
}