package park.management.com.vn.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import park.management.com.vn.testutil.AbstractEndpointMappingTest;

public class UT03_OrderUseMappingTest extends AbstractEndpointMappingTest {
    @Test
    void mappingExists() {
        assertMapped(RequestMethod.PUT, "/api/orders/{id}");
    }
}
