package dk.navicon.binpack;

import static dk.navicon.binpack.Rect.*;
import static java.lang.Integer.*;
import static java.lang.Math.*;
import static java.lang.System.*;

import java.util.ArrayList;
import java.util.List;

public class MaxRectsBinPack {

	// / Instantiates a bin of size (0,0). Call Init to create a new bin.
	// MaxRectsBinPack(){}
	//
	// /// Instantiates a bin of the given size.
	// MaxRectsBinPack(int width, int height){}
	//
	// /// (Re)initializes the packer to an empty bin of width x height units.
	// Call whenever
	// /// you need to restart with a new bin.
	// void Init(int width, int height){}

	// / Specifies the different heuristic rules that can be used when deciding
	// where to place a new rectangle.
	public enum FreeRectChoiceHeuristic {
		/**
		 * < -BSSF: Positions the rectangle against the short side of a free
		 * rectangle into which it fits the best.
		 */
		RectBestShortSideFit,

		/**
		 * < -BLSF: Positions the rectangle against the long side of a free
		 * rectangle into which it fits the best.
		 */

		RectBestLongSideFit,

		/**
		 * -BAF: Positions the rectangle into the smallest free rect into which
		 * it fits.
		 */

		RectBestAreaFit,

		RectBottomLeftRule,

		/**
		 * < -CP: Choosest the placement where the rectangle touches other rects
		 * as much as possible.
		 */
		RectContactPointRule
	};

	// / Inserts the given list of rectangles in an offline/batch mode, possibly
	// rotated.
	// / @param rects The list of rectangles to insert. This vector will be
	// destroyed in the process.
	// / @param dst [out] This list will contain the packed rectangles. The
	// indices will not correspond to that of rects.
	// / @param method The rectangle placement rule to use when packing.
	// void Insert(List<RectSize> rects, List<Rect> dst, FreeRectChoiceHeuristic
	// method){}
	//
	// /// Inserts a single rectangle into the bin, possibly rotated.
	// Rect Insert(int width, int height, FreeRectChoiceHeuristic method){}
	//
	// /// Computes the ratio of used surface area to the total bin area.
	// float Occupancy(){}

	private int binWidth;

	private int binHeight;

	private List<Rect> usedRectangles = new ArrayList<Rect>();

	private List<Rect> freeRectangles = new ArrayList<Rect>();

	// / Computes the placement score for placing the given rectangle with the
	// given method.
	// / @param score1 [out] The primary placement score will be outputted here.
	// / @param score2 [out] The secondary placement score will be outputted
	// here. This isu sed to break ties.
	// / @return This struct identifies where the rectangle would be placed if
	// it were placed.
	// Rect ScoreRect(int width, int height, FreeRectChoiceHeuristic method, int
	// &score1, int &score2){}
	//
	// /// Places the given rectangle into the bin.
	// void PlaceRect(Rect node) {}
	//
	// /// Computes the placement score for the -CP variant.
	// int ContactPointScoreNode(int x, int y, int width, int height) {}
	//
	// Rect FindPositionForNewNodeBottomLeft(int width, int height, int &bestY,
	// int &bestX) {}
	//
	// Rect FindPositionForNewNodeBestShortSideFit(int width, int height, int
	// &bestShortSideFit, int &bestLongSideFit) {}
	//
	// Rect FindPositionForNewNodeBestLongSideFit(int width, int height, int
	// &bestShortSideFit, int &bestLongSideFit) {}
	//
	// Rect FindPositionForNewNodeBestAreaFit(int width, int height, int
	// bestAreaFit, int bestShortSideFit) {}
	//
	// Rect FindPositionForNewNodeContactPoint(int width, int height, int
	// contactScore) {}
	//
	// /// @return True if the free node was split.
	// boolean SplitFreeNode(Rect freeNode, Rect usedNode){}
	//
	// /// Goes through the free rectangle list and removes any redundant
	// entries.
	// void PruneFreeList();

	public MaxRectsBinPack() {
		binWidth = 0;
		binHeight = 0;
	}

	public MaxRectsBinPack(int width, int height) {
		Init(width, height);
	}

