package org.vicangel.reader;

import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public class DataReader implements IReader {

  public static final String MUSHROOM_FILE = "src/main/resources/mushroom.arff";
  private static final Logger LOGGER = Logger.getLogger(DataReader.class.getName());

  public Instances getInstancesFromDataSource(final String sourcePath) throws Exception {
    final String sourcePath1 = sourcePath != null ? sourcePath : MUSHROOM_FILE;
    final var source = new DataSource(sourcePath1);
    final Instances data = source.getDataSet();
    // setting class attribute if the data format does not provide this information
    // For example, the XRFF format saves the class attribute information as well
    if (data.classIndex() == -1) {
      data.setClassIndex(data.numAttributes() - 1);
      LOGGER.log(Level.INFO, "Class' name : {0}.", data.classAttribute().name());
    }
    return data;
  }
}
