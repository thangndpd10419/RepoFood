package com.example.foodbe.annotation_handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


import java.io.IOException;


public class TrimStringDeserializer extends JsonDeserializer<String> {


    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value= jsonParser.getValueAsString(); // phương thức này thực chất
        // đã thực hiện parse sagn java object từ token va lấy giá trị

        if(value== null) return null;

        return value.trim();
    }
}