	public void Init(int width, int height) {
		binWidth = width;
		binHeight = height;

		Rect n = new Rect();
		n.x = 0;
		n.y = 0;
		n.width = width;
		n.height = height;

		usedRectangles.clear();

		freeRectangles.clear();
		freeRectangles.add(n);
	}

	public Rect Insert(int width, int height, FreeRectChoiceHeuristic method) {
		Rect newNode = null;
		// int score1; // Unused in this function. We don't need to know the
		// score after finding the position.
		// int score2;
		MutableInt score1 = new MutableInt(0); // Unused in this function. We
												// don't need to know the score
												// after finding the position.
		MutableInt score2 = new MutableInt(0);

		switch (method) {
		case RectBestShortSideFit:
			newNode = FindPositionForNewNodeBestShortSideFit(width, height,
					score1, score2);
			break;
		case RectBottomLeftRule:
			newNode = FindPositionForNewNodeBottomLeft(width, height, score1,
					score2);
			break;
		case RectContactPointRule:
			newNode = FindPositionForNewNodeContactPoint(width, height, score1);
			break;
		case RectBestLongSideFit:
			newNode = FindPositionForNewNodeBestLongSideFit(width, height,
					score2, score1);
			break;
		case RectBestAreaFit:
			newNode = FindPositionForNewNodeBestAreaFit(width, height, score1,
					score2);
			break;
		}

		if (newNode.height == 0)
			return newNode;

		int numRectanglesToProcess = freeRectangles.size();
		for (int i = 0; i < numRectanglesToProcess; ++i) {
			if (SplitFreeNode(freeRectangles.get(i), newNode)) {
				// freeRectangles.erase(freeRectangles.begin() + i);
				boolean e = freeRectangles.remove(freeRectangles.get(i));
				// System.out.println("erased " + (e ? "ok" : "NOT OK!"));
				--i;
				--numRectanglesToProcess;
			}
		}

		PruneFreeList();

		usedRectangles.add(newNode);
		return newNode;
	}

	// void Insert(List<RectSize> &rects, List<Rect> &dst,
	// FreeRectChoiceHeuristic method)
	void Insert(List<RectSize> rects, List<Rect> dst,
			FreeRectChoiceHeuristic method) {
		dst.clear();

		while (rects.size() > 0) {
			// int bestScore1 = std::numeric_limits<int>::max();
			// int bestScore2 = std::numeric_limits<int>::max();
			MutableInt bestScore1 = new MutableInt(Integer.MAX_VALUE);
			MutableInt bestScore2 = new MutableInt(Integer.MAX_VALUE);
			int bestRectIndex = -1;
			Rect bestNode = null;

			for (int i = 0; i < rects.size(); ++i) {
				MutableInt score1 = new MutableInt(0);
				MutableInt score2 = new MutableInt(0);
				Rect newNode = ScoreRect(rects.get(i).width,
						rects.get(i).height, method, score1, score2);

				if (score1.get() < bestScore1.get()
						|| (score1.get() == bestScore1.get() && score2.get() < bestScore2
								.get())) {
					bestScore1.set(score1.get());
					bestScore2.set(score2.get());
					bestNode = newNode;
					bestRectIndex = i;
				}
			}

			if (bestRectIndex == -1)
				return;

			PlaceRect(bestNode);
			// rects.erase(rects.begin() + bestRectIndex);
			boolean e = rects.remove(rects.get(bestRectIndex));
			// System.out.println("erased " + (e ? "ok" : "NOT OK!"));
		}
	}

	void PlaceRect(Rect node) {
		int numRectanglesToProcess = freeRectangles.size();
		for (int i = 0; i < numRectanglesToProcess; ++i) {
			if (SplitFreeNode(freeRectangles.get(i), node)) {
				// freeRectangles.erase(freeRectangles.begin() + i);
				boolean e = freeRectangles.remove(freeRectangles.get(i));
				// System.out.println("erased " + (e ? "ok" : "NOT OK!"));
				--i;
				--numRectanglesToProcess;
			}
		}

		PruneFreeList();

		usedRectangles.add(node);
		// dst.add(bestNode); ///\todo Refactor so that this compiles.
	}

