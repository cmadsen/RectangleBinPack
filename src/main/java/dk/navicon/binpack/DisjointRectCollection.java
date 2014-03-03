package dk.navicon.binpack;

import java.util.List;

public class DisjointRectCollection {

	List<Rect> rects;

	boolean Add(Rect r) {
		// Degenerate rectangles are ignored.
		if (r.width == 0 || r.height == 0)
			return true;

		if (!Disjoint(r))
			return false;
		rects.add(r);
		return true;
	}

	void Clear() {
		rects.clear();
	}

	boolean Disjoint(Rect r) {
		// Degenerate rectangles are ignored.
		if (r.width == 0 || r.height == 0)
			return true;

		for (int i = 0; i < rects.size(); ++i)
			if (!Disjoint(rects.get(i), r))
				return false;
		return true;
	}

	static boolean Disjoint(Rect a, Rect b) {
		if (a.x + a.width <= b.x || b.x + b.width <= a.x
				|| a.y + a.height <= b.y || b.y + b.height <= a.y)
			return true;
		return false;
	}

}
