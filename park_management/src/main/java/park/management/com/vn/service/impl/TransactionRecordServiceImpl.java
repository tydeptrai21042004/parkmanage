package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.TransactionRecord;
import park.management.com.vn.entity.Wallet;
import park.management.com.vn.mapper.TransactionRecordMapper;
import park.management.com.vn.model.request.TransactionRecordRequest;
import park.management.com.vn.model.response.TransactionRecordResponse;
import park.management.com.vn.repository.TransactionRecordRepository;
import park.management.com.vn.repository.WalletRepository;

import java.util.List;
import java.util.stream.Collectors;
import park.management.com.vn.service.TransactionRecordService;

@Service
@RequiredArgsConstructor
public class TransactionRecordServiceImpl implements TransactionRecordService {

    private final TransactionRecordRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionRecordMapper mapper;

    @Override
    public TransactionRecordResponse createTransaction(TransactionRecordRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        TransactionRecord entity = mapper.toEntity(request);
        entity.setWallet(wallet);
        return mapper.toResponse(transactionRepository.save(entity));
    }

    @Override
    public TransactionRecordResponse getTransactionById(Long id) {
        TransactionRecord entity = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapper.toResponse(entity);
    }

    @Override
    public List<TransactionRecordResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionRecordResponse updateTransaction(Long id, TransactionRecordRequest request) {
        TransactionRecord existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        TransactionRecord updated = mapper.toEntity(request);
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setCreatedBy(existing.getCreatedBy());
        updated.setWallet(existing.getWallet());

        return mapper.toResponse(transactionRepository.save(updated));
    }

    @Override
    public void deleteTransactionById(Long id) {
        transactionRepository.deleteById(id);
    }
}