	// Rect ScoreRect(int width, int height, FreeRectChoiceHeuristic method, int
	// &score1, int &score2)
	Rect ScoreRect(int width, int height, FreeRectChoiceHeuristic method,
			MutableInt score1, MutableInt score2) {
		Rect newNode = null;
		score1.set(Integer.MAX_VALUE);
		score2.set(Integer.MAX_VALUE);
		switch (method) {
		case RectBestShortSideFit:
			newNode = FindPositionForNewNodeBestShortSideFit(width, height,
					score1, score2);
			break;
		case RectBottomLeftRule:
			newNode = FindPositionForNewNodeBottomLeft(width, height, score1,
					score2);
			break;
		case RectContactPointRule:
			newNode = FindPositionForNewNodeContactPoint(width, height, score1);
			score1.set(-score1.get()); // Reverse since we are minimizing, but
										// for
			// contact point score bigger is better.
			break;
		case RectBestLongSideFit:
			newNode = FindPositionForNewNodeBestLongSideFit(width, height,
					score2, score1);
			break;
		case RectBestAreaFit:
			newNode = FindPositionForNewNodeBestAreaFit(width, height, score1,
					score2);
			break;
		}

		// Cannot fit the current rectangle.
		if (newNode.height == 0) {
			// score1 = std::numeric_limits<int>::max();
			// score2 = std::numeric_limits<int>::max();
			score1.set(Integer.MAX_VALUE);
			score2.set(Integer.MAX_VALUE);
		}

		return newNode;
	}

	// / Computes the ratio of used surface area.
	float Occupancy() {
		long usedSurfaceArea = 0;
		for (int i = 0; i < usedRectangles.size(); ++i)
			usedSurfaceArea += usedRectangles.get(i).width
					* usedRectangles.get(i).height;

		return (float) usedSurfaceArea / (binWidth * binHeight);
	}

