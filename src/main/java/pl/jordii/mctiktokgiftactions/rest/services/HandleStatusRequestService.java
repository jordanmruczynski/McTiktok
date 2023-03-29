package pl.jordii.mctiktokgiftactions.rest.services;

import pl.jordii.mctiktokgiftactions.rest.requests.StatusType;

public interface HandleStatusRequestService {

    void sendRequest(String name, StatusType statusType, Callback<String> callback);
}
