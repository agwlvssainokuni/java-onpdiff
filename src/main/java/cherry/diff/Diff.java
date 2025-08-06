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

package cherry.diff;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 二つのシーケンスの差分を算出する機能を提供する。
 */
public interface Diff {

    /**
     * 二つの{@link List}の差分を算出する。
     *
     * @param a          「変更前」の{@link List}。
     * @param b          「変更後」の{@link List}。
     * @param comparator 要素の同一性を判定。
     * @return 算出結果の「差分」情報を保持する。
     */
    <T> Info<T> diff(List<T> a, List<T> b, Comparator<T> comparator);

    /**
     * 二つの配列の差分を算出する。
     *
     * @param a          「変更前」の配列。
     * @param b          「変更後」の配列。
     * @param comparator 要素の同一性を判定。
     * @return 算出結果の「差分」情報を保持する。
     */
    default <T> Info<T> diff(T[] a, T[] b, Comparator<T> comparator) {
        return diff(Arrays.asList(a), Arrays.asList(b), comparator);
    }

    /**
     * 二つの文字列の差分を算出する。
     *
     * @param a 「変更前」の文字列。
     * @param b 「変更後」の文字列。
     * @return 算出結果の「差分」情報を保持する。
     */
    default Info<Character> diff(CharSequence a, CharSequence b) {
        List<Character> aa = a.chars().mapToObj(i -> Character.valueOf((char) i)).collect(Collectors.toList());
        List<Character> bb = b.chars().mapToObj(i -> Character.valueOf((char) i)).collect(Collectors.toList());
        return diff(aa, bb, (x, y) -> x.compareTo(y));
    }

    /**
     * 二つの文字列リストの差分を算出する。
     *
     * @param a 「変更前」の文字列リスト。
     * @param b 「変更後」の文字列リスト。
     * @return 算出結果の「差分」情報を保持する。
     */
    default Info<String> diff(List<String> a, List<String> b) {
        return diff(a, b, (x, y) -> x.compareTo(y));
    }

    /**
     * 算出結果の「差分」情報を保持する。
     *
     * @param <T> 要素の型。
     */
    public static class Info<T> {
        private final int edist;
        private final List<Elem<T>> ses;
        private final List<T> lcs;

        /**
         * 差分情報のインスタンスを構築する。
         *
         * @param edist Edit Distance (編集距離)
         * @param ses   Shortest Edit Script (最短編集スクリプト)
         * @param lcs   Longest Common Subsequence (最長共通部分列)
         */
        public Info(int edist, List<Elem<T>> ses, List<T> lcs) {
            this.edist = edist;
            this.ses = ses;
            this.lcs = lcs;
        }

        /**
         * Edit Distance (編集距離) を取得する。
         * 二つのシーケンス間の最小編集操作数を表す。
         *
         * @return Edit Distance
         */
        public int getEdist() {
            return edist;
        }

        /**
         * Shortest Edit Script (最短編集スクリプト) を取得する。
         * シーケンスaをシーケンスbに変換するための編集操作の列を表す。
         *
         * @return Shortest Edit Script
         */
        public List<Elem<T>> getSes() {
            return ses;
        }

        /**
         * Longest Common Subsequence (最長共通部分列) を取得する。
         * 二つのシーケンスに共通する最長の部分列を表す。
         *
         * @return Longest Common Subsequence
         */
        public List<T> getLcs() {
            return lcs;
        }
    }

    /**
     * 当該要素の差分の種類。
     */
    public enum Type {
        /** 差分がない (共通要素) */
        SAME,
        /** 追加である */
        ADD,
        /** 削除である */
        DEL
    }

    /**
     * 要素ごとの差分を保持する。
     *
     * @param <T> 要素の型
     */
    public static class Elem<T> {
        private final Type type;
        private final T value;

        /**
         * 差分要素のインスタンスを構築する。
         *
         * @param type  差分の種類
         * @param value 要素の値
         */
        public Elem(Type type, T value) {
            this.type = type;
            this.value = value;
        }

        /**
         * 差分の種類を取得する。
         *
         * @return 差分の種類
         */
        public Type getType() {
            return type;
        }

        /**
         * 要素の値を取得する。
         *
         * @return 要素の値
         */
        public T getValue() {
            return value;
        }
    }

}
