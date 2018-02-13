package pso.decision_engine.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import pso.decision_engine.model.enums.Comparator;

public class JsonSerializerUtils {
	
	static private DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	static public class JsonLocalDateTimeSerializer extends JsonSerializer<LocalDateTime>{
		
		@Override
		public void serialize(LocalDateTime ldt, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
			if (ldt==null) {
				gen.writeString("");
				return;
			}
			gen.writeString(formatter.format(ldt));
		}
		
	}
	
	static public class JsonLocalDateTimeDeSerializer extends JsonDeserializer<LocalDateTime> {

		@Override
		public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String value=p.getValueAsString();
			if (value==null || value.isEmpty()) return null;
			return LocalDateTime.parse(value, formatter);
		}
		
	}
	
	static public class JsonComparatorSerializer extends JsonSerializer<Comparator>{
		
		@Override
		public void serialize(Comparator comparator, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
			if (comparator==null) {
				gen.writeString("");
				return;
			}
			gen.writeString(ComparatorHelper.comparatorToShortString(comparator));
		}
		
	}
	
	static public class JsonComparatorDeSerializer extends JsonDeserializer<Comparator> {
		
		@Override
		public Comparator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String value=p.getValueAsString();
			if (value==null || value.isEmpty()) return null;
			return ComparatorHelper.shortStringToComparator(value);
		}
	}
	

}
