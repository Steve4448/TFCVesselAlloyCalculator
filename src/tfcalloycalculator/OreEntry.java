package tfcalloycalculator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import tfcalloycalculator.TFCVesselAlloyCalculator.BaseOreType;

public final class OreEntry extends ImageIcon {

	public enum Type {
		SMALL(10),
		POOR(15),
		REGULAR(25),
		RICH(35);
		int amount;

		Type(int amount) {
			this.amount = amount;
		}
	}

	public final BaseOreType baseType;
	public final Type type;
	public Color color = Color.GRAY;

	public OreEntry(BaseOreType baseType, Type type) throws IOException {
		this.baseType = baseType;
		this.type = type;
		setImage(getScaledImage(getTexturePathForType(baseType.presentableName, type), 32, 32));
	}

	public static String getBasePath() {
		return  "/ores/";
	}

	public static String getTexturePathForType(String name, Type type) {
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

	public static int getAmountForType(Type type) {
		return type.amount;
	}

	public Image getScaledImage(String texPath, int w, int h) throws IOException, IllegalArgumentException {
		System.out.println(texPath + " = " + getClass().getResource(texPath));
		
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.drawImage(ImageIO.read(getClass().getResource(texPath)), 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}

	public Color getBackgroundColor() {
		return color;
	}
	
	public String getTooltip() {
		switch(type) {
			case SMALL:
				return "Small " + baseType.presentableName + " (" + type.amount + " units)";
			case POOR:
				return "Poor " + baseType.presentableName + " (" + type.amount + " units)";
			case REGULAR:
				return "Regular " + baseType.presentableName + " (" + type.amount + " units)";
			case RICH:
				return "Rich " + baseType.presentableName + " (" + type.amount + " units)";
		}
		return baseType.presentableName;
	}
}
