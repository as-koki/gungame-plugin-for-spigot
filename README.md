# gungame plugin for spigot/bukkit/paper
## 概要
Crackshot APIを使ったPvPゲーム管理を行うSpigotプラグイン。
- 対応予定のゲームモード: 5v5 爆破, FFA, TDA, コンクエスト...
- CrackShotPlusのアプデが止まっているのでそれの代替も実装予定
## 機能:
### コマンド追加(/gg)
    爆破モード:
    - gg bomb create <name>
    - gg bomb join <game> <player> <t/ct>
    - gg bomb leave <game> <player> <t/ct>
    - gg bomb start <game>
    - gg bomb list

    TDM:
    FFA:
### イベント
    - バニラアイテム死亡時
    - CSアイテム死亡時
        