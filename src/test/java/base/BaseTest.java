package base;

import org.apache.http.impl.client.HttpClients;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import utils.BuildRequest;

import java.io.IOException;

public class BaseTest {

    @BeforeClass
    public void setUp() {
        BuildRequest.client = HttpClients.createDefault();
    }

    @AfterClass
    public void tearDown() throws IOException {
        BuildRequest.client.close();
    }
}
