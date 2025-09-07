package park.management.com.vn.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import park.management.com.vn.entity.Wallet;
import park.management.com.vn.model.request.WalletRequest;
import park.management.com.vn.model.response.WalletResponse;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface WalletMapper {

  // Do not try to set id; JPA manages it. We also ignore userEntity & balance here.
  @Mapping(target = "userEntity", ignore = true)
  @Mapping(target = "balance", ignore = true)
  Wallet toEntity(WalletRequest request);

  // Expose userId in response
  @Mapping(target = "userId", source = "userEntity.id")
  WalletResponse toResponse(Wallet entity);
}
