package com.reddit.aroundtheworldmc

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.UUID

class Repository(private val llChat: LLChat) {

    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:plugins/LLChat/llchat.db")

    init {
        connection.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS mastery (" +
                "uuid VARCHAR(36)," +
                "language VARCHAR(255)," +
                "mlevel VARCHAR(255)," +
                "PRIMARY KEY (uuid, language));"
        )
    }

    fun setPlayerMastery(player: Player, language: String, level: String) {
        try {
            val statement =
                connection.prepareStatement("REPLACE INTO mastery (uuid, language, mlevel) VALUES (?, ?, ?);")
            statement.setString(1, player.uniqueId.toString())
            statement.setString(2, language)
            statement.setString(3, level)
            statement.executeUpdate()
            player.sendMessage("${SUCCESS}Language '${INFO}${language}${SUCCESS}' updated in mastery")
        } catch (e: SQLException) {
            player.sendMessage("${ERROR}An internal error has occurred; your languages have not been updated")
            e.printStackTrace()
        }
    }

    fun removePlayerMastery(player: Player, language: String) {
        try {
            val statement = connection.prepareStatement("DELETE FROM mastery WHERE uuid = ? AND language = ?")
            statement.setString(1, player.uniqueId.toString())
            statement.setString(2, language)
            statement.executeUpdate()
            player.sendMessage("${SUCCESS}Language '${INFO}${language}${SUCCESS}' removed from mastery")
        } catch (e: SQLException) {
            player.sendMessage("${ERROR}An internal error has occurred; your languages have not been updated")
            e.printStackTrace()
        }
    }

    fun retrievePlayerMastery(uuid: UUID): MutableList<Mastery> {
        val languages = mutableListOf<Mastery>()
        try {
            val statement = connection.prepareStatement("SELECT language, mlevel FROM mastery WHERE uuid LIKE ?")
            statement.setString(1, uuid.toString())
            val result = statement.executeQuery()
            while (result.next()) {
                languages.add(Mastery(result.getString(1), result.getString(2)))
            }
            result.close()
            statement.close()
        } catch (e: SQLException) {
            Bukkit.getLogger().warning("Could not get language mastery data from DB")
            e.printStackTrace()
        }
        llChat.util.sortLanguages(languages)
        return languages
    }

    fun retrieveSpeakers(language: String, level: String?): List<Speaker> {
        val speakers = mutableListOf<Speaker>()
        try {
            val statement = if (level == null)
                connection.prepareStatement("SELECT uuid, mlevel FROM mastery WHERE language LIKE ?").also {
                    it.setString(1, language)
                }
            else
                connection.prepareStatement("SELECT uuid, mlevel FROM mastery WHERE language LIKE ? AND mlevel LIKE ?")
                    .also {
                        it.setString(1, language)
                        it.setString(2, level)
                    }
            val result = statement.executeQuery()
            while (result.next()) {
                val uuid = UUID.fromString(result.getString(1))
                val masteryLevel = result.getString(2)
                val player = Bukkit.getPlayer(uuid) ?: Bukkit.getOfflinePlayer(uuid)
                speakers.add(Speaker(player, masteryLevel))
            }
            result.close()
            statement.close()
        } catch (e: SQLException) {
            Bukkit.getLogger().warning("Could not get speakers from DB")
            e.printStackTrace()
        }
        llChat.util.sortSpeakers(speakers)
        return speakers
    }

}
