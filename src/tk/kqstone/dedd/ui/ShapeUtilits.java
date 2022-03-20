package tk.kqstone.dedd.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public final class ShapeUtilits {

	private static Area genleftinArc(int width, int height, int radius) {
		Area rect = new Area(new Rectangle2D.Float(0, height - radius, radius, radius));
		Area circ = new Area(new Ellipse2D.Float(-radius, height - radius * 2, radius * 2, radius * 2));
		rect.subtract(circ);
		return rect;

	}

	private static Area genleftoutArc(int width, int height, int radius) {
		Area arc = new Area(new Arc2D.Float(radius, 0, radius * 2, radius * 2, 90, 180, Arc2D.PIE));
		return arc;
	}

	private static Area genrightinArc(int width, int height, int radius) {
		Area rect = new Area(new Rectangle2D.Float(width - radius, height - radius, radius, radius));
		Area circ = new Area(new Ellipse2D.Float(width - radius, height - radius * 2, radius * 2, radius * 2));
		rect.subtract(circ);
		return rect;

	}

	private static Area genrightoutArc(int width, int height, int radius) {
		Area arc = new Area(new Arc2D.Float(width - 3 * radius, 0, radius * 2, radius * 2, 0, 90, Arc2D.PIE));
		return arc;
	}

	public static Area genTabArea(int width, int height, int radius) {
		Area upperRec = new Area(new Rectangle2D.Float(2 * radius, 0, width - 4 * radius, radius));
		Area lowerRec = new Area(new Rectangle2D.Float(radius, radius, width - 2 * radius, height - radius));

		upperRec.add(lowerRec);
		upperRec.add(genleftinArc(width, height, radius));
		upperRec.add(genleftoutArc(width, height, radius));
		upperRec.add(genrightinArc(width, height, radius));
		upperRec.add(genrightoutArc(width, height, radius));
		return upperRec;
	}

}
