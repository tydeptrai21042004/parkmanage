package park.management.com.vn.testutil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractEndpointMappingTest {

    @Autowired
    protected RequestMappingHandlerMapping handlerMapping;

    @BeforeEach
    void ensureContextLoaded() {
        assertTrue(handlerMapping != null, "HandlerMapping must be autowired");
    }

    protected void assertMapped(RequestMethod method, String pattern) {
        boolean found = handlerMapping.getHandlerMethods().keySet().stream().anyMatch(info ->
            methodsContain(info, method) && patternsContain(info, pattern)
        );
        assertTrue(found, () -> "Expected mapping not found: " + method + " " + pattern);
    }

    private boolean methodsContain(RequestMappingInfo info, RequestMethod method) {
        return info.getMethodsCondition().getMethods().contains(method);
    }

    private boolean patternsContain(RequestMappingInfo info, String pattern) {
        Set<String> patterns = Optional.ofNullable(info.getPathPatternsCondition())
                .map(PathPatternsRequestCondition::getPatternValues)
                .orElse(Collections.emptySet());
        return patterns.contains(pattern);
    }
}
