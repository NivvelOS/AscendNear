# AscendNear

![AscendNear Banner](https://img.shields.io/badge/Minecraft-1.16.5-blue)
![Java](https://img.shields.io/badge/Java-8-orange)
![Spigot](https://img.shields.io/badge/Spigot-API-green)

**AscendNear** — это мощный и настраиваемый плагин для Minecraft, который позволяет игрокам видеть ближайших игроков и направление к ним с помощью стрелок. Полностью интегрируется с PlaceholderAPI и LuckPerms, поддерживает скрытие игроков через права и настраиваемый HoverText.

---

## Features / Функции

- 🔹 Показывает ближайших игроков с указанием направления стрелкой.  
- 🔹 Игроки с правом `ascendnear.near.hide` автоматически скрываются.  
- 🔹 Поддержка PlaceholderAPI (например, `%luckperms_prefix% {player}`) для динамического отображения никнейма, префикса и других данных.  
- 🔹 Настраиваемые HoverText для быстрых команд, например `/invsee`.  
- 🔹 Полностью настраиваемый через `config.yml`.  
- 🔹 Работает на Spigot/Paper 1.16.5 с Java 8.

---

## Installation / Установка

1. Скачайте последний `.jar` файл плагина.  
2. Поместите его в папку `plugins` вашего сервера Spigot/Paper.  
3. Убедитесь, что установлены **LuckPerms** и **PlaceholderAPI** (если используете placeholders).  
4. Перезапустите сервер.  
5. Настройте `config.yml` для персонализации сообщений и HoverText.  

---

## Commands / Команды

```text
/near [radius] - Показывает ближайших игроков в радиусе.
```
📝 Example Config
```
permissions:
  player: 200
  vip: 400

messages:
  header: "<#FFD700>Nearby players within <#FFFFFF>{radius} <#FFD700>blocks:"
  no-players: "<#FF5555>No one nearby."
  player-line: "<#55FF55>* {arrow} {player} - {distance} blocks."
  hover-text: "Click to interactpp"
  click-command: "/invsee {player}"
  no-custom-radius: "<#FF5555>You need permission to use custom radius!"
  invalid-radius: "<#FF5555>Radius must be 1-1000 blocks!"
  invalid-number: "<#FF5555>Invalid number!"
  usage: "<#FF5555>/near or /near <radius>"


arrows:
  north: "⬆"
  north-east: "⬈"
  east: "➜"
  south-east: "⬊"
  south: "⬇"
  south-west: "⬋"
  west: "⬅"
  north-west: "⬉"

sounds:
  enabled: true
  found-players:
    sound: "ENTITY_EXPERIENCE_ORB_PICKUP"
    volume: 1.0
    pitch: 1.0
  no-players:
    sound: "BLOCK_NOTE_BLOCK_BASS"
    volume: 1.0
    pitch: 0.5
```

📞 Contact
```
Telegram: @nivvel
Discord: nivvel
```
