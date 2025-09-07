package park.management.com.vn.mapper;

import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import park.management.com.vn.entity.TicketDetail;
import park.management.com.vn.entity.TicketOrder;
import park.management.com.vn.entity.TicketType;
import park.management.com.vn.model.response.TicketDetailResponse;
import park.management.com.vn.model.response.TicketResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-07T15:21:08+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.16 (Amazon.com Inc.)"
)
@Component
public class TicketMapperImpl implements TicketMapper {

    @Override
    public TicketResponse toResponse(TicketOrder ticketOrder, List<TicketDetail> ticketDetails) {
        if ( ticketOrder == null && ticketDetails == null ) {
            return null;
        }

        TicketResponse ticketResponse = new TicketResponse();

        if ( ticketOrder != null ) {
            ticketResponse.setTicketId( ticketOrder.getId() );
            if ( ticketOrder.getStatus() != null ) {
                ticketResponse.setStatus( ticketOrder.getStatus().name() );
            }
        }
        ticketResponse.setDetails( toDetailResponseList(ticketDetails, ticketOrder.getTicketDate()) );

        return ticketResponse;
    }

    @Override
    public TicketDetailResponse toDetailResponse(TicketDetail detail) {
        if ( detail == null ) {
            return null;
        }

        TicketDetailResponse ticketDetailResponse = new TicketDetailResponse();

        ticketDetailResponse.setTicketTypeId( detailTicketTypeId( detail ) );
        ticketDetailResponse.setTicketTypeName( detailTicketTypeName( detail ) );
        ticketDetailResponse.setPrice( detail.getUnitPrice() );
        ticketDetailResponse.setQuantity( detail.getQuantity() );
        ticketDetailResponse.setDiscount( detail.getDiscountPercent() );
        ticketDetailResponse.setFinalPrice( detail.getFinalPrice() );

        return ticketDetailResponse;
    }

    private Long detailTicketTypeId(TicketDetail ticketDetail) {
        TicketType ticketType = ticketDetail.getTicketType();
        if ( ticketType == null ) {
            return null;
        }
        return ticketType.getId();
    }

    private String detailTicketTypeName(TicketDetail ticketDetail) {
        TicketType ticketType = ticketDetail.getTicketType();
        if ( ticketType == null ) {
            return null;
        }
        return ticketType.getName();
    }
}
