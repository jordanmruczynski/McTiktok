package pl.jordii.mctiktokgiftactions.rest.requests;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pl.jordii.mctiktokgiftactions.McTiktokGiftActions;
import pl.jordii.mctiktokgiftactions.rest.objects.Gift;
import pl.jordii.mctiktokgiftactions.rest.objects.Message;
import pl.jordii.mctiktokgiftactions.rest.services.Callback;
import pl.jordii.mctiktokgiftactions.rest.services.ChatMessageRequestService;
import pl.jordii.mctiktokgiftactions.rest.services.GiftRequestService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class ChatMessageRequest implements ChatMessageRequestService {

    @Override
    public void sendRequest(String name, Callback<Map<String, Message>> callback) {
        McTiktokGiftActions.getExecutorService().execute(() -> {
            try {
                URL url = new URL("http://localhost:1975/messages/" + name);
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet getRequest = new HttpGet(url.toString());
                CloseableHttpResponse response = httpClient.execute(getRequest);

                Scanner scanner = new Scanner(response.getEntity().getContent());
                StringBuilder responseBuilder = new StringBuilder();
                while (scanner.hasNext()) {
                    responseBuilder.append(scanner.nextLine());
                }

                Map<String, Message> jsonObjects = parseInput(responseBuilder.toString());
                scanner.close();
                callback.accept(jsonObjects);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Map<String, Message> parseInput(String json) {
        Type targetClassType = new TypeToken<Map<String, Message>>(){}.getType();
        return new Gson().fromJson(json, targetClassType);
    }
}
