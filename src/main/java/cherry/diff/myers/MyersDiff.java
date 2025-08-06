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

package cherry.diff.myers;

import cherry.diff.Diff;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Myers差分アルゴリズムの実装。
 * <p>
 * Eugene W. Myers著 "An ON(ND) Difference Algorithm and Its Variations" (1986)
 * に基づいた差分計算アルゴリズムを実装している。
 * </p>
 * <p>
 * このアルゴリズムは時間計算量O((N+M)D)、空間計算量O((N+M)D)を持つ。
 * ここでN, Mはそれぞれ入力シーケンスのサイズ、DはEdit Distanceである。
 * </p>
 *
 * @see <a href="http://www.xmailserver.org/diff2.pdf">An ON(ND) Difference Algorithm and Its Variations</a>
 */
public class MyersDiff implements Diff {

    /**
     * {@inheritDoc}
     * <p>
     * Myersアルゴリズムを使用して二つのリスト間の差分を計算する。
     * アルゴリズムは編集グラフを構築し、最短経路を探索することで
     * 最小編集距離と最短編集スクリプトを求める。
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

        int N = a.size();
        int M = b.size();

        // ////////////////////////////////////////////////////////
        // Myersアルゴリズム本体：ここから
        int MAX = M + N;
        Path[] V = new Path[MAX + MAX + 1];
        int offset = MAX;

        V[1 + offset] = new Path(1, 0, null);
        int edist = -1;
        LOOP:
        for (int D = 0; D <= MAX; D += 1) {
            for (int k = -D; k <= D; k += 2) {
                Path prev;
                int x;
                if (k <= -D || (k < D && V[k - 1 + offset].getX() < V[k + 1 + offset].getX())) {
                    // 左端またはk+1先行：k + 1から下へ
                    prev = V[k + 1 + offset];
                    x = prev.getX();
                } else {
                    // 右端またはk-1先行：k - 1から右へ
                    prev = V[k - 1 + offset];
                    x = prev.getX() + 1;
                }
                int y = x - k;
                while (x < N && y < M && comparator.compare(a.get(x + 1 - 1), b.get(y + 1 - 1)) == 0) {
                    x += 1;
                    y += 1;
                }
                V[k + offset] = new Path(k, x, prev);
                if (x >= N && y >= M) {
                    edist = D;
                    break LOOP;
                }
            }
        }
        // Myersアルゴリズム本体：ここまで
        // ////////////////////////////////////////////////////////

        // リストの先頭から見られるよう並べ直す。
        // ※Edit Graphの末端は「k=N-M」なので、そこから逆順に辿る。
        List<Path> l = new LinkedList<>();
        for (Path pt = V[N - M + offset]; pt.getPrev() != null; pt = pt.getPrev()) {
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
            if (x - y < pt.getK()) {
                // 差分の経路(k線)は現在位置よりも右側：削除
                Type type = Type.DEL;
                while (x - y < pt.getK()) {
                    ses.add(new Elem<>(type, a.get(x)));
                    x += 1;
                }
            }
            if (x - y > pt.getK()) {
                // 差分の経路(k線)は現在位置よりも左側：追加
                Type type = Type.ADD;
                while (x - y > pt.getK()) {
                    ses.add(new Elem<>(type, b.get(y)));
                    y += 1;
                }
            }
            // 差分の経路(k線)上で進めるだけ進む。
            while (x < pt.getX()) {
                ses.add(new Elem<>(Type.SAME, b.get(y)));
                lcs.add(b.get(y));
                x += 1;
                y += 1;
            }
        }

        return new Info<>(edist, ses, lcs);
    }

    /**
     * 編集グラフにおける経路上の点を表現するクラス。
     * <p>
     * 編集グラフではk線（k = x - y）上の点(x, y)で経路を追跡する。
     * このクラスはその点の位置と前の点への参照を保持する。
     * </p>
     */
    private static class Path {
        /**
         * k線の値（x - yで計算される対角線インデックス）
         */
        private final int k;
        /**
         * 編集グラフのx座標（シーケンスaのインデックス）
         */
        private final int x;
        /**
         * 前の経路点への参照（経路を逆辿りするため）
         */
        private final Path prev;

        /**
         * 経路点のインスタンスを構築する。
         *
         * @param k    k線の値
         * @param x    x座標
         * @param prev 前の経路点
         */
        public Path(int k, int x, Path prev) {
            this.k = k;
            this.x = x;
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
         * x座標を取得する。
         *
         * @return x座標
         */
        public int getX() {
            return x;
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
