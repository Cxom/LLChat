package com.reddit.aroundtheworldmc

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Events: Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        Bukkit.broadcastMessage("Hello everyone! &4Nice colour!")
    }

}
