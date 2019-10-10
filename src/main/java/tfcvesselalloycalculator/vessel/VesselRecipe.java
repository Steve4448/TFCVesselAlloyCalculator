package tfcvesselalloycalculator.vessel;

import java.util.ArrayList;
import java.util.Arrays;

public class VesselRecipe {

	public static class Ore {

		public enum SizeType {
			SMALL(10),
			POOR(15),
			REGULAR(25),
			RICH(35);
			private int amount;

			private SizeType(int amount) {
				this.amount = amount;
			}

			public int getAmount() {
				return amount;
			}

			public void setAmount(int amount) {
				this.amount = amount;
			}

			public static int getAmount(SizeType type) {
				return type.amount;
			}
		}

		private String name;

		public Ore(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Ingredient {

		public final Ore requiredOre;
		public double requiredPercentMin;
		public double requiredPercentMax;

		public Ingredient(Ore requiredOre, double requiredPercentMin, double requiredPercentMax) {
			this.requiredOre = requiredOre;
			this.requiredPercentMin = requiredPercentMin;
			this.requiredPercentMax = requiredPercentMax;
		}
	}

	private String name;
	private final ArrayList<Ingredient> ingredient;

	public VesselRecipe(String name, Ingredient[] ingredient) {
		this.name = name;
		this.ingredient = new ArrayList<>(Arrays.asList(ingredient));
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Ingredient> getIngredients() {
		return ingredient;
	}
}
