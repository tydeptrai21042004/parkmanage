package park.management.com.vn.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.Event;
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.model.request.EventRequest;
import park.management.com.vn.model.response.EventResponse;
import park.management.com.vn.repository.EventRepository;
import park.management.com.vn.repository.ParkBranchRepository;
import park.management.com.vn.service.EventService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ParkBranchRepository parkBranchRepository;

    private EventResponse toResp(Event e) {
        return EventResponse.builder()
            .id(e.getId())
            .name(e.getName())
            .description(e.getDescription())
            .startAt(e.getStartAt())
            .endAt(e.getEndAt())
            .parkBranchId(e.getParkBranch() != null ? e.getParkBranch().getId() : null)
            .imageUrl(e.getImageUrl())
            .status(e.getStatus())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    @Override
    public List<EventResponse> list() {
        return eventRepository.findAll().stream().map(this::toResp).toList();
    }

    @Override
    public EventResponse get(Long id) {
        Event e = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("EVENT_NOT_FOUND"));
        return toResp(e);
    }

    @Override
    @Transactional
    public EventResponse create(EventRequest request) {
        ParkBranch branch = parkBranchRepository.findById(request.getParkBranchId())
            .orElseThrow(() -> new RuntimeException("BRANCH_NOT_FOUND"));

        Event e = Event.builder()
            .name(request.getName())
            .description(request.getDescription())
            .startAt(request.getStartAt())
            .endAt(request.getEndAt())
            .parkBranch(branch)
            .imageUrl(request.getImageUrl())
            .status(request.getStatus())
            .build();

        return toResp(eventRepository.save(e));
    }

    @Override
    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event e = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("EVENT_NOT_FOUND"));

        if (request.getParkBranchId() != null &&
            (e.getParkBranch() == null || !request.getParkBranchId().equals(e.getParkBranch().getId()))) {
            ParkBranch branch = parkBranchRepository.findById(request.getParkBranchId())
                .orElseThrow(() -> new RuntimeException("BRANCH_NOT_FOUND"));
            e.setParkBranch(branch);
        }

        if (request.getName() != null) e.setName(request.getName());
        e.setDescription(request.getDescription());
        if (request.getStartAt() != null) e.setStartAt(request.getStartAt());
        if (request.getEndAt() != null) e.setEndAt(request.getEndAt());
        if (request.getImageUrl() != null) e.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) e.setStatus(request.getStatus());

        return toResp(eventRepository.save(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) throw new RuntimeException("EVENT_NOT_FOUND");
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventResponse> listOfBranch(Long branchId) {
        return eventRepository.findByParkBranch_Id(branchId).stream().map(this::toResp).toList();
    }
}
