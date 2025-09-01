package com.artillexstudios.axtrade.hooks;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axtrade.hooks.currency.CoinsEngineHook;
import com.artillexstudios.axtrade.hooks.currency.CurrencyHook;
import com.artillexstudios.axtrade.hooks.currency.ExperienceHook;
import com.artillexstudios.axtrade.hooks.currency.PlaceholderCurrencyHook;
import com.artillexstudios.axtrade.hooks.currency.VaultHook;
import com.artillexstudios.axtrade.hooks.other.AxShulkersHook;
import com.artillexstudios.axtrade.hooks.other.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class HookManager {
    private static final ArrayList<CurrencyHook> currency = new ArrayList<>();
    private static AxShulkersHook axShulkersHook = null;

    public static void setupHooks() {
        updateHooks();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders();
        }

        if (Bukkit.getPluginManager().getPlugin("AxShulkers") != null) {
            axShulkersHook = new AxShulkersHook();
        }
    }

    public static void updateHooks() {
        currency.removeIf(currencyHook -> !currencyHook.isPersistent());

        if (HOOKS.getBoolean("currencies.Experience.register", true))
            currency.add(new ExperienceHook());

        if (HOOKS.getBoolean("currencies.Vault.register", true) && Bukkit.getPluginManager().getPlugin("Vault") != null) {
            currency.add(new VaultHook());
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into Vault!"));
        }

        if (HOOKS.getBoolean("currencies.CoinsEngine.register", true) && Bukkit.getPluginManager().getPlugin("CoinsEngine") != null) {
            for (Map<Object, Object> curr : HOOKS.getMapList("currencies.CoinsEngine.enabled")) {
                currency.add(new CoinsEngineHook(curr));
            }
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into CoinsEngine!"));
        }

        for (String str : HOOKS.getSection("placeholder-currencies").getRoutesAsStrings(false)) {
            if (!HOOKS.getBoolean("placeholder-currencies." + str + ".register", false)) continue;
            currency.add(new PlaceholderCurrencyHook(str, HOOKS.getSection("placeholder-currencies." + str)));
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Loaded placeholder currency " + str + "!"));
        }

        for (CurrencyHook hook : currency) hook.setup();
    }

    @SuppressWarnings("unused")
    public static void registerCurrencyHook(@NotNull Plugin plugin, @NotNull CurrencyHook currencyHook) {
        currency.add(currencyHook);

        Section section = HOOKS.getSection("currencies." + currencyHook.getName());
        if (section == null) {
            section = HOOKS.getBackingDocument().createSection("currencies." + currencyHook.getName());
            section.set("enabled", true);
            section.set("name", currencyHook.getName());
            section.set("tax", 0);
            HOOKS.save();
        }

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxTrade] Hooked into " + plugin.getName() + "! Note: Check the currencies.yml for settings!"));
    }

    @NotNull
    public static ArrayList<CurrencyHook> getCurrency() {
        return currency;
    }

    @Nullable
    public static CurrencyHook getCurrencyHook(@NotNull String name) {
        for (CurrencyHook hook : currency) {
            if (!hook.getName().equals(name)) continue;
            return hook;
        }

        return null;
    }

    public static AxShulkersHook getAxShulkersHook() {
        return axShulkersHook;
    }
}
