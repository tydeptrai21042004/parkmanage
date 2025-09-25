package park.management.com.vn.service;

import park.management.com.vn.model.request.EventRequest;
import park.management.com.vn.model.response.EventResponse;

import java.util.List;

public interface EventService {
  List<EventResponse> list();
  EventResponse get(Long id);
  EventResponse create(EventRequest request);
  EventResponse update(Long id, EventRequest request);
  void delete(Long id);

  // <<< NEW >>>
  List<EventResponse> listOfBranch(Long branchId);
}
