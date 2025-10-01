package park.management.com.vn.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import park.management.com.vn.testutil.AbstractEndpointMappingTest;

public class UT16_LocalTopupConfirmMappingTest extends AbstractEndpointMappingTest {
    @Test
    void mappingExists() {
        assertMapped(RequestMethod.POST, "/api/local/topups/confirm");
    }
}
