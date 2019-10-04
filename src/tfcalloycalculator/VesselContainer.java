package tfcalloycalculator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import tfcalloycalculator.TFCVesselAlloyCalculator.AlloyType;
import tfcalloycalculator.TFCVesselAlloyCalculator.AlloyTypeEntry;
import tfcalloycalculator.TFCVesselAlloyCalculator.BaseOreType;

public class VesselContainer {

	public static final int MAX_STACK_SIZE = 16;

	public static OreEntry[] currentContents = new OreEntry[4];
	public static int[] currentContentsCount = new int[4];
	public static int[] currentContentsAmounts = new int[4];
	public static JLabel[] guiComponents = new JLabel[4];

	public static boolean add(OreEntry toAdd) {
		for(int i = 0; i < currentContents.length; i++) {
			if(currentContents[i] == toAdd) {
				if(currentContentsCount[i] < MAX_STACK_SIZE) {
					currentContentsCount[i]++;
					currentContentsAmounts[i] += toAdd.type.amount;
					guiComponents[i].setText("" + currentContentsCount[i]);
					return true;
				}
			}
		}
		for(int i = 0; i < currentContents.length; i++) {
			if(currentContents[i] == null) {
				currentContents[i] = toAdd;
				currentContentsCount[i] = 1;
				currentContentsAmounts[i] = toAdd.type.amount;
				guiComponents[i].setIcon(toAdd);
				guiComponents[i].setText("" + currentContentsCount[i]);
				return true;
			}
		}
		return false;
	}

	public static boolean remove(OreEntry toRemove) {
		for(int i = currentContents.length - 1; i >= 0; i--) {
			if(currentContents[i] == toRemove) {
				if(--currentContentsCount[i] <= 0) {
					completelyClear(i);
					for(OreEntry ore : currentContents) {
						if(ore == toRemove) {
							return false;
						}
					}
					return true;
				} else {
					currentContentsAmounts[i] -= toRemove.type.amount;
					guiComponents[i].setText("" + currentContentsCount[i]);
				}
				return false;
			}
		}
		return false;
	}

