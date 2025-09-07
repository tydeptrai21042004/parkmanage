package park.management.com.vn.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.UserEntity;
import park.management.com.vn.entity.Wallet;
import park.management.com.vn.model.request.WalletRequest;
import park.management.com.vn.model.response.WalletResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:09+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class WalletMapperImpl implements WalletMapper {

    @Override
    public Wallet toEntity(WalletRequest request) {
        if ( request == null ) {
            return null;
        }

        Wallet wallet = new Wallet();

        return wallet;
    }

    @Override
    public WalletResponse toResponse(Wallet entity) {
        if ( entity == null ) {
            return null;
        }

        WalletResponse walletResponse = new WalletResponse();

        walletResponse.setUserId( entityUserEntityId( entity ) );
        walletResponse.setId( entity.getId() );
        if ( entity.getBalance() != null ) {
            walletResponse.setBalance( entity.getBalance().doubleValue() );
        }
        walletResponse.setCreatedAt( entity.getCreatedAt() );
        walletResponse.setUpdatedAt( entity.getUpdatedAt() );

        return walletResponse;
    }

    private Long entityUserEntityId(Wallet wallet) {
        UserEntity userEntity = wallet.getUserEntity();
        if ( userEntity == null ) {
            return null;
        }
        return userEntity.getId();
    }
}
