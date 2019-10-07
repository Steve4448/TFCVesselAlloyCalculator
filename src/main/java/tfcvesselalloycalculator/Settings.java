package tfcvesselalloycalculator;

import tfcvesselalloycalculator.vessel.VesselRecipe;

public class Settings {

	public static final String COPPER = "Native Copper";
	public static final String CASSITERITE = "Cassiterite";
	public static final String BISMUTHINITE = "Bismuthinite";
	public static final String SPHALERITE = "Sphalerite";
	public static final String GOLD = "Native Gold";
	public static final String SILVER = "Native Silver";

	public VesselRecipe.Ore.SizeType[] sizes = VesselRecipe.Ore.SizeType.values();
	public VesselRecipe.Ore[] ores = {
		new VesselRecipe.Ore(COPPER),
		new VesselRecipe.Ore(CASSITERITE),
		new VesselRecipe.Ore(BISMUTHINITE),
		new VesselRecipe.Ore(SPHALERITE),
		new VesselRecipe.Ore(GOLD),
		new VesselRecipe.Ore(SILVER)
	};

	public VesselRecipe[] recipes = {
		new VesselRecipe("Copper", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(COPPER, 100, 100)
		}),
		new VesselRecipe("Tin", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(CASSITERITE, 100, 100)
		}),
		new VesselRecipe("Bismuth", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(BISMUTHINITE, 100, 100)
		}),
		new VesselRecipe("Zinc", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(SPHALERITE, 100, 100)
		}),
		new VesselRecipe("Gold", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(GOLD, 100, 100)
		}),
		new VesselRecipe("Silver", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(SILVER, 100, 100)
		}),
		new VesselRecipe("Bismuth Bronze", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(SPHALERITE, 20, 30),
			new VesselRecipe.Ingredient(COPPER, 50, 65),
			new VesselRecipe.Ingredient(BISMUTHINITE, 10, 20)
		}),
		new VesselRecipe("Black Bronze", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(COPPER, 50, 70),
			new VesselRecipe.Ingredient(SILVER, 10, 25),
			new VesselRecipe.Ingredient(GOLD, 10, 25)
		}),
		new VesselRecipe("Bronze", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(COPPER, 88, 92),
			new VesselRecipe.Ingredient(CASSITERITE, 8, 12)
		}),
		new VesselRecipe("Brass", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(COPPER, 88, 92),
			new VesselRecipe.Ingredient(SPHALERITE, 8, 12)
		}),
		new VesselRecipe("Rose Gold", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(COPPER, 15, 30),
			new VesselRecipe.Ingredient(GOLD, 70, 85)
		}),
		new VesselRecipe("Sterling Silver", new VesselRecipe.Ingredient[]{
			new VesselRecipe.Ingredient(COPPER, 20, 40),
			new VesselRecipe.Ingredient(SILVER, 60, 80)
		})
	};
}
