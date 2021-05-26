package com.reddit.aroundtheworldmc

import net.md_5.bungee.api.ChatColor
import org.bukkit.configuration.ConfigurationSection
import java.util.regex.Pattern

class Util(config: ConfigurationSection) {

    private val hexPattern = Pattern.compile("&(#\\w{6})")

    private val levels = config.getStringList("levels")
    private val levelMap = levels.mapIndexed { index, level -> Pair(level, index) }.toMap()

    private val languages = config.getStringList("languages").toHashSet()

    /**
     * Sort list of languages by level and by language alphabetically
     */
    fun sortLanguages(languages: MutableList<Mastery>) =
        languages.sortWith(compareBy({ levelMap[it.level] }, { it.language }))

    /**
     * Sort speakers by language proficiency and alphabetically
     */
    fun sortSpeakers(speakers: MutableList<Speaker>) =
        speakers.sortWith(compareBy({ levelMap[it.level] }, { -it.player.lastSeen }))

    fun languageExists(language: String) = languages.contains(language)

    fun levelExists(level: String) = levelMap.contains(level)

    fun translateColorCodes(message: String): String {
        val matcher = hexPattern.matcher(ChatColor.translateAlternateColorCodes('&', message))
        val buffer = StringBuffer()

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString())
        }

        return matcher.appendTail(buffer).toString()
    }

}
