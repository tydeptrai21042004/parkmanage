package park.management.com.vn.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import park.management.com.vn.testutil.AbstractEndpointMappingTest;

public class AdditionalEndpointSmokeTest extends AbstractEndpointMappingTest {

    @Test
    void ticketPassStatus_mappingExists() {
        assertMapped(RequestMethod.GET, "/api/passes/{code}");
    }

    @Test
    void refundEndpoints_mappingExist() {
        assertMapped(RequestMethod.GET, "/api/orders/{orderId}/refund");
        assertMapped(RequestMethod.POST, "/api/orders/{orderId}/refund");
    }
}
