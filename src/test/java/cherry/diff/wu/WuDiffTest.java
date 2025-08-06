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
import cherry.diff.Diff.Elem;
import cherry.diff.Diff.Info;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link WuDiff}クラスのテストケース。
 * <p>
 * 様々なシーケンスパターンに対してWu差分アルゴリズムの動作を検証する。
 * テストケースには同一シーケンス、要素の追加・削除・置換、複合的な変更が含まれる。
 * MyersDiffTestと同じテストケースで両アルゴリズムの結果一致性を確認している。
 * </p>
 */
public class WuDiffTest {

    private final Diff impl = new WuDiff();

    /**
     * Integer型のリストに対して差分計算を実行するヘルパーメソッド。
     *
     * @param a 変更前のIntegerリスト
     * @param b 変更後のIntegerリスト
     * @return 差分情報
     */
    private Info<Integer> idiff(List<Integer> a, List<Integer> b) {
        return impl.diff(a, b, (i, j) -> (i - j));
    }

    /**
     * 同一シーケンスのテスト。
     * <p>
     * 完全に同じ要素を持つシーケンス同士の差分を計算する。
     * 編集距離は0、全要素がLCSに含まれ、全要素がSAME操作になることを確認する。
     * </p>
     */
    @Test
    public void 同一シーケンス() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 3, 4, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(0, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(7, ses.size());
        for (int i = 0; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    // 追加
    // ・先頭
    // ・末尾
    // ・途中

    /**
     * シーケンス先頭への1要素追加テスト。
     * <p>
     * Wuアルゴリズムで元シーケンスの先頭に1つの要素が追加された場合の差分を検証する。
     * Myersアルゴリズムと同じ結果を得ることを確認する。
     * </p>
     */
    @Test
    public void 追加_先頭_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(-1, 0, 1, 2, 3, 4, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(1, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(8, ses.size());
        assertEquals(Diff.Type.ADD, ses.get(0).getType());
        for (int i = 1; i < 8; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    /**
     * シーケンス先頭への3要素追加テスト。
     * <p>
     * Wuアルゴリズムで元シーケンスの先頭に3つの要素が追加された場合の差分を検証する。
     * Myersアルゴリズムと同じ結果を得ることを確認する。
     * </p>
     */
    @Test
    public void 追加_先頭_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(3, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(10, ses.size());
        assertEquals(Diff.Type.ADD, ses.get(0).getType());
        assertEquals(Diff.Type.ADD, ses.get(1).getType());
        assertEquals(Diff.Type.ADD, ses.get(2).getType());
        for (int i = 3; i < 10; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void 追加_末尾_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 3, 4, 5, 6, 7);
        Info<Integer> info = idiff(a, b);
        assertEquals(1, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(8, ses.size());
        for (int i = 0; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.ADD, ses.get(7).getType());
    }

    @Test
    public void 追加_末尾_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Info<Integer> info = idiff(a, b);
        assertEquals(3, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(10, ses.size());
        for (int i = 0; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.ADD, ses.get(7).getType());
        assertEquals(Diff.Type.ADD, ses.get(8).getType());
        assertEquals(Diff.Type.ADD, ses.get(9).getType());
    }

    @Test
    public void 追加_途中_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 7, 3, 4, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(1, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(8, ses.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.ADD, ses.get(3).getType());
        for (int i = 4; i < 8; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void 追加_途中_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 7, 8, 9, 3, 4, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(3, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(10, ses.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.ADD, ses.get(3).getType());
        assertEquals(Diff.Type.ADD, ses.get(4).getType());
        assertEquals(Diff.Type.ADD, ses.get(5).getType());
        for (int i = 6; i < 10; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    // 削除
    // ・先頭
    // ・末尾
    // ・途中

    @Test
    public void 削除_先頭_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(1, 2, 3, 4, 5, 6, 9);
        Info<Integer> info = idiff(a, b);
        assertEquals(1 + 1, info.getEdist());
        assertEquals(asList(1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(7 + 1, ses.size());
        assertEquals(Diff.Type.DEL, ses.get(0).getType());
        for (int i = 1; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void 削除_先頭_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(3, 4, 5, 6, 9, 9, 9);
        Info<Integer> info = idiff(a, b);
        assertEquals(3 + 3, info.getEdist());
        assertEquals(asList(3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(7 + 3, ses.size());
        assertEquals(Diff.Type.DEL, ses.get(0).getType());
        assertEquals(Diff.Type.DEL, ses.get(1).getType());
        assertEquals(Diff.Type.DEL, ses.get(2).getType());
        for (int i = 3; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void 削除_末尾_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(9, 0, 1, 2, 3, 4, 5);
        Info<Integer> info = idiff(a, b);
        assertEquals(1 + 1, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(7 + 1, ses.size());
        for (int i = 1; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.DEL, ses.get(7).getType());
    }

    @Test
    public void 削除_末尾_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(9, 9, 9, 0, 1, 2, 3);
        Info<Integer> info = idiff(a, b);
        assertEquals(3 + 3, info.getEdist());
        assertEquals(asList(0, 1, 2, 3), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(7 + 3, ses.size());
        for (int i = 3; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.DEL, ses.get(7).getType());
        assertEquals(Diff.Type.DEL, ses.get(8).getType());
        assertEquals(Diff.Type.DEL, ses.get(9).getType());
    }

    @Test
    public void 削除_途中_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 4, 5, 6, 9);
        Info<Integer> info = idiff(a, b);
        assertEquals(1 + 1, info.getEdist());
        assertEquals(asList(0, 1, 2, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(7 + 1, ses.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.DEL, ses.get(3).getType());
        for (int i = 4; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void 削除_途中_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 5, 6, 9, 9, 9);
        Info<Integer> info = idiff(a, b);
        assertEquals(3 + 3, info.getEdist());
        assertEquals(asList(0, 1, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(7 + 3, ses.size());
        for (int i = 0; i < 2; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.DEL, ses.get(2).getType());
        assertEquals(Diff.Type.DEL, ses.get(3).getType());
        assertEquals(Diff.Type.DEL, ses.get(4).getType());
        for (int i = 5; i < 7; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    // 変更
    // ・先頭
    // ・末尾
    // ・途中

    @Test
    public void 変更_先頭_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(7, 1, 2, 3, 4, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(2, info.getEdist());
        assertEquals(asList(1, 2, 3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(8, ses.size());
        assertEquals(Diff.Type.DEL, ses.get(0).getType());
        assertEquals(Diff.Type.ADD, ses.get(1).getType());
        for (int i = 2; i < 8; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void 変更_先頭_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(7, 8, 9, 3, 4, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(6, info.getEdist());
        assertEquals(asList(3, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(10, ses.size());
        assertEquals(Diff.Type.DEL, ses.get(0).getType());
        assertEquals(Diff.Type.DEL, ses.get(1).getType());
        assertEquals(Diff.Type.DEL, ses.get(2).getType());
        assertEquals(Diff.Type.ADD, ses.get(3).getType());
        assertEquals(Diff.Type.ADD, ses.get(4).getType());
        assertEquals(Diff.Type.ADD, ses.get(5).getType());
        for (int i = 6; i < 10; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void 変更_末尾_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 3, 4, 5, 7);
        Info<Integer> info = idiff(a, b);
        assertEquals(2, info.getEdist());
        assertEquals(asList(0, 1, 2, 3, 4, 5), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(8, ses.size());
        for (int i = 0; i < 6; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.DEL, ses.get(6).getType());
        assertEquals(Diff.Type.ADD, ses.get(7).getType());
    }

    @Test
    public void 変更_末尾_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 3, 7, 8, 9);
        Info<Integer> info = idiff(a, b);
        assertEquals(6, info.getEdist());
        assertEquals(asList(0, 1, 2, 3), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(10, ses.size());
        for (int i = 0; i < 4; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.DEL, ses.get(4).getType());
        assertEquals(Diff.Type.DEL, ses.get(5).getType());
        assertEquals(Diff.Type.DEL, ses.get(6).getType());
        assertEquals(Diff.Type.ADD, ses.get(7).getType());
        assertEquals(Diff.Type.ADD, ses.get(8).getType());
        assertEquals(Diff.Type.ADD, ses.get(9).getType());
    }

    @Test
    public void 変更_途中_1要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 2, 7, 4, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(2, info.getEdist());
        assertEquals(asList(0, 1, 2, 4, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(8, ses.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.DEL, ses.get(3).getType());
        assertEquals(Diff.Type.ADD, ses.get(4).getType());
        for (int i = 5; i < 8; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void 変更_途中_3要素() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 1, 7, 8, 9, 5, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(6, info.getEdist());
        assertEquals(asList(0, 1, 5, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(10, ses.size());
        for (int i = 0; i < 2; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
        assertEquals(Diff.Type.DEL, ses.get(2).getType());
        assertEquals(Diff.Type.DEL, ses.get(3).getType());
        assertEquals(Diff.Type.DEL, ses.get(4).getType());
        assertEquals(Diff.Type.ADD, ses.get(5).getType());
        assertEquals(Diff.Type.ADD, ses.get(6).getType());
        assertEquals(Diff.Type.ADD, ses.get(7).getType());
        for (int i = 8; i < 10; i++) {
            assertEquals(Diff.Type.SAME, ses.get(i).getType());
        }
    }

    @Test
    public void AB入れ替え実行の場合() {
        List<Integer> a = asList(0, 1, 2, 3, 4, 5, 6);
        List<Integer> b = asList(0, 3, 7, 4, 8, 6);
        Info<Integer> info = idiff(a, b);
        assertEquals(5, info.getEdist());
        assertEquals(asList(0, 3, 4, 6), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(9, ses.size());
        assertEquals(Diff.Type.SAME, ses.get(0).getType());
        assertEquals(Diff.Type.DEL, ses.get(1).getType());
        assertEquals(Diff.Type.DEL, ses.get(2).getType());
        assertEquals(Diff.Type.SAME, ses.get(3).getType());
        assertEquals(Diff.Type.ADD, ses.get(4).getType());
        assertEquals(Diff.Type.SAME, ses.get(5).getType());
        assertEquals(Diff.Type.DEL, ses.get(6).getType());
        assertEquals(Diff.Type.ADD, ses.get(7).getType());
        assertEquals(Diff.Type.SAME, ses.get(8).getType());
    }

    // 完全不一致
    @Test
    public void 完全不一致() {
        List<Integer> a = asList(0, 1, 2, 3, 4);
        List<Integer> b = asList(5, 6, 7, 8, 9);
        Info<Integer> info = idiff(a, b);
        assertEquals(10, info.getEdist());
        assertEquals(Collections.emptyList(), info.getLcs());
        List<Elem<Integer>> ses = info.getSes();
        assertEquals(10, ses.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(Diff.Type.DEL, ses.get(i).getType());
        }
        for (int i = 5; i < 10; i++) {
            assertEquals(Diff.Type.ADD, ses.get(i).getType());
        }
    }
}
