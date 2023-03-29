package pl.jordii.mctiktokgiftactions.giftactions;

import org.bukkit.entity.Player;
import pl.jordii.mctiktokgiftactions.rest.objects.Gift;

public class GiftActionHandler {
    private final GiftAction giftAction = new GiftAction();

    public void handleGift(int giftNumber, Player player, Gift gift) {
        player.sendTitle("", "§e" + gift.uniqueId + " §7wysłał §f" + gift.data.giftName);
        player.sendMessage("§e" + gift.uniqueId + " §7wysłał §f" + gift.data.giftName);
        switch (giftNumber) {
            case 1:
                giftAction.spawnZombie(player, gift.uniqueId);
                break;
            case 2:
                giftAction.setCowweb(player);
                break;
            case 3:
                giftAction.giveChlebeek(player);
                break;
            case 4:
                giftAction.spawnBlackSkeletons(player, gift.uniqueId);
                break;
            case 5:
                giftAction.spawnCreeper(player, gift.uniqueId);
                break;
            case 6:
                giftAction.giveEnderpearl(player);
                break;
            case 7:
                giftAction.giveKox(player);
                break;
            case 8:
                giftAction.clearEq(player);
                break;
            case 9:
                giftAction.giveTotem(player);
                break;
            case 10:
                giftAction.throwTnt(player);
                break;
            case 11:
                giftAction.setFreeze(player);
                break;
            case 12:
                giftAction.kill(player);
                break;
            case 13:
                giftAction.gamemode(player);
                break;
            case 14:
                giftAction.addOneHeart(player);
                break;
            case 15:
                giftAction.removeOneHeart(player);
                break;
            case 16:
                giftAction.spawnFriendlyIronGolem(player, gift.uniqueId);
                break;
            default:
                break;
        }
    }
}
