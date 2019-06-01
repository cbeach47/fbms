package org.apereo.portal.fbms.util;

import org.apereo.portal.fbms.data.ExtensionFilter;
import org.apereo.portal.fbms.data.ExtensionFilterChain;
import org.apereo.portal.fbms.data.ExtensionFilterChainBuilder;
import org.apereo.portal.fbms.data.ExtensionFilterChainMetadata;
import org.apereo.portal.fbms.data.FbmsEntity;
import org.apereo.portal.fbms.data.FormEntity;
import org.apereo.portal.fbms.data.SubmissionEntity;
import org.apereo.portal.fbms.data.filter.AbstractExtensionFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.core.OrderComparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class tests the {@link OpenEquellaServices} to insure it connects with
 * the openEQUELLA test instance properly
 */
@RunWith(JUnitPlatform.class)
public class OpenEquellaServicesTests extends ExtensionFilterChainBuilder {

    @Test
    public void gatherTokenTest() {
        OpenEquellaServices oes = new OpenEquellaServices();
        assertTrue(oes.gatherAccessToken());
    }

    @Test
    public void saveItemTest() {
        OpenEquellaServices oes = new OpenEquellaServices();
        assertTrue(oes.gatherAccessToken());
        assertTrue(oes.save("Testing the save item REST API from FBMS 2"));

    }
}
