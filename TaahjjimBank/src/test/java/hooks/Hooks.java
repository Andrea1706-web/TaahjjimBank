package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import service.DriverS3MockSetup;

public class Hooks {

    @Before
    public void beforeScenario() {
        DriverS3MockSetup.startMock();
        DriverS3MockSetup.clearStorage();
    }

    @After
    public void afterScenario() {
        DriverS3MockSetup.stopMock();
    }
}