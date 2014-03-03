package dk.navicon.binpack;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMasoneryLayout {

	private static final Logger log = LoggerFactory
			.getLogger(TestMasoneryLayout.class);

	public static void main(final String[] args) throws Exception {
		final JFrame frame = new JFrame("Masonery Layout");

		// final JPanel panel = new JPanel();
		final ScrollablePanel panel = new ScrollablePanel();

		panel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
		panel.setScrollableBlockIncrement(ScrollablePanel.VERTICAL,
				ScrollablePanel.IncrementType.PERCENT, 200);

		panel.setPreferredSize(new Dimension(300, 500));
		panel.setLayout(new MasoneryLayout());

		for (File childDir : new File("/home/com/VesselImages/246167000")
				.listFiles()) {
			if (childDir.isFile()
					&& childDir.getAbsoluteFile().toString().endsWith(".png")) {
				BufferedImage image = ImageIO.read(childDir);
				ScalablePane p = new ScalablePane(createResizedCopy(image,
						.15f, true));
				// JButton bb = new JButton();
				// bb.setIcon(new ImageIcon(image));
				// bb.setPreferredSize(new Dimension(image.getWidth(), image
				// .getHeight()));
				panel.add(p);
			}
		}

		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 500);
		frame.setVisible(true);
	}

	static BufferedImage createResizedCopy(Image originalImage, float scaled,
			boolean preserveAlpha) {
		System.out.println("resizing...");
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(
				(int) (originalImage.getWidth(null) * scaled),
				(int) (originalImage.getWidth(null) * scaled), imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(originalImage, 0, 0,
				(int) (originalImage.getWidth(null) * scaled),
				(int) (originalImage.getWidth(null) * scaled), null);
		g.dispose();
		return scaledBI;
	}

}
