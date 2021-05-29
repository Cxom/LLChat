package com.reddit.aroundtheworldmc

import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class EventHandler(
    private val llChat: LLChat
) : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        llChat.addLLChatPlayer(e.player)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        llChat.players.remove(e.player.uniqueId)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onChat(e: AsyncPlayerChatEvent) {
        val senderString = llChat.util.translateColorCodes(e.player.displayName)
        val messageString = llChat.util.translateColorCodes(e.message)

        val message = TextComponent()
        val nameComponent = TextComponent(*TextComponent.fromLegacyText(senderString))
        val messageComponent = TextComponent(*TextComponent.fromLegacyText(messageString))

        val llChatPlayer = llChat.players[e.player.uniqueId] ?: llChat.addLLChatPlayer(e.player)
        val languages = llChatPlayer.languages

        val languageList = if (languages.isEmpty()) "${ERROR}None"
        else languages.joinToString("\n") { "${PLAIN}${it.language}${STYLING} - ${INFO}${it.level}" }

        nameComponent.hoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${STYLING}Languages:\n$languageList"))

        message.addExtra(nameComponent)
        message.addExtra(": ")
        message.addExtra(messageComponent)

        Bukkit.getLogger().info("${e.player.displayName}: ${e.message}")
        Bukkit.broadcast(message)
        e.isCancelled = true
    }


}
