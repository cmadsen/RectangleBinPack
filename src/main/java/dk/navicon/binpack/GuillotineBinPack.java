package dk.navicon.binpack;

import static java.lang.Integer.*;
import static java.lang.Math.*;
import static java.lang.System.*;

import java.util.ArrayList;
import java.util.List;

public class GuillotineBinPack {
	// /// The initial bin size will be (0,0). Call Init to set the bin size.
	// GuillotineBinPack();
	//
	// /// Initializes a new bin of the given size.
	// GuillotineBinPack(int width, int height);
	//
	// /// (Re)initializes the packer to an empty bin of width x height units.
	// Call whenever
	// /// you need to restart with a new bin.
	// void Init(int width, int height);

	// / Specifies the different choice heuristics that can be used when
	// deciding which of the free subrectangles
	// / to place the to-be-packed rectangle into.
	enum FreeRectChoiceHeuristic {
		RectBestAreaFit, // /< -BAF
		RectBestShortSideFit, // /< -BSSF
		RectBestLongSideFit, // /< -BLSF
		RectWorstAreaFit, // /< -WAF
		RectWorstShortSideFit, // /< -WSSF
		RectWorstLongSideFit // /< -WLSF
	};

	// / Specifies the different choice heuristics that can be used when the
	// packer needs to decide whether to
	// / subdivide the remaining free space in horizontal or vertical direction.
	enum GuillotineSplitHeuristic {
		SplitShorterLeftoverAxis, // /< -SLAS
		SplitLongerLeftoverAxis, // /< -LLAS
		SplitMinimizeArea, // /< -MINAS, Try to make a single big rectangle at
							// the expense of making the other small.
		SplitMaximizeArea, // /< -MAXAS, Try to make both remaining rectangles
							// as even-sized as possible.
		SplitShorterAxis, // /< -SAS
		SplitLongerAxis // /< -LAS
	};

	// / Inserts a single rectangle into the bin. The packer might rotate the
	// rectangle, in which case the returned
	// / struct will have the width and height values swapped.
	// / @param merge If true, performs free Rectangle Merge procedure after
	// packing the new rectangle. This procedure
	// / tries to defragment the list of disjoint free rectangles to improve
	// packing performance, but also takes up
	// / some extra time.
	// / @param rectChoice The free rectangle choice heuristic rule to use.
	// / @param splitMethod The free rectangle split heuristic rule to use.
	// Rect Insert(int width, int height, boolean merge, FreeRectChoiceHeuristic
	// rectChoice, GuillotineSplitHeuristic splitMethod);

	// / Inserts a list of rectangles into the bin.
	// / @param rects The list of rectangles to add. This list will be destroyed
	// in the packing process.
	// / @param merge If true, performs Rectangle Merge operations during the
	// packing process.
	// / @param rectChoice The free rectangle choice heuristic rule to use.
	// / @param splitMethod The free rectangle split heuristic rule to use.
	// void Insert(List<RectSize> &rects, boolean merge,
	// FreeRectChoiceHeuristic rectChoice, GuillotineSplitHeuristic
	// splitMethod);

	// Implements GUILLOTINE-MAXFITTING, an experimental heuristic that's really
	// cool but didn't quite work in practice.
	// void InsertMaxFitting(List<RectSize> &rects, List<Rect> &dst, boolean
	// merge,
	// FreeRectChoiceHeuristic rectChoice, GuillotineSplitHeuristic
	// splitMethod);

	// / Computes the ratio of used/total surface area. 0.00 means no space is
	// yet used, 1.00 means the whole bin is used.
	// float Occupancy() ;

	// / Returns the internal list of disjoint rectangles that track the free
	// area of the bin. You may alter this vector
	// / any way desired, as long as the end result still is a list of disjoint
	// rectangles.
	// List<Rect> &GetFreeRectangles() { return freeRectangles; }

	// / Returns the list of packed rectangles. You may alter this vector at
	// will, for example, you can move a Rect from
	// / this list to the Free Rectangles list to free up space on-the-fly, but
	// notice that this causes fragmentation.
	// List<Rect> &GetUsedRectangles() { return usedRectangles; }

	// / Performs a Rectangle Merge operation. This procedure looks for adjacent
	// free rectangles and merges them if they
	// / can be represented with a single rectangle. Takes up
	// Theta(|freeRectangles|^2) time.
	// void MergeFreeList();

