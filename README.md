# LLChat

A chat plugin to facilitate language learning on AroundTheWorldMC.

## Features

1. Specify your CERT language levels with `/lang add <language> <level>`
2. See other people's language skills by hovering over their name or typing `/lang see <player>`
3. Join language-specific chat channels

## Build instructions

1. Open this as a Gradle project in your IDE.

2. Run the Gradle "jar" command, e.g. using `./gradlew jar`. This will build the plugin and place the resulting plugin
   file in `papermc/plugins/`.

3. Run `start.sh` to set up and start a Papermc Minecraft server.

To update the server after changing code simply rerun the jar command and type `reload confirm` in the server console.
No need to restart the server every time.

Check `start.sh` to change Minecraft versions, Papermc builds, etc.

## Plugin development

Visit https://www.spigotmc.org/wiki/spigot-plugin-development/
