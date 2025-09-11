package com.artillexstudios.axtrade.trade;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public enum ItemType {
    // Spade
    SWORD(
            Material.NETHERITE_SWORD
    ),

    // Picconi
    PICKAXE(

            Material.NETHERITE_PICKAXE
    ),

    // Asce
    AXE(
            Material.NETHERITE_AXE

    ),

    // Pale
    SHOVEL(
            Material.NETHERITE_SHOVEL
    ),

    // Zappe
    HOE(
            Material.NETHERITE_HOE
    ),

    // Archi
    BOW(
            Material.BOW
    ),

    // Scudi
    SHIELD(
            Material.SHIELD
    ),

    // Balestre
    CROSSBOW(
            Material.CROSSBOW
    ),

    // Elmi
    HELMET(
            Material.NETHERITE_HELMET
    ),

    // Corpetti
    CHESTPLATE(

            Material.NETHERITE_CHESTPLATE
    ),

    // Gambali
    LEGGINGS(

            Material.NETHERITE_LEGGINGS
    ),

    // Stivali
    BOOTS(
            Material.NETHERITE_BOOTS
    );

    private final List<Material> materialList;
    private final Set<Material> materialSet;

    ItemType(Material... materials) {
        this.materialList = List.of(materials);  // Ordine garantito
        this.materialSet = Set.copyOf(materialList);  // Per lookup rapido
    }

    public Material getDefaultMaterial() {
        return materialList.get(0); // Ora è garantito che sia il primo
    }

    public boolean matches(ItemStack item) {
        return materialSet.contains(item.getType());
    }


    public static ItemType fromItemStack(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;

        for (ItemType type : values()) {
            if (type.matches(item)) {
                return type;
            }
        }
        return null; // Nessuna corrispondenza trovata
    }


}
