package de.valtech.aecu.api.groovy.console.bindings.filters;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FilterByNodeRootPathsTest {

    private static final String TEST_PATH = "/content/my-site/nl/my-page";

    @Mock
    Resource resource;

    @BeforeEach
    void setUp() {
        when(resource.getPath()).thenReturn(TEST_PATH);
    }

    @Test
    void test_whenRootPathsMatches_filterAccepts() {
        boolean accept = new FilterByNodeRootPaths(Arrays.asList("/content/my-site")).filter(resource, new StringBuilder());
        assertTrue(accept);
    }

    @Test
    void test_whenRootPathsDontMatch_filterDenies() {
        boolean accept = new FilterByNodeRootPaths(Arrays.asList("/content/my-other-site")).filter(resource, new StringBuilder());
        assertFalse(accept);
    }

}