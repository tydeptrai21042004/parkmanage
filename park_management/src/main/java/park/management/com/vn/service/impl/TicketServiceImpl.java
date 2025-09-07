package park.management.com.vn.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import park.management.com.vn.constant.DiscountType;
import park.management.com.vn.constant.TicketStatus;
import park.management.com.vn.entity.*;
import park.management.com.vn.exception.promotion.InvalidPromotionBranchException;
import park.management.com.vn.exception.promotion.PromotionExpiredException;
import park.management.com.vn.exception.promotion.PromotionNotActiveException;
import park.management.com.vn.exception.ticket.DailyTicketInventoryExceedException;
import park.management.com.vn.exception.ticket.DailyTicketInventoryNotFoundException;
import park.management.com.vn.exception.ticket.TicketNotFoundException;
import park.management.com.vn.exception.ticket.TicketTypeNotFoundException;
import park.management.com.vn.mapper.TicketMapper;
import park.management.com.vn.model.request.TicketRequest;
import park.management.com.vn.model.response.TicketResponse;
import park.management.com.vn.repository.*;
import park.management.com.vn.service.BranchPromotionService;
import park.management.com.vn.service.ParkBranchService;
import park.management.com.vn.service.TicketService;
import park.management.com.vn.service.UserService;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketDetailRepository ticketDetailRepository;
    private final DailyTicketInventoryRepository dailyTicketInventoryRepository;
    private final BulkPricingRuleRepository bulkPricingRuleRepository;

    private final WalletRepository walletRepository;                   // locking finder must exist
    private final TransactionRecordRepository transactionRecordRepository;

    private final ParkBranchService parkBranchService;
    private final BranchPromotionService branchPromotionService;
    private final UserService userService;

    private final TicketMapper ticketMapper;

    @Override
    public TicketOrder getTicketOrderById(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    @Override
    public Optional<TicketOrder> findTicketOrderById(Long id) { return ticketRepository.findById(id); }

    @Override
    public Optional<TicketType> findTicketTypeById(Long id) { return ticketTypeRepository.findById(id); }

    @Override
    public TicketType getTicketTypeById(Long id) {
        return this.findTicketTypeById(id).orElseThrow(() -> new TicketTypeNotFoundException(id));
    }

    @Override
    public DailyTicketInventory getDailyTicketInventory(Long ticketTypeId, LocalDate ticketDate) {
        return dailyTicketInventoryRepository
            .getDailyTicketInventoriesByTicketType_IdAndDate(ticketTypeId, ticketDate)
            .orElseThrow(() -> new DailyTicketInventoryNotFoundException(ticketTypeId, ticketDate));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Long createTicketOrder(TicketRequest ticketRequest, Long userId) {
        UserEntity customer = userService.getUserById(userId);
        ParkBranch branch = parkBranchService.getById(ticketRequest.getBranchId());
        LocalDate ticketDate = ticketRequest.getTicketDate();

        Map<Pair<Long, LocalDate>, Integer> remainingCapacityMap = new HashMap<>();
        Map<TicketType, Integer> ticketTypeQuantityMap = new HashMap<>();
        Map<TicketType, BigDecimal> ticketTypePriceMap = new HashMap<>();

        // 1) Validate & reserve
        for (TicketRequest.TicketDetailRequest detailRequest : ticketRequest.getDetails()) {
            TicketType ticketType = this.getTicketTypeById(detailRequest.getTicketTypeId());
            Integer quantityRequested = detailRequest.getQuantity();

            DailyTicketInventory inventory = this.getDailyTicketInventory(ticketType.getId(), ticketDate);
            Pair<Long, LocalDate> key = Pair.of(ticketType.getId(), ticketDate);

            remainingCapacityMap.putIfAbsent(key, inventory.getTotalAvailable() - inventory.getSold());
            int available = remainingCapacityMap.get(key);
            if (quantityRequested > available) throw new DailyTicketInventoryExceedException(ticketDate);
            remainingCapacityMap.put(key, available - quantityRequested);

            ticketTypeQuantityMap.merge(ticketType, quantityRequested, Integer::sum);
        }

        // 2) Price (bulk)
        for (Map.Entry<TicketType, Integer> entry : ticketTypeQuantityMap.entrySet()) {
            TicketType ticketType = entry.getKey();
            BigDecimal basePrice = ticketType.getBasePrice();
            int quantity = entry.getValue();
            BulkPricingRule rule = this.findBulkPricingRuleByTicketTypeId(ticketType.getId()).orElse(null);
            int discountPercent = (rule != null) ? rule.getDiscountPercent() : 0;
            BigDecimal priceForType = calculateDiscountedPrice(basePrice, quantity, discountPercent);
            ticketTypePriceMap.put(ticketType, priceForType);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<TicketType, BigDecimal> e : ticketTypePriceMap.entrySet()) {
            total = total.add(e.getValue());
        }

        // 3) Promotion
        Optional<BranchPromotion> promotion = branchPromotionService.findBranchPromotionById(ticketRequest.getPromotionId());
        BigDecimal promotionDiscount = BigDecimal.ZERO;
        if (promotion.isPresent()) {
            BranchPromotion promo = promotion.get();
            if (!Boolean.TRUE.equals(promo.getIsActive())) throw new PromotionNotActiveException(promo.getId());
            if (ticketDate.isBefore(promo.getValidFrom().toLocalDate())
             || ticketDate.isAfter(promo.getValidUntil().toLocalDate())) throw new PromotionExpiredException(promo.getId());
            if (promo.getParkBranch() != null && !promo.getParkBranch().getId().equals(branch.getId())) {
                throw new InvalidPromotionBranchException(promo.getId());
            }
            promotionDiscount = (promo.getDiscountType() == DiscountType.FIXED_AMOUNT)
                ? promo.getDiscountValue()
                : total.multiply(promo.getDiscountValue()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
        }

        BigDecimal net = total.subtract(promotionDiscount);
        if (net.signum() < 0) net = BigDecimal.ZERO;
        net = net.setScale(0, RoundingMode.HALF_EVEN); // VND integer

        // 4) LOCK wallet row (exclusive)
        Wallet wallet = walletRepository.findByUserEntity_IdForUpdate(userId)
            .orElseThrow(() -> new IllegalStateException("Wallet not found for user " + userId));
        if (wallet.getBalance() == null) wallet.setBalance(BigDecimal.ZERO);
        if (wallet.getBalance().compareTo(net) < 0) throw new IllegalStateException("Insufficient wallet balance");

        // 5) Persist order + details
        TicketOrder order = TicketOrder.builder()
            .userEntity(customer)
            .parkBranch(branch)
            .ticketDate(ticketDate)
            .status(TicketStatus.PENDING)
            .totalAmount(total)
            .finalAmount(net)
            .promotion(promotion.orElse(null))
            .build();
        final TicketOrder savedOrder = ticketRepository.save(order);

        List<TicketDetail> details = new ArrayList<>();
        for (Map.Entry<TicketType, Integer> entry : ticketTypeQuantityMap.entrySet()) {
            TicketType ticketType = entry.getKey();
            int quantity = entry.getValue();
            BigDecimal unitPrice = ticketType.getBasePrice();
            BulkPricingRule rule = this.findBulkPricingRuleByTicketTypeId(ticketType.getId()).orElse(null);
            int discountPercent = (rule != null) ? rule.getDiscountPercent() : 0;
            BigDecimal finalPrice = this.calculateDiscountedPrice(unitPrice, quantity, discountPercent);

            TicketDetail ticketDetail = TicketDetail.builder()
                .ticketOrder(savedOrder)
                .ticketType(ticketType)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .discountPercent(discountPercent)
                .finalPrice(finalPrice.setScale(0, RoundingMode.HALF_EVEN))
                .build();
            details.add(ticketDetail);
        }
        ticketDetailRepository.saveAll(details);

        // 6) Debit wallet & log transaction
        wallet.setBalance(wallet.getBalance().subtract(net));
        walletRepository.save(wallet);

        TransactionRecord tr = new TransactionRecord();
        tr.setWallet(wallet);
        // pass double if TransactionRecord.amount is a double/Double
        tr.setAmount(net.negate().doubleValue());
        // remove the description line because your entity doesn't have it
        // tr.setDescription("Ticket purchase order#" + savedOrder.getId());
        transactionRecordRepository.save(tr);

        // 7) Update inventories
        updateDailyInventoryAfterPurchase(ticketTypeQuantityMap, ticketDate);

        // 8) Optionally mark order success if you have a CONFIRMED/PAID status
        // savedOrder.setStatus(TicketStatus.CONFIRMED);
        // ticketRepository.save(savedOrder);

        return savedOrder.getId();
    }

    @Transactional(rollbackOn = Exception.class)
    void updateDailyInventoryAfterPurchase(Map<TicketType, Integer> ticketTypeQuantityMap, LocalDate ticketDate) {
        List<DailyTicketInventory> updatedInventories = new ArrayList<>();
        for (Map.Entry<TicketType, Integer> entry : ticketTypeQuantityMap.entrySet()) {
            TicketType ticketType = entry.getKey();
            int quantity = entry.getValue();

            DailyTicketInventory inventory = this.getDailyTicketInventory(ticketType.getId(), ticketDate);
            int updatedSold = inventory.getSold() + quantity;
            if (updatedSold > inventory.getTotalAvailable()) {
                throw new IllegalStateException("Overselling tickets for " + ticketType.getName());
            }
            inventory.setSold(updatedSold);
            updatedInventories.add(inventory);
        }
        dailyTicketInventoryRepository.saveAll(updatedInventories);
    }

    @Override
    public TicketResponse getTicketResponseById(Long ticketId) {
        TicketOrder order = this.getTicketOrderById(ticketId);
        List<TicketDetail> details = ticketDetailRepository.findByTicketOrder_Id(ticketId);
        return ticketMapper.toResponse(order, details);
    }

    private Optional<BulkPricingRule> findBulkPricingRuleByTicketTypeId(Long id) {
        return bulkPricingRuleRepository.findByTicketType_Id(id);
    }

    private BigDecimal calculateDiscountedPrice(BigDecimal basePrice, int quantity, int discountPercent) {
        return basePrice
            .multiply(BigDecimal.valueOf(quantity))
            .multiply(BigDecimal.valueOf(100 - discountPercent))
            .divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
    }
}
