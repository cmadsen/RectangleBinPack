package dk.navicon.binpack;

class Rect {
	int x;
	int y;
	int width;
	int height;

	public Rect() {
	}

	public Rect(Rect r) {
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}

	static public boolean IsContainedIn(Rect a, Rect b) {
		return a.x >= b.x && a.y >= b.y && a.x + a.width <= b.x + b.width
				&& a.y + a.height <= b.y + b.height;
	}

}

// / Performs a lexicographic compare on (rect short side, rect long side).
// / @return -1 if the smaller side of a is shorter than the smaller side of b,
// 1 if the other way around.
// / If they are equal, the larger side length is used as a tie-breaker.
// / If the rectangles are of same size, returns 0.
// int CompareRectShortSide(const Rect &a, const Rect &b);

// / Performs a lexicographic compare on (x, y, width, height).
// int NodeSortCmp(const Rect &a, const Rect &b);

// / Returns true if a is contained in b.
// bool IsContainedIn(const Rect &a, const Rect &b);

