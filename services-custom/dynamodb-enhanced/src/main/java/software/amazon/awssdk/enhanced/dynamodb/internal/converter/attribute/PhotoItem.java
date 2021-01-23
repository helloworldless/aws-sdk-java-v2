package software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;

//@DynamoDbBean
//public class PhotoItem {
//
//    private String id;
//
//    private Metadata metadata;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        PhotoItem photoItem = (PhotoItem) o;
//        return Objects.equals(id, photoItem.id) && Objects.equals(metadata, photoItem.metadata);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, metadata);
//    }
//
//    @DynamoDbPartitionKey
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    @DynamoDbConvertedBy(value = JsonAttributeConverter.class, isGeneric = true) //, generic = Metadata.class)
//    public Metadata getMetadata() {
//        return metadata;
//    }
//
//    public void setMetadata(Metadata metadata) {
//        this.metadata = metadata;
//    }
//}