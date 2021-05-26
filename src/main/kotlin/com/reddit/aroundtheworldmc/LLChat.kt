package com.reddit.aroundtheworldmc

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


/**
 * A chat plugin to facilitate language learning on AroundTheWorldMC
 *
 * @author Cxom (original) Puzzle & Martionex (Kotlin rewrite)
 * @version 1.1
 * @since 1.1
 */
class LLChat : JavaPlugin() {
    override fun onEnable() {
        // Copy the config.yml in the plugin configuration folder if it doesn't exists.
        saveDefaultConfig()
        Bukkit.getPluginManager().registerEvents(Events(), this)
    }

    override fun onDisable() {

    }
}
