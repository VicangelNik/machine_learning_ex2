package org.vicangel.reader;

import weka.core.Instances;

/**
 * @author Nikiforos Xylogiannopoulos
 */
public interface IReader {

  Instances getInstancesFromDataSource(final String sourcePath) throws Exception;
}
