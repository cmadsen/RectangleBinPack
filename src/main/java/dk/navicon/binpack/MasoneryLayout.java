package dk.navicon.binpack;

import static java.lang.System.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import dk.navicon.binpack.MaxRectsBinPack.FreeRectChoiceHeuristic;

public class MasoneryLayout implements LayoutManager, Serializable {

	/** For serialization. */
	private static final long serialVersionUID = -7582156799248315534L;

	/** The minimum width. */
	private int minWidth = 0;

	/** The minimum height. */
	private int minHeight = 0;

	/** The maximum component width. */
	private int maxCompWidth = 0;

	/** The maximum component height. */
	private int maxCompHeight = 0;

	/** The preferred width. */
	private int preferredWidth = 0;

	/** The preferred height. */
	private int preferredHeight = 0;

	/** Size unknown flag. */
	private boolean sizeUnknown = true;

	/**
	 * Constructs this layout manager with default properties.
	 */
	public MasoneryLayout() {
	}

	/**
	 * Not used.
	 * 
	 * @param comp
	 *            the component.
	 */
	public void addLayoutComponent(final Component comp) {
		// not used
	}

	/**
	 * Not used.
	 * 
	 * @param comp
	 *            the component.
	 */
	public void removeLayoutComponent(final Component comp) {
		// not used
	}

	/**
	 * Not used.
	 * 
	 * @param name
	 *            the component name.
	 * @param comp
	 *            the component.
	 */
	public void addLayoutComponent(final String name, final Component comp) {
		// not used
	}

	/**
	 * Not used.
	 * 
	 * @param name
	 *            the component name.
	 * @param comp
	 *            the component.
	 */
	public void removeLayoutComponent(final String name, final Component comp) {
		// not used
	}

	/**
	 * Sets the sizes attribute of the RadialLayout object.
	 * 
	 * @param parent
	 *            the parent.
	 * 
	 * @see LayoutManager
	 */
	private void setSizes(final Container parent) {
		final int nComps = parent.getComponentCount();
		// Reset preferred/minimum width and height.
		this.preferredWidth = 0;
		this.preferredHeight = 0;
		this.minWidth = 0;
		this.minHeight = 0;
		for (int i = 0; i < nComps; i++) {
			final Component c = parent.getComponent(i);
			if (c.isVisible()) {
				final Dimension d = c.getPreferredSize();
				if (this.maxCompWidth < d.width) {
					this.maxCompWidth = d.width;
				}
				if (this.maxCompHeight < d.height) {
					this.maxCompHeight = d.height;
				}
				this.preferredWidth += d.width;
				this.preferredHeight += d.height;
			}
		}
		this.preferredWidth = this.preferredWidth / 2;
		this.preferredHeight = this.preferredHeight / 2;
		this.minWidth = this.preferredWidth;
		this.minHeight = this.preferredHeight;
	}

	/**
	 * Returns the preferred size.
	 * 
	 * @param parent
	 *            the parent.
	 * 
	 * @return The preferred size.
	 * @see LayoutManager
	 */
	public Dimension preferredLayoutSize(final Container parent) {
		final Dimension dim = new Dimension(0, 0);
		setSizes(parent);

		// Always add the container's insets!
		final Insets insets = parent.getInsets();
		dim.width = this.preferredWidth + insets.left + insets.right;
		dim.height = this.preferredHeight + insets.top + insets.bottom;

		this.sizeUnknown = false;
		return dim;
	}

	/**
	 * Returns the minimum size.
	 * 
	 * @param parent
	 *            the parent.
	 * 
	 * @return The minimum size.
	 * @see LayoutManager
	 */
	public Dimension minimumLayoutSize(final Container parent) {
		final Dimension dim = new Dimension(0, 0);

		// Always add the container's insets!
		final Insets insets = parent.getInsets();
		dim.width = this.minWidth + insets.left + insets.right;
		dim.height = this.minHeight + insets.top + insets.bottom;

		this.sizeUnknown = false;
		return dim;
	}

