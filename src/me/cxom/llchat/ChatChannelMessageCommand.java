package me.cxom.llchat;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class ChatChannelMessageCommand extends BukkitCommand {

    public static void registerChannelMessageCommands() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer()
                    .getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(
                    Bukkit.getServer());

            for (ChatChannel cc : LLChat.getChannels()) {
                commandMap.register(cc.getName(),
                        new ChatChannelMessageCommand(cc.getName(),
                                "Shout to the '" + cc.getName() + "' channel.",
                                "", Collections.singletonList(cc.getLanguage()
                                .getRawISO())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ChatChannel cc;

    protected ChatChannelMessageCommand(String name, String description,
                                        String usageMessage,
                                        List<String> aliases) {
        super(name, description, usageMessage, aliases);
        cc = LLChat.getChatChannel(name);
        System.out.println(getAliases());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            cc.sendMessage(String.join(" ", args), sender instanceof Player ?
                    ((Player) sender).getDisplayName() : sender.getName());
        }
        return true;
    }


}
