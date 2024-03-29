package org.vicangel;

import org.vicangel.experiments.MultilayerPerceptronExperiments;
import org.vicangel.experiments.SGDExperiments;
import org.vicangel.reader.DataReader;
import org.vicangel.reader.IReader;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class Main {

  public static void main(String[] args) throws Exception {

    // IReader reader = new DataReader();
    // final var instances = reader.getInstancesFromDataSource("/home/nikiforos/weka-3-8-6-azul-zulu-linux/weka-3-8-6/data/iris.arff");
    // performNaiveBayesExperiments(instances);
    // new NaiveBayesExperiments().performExperiments(false);

    // new J48Experiments().performExperiments(true);
    // new J48Experiments().performReducedErrorPruningExperiments(true);
    // new MultilayerPerceptronExperiments().performExperiments(true);
    // new IBKExperiments().performExperiments(true);
    // new SGDExperiments().performExperiments(true);
    // new VisualizeRocCurve().visualize(instances);
    IReader reader = new DataReader();
    final var instances = reader.getInstancesFromDataSource("src/main/resources/wap.wc.arff");
    new MultilayerPerceptronExperiments().performExperimentsForWAP(instances);
  }
}