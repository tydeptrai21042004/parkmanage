package park.management.com.vn.model.response;

import java.time.LocalDateTime;

public record GameReviewResponse(
    Long id,
    Long gameId,
    Long userId,
    int rating,
    String comment,
    boolean approved,
    LocalDateTime createdAt   // ✅ LocalDateTime, not Instant
) {}
