/*
 * Copyright 2018,2024 agwlvssainokuni
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cherry.diff.wu;

import cherry.diff.Diff;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Wu差分アルゴリズムの実装。
 * <p>
 * Sun Wu, Udi Manber, Gene Myers, Webb Miller著
 * "An ON(NP) Sequence Comparison Algorithm" (1989)
 * に基づいた差分計算アルゴリズムを実装している。
 * </p>
 * <p>
 * このアルゴリズムは時間計算量O((N+M)P)、空間計算量O(N+M)を持つ。
 * ここでN, Mはそれぞれ入力シーケンスのサイズ、Pは共通部分列の数である。
 * 特に差分が少ない場合にMyersアルゴリズムよりも効率的である。
 * </p>
 *
 * @see <a href="https://doi.org/10.1016/0020-0190(89)90010-2">An ON(NP) Sequence Comparison Algorithm</a>
 */
public class WuDiff implements Diff {

    /**
     * {@inheritDoc}
     * <p>
     * Wuアルゴリズムを使用して二つのリスト間の差分を計算する。
     * 性能最適化のため、シーケンスの長さを比較し、短い方を第一引数として処理する。
     * </p>
     *
     * @param <T>        要素の型
     * @param a          変更前のリスト
     * @param b          変更後のリスト
     * @param comparator 要素の同一性を判定するComparator
     * @return 差分情報（編集距離、最短編集スクリプト、最長共通部分列）
     */
    @Override
    public <T> Info<T> diff(List<T> a, List<T> b, Comparator<T> comparator) {
        int m = a.size();
        int n = b.size();
        if (m <= n) {
            return doDiff(a, b, m, n, true, comparator);
        } else {
            return doDiff(b, a, n, m, false, comparator);
        }
    }

    /**
     * Wuアルゴリズムの本体処理を実行する。
     *
     * @param <T>        要素の型
     * @param a          短い方のシーケンス
     * @param b          長い方のシーケンス
     * @param m          シーケンスaのサイズ
     * @param n          シーケンスbのサイズ
     * @param normal     元の順序で処理しているかどうか
     * @param comparator 要素の同一性を判定するComparator
     * @return 差分情報
     */
    private <T> Info<T> doDiff(List<T> a, List<T> b, int m, int n, boolean normal, Comparator<T> comparator) {
        MaxAndSnake maxAndSnake = createMaxAndSnake(a, b, m, n, normal, comparator);

        // ////////////////////////////////////////////////////////
        // Wuアルゴリズム本体：ここから
        int offset = m + 1;
        Path[] fp = new Path[(m + 1) + (n + 1) + 1];
        for (int k = -(m + 1); k <= (n + 1); k++) {
            fp[k + offset] = new Path(k, -1, null);
        }

        int delta = n - m;
        int p = -1;
        do {
            p += 1;
            for (int k = -p; k < delta; k++) {
                fp[k + offset] = maxAndSnake.apply(k, fp[k - 1 + offset], fp[k + 1 + offset]);
            }
            for (int k = delta + p; k > delta; k--) {
                fp[k + offset] = maxAndSnake.apply(k, fp[k - 1 + offset], fp[k + 1 + offset]);
            }
            fp[delta + offset] = maxAndSnake.apply(delta, fp[delta - 1 + offset], fp[delta + 1 + offset]);
        } while (fp[delta + offset].getY() < n);
        int edist = delta + 2 * p;
        // Wuアルゴリズム本体：ここまで
        // ////////////////////////////////////////////////////////

        // リストの先頭から見られるよう並べ直す。
        List<Path> l = new LinkedList<>();
        for (Path pt = fp[delta + offset]; pt.getPrev() != null; pt = pt.getPrev()) {
            l.add(0, pt);
        }

        // 差分の算出結果として以下の2点を導出する。
        // ・Shortest Edit Script
        // ・Longest Common Sequence
        List<Elem<T>> ses = new ArrayList<>();
        List<T> lcs = new ArrayList<>();
        int x = 0;
        int y = 0;
        for (Path pt : l) {
            if (y - x < pt.getK()) {
                // 差分の経路(k線)は現在位置よりも右側：追加
                Type type = normal ? Type.ADD : Type.DEL;
                while (y - x < pt.getK()) {
                    ses.add(new Elem<>(type, b.get(y)));
                    y += 1;
                }
            }
            if (y - x > pt.getK()) {
                // 差分の経路(k線)は現在位置よりも左側：削除
                Type type = normal ? Type.DEL : Type.ADD;
                while (y - x > pt.getK()) {
                    ses.add(new Elem<>(type, a.get(x)));
                    x += 1;
                }
            }
            // 差分の経路(k線)上で進めるだけ進む。
            while (y < pt.getY()) {
                ses.add(new Elem<>(Type.SAME, b.get(y)));
                lcs.add(b.get(y));
                x += 1;
                y += 1;
            }
        }

        return new Info<>(edist, ses, lcs);
    }

