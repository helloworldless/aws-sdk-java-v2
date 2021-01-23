package software.amazon.awssdk.enhanced.dynamodb.mapper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import org.junit.Test;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DGTest {

    @Test
    public void mapToItem() {
        TableSchema<DG> dgTableSchema = TableSchema.fromClass(DG.class);

        Map<String, AttributeValue> map = new HashMap<>();
        map.put("id", AttributeValue.builder().s("asdf").build());
        DG dg = dgTableSchema.mapToItem(map);
        assertThat(dg.getId()).isEqualTo("asdf");
    }

    @DynamoDbBean
    public static class DG {

        private String id;

//        public DG() {
//
//        }

//        @DynamoDBAttribute
        @DynamoDbPartitionKey
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}