package park.management.com.vn.service;

import park.management.com.vn.model.request.TransactionRecordRequest;
import park.management.com.vn.model.response.TransactionRecordResponse;

import java.util.List;

public interface TransactionRecordService {

    TransactionRecordResponse getTransactionById(Long id);

    List<TransactionRecordResponse> getAllTransactions();

    TransactionRecordResponse createTransaction(TransactionRecordRequest request);

    TransactionRecordResponse updateTransaction(Long id, TransactionRecordRequest request);

    void deleteTransactionById(Long id);
}