	private int binWidth;

	private int binHeight;

	// / Stores a list of all the rectangles that we have packed so far. This is
	// used only to compute the Occupancy ratio,
	// / so if you want to have the packer consume less memory, this can be
	// removed.
	List<Rect> usedRectangles = new ArrayList<Rect>();

	// / Stores a list of rectangles that represents the free area of the bin.
	// This rectangles in this list are disjoint.
	List<Rect> freeRectangles = new ArrayList<Rect>();

	// #ifdef _DEBUG
	// /// Used to track that the packer produces proper packings.
	// DisjointRectCollection disjointRects;
	// #endif

	// / Goes through the list of free rectangles and finds the best one to
	// place a rectangle of given size into.
	// / Running time is Theta(|freeRectangles|).
	// / @param nodeIndex [out] The index of the free rectangle in the
	// freeRectangles array into which the new
	// / rect was placed.
	// / @return A Rect structure that represents the placement of the new rect
	// into the best free rectangle.
	// Rect FindPositionForNewNode(int width, int height,
	// FreeRectChoiceHeuristic rectChoice, int *nodeIndex);
	//
	// static int ScoreByHeuristic(int width, int height, Rect &freeRect,
	// FreeRectChoiceHeuristic rectChoice);
	// // The following functions compute (penalty) score values if a rect of
	// the given size was placed into the
	// // given free rectangle. In these score values, smaller is better.
	//
	// static int ScoreBestAreaFit(int width, int height, Rect &freeRect);
	// static int ScoreBestShortSideFit(int width, int height, Rect &freeRect);
	// static int ScoreBestLongSideFit(int width, int height, Rect &freeRect);
	//
	// static int ScoreWorstAreaFit(int width, int height, Rect &freeRect);
	// static int ScoreWorstShortSideFit(int width, int height, Rect &freeRect);
	// static int ScoreWorstLongSideFit(int width, int height, Rect &freeRect);
	//
	// /// Splits the given L-shaped free rectangle into two new free rectangles
	// after placedRect has been placed into it.
	// /// Determines the split axis by using the given heuristic.
	// void SplitFreeRectByHeuristic( Rect &freeRect, Rect &placedRect,
	// GuillotineSplitHeuristic method);
	//
	// /// Splits the given L-shaped free rectangle into two new free rectangles
	// along the given fixed split axis.
	// void SplitFreeRectAlongAxis( Rect &freeRect, Rect &placedRect, boolean
	// splitHorizontal);

	GuillotineBinPack() {
		binWidth = 0;
		binHeight = 0;

	}

	GuillotineBinPack(int width, int height) {
		Init(width, height);
	}

	void Init(int width, int height) {
		binWidth = width;
		binHeight = height;

		// #ifdef _DEBUG
		// disjointRects.Clear();
		// #endif

		// Clear any memory of previously packed rectangles.
		usedRectangles.clear();

		// We start with a single big free rectangle that spans the whole bin.
		Rect n = new Rect();
		n.x = 0;
		n.y = 0;
		n.width = width;
		n.height = height;

		freeRectangles.clear();
		freeRectangles.add(n);
	}

