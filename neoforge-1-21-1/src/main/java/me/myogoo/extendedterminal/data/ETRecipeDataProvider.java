package me.myogoo.extendedterminal.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.myotus.data.recipe.JsonRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.item;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.myoCondition;

public final class ETRecipeDataProvider extends JsonRecipeProvider {
    public ETRecipeDataProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(JsonRecipeOutput output) {
        buildMyotusConversions(output);
        buildTerminalCrafting(output);
        buildAE2WTLibRecipes(output);
    }

    @Override
    public @NotNull String getName() {
        return "Extended Terminal recipes";
    }

    private static void buildMyotusConversions(JsonRecipeOutput output) {
        saveShapeless(output, "extendedterminal/conversion/charged_ender_pearl",
                "extendedterminal:charged_ender_pearl", "myotus:charged_ender_pearl");
        saveShapeless(output, "extendedterminal/conversion/compat_press",
                "extendedterminal:compat_press", "myotus:compat_press");
        saveShapeless(output, "extendedterminal/conversion/compat_processor",
                "extendedterminal:compat_processor", "myotus:compat_processor");
        saveShapeless(output, "extendedterminal/conversion/printed_compat_processor",
                "extendedterminal:printed_compat_processor", "myotus:printed_compat_processor");
    }

    private static void buildTerminalCrafting(JsonRecipeOutput output) {
        saveShaped(output, "extendedterminal/et_terminal", null, "extendedterminal:et_terminal",
                new String[]{" E ", "ABC", " D "},
                key('A', "minecraft:smithing_table", 'B', "ae2:crafting_terminal", 'C', "minecraft:stonecutter", 'D', "myotus:compat_processor", 'E', "minecraft:anvil"));
        saveShaped(output, "extendedterminal/material_converter", null, "extendedterminal:material_converter",
                new String[]{"ICI", "CMC", "ICI"},
                key('C', "ae2:fluix_glass_cable", 'I', "minecraft:iron_ingot", 'M', "myotus:compat_processor"));

        JsonArray extendedCrafting = conditions("extendedcrafting");
        saveTerminalUpgrade(output, "extendedcrafting/basic_terminal", extendedCrafting,
                "extendedcrafting:basic_table", "ae2:crafting_terminal", "extendedterminal:basic_terminal");
        saveTerminalUpgrade(output, "extendedcrafting/advanced_terminal", extendedCrafting,
                "extendedcrafting:advanced_table", "ae2:crafting_terminal", "extendedterminal:advanced_terminal");
        saveTerminalUpgrade(output, "extendedcrafting/elite_terminal", extendedCrafting,
                "extendedcrafting:elite_table", "ae2:crafting_terminal", "extendedterminal:elite_terminal");
        saveTerminalUpgrade(output, "extendedcrafting/ultimate_terminal", extendedCrafting,
                "extendedcrafting:ultimate_table", "ae2:crafting_terminal", "extendedterminal:ultimate_terminal");

        JsonArray avaritiaNeo = conditions("avaritia-neo");
        saveTerminalUpgrade(output, "avaritianeo/extreme_terminal", avaritiaNeo,
                "avaritia:extreme_crafting_table", "ae2:crafting_terminal", "extendedterminal:neo_extreme_terminal");

        JsonArray reAvaritia = conditions("re-avaritia");
        saveTerminalUpgrade(output, "reavaritia/sculk_terminal", reAvaritia,
                "avaritia:sculk_crafting_table", "ae2:crafting_terminal", "extendedterminal:sculk_terminal");
        saveTerminalUpgrade(output, "reavaritia/nether_terminal", reAvaritia,
                "avaritia:nether_crafting_table", "ae2:crafting_terminal", "extendedterminal:nether_terminal");
        saveTerminalUpgrade(output, "reavaritia/end_terminal", reAvaritia,
                "avaritia:end_crafting_table", "ae2:crafting_terminal", "extendedterminal:end_terminal");
        saveTerminalUpgrade(output, "reavaritia/extreme_terminal", reAvaritia,
                "avaritia:extreme_crafting_table", "ae2:crafting_terminal", "extendedterminal:extreme_terminal");

        buildUnitedTerminalCrafting(output);

        saveShaped(output, "extendedterminal/wt/wireless_et_terminal", conditions("ae2wtlib"),
                "extendedterminal:wireless_et_terminal",
                new String[]{"A", "B", "C"},
                key('A', "ae2:wireless_receiver", 'B', "extendedterminal:et_terminal", 'C', "ae2:dense_energy_cell"));
        saveShaped(output, "extendedterminal/wt/wireless_united_terminal", conditions("ae2wtlib", "extendedcrafting"),
                "extendedterminal:wireless_united_terminal",
                new String[]{"A", "B", "C"},
                key('A', "ae2:wireless_receiver", 'B', "extendedterminal:united_terminal", 'C', "ae2:dense_energy_cell"));
    }

