package park.management.com.vn.mapper;

import org.mapstruct.Mapper;
import park.management.com.vn.entity.TransactionRecord;
import park.management.com.vn.model.request.TransactionRecordRequest;
import park.management.com.vn.model.response.TransactionRecordResponse;

@Mapper(componentModel = "spring")
public interface TransactionRecordMapper {

    TransactionRecord toEntity(TransactionRecordRequest request);

    TransactionRecordResponse toResponse(TransactionRecord entity);
}