	void Insert(List<RectSize> rects, boolean merge,
			FreeRectChoiceHeuristic rectChoice,
			GuillotineSplitHeuristic splitMethod) {
		// Remember variables about the best packing choice we have made so far
		// during the iteration process.
		int bestFreeRect = 0;
		int bestRect = 0;
		boolean bestFlipped = false;

		// Pack rectangles one at a time until we have cleared the rects array
		// of all rectangles.
		// rects will get destroyed in the process.
		while (rects.size() > 0) {
			// Stores the penalty score of the best rectangle placement -
			// bigger=worse, smaller=better.
			MutableInt bestScore = new MutableInt(Integer.MAX_VALUE);

			for (int i = 0; i < freeRectangles.size(); ++i) {
				for (int j = 0; j < rects.size(); ++j) {
					// If this rectangle is a perfect match, we pick it
					// instantly.
					if (rects.get(j).width == freeRectangles.get(i).width
							&& rects.get(j).height == freeRectangles.get(i).height) {
						bestFreeRect = i;
						bestRect = j;
						bestFlipped = false;
						bestScore = new MutableInt(Integer.MAX_VALUE);
						i = freeRectangles.size(); // Force a jump out of the
													// outer loop as well - we
													// got an instant fit.
						break;
					}
					// If flipping this rectangle is a perfect match, pick that
					// then.
					else if (rects.get(j).height == freeRectangles.get(i).width
							&& rects.get(j).width == freeRectangles.get(i).height) {
						bestFreeRect = i;
						bestRect = j;
						bestFlipped = true;
						bestScore = new MutableInt(Integer.MAX_VALUE);
						i = freeRectangles.size(); // Force a jump out of the
													// outer loop as well - we
													// got an instant fit.
						break;
					}
					// Try if we can fit the rectangle upright.
					else if (rects.get(j).width <= freeRectangles.get(i).width
							&& rects.get(j).height <= freeRectangles.get(i).height) {
						int score = ScoreByHeuristic(rects.get(j).width,
								rects.get(j).height, freeRectangles.get(i),
								rectChoice);
						if (score < bestScore.get()) {
							bestFreeRect = i;
							bestRect = j;
							bestFlipped = false;
							bestScore.set(score);
						}
					}
					// If not, then perhaps flipping sideways will make it fit?
					else if (rects.get(j).height <= freeRectangles.get(i).width
							&& rects.get(j).width <= freeRectangles.get(i).height) {
						int score = ScoreByHeuristic(rects.get(j).height,
								rects.get(j).width, freeRectangles.get(i),
								rectChoice);
						if (score < bestScore.get()) {
							bestFreeRect = i;
							bestRect = j;
							bestFlipped = true;
							bestScore.set(score);
						}
					}
				}
			}

			// If we didn't manage to find any rectangle to pack, abort.
			if (bestScore.get() == Integer.MAX_VALUE)
				return;

			// Otherwise, we're good to go and do the actual packing.
			Rect newNode = new Rect();
			newNode.x = freeRectangles.get(bestFreeRect).x;
			newNode.y = freeRectangles.get(bestFreeRect).y;
			newNode.width = rects.get(bestRect).width;
			newNode.height = rects.get(bestRect).height;

			if (bestFlipped) {
				int tmp = newNode.width;
				newNode.width = newNode.height;
				newNode.height = tmp;
				// std::swap(newNode.width, newNode.height);
			}

			// Remove the free space we lost in the bin.
			SplitFreeRectByHeuristic(freeRectangles.get(bestFreeRect), newNode,
					splitMethod);
			freeRectangles.remove(freeRectangles.get(bestFreeRect));

			// Remove the rectangle we just packed from the input list.
			rects.remove(rects.get(bestRect));

			// Perform a Rectangle Merge step if desired.
			if (merge) {
				MergeFreeList();
			}

			// Remember the new used rectangle.
			usedRectangles.add(newNode);

			// Check that we're really producing correct packings here.
			// debug_assert(disjointRects.Add(newNode) == true);
		}
	}

	// / @return True if r fits inside freeRect (possibly rotated).
	boolean Fits(RectSize r, Rect freeRect) {
		return (r.width <= freeRect.width && r.height <= freeRect.height)
				|| (r.height <= freeRect.width && r.width <= freeRect.height);
	}

	// / @return True if r fits perfectly inside freeRect, i.e. the leftover
	// area is 0.
	boolean FitsPerfectly(RectSize r, Rect freeRect) {
		return (r.width == freeRect.width && r.height == freeRect.height)
				|| (r.height == freeRect.width && r.width == freeRect.height);
	}

