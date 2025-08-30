# magic-aop 使い方

## 概要

## javaagentの引数

- libdir
    - MagicAop の javaagent で利用するJarファイルを保存するディレクトリ
- config
    - `magic-aop.config` 設定ファイルのパス
- loglevel
    - OFF: エラー以外、出力しない
    - INFO: （デフォルト）標準的な出力
    - DEBUG: デバッグ出力

## magic-aop.configの記述方法

- コメント行
    - `#` 開始行

- Interceptorクラス名定義（１列目からの行）
    - １列目は、Interceptorクラス名
    - ２列目以降は、Interceptorクラスのオプション

- AOP埋め込み対象メソッド定義（２列目からの行）
    - ２列目は、クラス名
        - クラス名
            - 例：com.github.hondams.magic.aop.MagicAopSampleApplicationRunner
        - `*` でのパターンマッチ
            - 例：com.github.hondams.magic.aop.*
        - 正規表現
            - `^`開始、`$`終了
    - ３列目は、メソッド名
        - `*` のみは、すべてのメソッド
        - `*` でのパターンマッチ
            - 例：test*
        - メソッド名の、カンマ ‘,‘区切り
            - 例：test,test2
        - 正規表現
            - `^`開始、`$`終了
