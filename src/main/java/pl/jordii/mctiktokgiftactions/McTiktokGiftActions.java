package pl.jordii.mctiktokgiftactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.jordii.mctiktokgiftactions.commands.TiktokCommand;
import pl.jordii.mctiktokgiftactions.configuration.GiftCodesConfig;
import pl.jordii.mctiktokgiftactions.giftactions.GiftActionListeners;
import pl.jordii.mctiktokgiftactions.placeholderapi.PlaceholderInject;
import pl.jordii.mctiktokgiftactions.util.FileCopy;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class McTiktokGiftActions extends JavaPlugin {

    private static ScheduledExecutorService executorServiceScheduled = Executors.newScheduledThreadPool(1);
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private UserRepository userRepository;
    private GiftCodesConfig giftCodesConfig;
    private File giftsConfig, licenseConfig;
    private final String gifstConfigFileName = "gifts.json";
    private final String licenseConfigFileName = "license.json";

    @Override
    public void onEnable() {
        giftCodesConfig = new GiftCodesConfig();
        setupFiles();

        try {
            giftCodesConfig.loadCodesFromJson(getDataFolder() + "/" +gifstConfigFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userRepository = new UserRepository(executorServiceScheduled, Bukkit.getScheduler(), giftCodesConfig);
        userRepository.start();

        getCommand("mctiktok").setExecutor(new TiktokCommand(userRepository));

        Bukkit.getPluginManager().registerEvents(userRepository, this);
        Bukkit.getPluginManager().registerEvents(new GiftActionListeners(), this);

        new PlaceholderInject(userRepository).register();
       // if(!new License(getLicenseCode(), "https://jordanmruczynski.com/verify.php", this).setSecurityKey("ecoF0IM05hxLokoHuiUhIUInkfF").register()) return;
    }

    @Override
    public void onDisable() {
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static ScheduledExecutorService getExecutorServiceScheduled() {
        return executorServiceScheduled;
    }

    public static McTiktokGiftActions getMcTiktok() {
        return McTiktokGiftActions.getPlugin(McTiktokGiftActions.class);
    }

    private void setupFiles() {
        File dataFolder = getDataFolder();
        giftsConfig = new File(dataFolder, gifstConfigFileName);
        licenseConfig = new File(dataFolder, licenseConfigFileName);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        FileCopy.createFileFromResource(gifstConfigFileName, giftsConfig, this);
        FileCopy.createFileFromResource(licenseConfigFileName, licenseConfig, this);
    }

    private String getLicenseCode() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(licenseConfig);
            return rootNode.get("license").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
