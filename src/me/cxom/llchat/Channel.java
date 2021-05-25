package me.cxom.llchat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class Channel {
    private static ConfigurationSection conf;
    private static List<Channel> channels;
    private final String name;
    private final String prefix;
    private final ArrayList<String> languages;

    public Channel(String name, String prefix, ArrayList<String> languages) {
        this.name = name;
        this.prefix = prefix;
        this.languages = languages;
    }
    public Channel(String name, String prefix, String language) {
        this.name = name;
        this.prefix = prefix;
        this.languages = new ArrayList<String>();
        this.languages.add(language);
    }
    // If no arguments specified it will create the global channel
    public Channel() {
        this.name = "GLOBAL";
        this.prefix = "§3G";
        this.languages = new ArrayList<String>();
        this.languages.add("");
    }

    public String getName() {
        return name;
    }
    public String getPrefix() {
        return prefix;
    }
    public ArrayList<String> getLanguages() {
        return languages;
    }

    // Return true if there are more channels than global
    public static boolean setupChannels(ConfigurationSection config) {
        conf = config;
        Object[] channelNames = conf.getKeys(false).toArray();
        for (int i = 0; i < channelNames.length; i++) {
            String nam = channelNames[i].toString();
            Map<String, Object> vals = conf.getConfigurationSection(nam).getValues(false);
            String pref = vals.get("prefix").toString();
            String[] langs = (String[])vals.get("languages");
            String lang = vals.get("language").toString();
            if (langs.length == 0 || langs == null) {
                langs[0] = lang;
            }
            ArrayList<String> langList = new ArrayList<String>();
            for (int j = 0; j < langs.length; j++) {
                langList.add(langs[j]);
            }
            Channel chan = new Channel(nam,pref,langList);
            channels.add(chan);
        }
        if (channels.size() == 0) {
            channels.add(new Channel());
            return false;
        }
        return true;
    }

    public static List<Channel> getChannels() {
        return channels;
    }
    public static ArrayList<String> getChannelNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Channel chan : channels) {
            names.add(chan.getName());
        }
        return names;
    }
}
