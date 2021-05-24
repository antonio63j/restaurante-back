package com.afl.restaurante.formateadores;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.afl.restaurante.formateadores.LocalDateTimeSerializer;
public class LocalDatetimeDeserializer extends JsonDeserializer<LocalDateTime> {

  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx)
          throws IOException {
      String str = p.getText();
      try {
          return LocalDateTime.parse(str, LocalDateTimeSerializer.DATE_FORMATTER);
      } catch (DateTimeParseException e) {
          System.err.println(e);
          return null;
      }
  }
}
