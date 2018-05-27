/*
 * Copyright 2018 agwlvssainokuni
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

package cherry.diff.ond;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cherry.diff.Diff;

/**
 * "An ON(ND) Difference Algorithm"
 */
public class OndDiff implements Diff {

	@Override
	public <T> Info<T> diff(List<T> a, List<T> b, Comparator<T> comparator) {
		int N = a.size();
		int M = b.size();
		return doDiff(a, b, N, M, comparator);
	}

	private <T> Info<T> doDiff(List<T> a, List<T> b, int N, int M, Comparator<T> comparator) {
		// ////////////////////////////////////////////////////////
		// ON(NP)アルゴリズム本体：ここから
		int MAX = M + N;
		Path[] V = new Path[MAX + MAX + 1];
		int offset = MAX;

		V[1 + offset] = new Path(1, 0, null);
		int edist = -1;
		LOOP: for (int D = 0; D <= MAX; D += 1) {
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
		// ON(NP)アルゴリズム本体：ここまで
		// ////////////////////////////////////////////////////////

		// リストの先頭から見られるよう並べ直す。
		// ※Edit Graphの末端は「k=N-M」なので、そこから逆順に辿る。
		List<Path> l = new LinkedList<>();
		for (Path pt = V[N - M + offset]; pt.getX() > 0; pt = pt.getPrev()) {
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

	private static class Path {
		private final int k;
		private final int x;
		private final Path prev;

		public Path(int k, int x, Path prev) {
			this.k = k;
			this.x = x;
			this.prev = prev;
		}

		public int getK() {
			return k;
		}

		public int getX() {
			return x;
		}

		public Path getPrev() {
			return prev;
		}
	}

}
