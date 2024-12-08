package app.narvi.protego.spring.rules.logback;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.CompositeConverter;

public class NotEmptyConverter<E> extends CompositeConverter<E> {

  @Override
  protected String transform(E event, String in) {
    String emptyInput = in.replaceAll("\s", "");
    if (emptyInput.isEmpty()) {
      return CoreConstants.EMPTY_STRING;
    } else {
      return in;
    }
  }

}