	public static String getResultingString() {
		StringBuilder lukeHatesTheseSoILikeToUseThemNow = new StringBuilder();
		lukeHatesTheseSoILikeToUseThemNow.append("<html><p style=\"font-weight: bold;\">Inputs:</p>");
		HashMap<BaseOreType, Integer> totalForOreEntry = new HashMap<>();
		HashMap<BaseOreType, Double> percentForOreEntry = new HashMap<>();
		AlloyType currentOutputResult = null;
		ArrayList<AlloyType> currentPossibleResults = new ArrayList<>();
		
		int totalOre = 0;
		for(int i = 0; i < currentContents.length; i++) {
			if(currentContents[i] != null) {
				totalOre += currentContentsAmounts[i];
				totalForOreEntry.put(currentContents[i].baseType, totalForOreEntry.get(currentContents[i].baseType) != null ? totalForOreEntry.get(currentContents[i].baseType) + currentContentsAmounts[i] : currentContentsAmounts[i]);
			}
		}
		for(Map.Entry<BaseOreType, Integer> e : totalForOreEntry.entrySet()) {
			BaseOreType baseOreEntry = e.getKey();
			Integer value = e.getValue();
			double percentOfOre = (double)value / (double)totalOre * 100D;
			lukeHatesTheseSoILikeToUseThemNow.append("&emsp;").append(baseOreEntry.presentableName).append(" x ").append(value).append(" (").append(String.format("%.2f", percentOfOre)).append("%)");
			lukeHatesTheseSoILikeToUseThemNow.append("<br />");
			percentForOreEntry.put(baseOreEntry, percentOfOre);
		}
		lukeHatesTheseSoILikeToUseThemNow.append("<p style=\"font-weight: bold;\">Outputs:</p>");
		A: for(AlloyType alloyType : AlloyType.values()) {
			for(BaseOreType baseOreType : totalForOreEntry.keySet()) {
				boolean found = false;
				for(AlloyTypeEntry alloyOreRequirements : alloyType.entries) {
					if(baseOreType == alloyOreRequirements.baseType) {
						found = true;
						break;
					}
				}
				if(!found)
					continue A;
			}
			currentPossibleResults.add(alloyType);
			if(currentOutputResult == null) {
				for(AlloyTypeEntry alloyOreRequirements : alloyType.entries) {
					if(!percentForOreEntry.containsKey(alloyOreRequirements.baseType)) {
						continue A;
					}
					double percentForOre = percentForOreEntry.get(alloyOreRequirements.baseType);
					if(percentForOre < alloyOreRequirements.requiredPercentMin || percentForOre > alloyOreRequirements.requiredPercentMax) {
						continue A;
					}
				}
				currentOutputResult = alloyType;
			}
		}
		
		lukeHatesTheseSoILikeToUseThemNow.append(currentOutputResult == null ? "&emsp;Unknown Metal!" : "&emsp;" + currentOutputResult.outputName + " x " + totalOre).append("<br />");
		lukeHatesTheseSoILikeToUseThemNow.append("<br /><p style=\"font-weight: bold;\">Closest Alloy Results:</p>");
		for(AlloyType alloyType : currentPossibleResults) {
			if(alloyType.entries.length == 1)
				continue;
			lukeHatesTheseSoILikeToUseThemNow.append("&emsp;").append(alloyType.outputName).append(": ");
			for(AlloyTypeEntry alloyTypeEntry : alloyType.entries) {
				lukeHatesTheseSoILikeToUseThemNow.append("<br />");
				lukeHatesTheseSoILikeToUseThemNow.append("&emsp;&emsp;");
				boolean hasOre = percentForOreEntry.containsKey(alloyTypeEntry.baseType);
				if(!hasOre)
					lukeHatesTheseSoILikeToUseThemNow.append("<font color=\"#FF0000\">");
				lukeHatesTheseSoILikeToUseThemNow.append(alloyTypeEntry.baseType.presentableName).append(" ");
				if(!hasOre)
					lukeHatesTheseSoILikeToUseThemNow.append("</font>");
				if(hasOre && percentForOreEntry.get(alloyTypeEntry.baseType) < alloyTypeEntry.requiredPercentMin)
					lukeHatesTheseSoILikeToUseThemNow.append("<font color=\"#FF0000\">");
				lukeHatesTheseSoILikeToUseThemNow.append(alloyTypeEntry.requiredPercentMin).append("%");
				if(hasOre && percentForOreEntry.get(alloyTypeEntry.baseType) < alloyTypeEntry.requiredPercentMin)
					lukeHatesTheseSoILikeToUseThemNow.append("</font>");
				lukeHatesTheseSoILikeToUseThemNow.append(" - ");
				if(hasOre && percentForOreEntry.get(alloyTypeEntry.baseType) > alloyTypeEntry.requiredPercentMax)
					lukeHatesTheseSoILikeToUseThemNow.append("<font color=\"#FF0000\">");
				lukeHatesTheseSoILikeToUseThemNow.append(alloyTypeEntry.requiredPercentMax).append("%");
				if(hasOre && percentForOreEntry.get(alloyTypeEntry.baseType) > alloyTypeEntry.requiredPercentMax)
					lukeHatesTheseSoILikeToUseThemNow.append("</font>");
			}
			lukeHatesTheseSoILikeToUseThemNow.append("<br />");
		}
		lukeHatesTheseSoILikeToUseThemNow.append("</html>");
		return lukeHatesTheseSoILikeToUseThemNow.toString();
	}

	public static void completelyClear(int i) {
		if(currentContents[i] != null) {
			boolean stillHas = false;
			for(int i2 = 0; i2 < currentContents.length; i2++) {
				if(i2 == i)
					continue;
				if(currentContents[i2] == currentContents[i]) {
					stillHas = true;
					break;
				}
			}
			if(!stillHas) {
				currentContents[i].color = Color.GRAY;
				TFCVesselAlloyCalculator.oreSelectionTable.repaint();
			}
			currentContentsAmounts[i] = 0;
			currentContents[i] = null;
			guiComponents[i].setIcon(null);
			guiComponents[i].setText("");
			TFCVesselAlloyCalculator.updateResultingString();
		}
	}
}
