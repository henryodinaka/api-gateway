package ng.min.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//import org.springframework.mock.http.MockHttpOutputMessage;


public class JsonConverter {
	static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").
			registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
		@Override
		public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return  LocalDateTime.parse(json.getAsString());
		}
	}).registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
		@Override
		public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
			return jsonSerializationContext.serialize(localDateTime.format(DATE_FORMATTER));
		}
	}).create();

	public static <T> T getElement(String value, Class<T> clazz) {
		return gson.fromJson(value, clazz);
	}

	public static <T> T[] getElements(String value, Class<T[]> clazz) {
		return gson.fromJson(value, clazz);
	}

	public static <T> String getJson(T element) {
		return gson.toJson(element);
	}

	public static <T> String getJsonRecursive(T element) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString;
		try {
			jsonInString = mapper.writeValueAsString(element);
			return jsonInString;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