    /**
     * Wuアルゴリズムで使用するMaxAndSnake操作を生成する。
     * <p>
     * max操作では二つの経路からより進んだ方を選択し、
     * snake操作では共通要素を連続してスキップする。
     * </p>
     *
     * @param <T>        要素の型
     * @param a          シーケンスa
     * @param b          シーケンスb
     * @param m          シーケンスaのサイズ
     * @param n          シーケンスbのサイズ
     * @param normal     元の順序で処理しているかどうか
     * @param comparator 要素の同一性を判定するComparator
     * @return MaxAndSnake操作の実装
     */
    private <T> MaxAndSnake createMaxAndSnake(List<T> a, List<T> b, int m, int n, boolean normal,
                                              Comparator<T> comparator) {
        return (int k, Path pt1, Path pt2) -> {

            // Wuアルゴリズム：max
            int y;
            Path pt;
            if (pt1.getY() + 1 == pt2.getY()) {
                // k-1とk+1の両方から合流する場合は「削除、追加」の順になるよう選択する。
                if (normal) {
                    y = pt1.getY() + 1;
                    pt = pt1;
                } else {
                    y = pt2.getY();
                    pt = pt2;
                }
            } else if (pt1.getY() + 1 > pt2.getY()) {
                // k-1
                y = pt1.getY() + 1;
                pt = pt1;
            } else {
                // k+1
                y = pt2.getY();
                pt = pt2;
            }

            // Wuアルゴリズム：snake
            int x = y - k;
            while (x < m && y < n && comparator.compare(a.get(x), b.get(y)) == 0) {
                x += 1;
                y += 1;
            }
            return new Path(k, y, pt);
        };
    }

    /**
     * Wuアルゴリズムのmax及snake操作を定義する関数型インターフェース。
     */
    @FunctionalInterface
    private interface MaxAndSnake {
        /**
         * 指定されたk線上でmax及snake操作を実行し、新しい経路点を返す。
         *
         * @param k   k線の値
         * @param pt1 k-1線からの経路点
         * @param pt2 k+1線からの経路点
         * @return 新しい経路点
         */
        Path apply(int k, Path pt1, Path pt2);
    }

    /**
     * Wuアルゴリズムの編集グラフにおける経路上の点を表現するクラス。
     * <p>
     * Myersアルゴリズムとは異なり、こちらはy座標を保持する。
     * k線上の点は(x, y)で表現され、x = y - kで計算される。
     * </p>
     */
    private static class Path {
        /**
         * k線の値（y - xで計算される対角線インデックス）
         */
        private final int k;
        /**
         * 編集グラフのy座標（シーケンスbのインデックス）
         */
        private final int y;
        /**
         * 前の経路点への参照（経路を逆辿りするため）
         */
        private final Path prev;

        /**
         * 経路点のインスタンスを構築する。
         *
         * @param k    k線の値
         * @param y    y座標
         * @param prev 前の経路点
         */
        public Path(int k, int y, Path prev) {
            this.k = k;
            this.y = y;
            this.prev = prev;
        }

        /**
         * k線の値を取得する。
         *
         * @return k線の値
         */
        public int getK() {
            return k;
        }

        /**
         * y座標を取得する。
         *
         * @return y座標
         */
        public int getY() {
            return y;
        }

        /**
         * 前の経路点への参照を取得する。
         *
         * @return 前の経路点
         */
        public Path getPrev() {
            return prev;
        }
    }

}