	/*
	 * // A helper function for GUILLOTINE-MAXFITTING. Counts how many
	 * rectangles fit into the given rectangle // after it has been split. void
	 * CountNumFitting( Rect &freeRect, int width, int height, List<RectSize>
	 * &rects, int usedRectIndex, boolean splitHorizontal, int &score1, int
	 * &score2) { int w = freeRect.width - width; int h = freeRect.height -
	 * height;
	 * 
	 * Rect bottom; bottom.x = freeRect.x; bottom.y = freeRect.y + height;
	 * bottom.height = h;
	 * 
	 * Rect right; right.x = freeRect.x + width; right.y = freeRect.y;
	 * right.width = w;
	 * 
	 * if (splitHorizontal) { bottom.width = freeRect.width; right.height =
	 * height; } else // Split vertically { bottom.width = width; right.height =
	 * freeRect.height; }
	 * 
	 * int fitBottom = 0; int fitRight = 0; for(int i = 0; i < rects.size();
	 * ++i) if (i != usedRectIndex) { if (FitsPerfectly(rects[i], bottom))
	 * fitBottom |= 0x10000000; if (FitsPerfectly(rects[i], right)) fitRight |=
	 * 0x10000000;
	 * 
	 * if (Fits(rects[i], bottom)) ++fitBottom; if (Fits(rects[i], right))
	 * ++fitRight; }
	 * 
	 * score1 = min(fitBottom, fitRight); score2 = max(fitBottom, fitRight); }
	 */
	/*
	 * // Implements GUILLOTINE-MAXFITTING, an experimental heuristic that's
	 * really cool but didn't quite work in practice. void
	 * InsertMaxFitting(List<RectSize> &rects, List<Rect> &dst, boolean merge,
	 * FreeRectChoiceHeuristic rectChoice, GuillotineSplitHeuristic splitMethod)
	 * { dst.clear(); int bestRect = 0; boolean bestFlipped = false; boolean
	 * bestSplitHorizontal = false;
	 * 
	 * // Pick rectangles one at a time and pack the one that leaves the most
	 * choices still open. while(rects.size() > 0 && freeRectangles.size() > 0)
	 * { int bestScore1 = -1; int bestScore2 = -1;
	 * 
	 * ///\todo Different sort predicates.
	 * clb::sort::QuickSort(&freeRectangles[0], freeRectangles.size(),
	 * CompareRectShortSide);
	 * 
	 * Rect &freeRect = freeRectangles[0];
	 * 
	 * for(int j = 0; j < rects.size(); ++j) { int score1; int score2;
	 * 
	 * if (rects.get(j).width == freeRect.width && rects.get(j).height ==
	 * freeRect.height) { bestRect = j; bestFlipped = false; bestScore1 =
	 * bestScore2 = std::numeric_limits<int>::max(); break; } else if
	 * (rects.get(j).width <= freeRect.width && rects.get(j).height <=
	 * freeRect.height) { CountNumFitting(freeRect, rects.get(j).width,
	 * rects.get(j).height, rects, j, false, score1, score2);
	 * 
	 * if (score1 > bestScore1 || (score1 == bestScore1 && score2 > bestScore2))
	 * { bestRect = j; bestScore1 = score1; bestScore2 = score2; bestFlipped =
	 * false; bestSplitHorizontal = false; }
	 * 
	 * CountNumFitting(freeRect, rects.get(j).width, rects.get(j).height, rects,
	 * j, true, score1, score2);
	 * 
	 * if (score1 > bestScore1 || (score1 == bestScore1 && score2 > bestScore2))
	 * { bestRect = j; bestScore1 = score1; bestScore2 = score2; bestFlipped =
	 * false; bestSplitHorizontal = true; } }
	 * 
	 * if (rects.get(j).height == freeRect.width && rects.get(j).width ==
	 * freeRect.height) { bestRect = j; bestFlipped = true; bestScore1 =
	 * bestScore2 = std::numeric_limits<int>::max(); break; } else if
	 * (rects.get(j).height <= freeRect.width && rects.get(j).width <=
	 * freeRect.height) { CountNumFitting(freeRect, rects.get(j).height,
	 * rects.get(j).width, rects, j, false, score1, score2);
	 * 
	 * if (score1 > bestScore1 || (score1 == bestScore1 && score2 > bestScore2))
	 * { bestRect = j; bestScore1 = score1; bestScore2 = score2; bestFlipped =
	 * true; bestSplitHorizontal = false; }
	 * 
	 * CountNumFitting(freeRect, rects.get(j).height, rects.get(j).width, rects,
	 * j, true, score1, score2);
	 * 
	 * if (score1 > bestScore1 || (score1 == bestScore1 && score2 > bestScore2))
	 * { bestRect = j; bestScore1 = score1; bestScore2 = score2; bestFlipped =
	 * true; bestSplitHorizontal = true; } } }
	 * 
	 * if (bestScore1 >= 0) { Rect newNode; newNode.x = freeRect.x; newNode.y =
	 * freeRect.y; newNode.width = rects[bestRect].width; newNode.height =
	 * rects[bestRect].height; if (bestFlipped) std::swap(newNode.width,
	 * newNode.height);
	 * 
	 * assert(disjointRects.Disjoint(newNode)); SplitFreeRectAlongAxis(freeRect,
	 * newNode, bestSplitHorizontal);
	 * 
	 * rects.erase(rects.begin() + bestRect);
	 * 
	 * if (merge) MergeFreeList();
	 * 
	 * usedRectangles.add(newNode); #ifdef _DEBUG disjointRects.Add(newNode);
	 * #endif }
	 * 
	 * freeRectangles.erase(freeRectangles.begin()); } }
	 */

