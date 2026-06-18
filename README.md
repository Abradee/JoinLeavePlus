[![Patreon](https://img.shields.io/badge/Support%20On%20Patreon-8A2BE2)](https://patreon.com/abradee)


This is a simple plugin that will change the Minecraft Paper server's join and leave messages to custom ones that are configurable. Here are images of the defaults:

### First time join

![First time join text](https://cdn.modrinth.com/data/cached_images/362ecae90501431517ca9bb4a7f550ec1997413f.png)

### Join

![Join text](https://cdn.modrinth.com/data/cached_images/918aacb4f4dc71765e7f4449f7fb418c1d90ab99.png)

### Leave

![Leave text](https://cdn.modrinth.com/data/cached_images/6ff913530795e1cd8462920c8a745196e9f62402.png)


## How to configure messages

In JoinLeavePlus it is very simple to change the join/leave/first time join messages. You can EITHER use MiniMessage OR legacy color coding. Also, as of update 1.4 you can add multiple different messages to have them be randomized. If you don't want randomized messages, just set only one of them.

Location: 
```
/plugins/JoinLeavePlus/config.yml
```

Default config: 
```
#    __     _     __                    _____ _
# __|  |___|_|___|  |   ___ ___ _ _ ___|  _  | |_ _ ___
#|  |  | . | |   |  |__| -_| .'| | | -_|   __| | | |_ -|
#|_____|___|_|_|_|_____|___|__,|\_/|___|__|  |_|___|___|
# developed by abradee
# (c) 2020-2026 abradee
# (c) 2025-2026 JoinLeavePlus under GPL-3.0 license

# Configure your join and leave messages here!
# Note: supports EITHER MiniMessage or legacy formatting
first-time-join:
  - "<gold><b>[+] </b><aqua>%player% </aqua><gradient:#ffff55:#ffaa00>joined for the first time!</gradient> <white>Welcome aboard!</white>"
  - "<gold><b>[+] </b><aqua>%player% </aqua><gray>is new here!</gray> <yellow>Say hello and show them around!</yellow>"
  - "<gold><b>[+] </b><aqua>%player% </aqua><light_purple>just started their grand adventure today!</light_purple>"

join:
  - "<green><b>[+] </b><green>%player% </green><gray>joined the game.</gray> <gradient:#55ff55:#ffffff>Hope they brought pizza!</gradient>"
  - "<green><b>[+] </b><green>%player% </green><gray>entered the planet.</gray> <white>Good to see you!</white>"
  - "<green><b>[+] </b><green>%player% </green><gradient:#55ff55:#ffff55>has successfully connected.</gradient> <gray>Ready for action!</gray>"

leave:
  - "<red><b>[-] </b><red>%player% </red><gray>has left the server.</gray> <gradient:#ff5555:#ffaa00>See you next time!</gradient>"
  - "<red><b>[-] </b><red>%player% </red><gray>disconnected from the planet.</gray>"
  - "<red><b>[-] </b><red>%player% </red><gradient:#ff5555:#ffffff>has vanished into thin air.</gradient>"
```
