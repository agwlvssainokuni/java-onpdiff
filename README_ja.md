# Java O(ND) & O(NP) 差分ライブラリ

二つのシーケンス間の差分を計算する効率的な差分アルゴリズムを実装したJavaライブラリです。

## 特徴

このライブラリは、よく知られた二つの差分アルゴリズムの実装を提供します：

- **Myersアルゴリズム**: "An O(ND) Difference Algorithm" - 一般的な用途に最適
- **Wuアルゴリズム**: "An O(NP) Sequence Comparison Algorithm" - 短い差分を持つシーケンスに最適化

両方のアルゴリズムは同じインターフェースを実装し、以下の機能を提供します：
- 編集距離の計算
- 最短編集スクリプト（SES）の生成
- 最長共通部分列（LCS）の抽出
- リスト、配列、文字列、文字列シーケンスのサポート

## 動作要件

- Java 21以上
- Gradle（ラッパー付属）

## インストール

### Gradle

```gradle
dependencies {
    implementation 'cherry:java-onpdiff:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>cherry</groupId>
    <artifactId>java-onpdiff</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 使用方法

### 基本的な例

```java
import cherry.diff.Diff;
import cherry.diff.myers.MyersDiff;
import cherry.diff.wu.WuDiff;
import java.util.Arrays;
import java.util.List;

public class DiffExample {
    public static void main(String[] args) {
        // 差分インスタンスを作成
        Diff myersDiff = new MyersDiff();
        Diff wuDiff = new WuDiff();
        
        // 二つのシーケンスを比較
        List<String> a = Arrays.asList("A", "B", "C", "A", "B", "B", "A");
        List<String> b = Arrays.asList("C", "B", "A", "B", "A", "C");
        
        // Myersアルゴリズムを使用して差分を計算
        Diff.Info<String> result = myersDiff.diff(a, b, String::compareTo);
        
        System.out.println("編集距離: " + result.distance());
        System.out.println("LCS長: " + result.lcs().size());
        
        // 最短編集スクリプトを出力
        for (Diff.Elem<String> elem : result.ses()) {
            System.out.println(elem.type() + ": " + elem.value());
        }
    }
}
```

### 文字列比較

```java
Diff diff = new MyersDiff();
String text1 = "ABCABBA";
String text2 = "CBABAC";

Diff.Info<Character> result = diff.diff(text1, text2);
System.out.println("編集距離: " + result.distance());
```

### 配列比較

```java
Diff diff = new WuDiff();
Integer[] arr1 = {1, 2, 3, 4, 5};
Integer[] arr2 = {1, 3, 4, 6, 5};

Diff.Info<Integer> result = diff.diff(arr1, arr2, Integer::compareTo);
```

## API リファレンス

### コアインターフェース

#### `Diff`
差分計算メソッドを提供するメインインターフェース：
- `diff(List<T>, List<T>, Comparator<T>)` - 二つのリストを比較
- `diff(T[], T[], Comparator<T>)` - 二つの配列を比較
- `diff(String, String)` - 二つの文字列を比較
- `diff(CharSequence, CharSequence)` - 二つの文字シーケンスを比較

#### `Diff.Info<T>`
結果を格納するコンテナ：
- `distance()` - シーケンス間の編集距離
- `ses()` - 操作リストとしての最短編集スクリプト
- `lcs()` - 最長共通部分列

#### `Diff.Elem<T>`
個々の差分要素：
- `type()` - 操作タイプ（SAME、ADD、DEL）
- `value()` - 要素の値

#### `Diff.Type`
操作タイプの列挙型：
- `SAME` - 両方のシーケンスに存在する要素
- `ADD` - 第二シーケンスに追加された要素
- `DEL` - 第一シーケンスから削除された要素

### 実装

#### `MyersDiff`
Myers O(ND)アルゴリズムの実装：
- 汎用的な差分アルゴリズム
- ほとんどの用途で最適
- 編集グラフ探索アプローチを使用

#### `WuDiff`
Wu O(NP)アルゴリズムの実装：
- 短い差分を持つシーケンスに最適化
- 自動的に最適なシーケンス順序を選択
- シーケンス長に対して差分が小さい場合により良いパフォーマンス

## アルゴリズム比較

| アルゴリズム | 時間計算量 | 空間計算量 | 最適な用途 |
|-------------|-----------|-----------|-----------|
| Myers       | O(ND)     | O(D)      | 汎用、長いシーケンス |
| Wu          | O(NP)     | O(P)      | 短い差分、類似シーケンス |

ここで：
- N = 長い方のシーケンスの長さ
- D = 編集距離（差分の数）
- P = 削除の数

## ソースからのビルド

```bash
# リポジトリをクローン
git clone <repository-url>
cd java-onpdiff

# プロジェクトをビルド
./gradlew build

# テストを実行
./gradlew test

# 特定のアルゴリズムテストを実行
./gradlew test --tests "cherry.diff.myers.MyersDiffTest"
./gradlew test --tests "cherry.diff.wu.WuDiffTest"
```

## テスト

ライブラリには以下をカバーする包括的なテストが含まれています：
- 同一のシーケンス
- 完全に異なるシーケンス
- 部分的な重複
- エッジケース（空のシーケンス、単一要素）
- アルゴリズム間のパフォーマンス比較

両方のアルゴリズム実装は、一貫性を確保するために同一のテストケースでテストされています。

## ライセンス

このプロジェクトはApache License 2.0の下でライセンスされています - 詳細は[LICENSE](LICENSE)ファイルをご覧ください。

## 参考文献

- Myers, Eugene W. "An O(ND) difference algorithm and its variations." Algorithmica 1.1-4 (1986): 251-266.
- Wu, Sun, et al. "An O(NP) sequence comparison algorithm." Information Processing Letters 35.6 (1990): 317-323.

## 貢献

貢献を歓迎します！イシューやプルリクエストの提出をお気軽にどうぞ。