	Rect Insert(int width, int height, boolean merge,
			FreeRectChoiceHeuristic rectChoice,
			GuillotineSplitHeuristic splitMethod) {
		// Find where to put the new rectangle.
		MutableInt freeNodeIndex = new MutableInt(0);
		Rect newRect = FindPositionForNewNode(width, height, rectChoice,
				freeNodeIndex);

		// Abort if we didn't have enough space in the bin.
		if (newRect.height == 0)
			return newRect;

		// Remove the space that was just consumed by the new rectangle.
		SplitFreeRectByHeuristic(freeRectangles.get(freeNodeIndex.get()),
				newRect, splitMethod);
		freeRectangles.remove(freeRectangles.get(freeNodeIndex.get()));

		// Perform a Rectangle Merge step if desired.
		if (merge)
			MergeFreeList();

		// Remember the new used rectangle.
		usedRectangles.add(newRect);

		// Check that we're really producing correct packings here.
		// debug_assert(disjointRects.Add(newRect) == true);

		return newRect;
	}

	// / Computes the ratio of used surface area to the total bin area.
	float Occupancy() {
		// /\todo The occupancy rate could be cached/tracked incrementally
		// instead
		// / of looping through the list of packed rectangles here.
		long usedSurfaceArea = 0;
		for (int i = 0; i < usedRectangles.size(); ++i)
			usedSurfaceArea += usedRectangles.get(i).width
					* usedRectangles.get(i).height;

		return (float) usedSurfaceArea / (binWidth * binHeight);
	}

	// / Returns the heuristic score value for placing a rectangle of size
	// width*height into freeRect. Does not try to rotate.
	// int ScoreByHeuristic(int width, int height, Rect &freeRect,
	// FreeRectChoiceHeuristic rectChoice)
	int ScoreByHeuristic(int width, int height, Rect freeRect,
			FreeRectChoiceHeuristic rectChoice) {
		switch (rectChoice) {
		case RectBestAreaFit:
			return ScoreBestAreaFit(width, height, freeRect);
		case RectBestShortSideFit:
			return ScoreBestShortSideFit(width, height, freeRect);
		case RectBestLongSideFit:
			return ScoreBestLongSideFit(width, height, freeRect);
		case RectWorstAreaFit:
			return ScoreWorstAreaFit(width, height, freeRect);
		case RectWorstShortSideFit:
			return ScoreWorstShortSideFit(width, height, freeRect);
		case RectWorstLongSideFit:
			return ScoreWorstLongSideFit(width, height, freeRect);
		default:
			assert (false);
			return Integer.MAX_VALUE;
		}
	}

	int ScoreBestAreaFit(int width, int height, Rect freeRect) {
		return freeRect.width * freeRect.height - width * height;
	}

	int ScoreBestShortSideFit(int width, int height, Rect freeRect) {
		int leftoverHoriz = abs(freeRect.width - width);
		int leftoverVert = abs(freeRect.height - height);
		int leftover = min(leftoverHoriz, leftoverVert);
		return leftover;
	}

	int ScoreBestLongSideFit(int width, int height, Rect freeRect) {
		int leftoverHoriz = abs(freeRect.width - width);
		int leftoverVert = abs(freeRect.height - height);
		int leftover = max(leftoverHoriz, leftoverVert);
		return leftover;
	}

	int ScoreWorstAreaFit(int width, int height, Rect freeRect) {
		return -ScoreBestAreaFit(width, height, freeRect);
	}

	int ScoreWorstShortSideFit(int width, int height, Rect freeRect) {
		return -ScoreBestShortSideFit(width, height, freeRect);
	}

	int ScoreWorstLongSideFit(int width, int height, Rect freeRect) {
		return -ScoreBestLongSideFit(width, height, freeRect);
	}

