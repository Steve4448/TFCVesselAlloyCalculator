package tfcvesselalloycalculator.vessel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import tfcvesselalloycalculator.TFCVesselAlloyCalculator;
import tfcvesselalloycalculator.ui.OreTableEntry;
import static tfcvesselalloycalculator.TFCVesselAlloyCalculator.updateResultingString;
import static tfcvesselalloycalculator.vessel.VesselSlot.MAX_STACK_SIZE;

public class VesselContainer {
	public final VesselSlot[] slots;
	public VesselContainer(VesselSlot[] slots) {
		this.slots = slots;
	}
	
	/**
	 * Searches for a slot in the vessel and adds/fills the ore to it.
	 * @param oreToAdd the ore to be added.
	 * @param fillStack If the slot should be filled to <code>MAX_STACK_SIZE</code>
	 * @return true if the ore was added.
	 */
	public boolean add(OreTableEntry oreToAdd, boolean fillStack) {
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].getOreType() == oreToAdd) {
				if (slots[i].getOreCount() < MAX_STACK_SIZE) {
					int amt = 1;
					if(fillStack) {
						amt = MAX_STACK_SIZE - slots[i].getOreCount();
					}
					slots[i].setOreCount(slots[i].getOreCount() + amt);
					updateResultingString();
					return true;
				}
			}
		}
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].getOreType() == null) {
				slots[i].set(oreToAdd, fillStack ? MAX_STACK_SIZE : 1);
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
	public boolean remove(OreTableEntry toRemove, boolean removeAll) {
		for (int i = slots.length - 1; i >= 0; i--) {
			if (slots[i].getOreType() == toRemove) {
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
	public void remove(int vesselSlotId, boolean removeAll) {
		if (removeAll || slots[vesselSlotId].getOreCount() - 1 <= 0) {
			boolean stillSameOreInVessel = false;
			for (int i2 = 0; i2 < slots.length; i2++) {
				if (i2 == vesselSlotId) {
					continue;
				}
				if (slots[i2].getOreType() == slots[vesselSlotId].getOreType()) {
					stillSameOreInVessel = true;
					break;
				}
			}
			slots[vesselSlotId].setOreCount(0, !stillSameOreInVessel);
		} else {
			slots[vesselSlotId].setOreCount(slots[vesselSlotId].getOreCount()-1);
		}
		updateResultingString();
	}

	/**
	 * @return The lists the inputs, outputs, and closest matches of the current vessel contents.
	 */
	public String getResultingString() {
		StringBuilder resultText = new StringBuilder();
		resultText.append("<html><p style=\"font-weight: bold;\">Inputs:</p>");
		HashMap<String, Integer> totalForOreType = new HashMap<>();
		HashMap<String, Double> percentForOreType = new HashMap<>();
		VesselRecipe currentOutputResult = null;
		ArrayList<VesselRecipe> currentPossibleResults = new ArrayList<>();

		int totalOre = 0;
		for(int i = 0; i < slots.length; i++) {
			if (slots[i].getOreCount() > 0) {
				int amt = slots[i].getOreCount() * slots[i].getOreType().getSizeType().getAmount();
				totalOre += amt;
				if(totalForOreType.containsKey(slots[i].getOreType().getOre().getName())) {
					totalForOreType.put(slots[i].getOreType().getOre().getName(), totalForOreType.get(slots[i].getOreType().getOre().getName()) + amt);
				} else {
					totalForOreType.put(slots[i].getOreType().getOre().getName(), amt);
				}
			}
		}
		if(totalOre == 0) {
			resultText.append("&emsp;No ores selected.<br />");
		} else {
			for (Map.Entry<String, Integer> e : totalForOreType.entrySet()) {
				String oreTypeEntry = e.getKey();
				Integer value = e.getValue();
				double percentOfOre = (double) value / (double) totalOre * 100D;
				resultText.append("&emsp;").append(oreTypeEntry).append(" x ").append(value).append(" (").append(String.format("%.2f", percentOfOre)).append("%)");
				resultText.append("<br />");
				percentForOreType.put(oreTypeEntry, percentOfOre);
			}
		}
		resultText.append("<p style=\"font-weight: bold;\">Outputs:</p>");
		A:
		for(VesselRecipe recipe : TFCVesselAlloyCalculator.settings.recipes) {
			for (String ore : totalForOreType.keySet()) {
				boolean found = false;
				for (VesselRecipe.Ingredient ingredient : recipe.getIngredients()) {
					if (ore.equals(ingredient.requiredOre)) {
						found = true;
						break;
					}
				}
				if (!found) {
					continue A;
				}
			}
			currentPossibleResults.add(recipe);
			if (currentOutputResult == null) {
				for (VesselRecipe.Ingredient ingredient : recipe.getIngredients()) {
					if (!percentForOreType.containsKey(ingredient.requiredOre)) {
						continue A;
					}
					double percentForOre = percentForOreType.get(ingredient.requiredOre);
					if (percentForOre < ingredient.requiredPercentMin || percentForOre > ingredient.requiredPercentMax) {
						continue A;
					}
				}
				currentOutputResult = recipe;
			}
		}
		if(totalOre == 0) {
			resultText.append("&emsp;Nothing.<br />");
		} else {
			resultText.append(currentOutputResult == null ? "&emsp;<font color=\"#FF0000\">Unknown Metal</font>!" : "&emsp;" + currentOutputResult.getName() + " x " + totalOre).append("<br />");
		}
		resultText.append("<br /><p style=\"font-weight: bold;\">Closest Alloy Results:</p>");
		for (VesselRecipe recipe : currentPossibleResults) {
			if (recipe.getIngredients().length == 1) {
				continue;
			}
			resultText.append("&emsp;").append(recipe.getName()).append(": ");
			for (VesselRecipe.Ingredient ingredient : recipe.getIngredients()) {
				resultText.append("<br />");
				resultText.append("&emsp;&emsp;");
				boolean hasOre = percentForOreType.containsKey(ingredient.requiredOre);
				double currentPercentForOre = !hasOre ? 0 : percentForOreType.get(ingredient.requiredOre);
				if (!hasOre) {
					resultText.append("<font color=\"#FF0000\">");
				}
				resultText.append(ingredient.requiredOre).append(" ");
				if (!hasOre) {
					resultText.append("</font>");
				}
				if (hasOre && currentPercentForOre < ingredient.requiredPercentMin) {
					resultText.append("<font color=\"#FF0000\">");
				}
				resultText.append(ingredient.requiredPercentMin).append("%");
				if (hasOre && currentPercentForOre < ingredient.requiredPercentMin) {
					resultText.append("</font>");
				}
				resultText.append(" - ");
				if (hasOre && currentPercentForOre > ingredient.requiredPercentMax) {
					resultText.append("<font color=\"#FF0000\">");
				}
				resultText.append(ingredient.requiredPercentMax).append("%");
				if (hasOre && currentPercentForOre > ingredient.requiredPercentMax) {
					resultText.append("</font>");
				}
			}
			resultText.append("<br />");
		}
		resultText.append("</html>");
		return resultText.toString();
	}
}
