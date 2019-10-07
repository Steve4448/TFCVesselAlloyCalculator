package tfcvesselalloycalculator.ui;

import tfcvesselalloycalculator.vessel.VesselRecipe;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import static tfcvesselalloycalculator.TFCVesselAlloyCalculator.resourceHelper;

public final class OreTableEntry extends ImageIcon {

	private final VesselRecipe.Ore ore;
	private final String tooltip;
	private final VesselRecipe.Ore.SizeType sizeType;
	private final int column;
	private final int row;
	private Color backgroundColor = Color.GRAY;

	public OreTableEntry(VesselRecipe.Ore ore, VesselRecipe.Ore.SizeType sizeType, int column, int row) throws IOException {
		this.ore = ore;
		switch(sizeType) {
			case SMALL:
				tooltip = "Small " + ore.getName() + " (" + sizeType.getAmount() + " units)";
				break;
			case POOR:
				tooltip = "Poor " + ore.getName() + " (" + sizeType.getAmount() + " units)";
				break;
			case REGULAR:
				tooltip = "Regular " + ore.getName() + " (" + sizeType.getAmount() + " units)";
				break;
			case RICH:
				tooltip = "Rich " + ore.getName() + " (" + sizeType.getAmount() + " units)";
				break;
			default:
				tooltip = "Invalid size type for " + ore.getName();
				break;
		}
		this.sizeType = sizeType;
		this.column = column;
		this.row = row;
		setImage(getScaledImage(getTexturePathForSize(ore.getName(), sizeType), 32, 32));
	}

	public static String getBasePath() {
		return "ores/";
	}

	public static String getTexturePathForSize(String name, VesselRecipe.Ore.SizeType type) {
		switch(type) {
			case POOR:
				return getBasePath() + "Poor " + name + " Ore.png";
			case SMALL:
				return getBasePath() + name + " Small Ore.png";
			case REGULAR:
				return getBasePath() + name + " Ore.png";
			case RICH:
				return getBasePath() + "Rich " + name + " Ore.png";
		}
		return null;
	}

	public Image getScaledImage(String texPath, int w, int h) throws IOException, IllegalArgumentException {
		//System.out.println(texPath + " = " + getClass().getResource(texPath));
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.drawImage(ImageIO.read(resourceHelper.getResource(texPath)), 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}

	public VesselRecipe.Ore getOre() {
		return ore;
	}

	public String getTooltip() {
		return tooltip;
	}

	public VesselRecipe.Ore.SizeType getSizeType() {
		return sizeType;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