	/**
	 * This is called when the panel is first displayed, and every time its size
	 * changes. Note: You CAN'T assume preferredLayoutSize or minimumLayoutSize
	 * will be called -- in the case of applets, at least, they probably won't
	 * be.
	 * 
	 * @param parent
	 *            the parent.
	 * @see LayoutManager
	 */
	public void layoutContainer(final Container parent) {
		final Insets insets = parent.getInsets();
		final int maxWidth = parent.getSize().width
				- (insets.left + insets.right);
		final int maxHeight = parent.getSize().height
				- (insets.top + insets.bottom);
		final int nComps = parent.getComponentCount();

		// Go through the components' sizes, if neither preferredLayoutSize nor
		// minimumLayoutSize has been called.
		if (this.sizeUnknown) {
			setSizes(parent);
		}

		System.out.println("maxWidth=" + maxWidth + " maxHeight=" + maxHeight);
		MaxRectsBinPack bin = new MaxRectsBinPack(maxWidth, maxHeight);
		// GuillotineBinPack bin = new GuillotineBinPack(maxWidth, maxHeight);
		for (int i = 0; i < nComps; i++) {
			final Component c = parent.getComponent(i);
			if (c.isVisible()) {
				final Dimension d = c.getPreferredSize();
				// MaxRectsBinPack.FreeRectChoiceHeuristic heuristic =
				// MaxRectsBinPack.FreeRectChoiceHeuristic.RectBestLongSideFit;
				FreeRectChoiceHeuristic heuristic = FreeRectChoiceHeuristic.RectBestAreaFit;
				// MaxRectsBinPack.FreeRectChoiceHeuristic heuristic =
				// FreeRectChoiceHeuristic.RectContactPointRule;
				Rect packedRect = bin.Insert(d.width, d.height, heuristic);

				// GuillotineBinPack.FreeRectChoiceHeuristic heuristic =
				// GuillotineBinPack.FreeRectChoiceHeuristic.RectBestShortSideFit;
				// GuillotineBinPack.GuillotineSplitHeuristic splitHeuristic =
				// GuillotineSplitHeuristic.SplitLongerAxis;
				// Rect packedRect = bin.Insert(d.width, d.height, true,
				// heuristic, splitHeuristic);

				if (packedRect.height > 0) {
					out.printf(
							"Packed to (x,y)=(%d,%d), (w,h)=(%d,%d). Free space left: %.2f%%\n",
							packedRect.x, packedRect.y, packedRect.width,
							packedRect.height, 100.f - bin.Occupancy() * 100.f);

				} else {
					out.printf("Failed! Could not find a proper position to pack this rectangle into. Skipping this one.\n");

				}
				c.setBounds(packedRect.x, packedRect.y, packedRect.width,
						packedRect.height);
			}
		}
	}

	/**
	 * Returns the class name.
	 * 
	 * @return The class name.
	 */
	public String toString() {
		return getClass().getName();
	}

	/**
	 * Run a demonstration.
	 * 
	 * @param args
	 *            ignored.
	 * 
	 * @throws Exception
	 *             when an error occurs.
	 */
	public static void main(final String[] args) throws Exception {
		final JFrame frame = new JFrame("Masonery Layout");

		final JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, 500));
		panel.setLayout(new MasoneryLayout());

		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			int w = random.nextInt(80) + 20;
			int h = random.nextInt(80) + 20;
			JButton bb = new JButton("" + i);
			bb.setToolTipText("" + i);
			bb.setPreferredSize(new Dimension(w, h));
			// Random random = new Random();
			final float hue = random.nextFloat();
			final float saturation = 0.9f;// 1.0 for brilliant, 0.0 for dull
			final float luminance = 1.0f; // 1.0 for brighter, 0.0 for black
			Color color = Color.getHSBColor(hue, saturation, luminance);
			bb.setBackground(color);
			panel.add(bb);
		}

		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 500);
		frame.setVisible(true);
	}

}