package software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.functionaltests.LocalDynamoDbSyncTestBase;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

public class JsonAttributeConverterTest extends LocalDynamoDbSyncTestBase {

    String tableName = "JsonConverterTestTable";

    JsonAttributeConverter<List<Metadata>> converter = JsonAttributeConverter.create(EnhancedType.listOf(Metadata.class), new ObjectMapper());
    TableSchema<PhotoItem> schema = TableSchema.builder(PhotoItem.class)
            .newItemSupplier(PhotoItem::new)
            .addAttribute(String.class, a -> {
                a.name("id");
                a.getter(PhotoItem::getId);
                a.setter(PhotoItem::setId);
                a.tags(primaryPartitionKey());
            })
            .addAttribute(EnhancedType.listOf(Metadata.class), a -> {
                a.name("metadata");
                a.getter(PhotoItem::getMetadata);
                a.setter(PhotoItem::setMetadata);
                a.attributeConverter(converter);
            })
            .build();

    DynamoDbTable<PhotoItem> photoTable = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getDynamoDbClient())
            .build().table(getConcreteTableName(tableName), schema);

    @Before
    public void createTable() {
        photoTable.createTable();
    }

    @After
    public void deleteTable() {
        getDynamoDbClient().deleteTable(DeleteTableRequest.builder()
                .tableName(getConcreteTableName(tableName))
                .build());
    }

    @Test
    public void roundTripIdentityTest() {
        PhotoItem photo = new PhotoItem();
        photo.setId("123");
        Metadata metadata = new Metadata();
        metadata.setDevice("iPhone X");
        metadata.setLatitude(45d);
        metadata.setLongitude(100d);
        photo.setMetadata(Arrays.asList(metadata));
        photoTable.putItem(photo);

        PhotoItem retrievedPhoto = photoTable.getItem(Key.builder().partitionValue("123").build());
        assertThat(retrievedPhoto).isEqualTo(photo);
    }

    public static class PhotoItem {

        private String id;
        private List<Metadata> metadata;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PhotoItem photoItem = (PhotoItem) o;
            return Objects.equals(id, photoItem.id) && Objects.equals(metadata, photoItem.metadata);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, metadata);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Metadata> getMetadata() {
            return metadata;
        }

        public void setMetadata(List<Metadata> metadata) {
            this.metadata = metadata;
        }
    }


    public static class Metadata {
        private String device;
        private Double latitude;
        private Double longitude;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Metadata metadata = (Metadata) o;
            return Objects.equals(device, metadata.device) && Objects.equals(latitude, metadata.latitude) && Objects.equals(longitude, metadata.longitude);
        }

        @Override
        public int hashCode() {
            return Objects.hash(device, latitude, longitude);
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
}