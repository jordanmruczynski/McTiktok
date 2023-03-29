package pl.jordii.mctiktokgiftactions.rest.requests;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pl.jordii.mctiktokgiftactions.McTiktokGiftActions;
import pl.jordii.mctiktokgiftactions.rest.objects.Message;
import pl.jordii.mctiktokgiftactions.rest.services.Callback;
import pl.jordii.mctiktokgiftactions.rest.services.HandleStatusRequestService;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class HandleStatusRequest implements HandleStatusRequestService {

    @Override
    public void sendRequest(String name, StatusType statusType, Callback<String> callback) {
        McTiktokGiftActions.getExecutorService().execute(() -> {
            try {
                URL url = new URL("http://localhost:1975/" + statusType.name().toLowerCase() + "/" + name);
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet getRequest = new HttpGet(url.toString());
                CloseableHttpResponse response = httpClient.execute(getRequest);

                Scanner scanner = new Scanner(response.getEntity().getContent());
                StringBuilder responseBuilder = new StringBuilder();
                while (scanner.hasNext()) {
                    responseBuilder.append(scanner.nextLine());
                }

                scanner.close();
                callback.accept(responseBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
