package io.github.reclqtch.antitroll;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.ChatComponentText;

public class Hooks {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean shouldBlock(S27PacketExplosion packet) {
        if (packet.getX() > 30000000 || packet.getY() > 30000000 || packet.getZ() > 30000000 || packet.getStrength() > 32767) {
            printChat("\u00a7cAntiTroll \u00a78> \u00a7fSomeone tried to /crash you.");
            return true;
        }
        return false;
    }

    public static void onDemo() {
        printChat("\u00a7cAntiTroll \u00a78> \u00a7fSomeone tried to /demo you.");
    }

    public static void printChat(String msg) {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(msg));
    }

}
