package tfcvesselalloycalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import tfcvesselalloycalculator.vessel.VesselRecipe;

public class Settings {

	public static final VesselRecipe.Ore COPPER = new VesselRecipe.Ore("Native Copper");
	public static final VesselRecipe.Ore CASSITERITE = new VesselRecipe.Ore("Cassiterite");
	public static final VesselRecipe.Ore BISMUTHINITE = new VesselRecipe.Ore("Bismuthinite");
	public static final VesselRecipe.Ore SPHALERITE = new VesselRecipe.Ore("Sphalerite");
	public static final VesselRecipe.Ore GOLD = new VesselRecipe.Ore("Native Gold");
	public static final VesselRecipe.Ore SILVER = new VesselRecipe.Ore("Native Silver");

	public ArrayList<VesselRecipe.Ore.SizeType> sizes = new ArrayList<>(Arrays.asList(VesselRecipe.Ore.SizeType.values()));
	public ArrayList<VesselRecipe.Ore> ores = new ArrayList<>(Arrays.asList(new VesselRecipe.Ore[]{
		COPPER,
		CASSITERITE,
		BISMUTHINITE,
		SPHALERITE,
		GOLD,
		SILVER
	}));

	public CopyOnWriteArrayList<VesselRecipe> recipes = new CopyOnWriteArrayList<>(Arrays.asList(new VesselRecipe[]{
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
	}));
}
