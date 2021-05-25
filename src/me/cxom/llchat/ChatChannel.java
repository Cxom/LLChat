package me.cxom.llchat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatChannel {

    public static ChatChannel getGlobal() {
        return LLChat.getChatChannel("GLOBAL");
    }

    private final String name;
    private final Language language;
    private final Set<LLChatPlayer> subscribers = new HashSet<>();

    public ChatChannel(String name, Language language) {
        this.name = name;
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChatChannel)) {
            return false;
        }
        ChatChannel channel = (ChatChannel) o;
        return name.equals(channel.getName()) && language == channel.getLanguage();
    }

    public String getName() {
        return name;
    }

    public Language getLanguage() {
        return language;
    }

    public void addMember(LLChatPlayer player) {
        subscribers.add(player);
    }

    public void removeMember(LLChatPlayer player) {
        subscribers.remove(player);
    }

    public void sendMessage(String message, String sender, LLChatPlayer llChatPlayer) {

        System.out.println(ChatColor.stripColor("<" + language.getISO() + ": " + sender + "> ") + message);

        for (LLChatPlayer subscriber : subscribers) {
            Player player = subscriber.getPlayer();
            boolean isInChannel = subscriber.getMainChatChannel().getName().equals(language.getName());

            String color = this.equals(subscriber.getMainChatChannel())
                    ? ChatColor.WHITE.toString()
                    : ChatColor.GRAY.toString();

            TextComponent start = new TextComponent(TextComponent.fromLegacyText(color + "<"));
            TextComponent channel = new TextComponent(TextComponent.fromLegacyText(isInChannel ? "" : language.getISO() + " "));
            if (isInChannel) {
                channel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/channel main " + language.getName()));
                channel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join " + language
                        .getName() + "?").italic(true).create()));
            }
            TextComponent seprt = new TextComponent(TextComponent.fromLegacyText(isInChannel ? "" : String.format("%s: ", color)));

            TextComponent sendc = new TextComponent(TextComponent.fromLegacyText(sender));
            if (llChatPlayer != null) {
                sendc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, langsToComp(llChatPlayer.getLanguages())
                ));
            }

            String finalmsg = String.format("%s> %s", color, message);
            TextComponent rest = new TextComponent(TextComponent.fromLegacyText(finalmsg));
            if (isInChannel) {
                start.addExtra(channel);
                start.addExtra(seprt);
            }
            start.addExtra(sendc);
            start.addExtra(rest);
            player.spigot().sendMessage(start);
        }
    }

    public void sendMessage(String message, String sender) {
        sendMessage(message, sender, null);
    }

    private BaseComponent[] langsToComp(Map<String, String> langs) {
        ComponentBuilder b = new ComponentBuilder("Languages:" + "       ").bold(true);
        if (langs.isEmpty()) {
            b.append("\n");
            b.append("None").italic(true);
        } else {
            for (String k : langs.keySet()) {
                b.append("\n" + k + " - " + ChatColor.GREEN + langs.get(k)).bold(false);
            }
        }
        return b.create();
    }

}