package pl.jordii.mctiktokgiftactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.jordii.mctiktokgiftactions.UserRepository;
import pl.jordii.mctiktokgiftactions.model.StreamStatus;
import pl.jordii.mctiktokgiftactions.model.Streamer;
import pl.jordii.mctiktokgiftactions.rest.requests.StatusType;

public class TiktokAdminCommand implements CommandExecutor {

    private final UserRepository userRepository;

    public TiktokAdminCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //mctiktok register <tiktokName>
    //mctiktok start/stop
    //mctiktok info
    //mctiktok list

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        final Player player = (Player) sender;

        if (!player.hasPermission("mctiktok.command.tiktokadmin")) {
            player.sendMessage("§cBrak permisji §fmctiktok.command.tiktokadmin");
            return true;
        }

        if ((args[0].equalsIgnoreCase("register") || args[0].equalsIgnoreCase("unregister")) && args.length != 2) {
            player.sendMessage("Niepoprawne uzycie komendy");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Niepoprawne uzycie komendy");
            return true;
        }

//        if (args.length != 2) {
//            player.sendMessage("§cPoprawne użycie komendy:");
//            player.sendMessage("§c§o  -> /tiktok <start/stop> <tiktokUserName>");
//            player.sendMessage("§c§o  -> /tiktok editChest");
//            player.sendMessage("§c§o  -> /tiktok list");
//            player.sendMessage("");
//            player.sendMessage("§c< > argument wymagany §7| §c( ) argument opcjonalny");
//            return true;
//        }

        final Streamer streamer = userRepository.getStreamer(player.getUniqueId());

        switch (args[0]) {
            case "register":
                if (streamer == null) {
                    userRepository.addStreamer(new Streamer(player.getUniqueId(), player.getName(), args[1], StreamStatus.OFF, 0, "None", "None"));
                } else {
                    player.sendMessage("Jestes juz na liscie streamerow");
                    player.sendMessage(streamer.toString());
                }
                break;
            case "unregister":
                if (streamer == null) {
                    player.sendMessage("Nie ma cie na liscie streamerow");
                }
                break;
            case "start":
                if (streamer == null) {
                    player.sendMessage("Nie ma cie na liscie streamerow");
                } else {
                    if (streamer.getStreamStatus() == StreamStatus.ON) {
                        player.sendMessage("Masz juz wlaczonego streama.");
                    } else {
                        userRepository.getHandleStatusRequest().sendRequest(streamer.getTiktokName(), StatusType.START, callback -> {
                            player.sendMessage(callback.toString());
                        });
                    }
                }
                break;
            case "stop":
                if (streamer == null) {
                    player.sendMessage("Nie ma cie na liscie streamerow");
                } else {
                    if (streamer.getStreamStatus() == StreamStatus.OFF) {
                        player.sendMessage("Nie masz wlaczonego streama.");
                    } else {
                        userRepository.getHandleStatusRequest().sendRequest(streamer.getTiktokName(), StatusType.STOP, callback -> {
                            player.sendMessage(callback.toString());
                        });
                    }
                }
                break;
            case "list":
                player.sendMessage("Opcja zostanie dodana w nastepnym update");
                break;
            case "info":
                player.sendMessage(streamer.toString());
                break;
            default:
                break;

        }

        return false;
    }
}