	// Rect FindPositionForNewNode(int width, int height,
	// FreeRectChoiceHeuristic rectChoice, int *nodeIndex)
	Rect FindPositionForNewNode(int width, int height,
			FreeRectChoiceHeuristic rectChoice, MutableInt nodeIndex) {
		Rect bestNode = new Rect();
		// memset(&bestNode, 0, sizeof(Rect));

		int bestScore = Integer.MAX_VALUE;

		// / Try each free rectangle to find the best one for placement.
		for (int i = 0; i < freeRectangles.size(); ++i) {
			// If this is a perfect fit upright, choose it immediately.
			if (width == freeRectangles.get(i).width
					&& height == freeRectangles.get(i).height) {
				bestNode.x = freeRectangles.get(i).x;
				bestNode.y = freeRectangles.get(i).y;
				bestNode.width = width;
				bestNode.height = height;
				bestScore = Integer.MIN_VALUE;
				nodeIndex.set(i);
				// debug_assert(disjointRects.Disjoint(bestNode));
				break;
			}
			// If this is a perfect fit sideways, choose it.
			else if (height == freeRectangles.get(i).width
					&& width == freeRectangles.get(i).height) {
				bestNode.x = freeRectangles.get(i).x;
				bestNode.y = freeRectangles.get(i).y;
				bestNode.width = height;
				bestNode.height = width;
				bestScore = Integer.MIN_VALUE;
				nodeIndex.set(i);
				// debug_assert(disjointRects.Disjoint(bestNode));
				break;
			}
			// Does the rectangle fit upright?
			else if (width <= freeRectangles.get(i).width
					&& height <= freeRectangles.get(i).height) {
				int score = ScoreByHeuristic(width, height,
						freeRectangles.get(i), rectChoice);

				if (score < bestScore) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestScore = score;
					nodeIndex.set(i);
					// debug_assert(disjointRects.Disjoint(bestNode));
				}
			}
			// Does the rectangle fit sideways?
			else if (height <= freeRectangles.get(i).width
					&& width <= freeRectangles.get(i).height) {
				int score = ScoreByHeuristic(height, width,
						freeRectangles.get(i), rectChoice);

				if (score < bestScore) {
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestScore = score;
					nodeIndex.set(i);
					// debug_assert(disjointRects.Disjoint(bestNode));
				}
			}
		}
		return bestNode;
	}

	void SplitFreeRectByHeuristic(Rect freeRect, Rect placedRect,
			GuillotineSplitHeuristic method) {
		// Compute the lengths of the leftover area.
		int w = freeRect.width - placedRect.width;
		int h = freeRect.height - placedRect.height;

		// Placing placedRect into freeRect results in an L-shaped free area,
		// which must be split into
		// two disjoint rectangles. This can be achieved with by splitting the
		// L-shape using a single line.
		// We have two choices: horizontal or vertical.

		// Use the given heuristic to decide which choice to make.

		boolean splitHorizontal;
		switch (method) {
		case SplitShorterLeftoverAxis:
			// Split along the shorter leftover axis.
			splitHorizontal = (w <= h);
			break;
		case SplitLongerLeftoverAxis:
			// Split along the longer leftover axis.
			splitHorizontal = (w > h);
			break;
		case SplitMinimizeArea:
			// Maximize the larger area == minimize the smaller area.
			// Tries to make the single bigger rectangle.
			splitHorizontal = (placedRect.width * h > w * placedRect.height);
			break;
		case SplitMaximizeArea:
			// Maximize the smaller area == minimize the larger area.
			// Tries to make the rectangles more even-sized.
			splitHorizontal = (placedRect.width * h <= w * placedRect.height);
			break;
		case SplitShorterAxis:
			// Split along the shorter total axis.
			splitHorizontal = (freeRect.width <= freeRect.height);
			break;
		case SplitLongerAxis:
			// Split along the longer total axis.
			splitHorizontal = (freeRect.width > freeRect.height);
			break;
		default:
			splitHorizontal = true;
			assert (false);
		}

		// Perform the actual split.
		SplitFreeRectAlongAxis(freeRect, placedRect, splitHorizontal);
	}

	// / This function will add the two generated rectangles into the
	// freeRectangles array. The caller is expected to
	// / remove the original rectangle from the freeRectangles array after that.
	void SplitFreeRectAlongAxis(Rect freeRect, Rect placedRect,
			boolean splitHorizontal) {
		// Form the two new rectangles.
		Rect bottom = new Rect();
		bottom.x = freeRect.x;
		bottom.y = freeRect.y + placedRect.height;
		bottom.height = freeRect.height - placedRect.height;

		Rect right = new Rect();
		right.x = freeRect.x + placedRect.width;
		right.y = freeRect.y;
		right.width = freeRect.width - placedRect.width;

		if (splitHorizontal) {
			bottom.width = freeRect.width;
			right.height = placedRect.height;
		} else // Split vertically
		{
			bottom.width = placedRect.width;
			right.height = freeRect.height;
		}

		// Add the new rectangles into the free rectangle pool if they weren't
		// degenerate.
		if (bottom.width > 0 && bottom.height > 0)
			freeRectangles.add(bottom);
		if (right.width > 0 && right.height > 0)
			freeRectangles.add(right);

		// debug_assert(disjointRects.Disjoint(bottom));
		// debug_assert(disjointRects.Disjoint(right));
	}

	void MergeFreeList() {
		// #ifdef _DEBUG
		// DisjointRectCollection test;
		// for(int i = 0; i < freeRectangles.size(); ++i)
		// assert(test.Add(freeRectangles.get(i)) == true);
		// #endif

		// Do a Theta(n^2) loop to see if any pair of free rectangles could me
		// merged into one.
		// Note that we miss any opportunities to merge three rectangles into
		// one. (should call this function again to detect that)
		for (int i = 0; i < freeRectangles.size(); ++i)
			for (int j = i + 1; j < freeRectangles.size(); ++j) {
				if (freeRectangles.get(i).width == freeRectangles.get(j).width
						&& freeRectangles.get(i).x == freeRectangles.get(j).x) {
					if (freeRectangles.get(i).y == freeRectangles.get(j).y
							+ freeRectangles.get(j).height) {
						freeRectangles.get(i).y -= freeRectangles.get(j).height;
						freeRectangles.get(i).height += freeRectangles.get(j).height;
						freeRectangles.remove(freeRectangles.get(j));
						--j;
					} else if (freeRectangles.get(i).y
							+ freeRectangles.get(i).height == freeRectangles
								.get(j).y) {
						freeRectangles.get(i).height += freeRectangles.get(j).height;
						freeRectangles.remove(freeRectangles.get(j));
						--j;
					}
				} else if (freeRectangles.get(i).height == freeRectangles
						.get(j).height
						&& freeRectangles.get(i).y == freeRectangles.get(j).y) {
					if (freeRectangles.get(i).x == freeRectangles.get(j).x
							+ freeRectangles.get(j).width) {
						freeRectangles.get(i).x -= freeRectangles.get(j).width;
						freeRectangles.get(i).width += freeRectangles.get(j).width;
						freeRectangles.remove(freeRectangles.get(j));
						--j;
					} else if (freeRectangles.get(i).x
							+ freeRectangles.get(i).width == freeRectangles
								.get(j).x) {
						freeRectangles.get(i).width += freeRectangles.get(j).width;
						freeRectangles.remove(freeRectangles.get(j));
						--j;
					}
				}
			}

		// #ifdef _DEBUG
		// test.Clear();
		// for(int i = 0; i < freeRectangles.size(); ++i)
		// assert(test.Add(freeRectangles.get(i)) == true);
		// #endif
	}

	public static void main(String[] args) {
		if (args.length < 5 || args.length % 2 == 1) {
			out.printf("Usage: MaxRectsBinPackTest binWidth binHeight w_0 h_0 w_1 h_1 w_2 h_2 ... w_n h_n\n");
			out.printf(" where binWidth and binHeight define the size of the bin.\n");
			out.printf(" w_i is the width of the i'th rectangle to pack, and h_i the height.\n");
			out.printf("Example: GuillotineBinPack 256 256 30 20 50 20 10 80 90 20\n");
			return;
		}

		// using namespace rbp;

		// Create a bin to pack to, use the bin size from command line.
		// MaxRectsBinPack bin = new MaxRectsBinPack();
		GuillotineBinPack bin = new GuillotineBinPack();
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
			GuillotineBinPack.FreeRectChoiceHeuristic heuristic = GuillotineBinPack.FreeRectChoiceHeuristic.RectBestShortSideFit;
			GuillotineBinPack.GuillotineSplitHeuristic splitHeuristic = GuillotineSplitHeuristic.SplitLongerAxis;
			Rect packedRect = bin.Insert(rectWidth, rectHeight, true,
					heuristic, splitHeuristic);

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
