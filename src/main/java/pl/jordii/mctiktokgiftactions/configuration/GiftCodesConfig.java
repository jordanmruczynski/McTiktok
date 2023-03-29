package pl.jordii.mctiktokgiftactions.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class GiftCodesConfig {
    private Map<String, Integer> codeMap;

    public void loadCodesFromJson(String jsonFile) throws IOException {
        byte[] jsonData = Files.readAllBytes(Paths.get(jsonFile));
        ObjectMapper objectMapper = new ObjectMapper();
        codeMap = objectMapper.readValue(jsonData, Map.class);
    }

    public Integer getNumberFromCode(String code) {
        if (codeMap.containsKey(code)) {
            return codeMap.get(code);
        } else {
            return -1;
        }
    }
}
