package com.sayhi.messaging.api;

import com.sayhi.exception.BadRequestException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToMTTEnumConverter());
  }
}

class StringToMTTEnumConverter implements Converter<String, MessageTransferType> {
  @Override
  public MessageTransferType convert(String source) {
    try {
      return MessageTransferType.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(STR."Invalid message transfer type supplied, supplied: \{source}");
    }
  }
}