    private static void buildAE2WTLibRecipes(JsonRecipeOutput output) {
        saveCombine(output, "ae2wtlib/etc", conditions("ae2wtlib"),
                "extendedterminal:wireless_et_terminal", "ae2:wireless_crafting_terminal",
                "wireless_et_terminal", "crafting");
        saveCombine(output, "ae2wtlib/etp", conditions("ae2wtlib"),
                "extendedterminal:wireless_et_terminal", "ae2wtlib:wireless_pattern_encoding_terminal",
                "wireless_et_terminal", "pattern_encoding");
        saveCombine(output, "ae2wtlib/united_etc", conditions("ae2wtlib", "extendedcrafting"),
                "extendedterminal:wireless_united_terminal", "ae2:wireless_crafting_terminal",
                "wireless_united_terminal", "crafting");
        saveCombine(output, "ae2wtlib/united_etp", conditions("ae2wtlib", "extendedcrafting"),
                "extendedterminal:wireless_united_terminal", "ae2wtlib:wireless_pattern_encoding_terminal",
                "wireless_united_terminal", "pattern_encoding");
        saveUpgrade(output, "ae2wtlib/upgrade_wireless_et_terminal", conditions("ae2wtlib"),
                "extendedterminal:wireless_et_terminal", "wireless_et_terminal");
        saveUpgrade(output, "ae2wtlib/upgrade_wireless_united_terminal", conditions("ae2wtlib", "extendedcrafting"),
                "extendedterminal:wireless_united_terminal", "wireless_united_terminal");
    }

    private static void buildUnitedTerminalCrafting(JsonRecipeOutput output) {
        saveUnitedTerminal(output, "reavaritia/united_terminal",
                conditionsFor(new String[]{"re-avaritia"}, "avaritia-neo", "extendedcrafting"),
                "extendedterminal:sculk_terminal", "extendedterminal:nether_terminal",
                "extendedterminal:end_terminal", "extendedterminal:extreme_terminal");
        saveUnitedTerminal(output, "avaritianeo/united_terminal",
                conditionsFor(new String[]{"avaritia-neo"}, "re-avaritia", "extendedcrafting"),
                "extendedterminal:neo_extreme_terminal");
        saveUnitedTerminal(output, "extendedcrafting/united_terminal",
                conditionsFor(new String[]{"extendedcrafting"}, "re-avaritia", "avaritia-neo"),
                "extendedterminal:basic_terminal", "extendedterminal:advanced_terminal",
                "extendedterminal:elite_terminal", "extendedterminal:ultimate_terminal");
        saveUnitedTerminal(output, "reavaritia_extendedcrafting/united_terminal",
                conditionsFor(new String[]{"re-avaritia", "extendedcrafting"}, "avaritia-neo"),
                "extendedterminal:sculk_terminal", "extendedterminal:nether_terminal",
                "extendedterminal:end_terminal", "extendedterminal:extreme_terminal",
                "extendedterminal:basic_terminal", "extendedterminal:advanced_terminal",
                "extendedterminal:elite_terminal", "extendedterminal:ultimate_terminal");
        saveUnitedTerminal(output, "avaritianeo_extendedcrafting/united_terminal",
                conditionsFor(new String[]{"avaritia-neo", "extendedcrafting"}, "re-avaritia"),
                "extendedterminal:neo_extreme_terminal",
                "extendedterminal:basic_terminal", "extendedterminal:advanced_terminal",
                "extendedterminal:elite_terminal", "extendedterminal:ultimate_terminal");
    }

    private static void saveUnitedTerminal(JsonRecipeOutput output, String path, JsonArray conditions,
            String... terminals) {
        String[] ingredients = new String[terminals.length + 1];
        ingredients[0] = "myotus:compat_processor";
        System.arraycopy(terminals, 0, ingredients, 1, terminals.length);
        saveShapeless(output, path, conditions, "extendedterminal:united_terminal", ingredients);
    }

