package park.management.com.vn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import park.management.com.vn.model.request.GameRequest;
import park.management.com.vn.model.response.GameResponse;
import park.management.com.vn.service.GameService;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

  private final GameService service;

  @GetMapping
  public ResponseEntity<Page<GameResponse>> list(Pageable pageable) {
    return ResponseEntity.ok(service.list(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GameResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(service.get(id));
  }

  @PostMapping
  @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
  public ResponseEntity<GameResponse> create(@RequestBody @Valid GameRequest req) {
    return ResponseEntity.ok(service.create(req));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
  public ResponseEntity<GameResponse> update(@PathVariable Long id, @RequestBody @Valid GameRequest req) {
    return ResponseEntity.ok(service.update(id, req));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
