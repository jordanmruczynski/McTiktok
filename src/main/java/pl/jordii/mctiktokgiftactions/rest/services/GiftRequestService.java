package pl.jordii.mctiktokgiftactions.rest.services;

import pl.jordii.mctiktokgiftactions.rest.objects.Gift;

import java.util.Map;

public interface GiftRequestService extends RequestService<String, Callback<Map<String,Gift>>> {

    void sendRequest(String name, Callback<Map<String, Gift>> callback);
    Map<String, Gift> parseInput(String json);

}