    private static void saveTerminalUpgrade(JsonRecipeOutput output, String path, JsonArray conditions,
            String table, String terminal, String result) {
        saveShaped(output, path, conditions, result,
                new String[]{"IT", "P "},
                key('I', table, 'P', "myotus:compat_processor", 'T', terminal));
    }

    private static void saveShapeless(JsonRecipeOutput output, String path, String ingredient, String result) {
        JsonObject recipe = recipe("minecraft:crafting_shapeless");
        JsonArray ingredients = new JsonArray();
        ingredients.add(item(ingredient));
        recipe.add("ingredients", ingredients);
        recipe.add("result", result(result, 1));
        save(output, path, recipe);
    }

    private static void saveShapeless(JsonRecipeOutput output, String path, JsonArray conditions, String result,
            String... ingredients) {
        JsonObject recipe = recipe("minecraft:crafting_shapeless");
        JsonArray ingredientsJson = new JsonArray();
        for (String ingredient : ingredients) {
            ingredientsJson.add(item(ingredient));
        }
        recipe.add("ingredients", ingredientsJson);
        recipe.add("result", result(result, 1));
        save(output, path, conditional(conditions, recipe));
    }

    private static void saveShaped(JsonRecipeOutput output, String path, JsonArray conditions, String result,
            String[] pattern, JsonObject key) {
        JsonObject recipe = recipe("minecraft:crafting_shaped");
        if (conditions != null && !conditions.isEmpty()) {
            recipe.add("neoforge:conditions", conditions);
        }
        JsonArray patternJson = new JsonArray();
        for (String row : pattern) {
            patternJson.add(row);
        }
        recipe.add("pattern", patternJson);
        recipe.add("key", key);
        recipe.add("result", result(result, 1));
        recipe.addProperty("show_notification", false);
        save(output, path, recipe);
    }

    private static void saveCombine(JsonRecipeOutput output, String path, JsonArray conditions,
            String terminalA, String terminalB, String terminalAName, String terminalBName) {
        JsonObject recipe = conditional(conditions, recipe("ae2wtlib:combine"));
        recipe.add("terminalA", item(terminalA));
        recipe.add("terminalB", item(terminalB));
        recipe.addProperty("terminalAName", terminalAName);
        recipe.addProperty("terminalBName", terminalBName);
        save(output, path, recipe);
    }

    private static void saveUpgrade(JsonRecipeOutput output, String path, JsonArray conditions,
            String terminal, String terminalName) {
        JsonObject recipe = conditional(conditions, recipe("ae2wtlib:upgrade"));
        recipe.add("terminal", item(terminal));
        recipe.addProperty("terminalName", terminalName);
        save(output, path, recipe);
    }

    private static JsonObject conditional(JsonArray conditions, JsonObject recipe) {
        if (conditions != null && !conditions.isEmpty()) {
            recipe.add("neoforge:conditions", conditions);
        }
        return recipe;
    }

    private static JsonArray conditions(String... activeMods) {
        JsonArray conditions = new JsonArray();
        for (String activeMod : activeMods) {
            conditions.add(myoCondition(activeMod));
        }
        return conditions;
    }

    private static JsonArray conditionsFor(String[] activeMods, String... inactiveMods) {
        JsonArray conditions = conditions(activeMods);
        for (String inactiveMod : inactiveMods) {
            conditions.add(notCondition(myoCondition(inactiveMod)));
        }
        return conditions;
    }

    private static JsonObject notCondition(JsonObject value) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "neoforge:not");
        condition.add("value", value);
        return condition;
    }

    private static JsonObject recipe(String type) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", type);
        return recipe;
    }

    private static JsonObject result(String item, int count) {
        JsonObject result = new JsonObject();
        result.addProperty("id", item);
        if (count != 1) {
            result.addProperty("count", count);
        }
        return result;
    }

    private static JsonObject key(Object... entries) {
        JsonObject key = new JsonObject();
        for (int i = 0; i < entries.length; i += 2) {
            key.add(String.valueOf(entries[i]), item((String) entries[i + 1]));
        }
        return key;
    }

    private static void save(JsonRecipeOutput output, String path, JsonObject recipe) {
        output.accept(recipeId(path), recipe);
    }

    private static ResourceLocation recipeId(String path) {
        return ExtendedTerminal.makeId(path);
    }
}
