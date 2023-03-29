package pl.jordii.mctiktokgiftactions.rest.services;

import pl.jordii.mctiktokgiftactions.rest.objects.Message;

import java.util.Map;

public interface ChatMessageRequestService extends RequestService<String, Callback<Map<String, Message>>> {

    void sendRequest(String name, Callback<Map<String, Message>> callback);
    Map<String, Message> parseInput(String json);

}
