package tfcalloycalculator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import tfcalloycalculator.TFCVesselAlloyCalculator.AlloyType;
import tfcalloycalculator.TFCVesselAlloyCalculator.AlloyTypeEntry;
import tfcalloycalculator.TFCVesselAlloyCalculator.BaseOreType;
import static tfcalloycalculator.TFCVesselAlloyCalculator.updateResultingString;

public class VesselContainer {

	public static final int MAX_STACK_SIZE = 16;

	private static int[] currentContentsCount = new int[4];
	private static int[] currentContentsAmounts = new int[4];
	public static OreEntry[] currentContents = new OreEntry[4];
	public static JLabel[] guiComponents = new JLabel[4];
	
	/**
	 * Searches for a slot in the vessel and adds/fills the ore to it.
	 * @param toAdd the ore to be added.
	 * @param fillStack If the slot should be filled to <code>MAX_STACK_SIZE</code>
	 * @return true if the ore was added.
	 */
	public static boolean add(OreEntry toAdd, boolean fillStack) {
		for (int i = 0; i < currentContents.length; i++) {
			if (currentContents[i] == toAdd) {
				if (currentContentsCount[i] < MAX_STACK_SIZE) {
					int amt = 1;
					if(fillStack) {
						amt = MAX_STACK_SIZE - currentContentsCount[i];
					}
					currentContentsCount[i] += amt;
					currentContentsAmounts[i] += amt * toAdd.type.amount;
					guiComponents[i].setText("" + currentContentsCount[i]);
					updateResultingString();
					return true;
				}
			}
		}
		for (int i = 0; i < currentContents.length; i++) {
			if (currentContents[i] == null) {
				currentContents[i] = toAdd;
				currentContentsCount[i] = fillStack ? MAX_STACK_SIZE : 1;
				currentContentsAmounts[i] = (fillStack ? MAX_STACK_SIZE : 1) * toAdd.type.amount;
				guiComponents[i].setIcon(toAdd);
				guiComponents[i].setText("" + currentContentsCount[i]);
				toAdd.color = Color.WHITE;
				((DefaultTableModel) TFCVesselAlloyCalculator.oreSelectionTable.getModel()).fireTableCellUpdated(currentContents[i].row, currentContents[i].column);
				updateResultingString();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Searches for an ore within the vessel and removes it.
	 * @param toRemove the ore to be removed.
	 * @param removeAll if all of the ore within the slot found should be removed.
	 * @return true if the ore was removed.
	 */
	public static boolean remove(OreEntry toRemove, boolean removeAll) {
		for (int i = currentContents.length - 1; i >= 0; i--) {
			if (currentContents[i] == toRemove) {
				remove(i, removeAll);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes an ore from the vessel at a specific slot.
	 * @param vesselSlotId the slot to remove the ore from.
	 * @param removeAll if all the ore within that slot should be removed.
	 */
	public static void remove(int vesselSlotId, boolean removeAll) {
		if (removeAll || --currentContentsCount[vesselSlotId] <= 0) {
			completelyClear(vesselSlotId);
		} else {
			currentContentsAmounts[vesselSlotId] -= currentContents[vesselSlotId].type.amount;
			guiComponents[vesselSlotId].setText("" + currentContentsCount[vesselSlotId]);
		}
		updateResultingString();
	}

	/**
	 * @return The lists the inputs, outputs, and closest matches of the current vessel contents.
	 */
	public static String getResultingString() {
		StringBuilder resultText = new StringBuilder();
		resultText.append("<html><p style=\"font-weight: bold;\">Inputs:</p>");
		HashMap<BaseOreType, Integer> totalForOreEntry = new HashMap<>();
		HashMap<BaseOreType, Double> percentForOreEntry = new HashMap<>();
		AlloyType currentOutputResult = null;
		ArrayList<AlloyType> currentPossibleResults = new ArrayList<>();

		int totalOre = 0;
		for (int i = 0; i < currentContents.length; i++) {
			if (currentContents[i] != null) {
				totalOre += currentContentsAmounts[i];
				totalForOreEntry.put(currentContents[i].baseType, totalForOreEntry.get(currentContents[i].baseType) != null ? totalForOreEntry.get(currentContents[i].baseType) + currentContentsAmounts[i] : currentContentsAmounts[i]);
			}
		}
		if(totalOre == 0) {
			resultText.append("&emsp;No ores selected.<br />");
		} else {
			for (Map.Entry<BaseOreType, Integer> e : totalForOreEntry.entrySet()) {
				BaseOreType baseOreEntry = e.getKey();
				Integer value = e.getValue();
				double percentOfOre = (double) value / (double) totalOre * 100D;
				resultText.append("&emsp;").append(baseOreEntry.presentableName).append(" x ").append(value).append(" (").append(String.format("%.2f", percentOfOre)).append("%)");
				resultText.append("<br />");
				percentForOreEntry.put(baseOreEntry, percentOfOre);
			}
		}
		resultText.append("<p style=\"font-weight: bold;\">Outputs:</p>");
		A:
		for (AlloyType alloyType : AlloyType.values()) {
			for (BaseOreType baseOreType : totalForOreEntry.keySet()) {
				boolean found = false;
				for (AlloyTypeEntry alloyOreRequirements : alloyType.entries) {
					if (baseOreType == alloyOreRequirements.baseType) {
						found = true;
						break;
					}
				}
				if (!found) {
					continue A;
				}
			}
			currentPossibleResults.add(alloyType);
			if (currentOutputResult == null) {
				for (AlloyTypeEntry alloyOreRequirements : alloyType.entries) {
					if (!percentForOreEntry.containsKey(alloyOreRequirements.baseType)) {
						continue A;
					}
					double percentForOre = percentForOreEntry.get(alloyOreRequirements.baseType);
					if (percentForOre < alloyOreRequirements.requiredPercentMin || percentForOre > alloyOreRequirements.requiredPercentMax) {
						continue A;
					}
				}
				currentOutputResult = alloyType;
			}
		}
		if(totalOre == 0) {
			resultText.append("&emsp;Nothing.<br />");
		} else {
			
			resultText.append(currentOutputResult == null ? "&emsp;<font color=\"#FF0000\">Unknown Metal</font>!" : "&emsp;" + currentOutputResult.outputName + " x " + totalOre).append("<br />");
		}
		resultText.append("<br /><p style=\"font-weight: bold;\">Closest Alloy Results:</p>");
		for (AlloyType alloyType : currentPossibleResults) {
			if (alloyType.entries.length == 1) {
				continue;
			}
			resultText.append("&emsp;").append(alloyType.outputName).append(": ");
			for (AlloyTypeEntry alloyTypeEntry : alloyType.entries) {
				resultText.append("<br />");
				resultText.append("&emsp;&emsp;");
				boolean hasOre = percentForOreEntry.containsKey(alloyTypeEntry.baseType);
				if (!hasOre) {
					resultText.append("<font color=\"#FF0000\">");
				}
				resultText.append(alloyTypeEntry.baseType.presentableName).append(" ");
				if (!hasOre) {
					resultText.append("</font>");
				}
				if (hasOre && percentForOreEntry.get(alloyTypeEntry.baseType) < alloyTypeEntry.requiredPercentMin) {
					resultText.append("<font color=\"#FF0000\">");
				}
				resultText.append(alloyTypeEntry.requiredPercentMin).append("%");
				if (hasOre && percentForOreEntry.get(alloyTypeEntry.baseType) < alloyTypeEntry.requiredPercentMin) {
					resultText.append("</font>");
				}
				resultText.append(" - ");
				if (hasOre && percentForOreEntry.get(alloyTypeEntry.baseType) > alloyTypeEntry.requiredPercentMax) {
					resultText.append("<font color=\"#FF0000\">");
				}
				resultText.append(alloyTypeEntry.requiredPercentMax).append("%");
				if (hasOre && percentForOreEntry.get(alloyTypeEntry.baseType) > alloyTypeEntry.requiredPercentMax) {
					resultText.append("</font>");
				}
			}
			resultText.append("<br />");
		}
		resultText.append("</html>");
		return resultText.toString();
	}

	/**
	 * Sets the vessel slot to be completely empty and updates the UI.
	 * @param vesselSlotId the slot to be emptied.
	 */
	private static void completelyClear(int vesselSlotId) {
		if (currentContents[vesselSlotId] != null) {
			boolean stillSameOreInVessel = false;
			for (int i2 = 0; i2 < currentContents.length; i2++) {
				if (i2 == vesselSlotId) {
					continue;
				}
				if (currentContents[i2] == currentContents[vesselSlotId]) {
					stillSameOreInVessel = true;
					break;
				}
			}
			if (!stillSameOreInVessel) {
				currentContents[vesselSlotId].color = Color.GRAY;
				TFCVesselAlloyCalculator.oreSelectionTable.repaint();
				((DefaultTableModel) TFCVesselAlloyCalculator.oreSelectionTable.getModel()).fireTableCellUpdated(currentContents[vesselSlotId].row, currentContents[vesselSlotId].column);
			}
			currentContentsAmounts[vesselSlotId] = 0;
			currentContents[vesselSlotId] = null;
			guiComponents[vesselSlotId].setIcon(null);
			guiComponents[vesselSlotId].setText("");
		}
	}
}
