package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.TransactionRecord;
import park.management.com.vn.model.request.TransactionRecordRequest;
import park.management.com.vn.model.response.TransactionRecordResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:10+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class TransactionRecordMapperImpl implements TransactionRecordMapper {

    @Override
    public TransactionRecord toEntity(TransactionRecordRequest request) {
        if ( request == null ) {
            return null;
        }

        TransactionRecord transactionRecord = new TransactionRecord();

        transactionRecord.setAmount( request.getAmount() );
        transactionRecord.setType( request.getType() );

        return transactionRecord;
    }

    @Override
    public TransactionRecordResponse toResponse(TransactionRecord entity) {
        if ( entity == null ) {
            return null;
        }

        TransactionRecordResponse.TransactionRecordResponseBuilder transactionRecordResponse = TransactionRecordResponse.builder();

        transactionRecordResponse.id( entity.getId() );
        transactionRecordResponse.amount( entity.getAmount() );
        transactionRecordResponse.type( entity.getType() );
        transactionRecordResponse.createdAt( entity.getCreatedAt() );
        transactionRecordResponse.updatedAt( entity.getUpdatedAt() );

        return transactionRecordResponse.build();
    }
}
