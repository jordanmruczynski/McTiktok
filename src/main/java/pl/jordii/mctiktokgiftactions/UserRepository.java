package pl.jordii.mctiktokgiftactions;

import org.apache.http.client.utils.URLEncodedUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import pl.jordii.mctiktokgiftactions.configuration.GiftCodesConfig;
import pl.jordii.mctiktokgiftactions.giftactions.GiftActionHandler;
import pl.jordii.mctiktokgiftactions.model.StreamStatus;
import pl.jordii.mctiktokgiftactions.model.Streamer;
import pl.jordii.mctiktokgiftactions.rest.objects.Gift;
import pl.jordii.mctiktokgiftactions.rest.requests.ChatMessageRequest;
import pl.jordii.mctiktokgiftactions.rest.requests.GiftRequest;
import pl.jordii.mctiktokgiftactions.rest.requests.HandleStatusRequest;
import pl.jordii.mctiktokgiftactions.rest.requests.StatusType;
import pl.jordii.mctiktokgiftactions.util.ServerMainThread;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserRepository implements Listener {

    private final ScheduledExecutorService scheduledExecutorService;
    private final BukkitScheduler bukkitScheduler;
    private final GiftCodesConfig giftCodesConfig;
    private GiftActionHandler giftActionHandler = new GiftActionHandler();

    private GiftRequest giftRequest = new GiftRequest();
    private ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
    private HandleStatusRequest handleStatusRequest = new HandleStatusRequest();

    private Map<UUID, Streamer> streamers = new HashMap<>();

    public UserRepository(ScheduledExecutorService scheduledExecutorService, BukkitScheduler bukkitScheduler, GiftCodesConfig giftCodesConfig) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.bukkitScheduler = bukkitScheduler;
        this.giftCodesConfig = giftCodesConfig;
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (streamers.containsKey(player.getUniqueId())) {
            final Streamer streamer = streamers.get(player.getUniqueId());
            if (streamer.getStreamStatus() == StreamStatus.ON) {
                handleStatusRequest.sendRequest(streamer.getTiktokName(), StatusType.STOP, callback -> {
                    System.out.println("STREAMER QUIT WITH ENABLED LIVE, TRYING TURN OFF LIVE.. CALLBACK: " + callback);
                });
            }
            streamers.remove(player.getUniqueId());
        }
    }

    public void start() {
        this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (streamers.isEmpty()) return;

            for (Streamer streamer : streamers.values()) {
                Player player = Bukkit.getPlayer(streamer.getPlayerUuid());

                if (streamer.getStreamStatus() == StreamStatus.ON) {
                    giftRequest.sendRequest(streamer.getTiktokName(), callback -> {
                        callback.values().forEach(g -> {
                            ServerMainThread.RunParallel.run(() -> {
                                int giftNumber = giftCodesConfig.getNumberFromCode(g.data.giftId);
                                if (giftNumber != -1) {
                                    giftActionHandler.handleGift(giftNumber, player, g);
                                    streamer.setLastGifter(g.uniqueId);
                                }
                            });
                        });

                    });

                    chatMessageRequest.sendRequest(streamer.getTiktokName(), callback -> {
                        callback.values().forEach(g -> {
                            player.sendMessage(ChatColor.GRAY + g.uniqueId + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + g.data.comment);
                        });
                    });
                }

            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public GiftRequest getGiftRequest() {
        return giftRequest;
    }

    public ChatMessageRequest getChatMessageRequest() {
        return chatMessageRequest;
    }

    public HandleStatusRequest getHandleStatusRequest() {
        return handleStatusRequest;
    }

    public void addStreamer(Streamer streamer) {
        streamers.put(streamer.getPlayerUuid(), streamer);
    }

    public void removeStreamer(UUID uuid) {
        streamers.remove(uuid);
    }

    public void replaceStreamer(UUID uuid, Streamer streamer) {
        streamers.replace(uuid, streamer);
    }

    public Streamer getStreamer(UUID uuid) {
        if (streamers.containsKey(uuid)) return streamers.get(uuid);
        else return null;
    }

    public int getStreamersAmount() {
        return streamers.size();
    }
}
