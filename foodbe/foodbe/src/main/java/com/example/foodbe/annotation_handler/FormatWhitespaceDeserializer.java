package com.example.foodbe.annotation_handler;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class FormatWhitespaceDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {

        String value = jsonParser.getValueAsString();

        if(value== null) return null;
        value = value.trim();

        return value.replaceAll("\\s+"," ");
        //Cả replace và replaceAll đều thay đổi tất cả các chỗ trùng khớp trong chuỗi.
        // replace: thay đổi target chính xác bằng replacement
        //repalecAll: thay thế target bằng replacement theo mẫu regex
    }
}
