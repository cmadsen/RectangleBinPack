package dk.navicon.binpack;

import static java.lang.Integer.*;
import static java.lang.System.*;
import static javax.swing.SwingUtilities.*;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;

public class GuiTest {

	static int binWidth;
	static int binHeight;
	static List<Rect> rects = new ArrayList<Rect>();

	public static void addComponentsToPane(Container pane) {
		pane.setLayout(null);

		JButton b1 = new JButton("one");
		// JButton b2 = new JButton("two");
		// JButton b3 = new JButton("three");
		//
		// pane.add(b1);
		// pane.add(b2);
		// pane.add(b3);
		//
		Insets insets = pane.getInsets();
		// Dimension size = b1.getPreferredSize();
		// b1.setBounds(25 + insets.left, 5 + insets.top, size.width,
		// size.height);
		// size = b2.getPreferredSize();
		// b2.setBounds(55 + insets.left, 40 + insets.top, size.width,
		// size.height);
		// size = b3.getPreferredSize();
		// b3.setBounds(150 + insets.left, 15 + insets.top, size.width + 50,
		// size.height + 20);

		int i = 0;
		for (Rect r : rects) {
			JButton rb = new JButton();
			pane.add(rb);
			// rb.setBounds(r.x + insets.left, r.y + insets.top, r.width,
			// r.height);
			rb.setBounds(r.x, r.y, r.width, r.height);
			// to get rainbow, pastel colors
			Random random = new Random();
			final float hue = random.nextFloat();
			final float saturation = 0.9f;// 1.0 for brilliant, 0.0 for dull
			final float luminance = 1.0f; // 1.0 for brighter, 0.0 for black
			Color color = Color.getHSBColor(hue, saturation, luminance);
			rb.setBackground(color);
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {

		// Create and set up the window.
		JFrame frame = new JFrame("AbsoluteLayoutDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up the content pane.
		addComponentsToPane(frame.getContentPane());

		// Size and display the window.
		Insets insets = frame.getInsets();
		frame.setSize(binWidth + insets.left + insets.right + 20, binHeight
				+ insets.top + insets.bottom + 20);
		// frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {

		if (args.length < 5 || args.length % 2 == 1) {
			out.printf("Usage: MaxRectsBinPackTest binWidth binHeight w_0 h_0 w_1 h_1 w_2 h_2 ... w_n h_n\n");
			out.printf(" where binWidth and binHeight define the size of the bin.\n");
			out.printf(" w_i is the width of the i'th rectangle to pack, and h_i the height.\n");
			out.printf("Example: MaxRectsBinPackTest 256 256 30 20 50 20 10 80 90 20 \n");
			return;
		}

		// using namespace rbp;

		// Create a bin to pack to, use the bin size from command line.
		MaxRectsBinPack bin = new MaxRectsBinPack();
		binWidth = parseInt(args[0]);
		binHeight = parseInt(args[1]);
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
			// MaxRectsBinPack.FreeRectChoiceHeuristic heuristic =
			// MaxRectsBinPack.FreeRectChoiceHeuristic.RectBestShortSideFit;
			// MaxRectsBinPack.FreeRectChoiceHeuristic heuristic =
			// MaxRectsBinPack.FreeRectChoiceHeuristic.RectBottomLeftRule;
			MaxRectsBinPack.FreeRectChoiceHeuristic heuristic = MaxRectsBinPack.FreeRectChoiceHeuristic.RectBottomLeftRule;
			Rect packedRect = bin.Insert(rectWidth, rectHeight, heuristic);

			// Test success or failure.
			if (packedRect.height > 0) {
				out.printf(
						"Packed to (x,y)=(%d,%d), (w,h)=(%d,%d). Free space left: %.2f%%\n",
						packedRect.x, packedRect.y, packedRect.width,
						packedRect.height, 100.f - bin.Occupancy() * 100.f);
				rects.add(packedRect);
			} else
				out.printf("Failed! Could not find a proper position to pack this rectangle into. Skipping this one.\n");
		}
		out.printf("Done. All rectangles packed.\n");

		invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
