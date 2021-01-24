package software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

// Not sure if these are needed and what for
@SdkInternalApi
@ThreadSafe
@Immutable
public class JsonAttributeConverter<T> implements AttributeConverter<T> {

    private final EnhancedType<T> enhancedType;
    private final ObjectMapper objectMapper;

    public JsonAttributeConverter(EnhancedType<T> enhancedType, ObjectMapper objectMapper) {
        this.enhancedType = enhancedType;
        this.objectMapper = objectMapper;
    }

    public static <T> JsonAttributeConverter<T> create(EnhancedType<T> enhancedType, ObjectMapper objectMapper) {
        return new JsonAttributeConverter<>(enhancedType, objectMapper);
    }

    @Override
    public AttributeValue transformFrom(T input) {
        try {
            return AttributeValue.builder().s(objectMapper.writeValueAsString(input)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T transformTo(AttributeValue input) {
        try {
            Class<T> tClass = this.enhancedType.rawClass();
            if (tClass.isAssignableFrom(List.class)) {

                List<EnhancedType<?>> parameterizedTypes = this.enhancedType.rawClassParameters();
                if (parameterizedTypes.isEmpty()) {
                    throw new RuntimeException("Expected parameterized type for list type. See EnhancedType#listOf and EnhancedType#loadTypeParameters");
                }

                EnhancedType<?> enhancedType = parameterizedTypes.get(0);
                Class<?> parameterizedClass = enhancedType.rawClass();

                JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, parameterizedClass);
                return objectMapper.readValue(input.s(), javaType);


            } else if(tClass.isAssignableFrom(Set.class)) {
                throw new IllegalStateException("Not implemented yet");
            } else if(tClass.isAssignableFrom(Map.class)) {
                throw new IllegalStateException("Not implemented yet");
            } else if (!this.enhancedType.rawClassParameters().isEmpty()) {
                throw new IllegalStateException("Unhandled parameterized type found; Only List, Set and Map are supported");
            }

            // Not a parameterized type
            return objectMapper.readValue(input.s(), this.type().rawClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EnhancedType<T> type() {
        return this.enhancedType;
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

}
