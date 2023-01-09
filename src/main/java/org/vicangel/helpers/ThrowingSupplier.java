package org.vicangel.helpers;

import java.util.function.Supplier;

/**
 * @author Nikiforos Xylogiannopoulos
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

  T get() throws E;

  static <T> Supplier<T> throwingSupplierWrapper(
    ThrowingSupplier<T, Exception> throwingSupplier) {
    return () -> {
      try {
        return throwingSupplier.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }
}
