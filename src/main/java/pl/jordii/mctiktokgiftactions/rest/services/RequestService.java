package pl.jordii.mctiktokgiftactions.rest.services;

public interface RequestService<String, Callback> {

    void sendRequest(String name, Callback callback);

}
