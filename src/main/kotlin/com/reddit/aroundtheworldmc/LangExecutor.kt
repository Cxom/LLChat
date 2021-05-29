package com.reddit.aroundtheworldmc

import org.apache.commons.lang.WordUtils
import org.apache.commons.lang.math.NumberUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.ArrayList

class LangExecutor(
    private val llChat: LLChat,
    private val repository: Repository,
) : CommandExecutor, TabCompleter {

    private val levels = llChat.config.getStringList("levels")
    private val languages = llChat.config.getStringList("languages")
    private val languagePages = llChat.config.getStringList("languages").chunked(15)

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player &&
            sender !is ConsoleCommandSender) {
            sender.sendMessage("${ERROR}This command can only be executed by players or the console!")
            return true
        }

        if (args.isEmpty()) {
            return false
        }

        return when (args[0].lowercase()) {
            "levels" -> handleLevels(sender)
            "list" -> handleList(sender, args)
            "set", "add", "change" -> handleSet(sender, args)
            "remove", "delete" -> handleRemove(sender, args)
            "see" -> handleSee(sender, args)
            "has", "knows" -> handleHas(sender, args)
            "help" -> false
            else -> false
        }
    }

    private fun handleLevels(sender: CommandSender): Boolean {
        if (sender !is Player) {
            return false
        }
        sender.sendMessage(
            "${STYLING}Available levels from high to low fluency:\n" +
                levels.joinToString("\n") { "$STYLING - ${INFO}${it}" }
        )
        return true
    }

    private fun handleList(sender: CommandSender, args: Array<String>): Boolean {
        try {
            val pages = languagePages.size
            val pageNumber = if (args.size > 1) args[1].toInt().coerceIn(1, pages) else 1
            val page = languagePages[pageNumber - 1]
            sender.sendMessage(
                "${STYLING}---- Language List ---- " +
                    "Page ${INFO}$pageNumber${STYLING}/${INFO}$pages" +
                    "$STYLING of ${INFO}${languages.size}${STYLING} languages\n" +
                    page.joinToString("\n") { "$STYLING - $INFO${it}" }

            )
            if (pageNumber < pages) sender.sendMessage(
                "${STYLING}Type ${INFO}/lang list ${pageNumber + 1}${STYLING} to read the next page"
            )
        } catch (e: NumberFormatException) {
            sender.sendMessage("${ERROR}'${args[1]}' is not a valid page number")
        }
        return true
    }

    private fun handleSet(sender: CommandSender, args: Array<String>): Boolean {
        if (sender !is Player) {
            return false
        }
        if (args.size < 2) {
            sender.sendMessage("${ERROR}Usage: /lang set <language> <level>")
            return true
        }

        // E.g. to glue ["Toki", "Pona"] together
        val rawLanguage = args.slice(1 until args.size - 1).joinToString(" ")

        val language = WordUtils.capitalize(rawLanguage.lowercase())
        if (!llChat.util.languageExists(language)) {
            sender.sendMessage("${ERROR}The language '${INFO}$language${ERROR}'is invalid!")
            return true
        }

        val level = WordUtils.capitalize(args[args.size - 1].lowercase())
        if (!llChat.util.levelExists(level)) {
            sender.sendMessage("${ERROR}You must specify a valid mastery level!")
            return true
        }

        repository.setPlayerMastery(sender, language, level)
        llChat.players[sender.uniqueId]?.let { llChatPlayer ->
            llChatPlayer.languages.removeIf { it.language == language }
            llChatPlayer.languages.add(Mastery(language, level))
            llChat.util.sortLanguages(llChatPlayer.languages)
        }
        return true
    }

    private fun handleRemove(sender: CommandSender, args: Array<String>): Boolean {
        if (sender !is Player) {
            return false
        }
        if (args.size < 2) {
            sender.sendMessage("${ERROR}Usage: /lang remove <language>")
            return true
        }

        // E.g. to glue ["Toki", "Pona"] together
        val rawLanguage = args.slice(1 until args.size).joinToString(" ")
        val language = WordUtils.capitalize(rawLanguage.lowercase())

        repository.removePlayerMastery(sender, language)
        llChat.players[sender.uniqueId]?.let { llChatPlayer ->
            llChatPlayer.languages.removeIf { it.language == language }
        }
        return true
    }

    private fun handleSee(sender: CommandSender, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("${ERROR}Usage: /lang see <player>")
            return true
        }

        val targetPlayer = Bukkit.getPlayer(args[1]) ?: Bukkit.getOfflinePlayerIfCached(args[1])
        if (targetPlayer == null) {
            sender.sendMessage("${ERROR}No player with that username found")
            return true
        }

        val languages = llChat.players[targetPlayer.uniqueId]?.languages
            ?: repository.retrievePlayerMastery(targetPlayer.uniqueId)

        sender.sendMessage(
            "${STYLING}Languages of ${INFO}${targetPlayer.name}${STYLING}:\n" +
                if (languages.isEmpty()) "${ERROR}None" else languages.joinToString("\n") {
                    "$STYLING - ${PLAIN}${it.language}${STYLING} - ${INFO}${it.level}"
                }
        )
        return true
    }

    private fun handleHas(sender: CommandSender, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("${ERROR}Usage: /lang has <language> [level] [page]")
            return true
        }

        // We require quite complex logic for difficult cases such as /lang has Toki Pona A2 4
        val hasPageArg = NumberUtils.isNumber(args[args.size - 1])
        var pageNumber = NumberUtils.toInt(args[args.size - 1], 1)

        var languageArgEnd = if (hasPageArg) args.size - 1 else args.size

        var rawLanguage = args.slice(1 until languageArgEnd).joinToString(" ")
        var language = WordUtils.capitalize(rawLanguage.lowercase())
        var level: String? = null

        // Enters if because, for example, "Toki Pona A2" isn't language
        // The logic inside the if can determine the language and the level in the cases
        if (!llChat.util.languageExists(language)) {
            rawLanguage = args.slice(1 until --languageArgEnd).joinToString(" ")
            language = WordUtils.capitalize(rawLanguage.lowercase())
            level = WordUtils.capitalize(args[languageArgEnd].lowercase())

            if (!llChat.util.languageExists(language)) {
                sender.sendMessage("${ERROR}The language '${INFO}$language${ERROR}'is invalid!")
                return true
            }
        }

        if (level != null && !llChat.util.levelExists(level)) {
            sender.sendMessage("${ERROR}You must specify a valid mastery level!")
            return true
        }

        val speakers = repository.retrieveSpeakers(language, level)
        val pages = speakers.chunked(15)

        if (pages.isEmpty()) {
            sender.sendMessage("${INFO}$language${STYLING} has ${ERROR}no${STYLING} speakers of ${INFO}${level ?: "any"}${STYLING} level")
            return true
        }

        pageNumber = pageNumber.coerceIn(1, pages.size)
        val page = pages[pageNumber - 1]

        sender.sendMessage(page.joinToString(
            "\n",
            "${INFO}$language${STYLING} has ${INFO}${speakers.size}${STYLING} speakers of ${INFO}${level ?: "any"}${STYLING} level\n"
                + "Page ${INFO}$pageNumber${STYLING}/${INFO}${pages.size}\n"
        ) {
            val lastSeenInDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(it.player.lastSeen), Instant.now())
            val seenMessage = when {
                it.player.isOnline -> "${SUCCESS}Online"
                it.player.name == null -> "${ERROR}never seen online"
                else -> "seen ${INFO}${lastSeenInDays}${STYLING} days ago"
            }
            "$STYLING - ${PLAIN}${it.player.name ?: "${ERROR}Unknown name"}${STYLING} - ${INFO}${it.level}${STYLING} - $seenMessage"
        })
        if (pageNumber < pages.size) sender.sendMessage(
            "${STYLING}Type ${INFO}/lang has $language [level] ${pageNumber + 1}${STYLING} to read the next page"
        )
        return true
    }

    override fun onTabComplete(
        commandSender: CommandSender,
        command: Command, label: String,
        args: Array<String>
    ): List<String>? {
        if (commandSender !is Player) {
            return null
        }

        if (args.size < 2) {
            return StringUtil.copyPartialMatches(
                args[0],
                listOf("set", "see", "has", "remove", "list", "levels"),
                ArrayList()
            )
        }

        return when (args[0].lowercase()) {
            "set", "add", "change", "has" ->
                when {
                    args.size == 2 -> StringUtil.copyPartialMatches(
                        args[1], languages,
                        ArrayList()
                    )
                    args.size > 2 -> levels
                    else -> null
                }
            "remove", "delete" ->
                when (args.size) {
                    2 -> StringUtil.copyPartialMatches(
                        args[1], languages,
                        ArrayList()
                    )
                    else -> null
                }
            else -> null
        }
    }
}
