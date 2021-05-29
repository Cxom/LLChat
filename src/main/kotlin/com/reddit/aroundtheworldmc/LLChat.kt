package com.reddit.aroundtheworldmc

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID


/**
 * A chat plugin to facilitate language learning on AroundTheWorldMC
 *
 * @author Cxom (original) Puzzle & Martionex (Kotlin rewrite)
 * @version 3.0.1
 * @since 3.0.1
 */
class LLChat : JavaPlugin() {

    val players = hashMapOf<UUID, LLChatPlayer>()

    lateinit var util: Util
    lateinit var repository: Repository

    override fun onEnable() {
        saveDefaultConfig()
        util = Util(config)
        repository = Repository(this)

        Bukkit.getPluginManager().registerEvents(EventHandler(this), this)

        getCommand("lang")!!.setExecutor(LangExecutor(this, repository))
    }

    override fun onDisable() {
        players.clear()
    }

    fun addLLChatPlayer(player: Player): LLChatPlayer {
        val llChatPlayer = LLChatPlayer(
            player,
            repository.retrievePlayerMastery(player.uniqueId)
        )
        players[player.uniqueId] = llChatPlayer
        return llChatPlayer
    }

}
