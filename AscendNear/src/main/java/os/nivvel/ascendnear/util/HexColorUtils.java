package os.nivvel.ascendnear.util;

import org.bukkit.entity.Player;

public class HexColorUtils {

    public static String parseHexColors(String message) {
        if (message == null) return "";
        StringBuilder builder = new StringBuilder(message);
        int index;
        while ((index = builder.indexOf("<#")) != -1) {
            if (index + 8 <= builder.length()) {
                String hex = builder.substring(index + 2, index + 8);
                if (hex.matches("[0-9a-fA-F]{6}")) {
                    StringBuilder mcColor = new StringBuilder("§x");
                    for (char c : hex.toCharArray()) {
                        mcColor.append("§").append(c);
                    }
                    builder.replace(index, index + 9, mcColor.toString());
                } else {
                    builder.delete(index, index + 2);
                }
            } else break;
        }
        return builder.toString();
    }

    public static String translate(String message) {
        return parseHexColors(message.replace("&", "§"));
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(translate(message));
    }
}
