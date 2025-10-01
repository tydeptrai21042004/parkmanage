package park.management.com.vn.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import park.management.com.vn.testutil.AbstractEndpointMappingTest;

public class UT14_BranchAmenityUpdateImageMappingTest extends AbstractEndpointMappingTest {
    @Test
    void mappingExists() {
        assertMapped(RequestMethod.PUT, "/api/branch-amenities/{id}/image");
    }
}
