package tfcvesselalloycalculator.vessel;

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

		private final String name;

		public Ore(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static class Ingredient {

		public final String requiredOre;
		public final double requiredPercentMin;
		public final double requiredPercentMax;

		public Ingredient(String requiredOre, double requiredPercentMin, double requiredPercentMax) {
			this.requiredOre = requiredOre;
			this.requiredPercentMin = requiredPercentMin;
			this.requiredPercentMax = requiredPercentMax;
		}
	}

	private final String name;
	private final Ingredient[] ingredient;

	public VesselRecipe(String name, Ingredient[] ingredient) {
		this.name = name;
		this.ingredient = ingredient;
	}

	public String getName() {
		return name;
	}

	public Ingredient[] getIngredients() {
		return ingredient;
	}
}
