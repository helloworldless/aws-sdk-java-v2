package software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.utils.Validate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class JsonAttributeConverter<T> implements AttributeConverter<T> {

//    private final Type type;
    private final EnhancedType<T> enhancedType;
    private final ObjectMapper objectMapper;

    public JsonAttributeConverter(EnhancedType<T> enhancedType, ObjectMapper objectMapper) {
        this.enhancedType = enhancedType;
//        this.type = type;
        this.objectMapper = objectMapper;
    }

    public static <T> JsonAttributeConverter<T> create(EnhancedType<T> enhancedType, ObjectMapper objectMapper) {




//        Type type = typeReference.rawClass();// typeReference.getType();
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

    TypeReference<T> convert(EnhancedType<T> enhancedType) {
        return new TypeReference<T>() {};
    }

    @Override
    public T transformTo(AttributeValue input) {
        try {
//            TypeReference<T> typeReference = convert(this.enhancedType);


            Class<T> tClass = this.enhancedType.rawClass();
            if (tClass.isAssignableFrom(List.class)) {
//                List<EnhancedType<?>> enhancedTypes = this.type().rawClassParameters();


                List<EnhancedType<?>> parameterizedTypes = this.enhancedType.rawClassParameters();
                if (parameterizedTypes.isEmpty()) {
                    throw new RuntimeException("Expected parameterized type for list type. See EnhancedType#listOf and EnhancedType#loadTypeParameters");
                }


                EnhancedType<?> enhancedType = parameterizedTypes.get(0);
                Class<?> parameterizedClass = enhancedType.rawClass();

                JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, parameterizedClass);
                return objectMapper.readValue(input.s(), javaType);

//                ParameterizedType parameterizedType = (ParameterizedType) this.type();
//                Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
//                Class<T> maybe = (Class<T>) parameterizedType.getActualTypeArguments()[0];
//                actualTypeArgument.toString();



//                List<EnhancedType<?>> enhancedTypes = Collections.unmodifiableList(
//                        Arrays.stream(parameterizedType.getActualTypeArguments())
//                                .peek(t -> Validate.validState(t != null, "Invalid type argument."))
//                                .map(EnhancedType::of)
//                                .collect(toList()));
//                EnhancedType<?> enhancedType = enhancedTypes.get(0);
//                Class<?> aClass = enhancedType.rawClass();

            } else if(tClass.isAssignableFrom(Set.class)) {

            } else if(tClass.isAssignableFrom(Map.class)) {

            } else if (!this.enhancedType.rawClassParameters().isEmpty()) {
                throw new IllegalStateException("Unhandled parameterized type found; Only List, Set and Map are supported");
            }


            // Not a parameterized type
            return objectMapper.readValue(input.s(), this.type().rawClass());

            //throw new RuntimeException("shouldn't arrive here");
//            return objectMapper.readValue(input.s(), typeReference);
//            return objectMapper.readValue(input.s(), this.type().rawClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public EnhancedType<T> type() {
        return this.enhancedType;
//        Class<?> rawType = type instanceof Class<?>
//            ? (Class<?>) type
//            : (Class<?>) ((ParameterizedType) type).getRawType();
//        Class<T> classType = (Class<T>) rawType;
//        return EnhancedType.of(classType);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

}
