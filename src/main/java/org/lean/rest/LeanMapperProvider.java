package org.lean.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class LeanMapperProvider implements ContextResolver<ObjectMapper> {

  final ObjectMapper defaultObjectMapper;

  public LeanMapperProvider() {
    defaultObjectMapper = createDefaultMapper();
  }

  @Override
  public ObjectMapper getContext( final Class<?> type ) {
    return defaultObjectMapper;
  }

  private static ObjectMapper createDefaultMapper() {
    final ObjectMapper result = new ObjectMapper();
    result.enable( SerializationFeature.INDENT_OUTPUT );

    return result;
  }
}
