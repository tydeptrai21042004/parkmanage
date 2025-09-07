package park.management.com.vn.model.response;

public record GameResponse(
    Long id,
    Long branchId,
    String name,
    String description,
    String location
) {}
