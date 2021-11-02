package com.reddit.aroundtheworldmc

import org.bukkit.entity.Player

data class LLChatPlayer(
    val bukkitPlayer: Player,
    val languages: MutableList<Mastery>
)
