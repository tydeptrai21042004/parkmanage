package park.management.com.vn.service;

import park.management.com.vn.model.request.WalletRequest;
import park.management.com.vn.model.response.WalletResponse;

import java.util.List;

public interface WalletService {
    WalletResponse getWalletById(Long id);

    List<WalletResponse> getAllWallets();

    WalletResponse createWallet(WalletRequest request);

    WalletResponse updateWallet(Long id, WalletRequest request);

    void deleteWalletById(Long id);
}
