# gungame plugin for spigot/bukkit/paper
5v5 爆破, FFA, TDA, ...

## 機能:
### コマンド追加(/gg)
    team add <player> <- ongoing
    team remove <player> <- ongoing
    team bots <num of bots> <- ongoing
    start <name of saved game>
    stop <name of saved game>
    buy <name of weapons>

### イベント
    - キル/デス
        - キル/死亡プレイヤーの表示
        - スコアボード反映
    - ゲーム開始
    - ゲーム終了

### ファイル管理
    - ファイル保存
        - ゲームファイル単位
            - マップ
            - マップのゲームモード
            - 両チームのスポーンの座標