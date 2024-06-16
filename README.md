# gungame plugin for spigot/bukkit/paper
## 概要
Crackshot APIを使ったPvPゲーム管理を行うSpigotプラグイン。
- 対応予定のゲームモード: 5v5 爆破, FFA, TDA, コンクエスト...
- CrackShotPlusのアプデが止まっているのでそれの代替も実装予定
## 機能:
### コマンド追加(/gg)
    - bomb create <name> <gamemode> <ff> <matchpoint> <roundtime> <planttime> <defusetime> <exploretime>
    - bomb spawnpoint <name> <team> <x> <y> <z>

    - team make <team>
    - team delete <team>
    - team add <player> <- ongoing
    - team remove <player> <- ongoing
    - team bots <num of bots> <- ongoing
    - start <name of saved game>
    - stop <name of saved game>
    - buy <name of weapons>

### イベント
    - キル/デス
        - キル/死亡プレイヤーの表示
        - スコアボード反映
    - ゲーム開始
    - ゲーム終了

### ファイル管理 (config参照)
    - ファイル保存
        - ゲームファイル単位
            - マップ
            - マップのゲームモード
            - 両チームのスポーンの座標
            - kit