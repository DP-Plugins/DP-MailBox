<center><img src="https://i.postimg.cc/MKPVVR1s/dplogo-512.png" alt="logo"></center>
<center><img src="https://i.postimg.cc/RZ9dqPFx/introduce.png" alt="introduce"></center>

This plugin adds an **in-game mailbox system** that allows players to send and receive items asynchronously.  
Players can send items even when the target player is offline, and receive them later through a mailbox GUI.

---

<center><img src="https://i.postimg.cc/RZ9dqP08/description.png" alt="description"></center>

- Send items to other players via **mailbox**, even if they are offline  
- Each player has a personal **mailbox GUI** to receive items  
- Mail items **expire automatically** after a configurable time  
- Supports **admin mail sending** to specific players or all players  
- Fully supports **English and Korean language files**  
- All mailbox data is saved automatically  

---

<center><img src="https://i.postimg.cc/rwcjzhpH/depend-plugin.png" alt="depend-plugin"></center>

- All DP-Plugins require the **`DPP-Core`** plugin  
- The plugin will not work if **`DPP-Core`** is not installed  
- You can download **`DPP-Core`** here: <a href="https://github.com/DP-Plugins/DPP-Core/releases" target="_blank">Click me!</a>  
- No economy or PlaceholderAPI dependency required  

---

<center><img src="https://i.postimg.cc/dV01RxJB/installation.png" alt="installation"></center>

1️⃣ Place the **`DPP-Core`** plugin and this plugin file (**`DP-MailBox-*.jar`**) into your server’s **`plugins`** folder  

2️⃣ Restart the server, and the plugin will be automatically enabled  

3️⃣ Configuration and language files will be generated on first run  

---

<center><img src="https://i.postimg.cc/jSKcC85K/settings.png" alt="settings"></center>

- **`config.yml`**  
  - Message prefix  
  - Language selection (`en_US`, `ko_KR`)  
  - Mail expiration time (in seconds, default: 7 days)  

- **Language files**  
  - `lang/en_US.yml`  
  - `lang/ko_KR.yml`  

---

<center><img src="https://i.postimg.cc/SxqdjZKw/command.png" alt="command"></center>

❗ Some commands require admin permission (`dpmb.admin`)

**Command List and Examples**

| Command | Permission | Description | Example |
|---|---|---|---|
| `/dpmb open` | dpmb.open | Open your mailbox | `/dpmb open` |
| `/dpmb send <player>` | dpmb.send | Send held item to player | `/dpmb send Steve` |
| `/dpmb admin open <player>` | dpmb.admin | Open another player’s mailbox | `/dpmb admin open Alex` |
| `/dpmb admin send <player>` | dpmb.admin | Admin send item to player | `/dpmb admin send BuilderBob` |
| `/dpmb admin send all` | dpmb.admin | Admin send item to all players | `/dpmb admin send all` |
| `/dpmb admin time <time>` | dpmb.admin | Set mail expiration time | `/dpmb admin time 2d 6h` |

**❗Notes when using commands**

- Items are sent from the **player’s hand**  
- If inventory is full, receiving is blocked until space is available  
- Expired mails are removed automatically  
- Admin commands require **OP** or `dpmb.admin` permission  

---

<center><img src="https://i.postimg.cc/Z5ZH0fqL/api-integration.png" alt="api-integration"></center>

- No external API integrations are used

---

<center><a href="https://discord.gg/JnMCqkn2FX"><img src="https://i.postimg.cc/4xZPn8dC/discord.png" alt="discord"></a></center>

- https://discord.gg/JnMCqkn2FX  
- Join our Discord for support, bug reports, or feature requests  
- Suggestions and feedback are always welcome!

---