	// Rect FindPositionForNewNodeBottomLeft(int width, int height, int &bestY,
	// int &bestX)
	Rect FindPositionForNewNodeBottomLeft(int width, int height,
			MutableInt bestY, MutableInt bestX) {
		Rect bestNode = new Rect();
		// memset(&bestNode, 0, sizeof(Rect));

		bestY.set(Integer.MAX_VALUE);

		for (int i = 0; i < freeRectangles.size(); ++i) {
			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width
					&& freeRectangles.get(i).height >= height) {
				int topSideY = freeRectangles.get(i).y + height;
				if (topSideY < bestY.get()
						|| (topSideY == bestY.get() && freeRectangles.get(i).x < bestX
								.get())) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestY.set(topSideY);
					bestX.set(freeRectangles.get(i).x);
				}
			}
			if (freeRectangles.get(i).width >= height
					&& freeRectangles.get(i).height >= width) {
				int topSideY = freeRectangles.get(i).y + width;
				if (topSideY < bestY.get()
						|| (topSideY == bestY.get() && freeRectangles.get(i).x < bestX
								.get())) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestY.set(topSideY);
					bestX.set(freeRectangles.get(i).x);
				}
			}
		}
		return bestNode;
	}

	// Rect FindPositionForNewNodeBestShortSideFit(int width, int height,
	// int &bestShortSideFit, int &bestLongSideFit)
	Rect FindPositionForNewNodeBestShortSideFit(int width, int height,
			MutableInt bestShortSideFit, MutableInt bestLongSideFit) {
		Rect bestNode = new Rect();
		// memset(&bestNode, 0, sizeof(Rect));

		bestShortSideFit = new MutableInt(Integer.MAX_VALUE);// std::numeric_limits<int>::max();

		for (int i = 0; i < freeRectangles.size(); ++i) {
			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width
					&& freeRectangles.get(i).height >= height) {
				int leftoverHoriz = abs(freeRectangles.get(i).width - width);
				int leftoverVert = abs(freeRectangles.get(i).height - height);
				int shortSideFit = min(leftoverHoriz, leftoverVert);
				int longSideFit = max(leftoverHoriz, leftoverVert);

				if (shortSideFit < bestShortSideFit.get()
						|| (shortSideFit == bestShortSideFit.get() && longSideFit < bestLongSideFit
								.get())) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestShortSideFit.set(shortSideFit);
					bestLongSideFit.set(longSideFit);
				}
			}

			if (freeRectangles.get(i).width >= height
					&& freeRectangles.get(i).height >= width) {
				int flippedLeftoverHoriz = abs(freeRectangles.get(i).width
						- height);
				int flippedLeftoverVert = abs(freeRectangles.get(i).height
						- width);
				int flippedShortSideFit = min(flippedLeftoverHoriz,
						flippedLeftoverVert);
				int flippedLongSideFit = max(flippedLeftoverHoriz,
						flippedLeftoverVert);

				if (flippedShortSideFit < bestShortSideFit.get()
						|| (flippedShortSideFit == bestShortSideFit.get() && flippedLongSideFit < bestLongSideFit
								.get())) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestShortSideFit.set(flippedShortSideFit);
					bestLongSideFit.set(flippedLongSideFit);
				}
			}
		}
		return bestNode;
	}

	// Rect FindPositionForNewNodeBestLongSideFit(int width, int height,
	// int &bestShortSideFit, int &bestLongSideFit) const
	Rect FindPositionForNewNodeBestLongSideFit(int width, int height,
			MutableInt bestShortSideFit, MutableInt bestLongSideFit) {
		Rect bestNode = new Rect();
		// memset(&bestNode, 0, sizeof(Rect));

		bestLongSideFit = new MutableInt(Integer.MAX_VALUE);

		for (int i = 0; i < freeRectangles.size(); ++i) {
			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width
					&& freeRectangles.get(i).height >= height) {
				int leftoverHoriz = abs(freeRectangles.get(i).width - width);
				int leftoverVert = abs(freeRectangles.get(i).height - height);
				int shortSideFit = min(leftoverHoriz, leftoverVert);
				int longSideFit = max(leftoverHoriz, leftoverVert);

				if (longSideFit < bestLongSideFit.get()
						|| (longSideFit == bestLongSideFit.get() && shortSideFit < bestShortSideFit
								.get())) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestShortSideFit.set(shortSideFit);
					bestLongSideFit.set(longSideFit);
				}
			}

			if (freeRectangles.get(i).width >= height
					&& freeRectangles.get(i).height >= width) {
				int leftoverHoriz = abs(freeRectangles.get(i).width - height);
				int leftoverVert = abs(freeRectangles.get(i).height - width);
				int shortSideFit = min(leftoverHoriz, leftoverVert);
				int longSideFit = max(leftoverHoriz, leftoverVert);

				if (longSideFit < bestLongSideFit.get()
						|| (longSideFit == bestLongSideFit.get() && shortSideFit < bestShortSideFit
								.get())) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestShortSideFit.set(shortSideFit);
					bestLongSideFit.set(longSideFit);
				}
			}
		}
		return bestNode;
	}

	// Rect FindPositionForNewNodeBestAreaFit(int width, int height,
	// int &bestAreaFit, int &bestShortSideFit)
	Rect FindPositionForNewNodeBestAreaFit(int width, int height,
			MutableInt bestAreaFit, MutableInt bestShortSideFit) {
		Rect bestNode = new Rect();
		// memset(&bestNode, 0, sizeof(Rect));

		// bestAreaFit = std::numeric_limits<int>::max();
		bestAreaFit = new MutableInt(Integer.MAX_VALUE);

		for (int i = 0; i < freeRectangles.size(); ++i) {
			int areaFit = freeRectangles.get(i).width
					* freeRectangles.get(i).height - width * height;

			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width
					&& freeRectangles.get(i).height >= height) {
				int leftoverHoriz = abs(freeRectangles.get(i).width - width);
				int leftoverVert = abs(freeRectangles.get(i).height - height);
				int shortSideFit = min(leftoverHoriz, leftoverVert);

				if (areaFit < bestAreaFit.get()
						|| (areaFit == bestAreaFit.get() && shortSideFit < bestShortSideFit
								.get())) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestShortSideFit.set(shortSideFit);
					bestAreaFit.set(areaFit);
				}
			}

			if (freeRectangles.get(i).width >= height
					&& freeRectangles.get(i).height >= width) {
				int leftoverHoriz = abs(freeRectangles.get(i).width - height);
				int leftoverVert = abs(freeRectangles.get(i).height - width);
				int shortSideFit = min(leftoverHoriz, leftoverVert);

				if (areaFit < bestAreaFit.get()
						|| (areaFit == bestAreaFit.get() && shortSideFit < bestShortSideFit
								.get())) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestShortSideFit.set(shortSideFit);
					bestAreaFit.set(areaFit);
				}
			}
		}
		return bestNode;
	}

	// / Returns 0 if the two intervals i1 and i2 are disjoint, or the length of
	// their overlap otherwise.
	int CommonIntervalLength(int i1start, int i1end, int i2start, int i2end) {
		if (i1end < i2start || i2end < i1start)
			return 0;
		return min(i1end, i2end) - max(i1start, i2start);
	}

	int ContactPointScoreNode(int x, int y, int width, int height) {
		int score = 0;

		if (x == 0 || x + width == binWidth)
			score += height;
		if (y == 0 || y + height == binHeight)
			score += width;

		for (int i = 0; i < usedRectangles.size(); ++i) {
			if (usedRectangles.get(i).x == x + width
					|| usedRectangles.get(i).x + usedRectangles.get(i).width == x)
				score += CommonIntervalLength(usedRectangles.get(i).y,
						usedRectangles.get(i).y + usedRectangles.get(i).height,
						y, y + height);
			if (usedRectangles.get(i).y == y + height
					|| usedRectangles.get(i).y + usedRectangles.get(i).height == y)
				score += CommonIntervalLength(usedRectangles.get(i).x,
						usedRectangles.get(i).x + usedRectangles.get(i).width,
						x, x + width);
		}
		return score;
	}

	// Rect FindPositionForNewNodeContactPoint(int width, int height, int
	// &bestContactScore)
	Rect FindPositionForNewNodeContactPoint(int width, int height,
			MutableInt bestContactScore) {
		Rect bestNode = new Rect();
		// memset(&bestNode, 0, sizeof(Rect));

		bestContactScore = new MutableInt(-1);

		for (int i = 0; i < freeRectangles.size(); ++i) {
			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width
					&& freeRectangles.get(i).height >= height) {
				int score = ContactPointScoreNode(freeRectangles.get(i).x,
						freeRectangles.get(i).y, width, height);
				if (score > bestContactScore.get()) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestContactScore.set(score);
				}
			}
			if (freeRectangles.get(i).width >= height
					&& freeRectangles.get(i).height >= width) {
				int score = ContactPointScoreNode(freeRectangles.get(i).x,
						freeRectangles.get(i).y, width, height);
				if (score > bestContactScore.get()) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestContactScore.set(score);
				}
			}
		}
		return bestNode;
	}

	// boolean SplitFreeNode(Rect freeNode, const Rect &usedNode)
	boolean SplitFreeNode(Rect freeNode, Rect usedNode) {
		// Test with SAT if the rectangles even intersect.
		if (usedNode.x >= freeNode.x + freeNode.width
				|| usedNode.x + usedNode.width <= freeNode.x
				|| usedNode.y >= freeNode.y + freeNode.height
				|| usedNode.y + usedNode.height <= freeNode.y)
			return false;

		if (usedNode.x < freeNode.x + freeNode.width
				&& usedNode.x + usedNode.width > freeNode.x) {
			// New node at the top side of the used node.
			if (usedNode.y > freeNode.y
					&& usedNode.y < freeNode.y + freeNode.height) {
				Rect newNode = new Rect(freeNode);
				newNode.height = usedNode.y - newNode.y;
				freeRectangles.add(newNode);
			}

			// New node at the bottom side of the used node.
			if (usedNode.y + usedNode.height < freeNode.y + freeNode.height) {
				Rect newNode = new Rect(freeNode);
				newNode.y = usedNode.y + usedNode.height;
				newNode.height = freeNode.y + freeNode.height
						- (usedNode.y + usedNode.height);
				freeRectangles.add(newNode);
			}
		}

		if (usedNode.y < freeNode.y + freeNode.height
				&& usedNode.y + usedNode.height > freeNode.y) {
			// New node at the left side of the used node.
			if (usedNode.x > freeNode.x
					&& usedNode.x < freeNode.x + freeNode.width) {
				Rect newNode = new Rect(freeNode);
				newNode.width = usedNode.x - newNode.x;
				freeRectangles.add(newNode);
			}

			// New node at the right side of the used node.
			if (usedNode.x + usedNode.width < freeNode.x + freeNode.width) {
				Rect newNode = new Rect(freeNode);
				newNode.x = usedNode.x + usedNode.width;
				newNode.width = freeNode.x + freeNode.width
						- (usedNode.x + usedNode.width);
				freeRectangles.add(newNode);
			}
		}

		return true;
	}

	void PruneFreeList() {
		/*
		 * /// Would be nice to do something like this, to avoid a Theta(n^2)
		 * loop through each pair. /// But unfortunately it doesn't quite cut
		 * it, since we also want to detect containment. /// Perhaps there's
		 * another way to do this faster than Theta(n^2).
		 * 
		 * if (freeRectangles.size() > 0)
		 * clb::sort::QuickSort(&freeRectangles[0], freeRectangles.size(),
		 * NodeSortCmp);
		 * 
		 * for(int i = 0; i < freeRectangles.size()-1; ++i) if
		 * (freeRectangles[i].x == freeRectangles[i+1].x && freeRectangles[i].y
		 * == freeRectangles[i+1].y && freeRectangles[i].width ==
		 * freeRectangles[i+1].width && freeRectangles[i].height ==
		 * freeRectangles[i+1].height) {
		 * freeRectangles.erase(freeRectangles.begin() + i); --i; }
		 */

		// / Go through each pair and remove any rectangle that is redundant.
		for (int i = 0; i < freeRectangles.size(); ++i)
			for (int j = i + 1; j < freeRectangles.size(); ++j) {
				if (IsContainedIn(freeRectangles.get(i), freeRectangles.get(j))) {
					// freeRectangles.erase(freeRectangles.begin()+i);
					boolean e = freeRectangles.remove(freeRectangles.get(i));
					// System.out.println("erased " + (e ? "ok" : "NOT OK!"));
					--i;
					break;
				}
				if (IsContainedIn(freeRectangles.get(j), freeRectangles.get(i))) {
					// freeRectangles.erase(freeRectangles.begin()+j);
					boolean e = freeRectangles.remove(freeRectangles.get(j));
					// System.out.println("erased " + (e ? "ok" : "NOT OK!"));
					--j;
				}
			}
	}

	public static void main(String[] args) {
		if (args.length < 5 || args.length % 2 == 1) {
			out.printf("Usage: MaxRectsBinPackTest binWidth binHeight w_0 h_0 w_1 h_1 w_2 h_2 ... w_n h_n\n");
			out.printf(" where binWidth and binHeight define the size of the bin.\n");
			out.printf(" w_i is the width of the i'th rectangle to pack, and h_i the height.\n");
			out.printf("Example: MaxRectsBinPackTest 256 256 30 20 50 20 10 80 90 20\n");
			return;
		}

		// using namespace rbp;

		// Create a bin to pack to, use the bin size from command line.
		MaxRectsBinPack bin = new MaxRectsBinPack();
		int binWidth = parseInt(args[0]);
		int binHeight = parseInt(args[1]);
		out.printf("Initializing bin to size %dx%d.\n", binWidth, binHeight);
		bin.Init(binWidth, binHeight);

		// Pack each rectangle (w_i, h_i) the user inputted on the command line.
		for (int i = 2; i < args.length; i += 2) {
			// Read next rectangle to pack.
			int rectWidth = parseInt(args[i]);
			int rectHeight = parseInt(args[i + 1]);
			out.printf("Packing rectangle of size %dx%d: ", rectWidth,
					rectHeight);

			// Perform the packing.
			// This can be changed individually even for each rectangle packed.
			MaxRectsBinPack.FreeRectChoiceHeuristic heuristic = MaxRectsBinPack.FreeRectChoiceHeuristic.RectBestShortSideFit;
			Rect packedRect = bin.Insert(rectWidth, rectHeight, heuristic);

			// Test success or failure.
			if (packedRect.height > 0)
				out.printf(
						"Packed to (x,y)=(%d,%d), (w,h)=(%d,%d). Free space left: %.2f%%\n",
						packedRect.x, packedRect.y, packedRect.width,
						packedRect.height, 100.f - bin.Occupancy() * 100.f);
			else
				out.printf("Failed! Could not find a proper position to pack this rectangle into. Skipping this one.\n");
		}
		out.printf("Done. All rectangles packed.\n");
	}

}
