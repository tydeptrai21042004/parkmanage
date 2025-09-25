package park.management.com.vn.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import park.management.com.vn.entity.TicketType;
import park.management.com.vn.model.request.TicketTypeRequest;
import park.management.com.vn.model.response.TicketTypeResponse;
import park.management.com.vn.repository.ParkBranchRepository;
import park.management.com.vn.repository.TicketTypeRepository;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ticket-types")
@RequiredArgsConstructor
@Tag(name = "ticket-type-controller")
public class TicketTypeController {

  private final TicketTypeRepository ticketTypeRepository;
  private final ParkBranchRepository parkBranchRepository;

  // --- Helpers ---
  private TicketTypeResponse toResp(TicketType t) {
    return TicketTypeResponse.builder()
      .id(t.getId())
      .name(t.getName())
      .description(t.getDescription())
      .basePrice(t.getBasePrice())
      .createdAt(t.getCreatedAt())
      .updatedAt(t.getUpdatedAt())
      .build();
  }

  private void apply(TicketType t, TicketTypeRequest req) {
    t.setName(req.getName());
    t.setDescription(req.getDescription());
    t.setBasePrice(req.getBasePrice());
    if (req.getParkBranchId() != null) {
      var b = parkBranchRepository.findById(req.getParkBranchId())
          .orElseThrow(() -> new RuntimeException("BRANCH_NOT_FOUND"));
      t.setParkBranch(b);
    }
    if (req.getStatus() != null) t.setStatus(req.getStatus());
  }

  // --- Endpoints ---

  // Public catalog (chỉ trả về loại đang bật)
  @GetMapping
  public ResponseEntity<List<TicketTypeResponse>> list() {
    List<TicketTypeResponse> out = ticketTypeRepository.findAllByStatusTrue()
      .stream().map(this::toResp).toList();
    return ResponseEntity.ok(out);
  }

  // Lấy tất cả ticket type của 1 chi nhánh
  @GetMapping("/of-branch/{branchId}")
  public ResponseEntity<List<TicketTypeResponse>> getAllOfBranch(@PathVariable Long branchId) {
    List<TicketTypeResponse> out = ticketTypeRepository.findByParkBranch_IdAndStatusTrue(branchId)
      .stream().map(this::toResp).toList();
    return ResponseEntity.ok(out);
  }

  // Public read single
  @GetMapping("/{id}")
  public ResponseEntity<TicketTypeResponse> get(@PathVariable Long id) {
    TicketType t = ticketTypeRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("TICKET_TYPE_NOT_FOUND"));
    return ResponseEntity.ok(toResp(t));
  }

  // Create (MANAGER/ADMIN)
  @PostMapping
  public ResponseEntity<TicketTypeResponse> create(@RequestBody @Valid TicketTypeRequest req) {
    TicketType t = new TicketType();
    apply(t, req);
    if (t.getParkBranch() == null) {
      var b = parkBranchRepository.findById(req.getParkBranchId())
          .orElseThrow(() -> new RuntimeException("BRANCH_NOT_FOUND"));
      t.setParkBranch(b);
    }
    if (t.getStatus() == null) t.setStatus(true);
    TicketType saved = ticketTypeRepository.save(t);
    return ResponseEntity
      .created(URI.create("/api/ticket-types/" + saved.getId()))
      .body(toResp(saved));
  }

  // Update (MANAGER/ADMIN)
  @PutMapping("/{id}")
  public ResponseEntity<TicketTypeResponse> update(@PathVariable Long id,
                                                   @RequestBody @Valid TicketTypeRequest req) {
    TicketType t = ticketTypeRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("TICKET_TYPE_NOT_FOUND"));
    apply(t, req);
    TicketType saved = ticketTypeRepository.save(t);
    return ResponseEntity.ok(toResp(saved));
  }

  // Soft delete: set status=false
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    TicketType t = ticketTypeRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("TICKET_TYPE_NOT_FOUND"));
    t.setStatus(false);
    ticketTypeRepository.save(t);
    return ResponseEntity.noContent().build();
  }
}
