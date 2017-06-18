package me.cxom.llchat;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangExecutor implements CommandExecutor, TabCompleter {
    private final ConfigurationSection config;
    private final List<String> languages = new ArrayList<>();
    private final List<List<String>> languagePages = new ArrayList<>();

    public LangExecutor(ConfigurationSection config) {
        this.config = config;
        this.languages.addAll(config.getStringList("languages"));

        for (int i = 0; i < languages.size(); i += 10) {
            int endid = (i + 9) > languages.size() ? languages.size() : i + 9;
            languagePages.add(languages.subList(i, endid));
            if ((i + 9) > languages.size()) break;
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {

        if (!(sender instanceof Player) &&
                !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED +
                    "This command can only be executed by players or " +
                    "the console!");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.RED +
                    "Usage: /lang <add|remove|list|levels>");
            return true;
        }

        if (args[0].equalsIgnoreCase("levels")) {
            List<String> levels = config.getStringList("levels");
            sender.sendMessage(ChatColor.YELLOW + "Available levels:");
            sender.sendMessage(levels.stream().map(l -> "- " + l)
                    .toArray(String[]::new));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            try {
                int pages = languagePages.size();
                int arg1 = args.length > 1 ? Integer.parseInt(args[1])-1 : 0;
                int pn = Math.max(0, Math.min(pages, arg1));
                System.out.println(pn);
                List<String> page = languagePages.get(pn);

                sender.sendMessage(ChatColor.YELLOW + " ---- " +
                        ChatColor.GOLD + "Language list" +
                        ChatColor.YELLOW + " -- " +
                        ChatColor.GOLD + "Page "+ ChatColor.RED + (pn + 1) +
                        ChatColor.GOLD + "/" + ChatColor.RED + pages +
                        ChatColor.YELLOW + " ----");
                sender.sendMessage(page.stream().map(l ->
                        ChatColor.GOLD + "- " + ChatColor.WHITE + l)
                        .toArray(String[]::new));
                if (pn < pages-1)
                    sender.sendMessage(ChatColor.GOLD + "Type " + ChatColor.RED +
                            "/lang list " + (pn + 2) + ChatColor.GOLD + " to " +
                            "read the next page");
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED +
                        "'" + args[1] + "' is not a valid page number");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED +
                        "Usage: /lang add <language> <level>");
                return true;
            }

            return !(sender instanceof Player) ||
                    onAddCommand((Player) sender, Arrays.copyOfRange(args,
                            1, args.length));
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED +
                        "Usage: /lang remove <language>");
                return true;
            }

            return !(sender instanceof Player) ||
                    onRemoveCommand((Player) sender, Arrays.copyOfRange(args,
                            1, args.length));
        }

        return false;
    }

    private boolean onAddCommand(Player p, String[] args) {
        StringBuilder langb = new StringBuilder(args[0]);
        if (args.length > 2) {
            for (String s : Arrays.asList(args).subList(1, args.length - 2)) {
                langb.append(" ");
                langb.append(s);
            }
            langb.append(" ");
            langb.append(args[args.length-2]);
        }
        String lang = WordUtils.capitalize(langb.toString().toLowerCase());
        String level = WordUtils.capitalize(args[args.length-1].toLowerCase());

        if (!config.getStringList("levels").contains(level)) {
            p.sendMessage(ChatColor.RED +
                    "You must specify a valid mastery level!");
            return true;
        }

        if (!config.getStringList("languages").contains(lang)) {
            p.sendMessage(ChatColor.RED +
                    "The language \"" + lang + "\" is invalid!");
            return true;
        }

        try {
            Connection c = LLChat.getConn();
            PreparedStatement stmt = c.prepareStatement(
                    "INSERT INTO mastery (uuid, language, mlevel) " +
                            "VALUES (?, ?, ?);");
            stmt.setString(1, p.getUniqueId().toString());
            stmt.setString(2, lang);
            stmt.setString(3, level);
            stmt.executeUpdate();
            p.sendMessage(ChatColor.GREEN + "Your languages have been updated");
        } catch (SQLException e) {
            p.sendMessage(ChatColor.RED +
                    "An internal error has occurred; " +
                    "your languages have not been updated");
            e.printStackTrace();
        }

        return true;
    }

    private boolean onRemoveCommand(Player p, String[] args) {
        StringBuilder langb = new StringBuilder();
        for (String s : Arrays.asList(args).subList(0, args.length - 1)) {
            langb.append(s);
            langb.append(" ");
        }
        langb.append(args[args.length - 1]);
        String lang = WordUtils.capitalize(langb.toString().toLowerCase());

        try {
            Connection c = LLChat.getConn();
            PreparedStatement stmt = c.prepareStatement(
                    "DELETE FROM mastery WHERE uuid = ? AND language = ?");
            stmt.setString(1, p.getUniqueId().toString());
            stmt.setString(2, lang);
            stmt.executeUpdate();
            p.sendMessage(ChatColor.GREEN + "Your languages have been updated");
        } catch (SQLException e) {
            p.sendMessage(ChatColor.RED +
                    "An internal error has occurred; " +
                    "your languages have not been updated");
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender,
                                      Command command, String label,
                                      String[] args) {
        if (!(commandSender instanceof Player)) {
            return null;
        }

        if (command.getName().equalsIgnoreCase("lang")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("")
                    || args.length == 1) {
                return StringUtil.copyPartialMatches(args[0],
                        Arrays.asList("add", "remove", "list", "levels"),
                        new ArrayList<>());
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (args.length == 2) {
                    return StringUtil.copyPartialMatches(args[1], languages,
                            new ArrayList<>());
                } else if (args.length > 2) {
                    return config.getStringList("levels");
                }
            }
            if (args[0].equalsIgnoreCase("remove")) {
                return StringUtil.copyPartialMatches(args[1], languages,
                        new ArrayList<>());
            }
        }

        return null;
    }
}
