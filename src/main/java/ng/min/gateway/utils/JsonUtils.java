package ng.min.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import ng.min.gateway.dto.Response;

import java.io.IOException;

/**
 * @author chenkunyun
 * @date 2019-05-02
 */
public final class JsonUtils {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final static ObjectReader OBJECT_READER = OBJECT_MAPPER.readerFor(Response.class);
    private final static ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writerFor(Response.class);

    public static Response read(byte[] bytes) throws IOException {
        return OBJECT_READER.readValue(bytes);
    }

    public static byte[] write(Response gatewayResult) throws JsonProcessingException {
        return OBJECT_WRITER.writeValueAsBytes(gatewayResult);
    }
}