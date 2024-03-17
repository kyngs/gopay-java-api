package test.utils;

import cz.gopay.api.v3.GPClientException;
import cz.gopay.api.v3.IGPConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoginTests implements RestClientTest {

    private static final Logger logger = LoggerFactory.getLogger(LoginTests.class);

    @Test
    public void testAuthApacheHttpClient() {
        try {
            IGPConnector connector = getConnector().getAppToken(TestUtils.CLIENT_ID, TestUtils.CLIENT_SECRET);
            Assertions.assertNotNull(connector.getAccessToken());
        } catch (GPClientException ex) {
            TestUtils.handleException(ex, logger);
        }
    }
    
    
}
