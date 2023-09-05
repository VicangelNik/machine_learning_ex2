package org.vicangel.helpers;

import java.util.function.Consumer;

/**
 * @author Nikiforos Xylogiannopoulos
 * @see <a href="https://www.baeldung.com/java-lambda-exceptions">java-lambda-exceptions</a>
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {

  void accept(T t) throws E;

  static <T> Consumer<T> throwingConsumerWrapper(
    ThrowingConsumer<T, Exception> throwingConsumer) {

    return i -> {
      try {
        throwingConsumer.accept(i);
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
       // throw new RuntimeException(ex);
      }
    };
  }
}
