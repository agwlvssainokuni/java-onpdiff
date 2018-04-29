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

package cherry.diff.onp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cherry.diff.Diff;

/**
 * "An ON(NP) Sequence Comparison Algorithm"
 */
public class OnpDiff implements Diff {

	@Override
	public <T> Info<T> diff(List<T> a, List<T> b, Comparator<T> comparator) {
		if (a.size() <= b.size()) {
			return doDiff(a, b, comparator, true);
		} else {
			return doDiff(b, a, comparator, false);
		}
	}

	private <T> Info<T> doDiff(List<T> a, List<T> b, Comparator<T> comparator, boolean normal) {

		// ////////////////////////////////////////////////////////
		// ON(NP)アルゴリズム本体：ここから
		int m = a.size();
		int n = b.size();
		Snake<T> snake = new Snake<>(a, b, m, n, comparator);

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
				fp[k + offset] = snake.maxAndSnake(k, fp[k - 1 + offset], fp[k + 1 + offset], normal);
			}
			for (int k = delta + p; k > delta; k--) {
				fp[k + offset] = snake.maxAndSnake(k, fp[k - 1 + offset], fp[k + 1 + offset], normal);
			}
			fp[delta + offset] = snake.maxAndSnake(delta, fp[delta - 1 + offset], fp[delta + 1 + offset], normal);
		} while (fp[delta + offset].getY() < n);
		int edist = delta + 2 * p;
		// ON(NP)アルゴリズム本体：ここまで
		// ////////////////////////////////////////////////////////

		// リストの先頭から見られるよう並べ直す。
		List<Path> l = new LinkedList<>();
		for (Path pt = fp[delta + offset]; pt.getY() >= 0; pt = pt.getPrev()) {
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
				// K+1：追加
				Type type = normal ? Type.ADD : Type.DEL;
				while (y - x < pt.getK()) {
					ses.add(new Elem<>(type, b.get(y)));
					y += 1;
				}
			}
			if (y - x > pt.getK()) {
				// K-1線：削除
				Type type = normal ? Type.DEL : Type.ADD;
				while (y - x > pt.getK()) {
					ses.add(new Elem<>(type, a.get(x)));
					x += 1;
				}
			}
			// K線：共通
			while (y < pt.getY()) {
				ses.add(new Elem<>(Type.SAME, b.get(y)));
				lcs.add(b.get(y));
				x += 1;
				y += 1;
			}
		}

		return new Info<>(edist, ses, lcs);
	}

	private static class Snake<T> {
		private final List<T> a;
		private final List<T> b;
		private final int m;
		private final int n;
		private final Comparator<T> comparator;

		public Snake(List<T> a, List<T> b, int m, int n, Comparator<T> comparator) {
			this.a = a;
			this.b = b;
			this.m = m;
			this.n = n;
			this.comparator = comparator;
		}

		public Path maxAndSnake(int k, Path pt1, Path pt2, boolean normal) {

			// max
			int y;
			Path pt;
			if (pt1.getY() + 1 == pt2.getY()) {
				// k-1とk+1から合流する場合は「削除、追加」の順になるよう調整する。
				if (normal) {
					y = pt1.getY() + 1;
					pt = pt1;
				} else {
					y = pt2.getY();
					pt = pt2;
				}
			} else if (pt1.getY() + 1 > pt2.getY()) {
				// k - 1
				y = pt1.getY() + 1;
				pt = pt1;
			} else {
				// k + 1
				y = pt2.getY();
				pt = pt2;
			}

			// snake
			int x = y - k;
			while (x < m && y < n && comparator.compare(a.get(x), b.get(y)) == 0) {
				x += 1;
				y += 1;
			}
			return new Path(k, y, pt);
		}
	}

	private static class Path {
		private final int k;
		private final int y;
		private final Path prev;

		public Path(int k, int y, Path prev) {
			this.k = k;
			this.y = y;
			this.prev = prev;
		}

		public int getK() {
			return k;
		}

		public int getY() {
			return y;
		}

		public Path getPrev() {
			return prev;
		}
	}

}
