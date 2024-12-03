package app.narvi.protego.spring.rules.logback;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.event.KeyValuePair;

public class KeyValueJsonConverter extends ClassicConverter {

  @Override
  public String convert(ILoggingEvent event) {
    List<KeyValuePair> kvpList = event.getKeyValuePairs();

    if (kvpList == null || kvpList.isEmpty()) {
      return CoreConstants.EMPTY_STRING;
    }

    Collections.sort(kvpList, Comparator.comparing(k -> k.key));

    StringBuilder sb = new StringBuilder();
    for (KeyValuePair keyValuePair : kvpList) {
      sb.append("\n\t- ")
          .append(keyValuePair.key)
          .append(" = ")
          .append(toJson(keyValuePair.value).replaceAll("\n",
              "\n\t" + " ".repeat(5 + keyValuePair.key.length())));
    }
    return sb.toString();
  }

  public String toJson(Object o) {
    try {
      return getJsonMapper().writeValueAsString(o);
    } catch (JsonProcessingException e) {
      return "ERROR SERIALIZING TO JSON";
    }
  }

  public JsonMapper getJsonMapper() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    Jdk8Module jdk8Module = new Jdk8Module();
    SimpleModule enumsModule = new SimpleModule();

    JsonMapper objectMapper = JsonMapper
        .builder()
        .addModule(javaTimeModule)
        .addModule(jdk8Module)
        .addModule(enumsModule)
        .build();

    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    //JSON ISO 8601 Format
    objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.of(ZoneOffset.UTC.getId())));

    // null and "absent" values are to be excluded example: empty optionals are serialized as nulls
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

    //write Instant as ISO date, not timestamp
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

    return objectMapper;
  }

}
