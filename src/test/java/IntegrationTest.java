import com.interview.ServiceApplication;
import com.interview.ServiceConfiguration;
import com.interview.pojos.TransferRequest;
import com.interview.pojos.TransferResponse;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * This is the integration test for transfer balance service. It currently runs on the same database which is provided.
 * However, we must create a separate database in PostgreSQL for integration testing.
 */
public class IntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("configuration_test.yml");

    @ClassRule
    public static final DropwizardAppRule<ServiceConfiguration> RULE = new DropwizardAppRule<>(
            ServiceApplication.class, CONFIG_PATH);


    @BeforeClass
    public static void migrateDb() throws Exception {
        RULE.getApplication().run("db", "migrate", CONFIG_PATH);
    }

    @Test
    public void testTransferBalanceForSuccess() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setReceiverAccountId("B");
        transferRequest.setSenderAccountId("A");
        transferRequest.setBalanceToTransfer(30d);
        Response response = sendPostRequest(transferRequest);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        TransferResponse transferResponse = response.readEntity(TransferResponse.class);
        assertEquals(false, transferResponse.getErrorResponse());
    }

    @Test
    public void testTransferBalanceForNegativeBalance() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setReceiverAccountId("B");
        transferRequest.setSenderAccountId("A");
        transferRequest.setBalanceToTransfer(130d);
        Response response = sendPostRequest(transferRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, response.getStatus());
        TransferResponse transferResponse = response.readEntity(TransferResponse.class);
        assertEquals(true, transferResponse.getErrorResponse());
        assertEquals("Not enough balance in sender`s account", transferResponse.getMessage());
    }

    @Test
    public void testTransferBalanceForNonExistingAccount() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setReceiverAccountId("Z");
        transferRequest.setSenderAccountId("A");
        transferRequest.setBalanceToTransfer(10d);
        Response response = sendPostRequest(transferRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, response.getStatus());
        TransferResponse transferResponse = response.readEntity(TransferResponse.class);
        assertEquals(true, transferResponse.getErrorResponse());
        assertEquals("Sender or receiver does not exist", transferResponse.getMessage());
    }

    private Response sendPostRequest(TransferRequest transferRequest) {
        Client client = ClientBuilder.newClient();
        String url = "http://localhost:" + RULE.getLocalPort() + "/api";
        WebTarget webTarget = client.target(url);
        WebTarget balanceWebTarget = webTarget.path("/balance/transfer");
        Invocation.Builder invocationBuilder = balanceWebTarget.request(MediaType.APPLICATION_JSON);
        return invocationBuilder.accept(MediaType.APPLICATION_JSON).post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON));
    }

}