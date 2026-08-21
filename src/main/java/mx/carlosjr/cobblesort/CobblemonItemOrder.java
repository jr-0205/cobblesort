package mx.carlosjr.cobblesort;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Set;

final class CobblemonItemOrder {
    private static final Set<String> EVOLUTION_ITEMS = Set.of(
            "black_augurite", "peat_block", "link_cable", "kings_rock", "metal_coat",
            "dragon_scale", "deep_sea_tooth", "deep_sea_scale", "upgrade", "dubious_disc",
            "protector", "electirizer", "magmarizer", "reaper_cloth", "prism_scale",
            "razor_claw", "razor_fang", "sachet", "whipped_dream", "sweet_apple",
            "tart_apple", "cracked_pot", "chipped_pot", "galarica_cuff", "galarica_wreath",
            "auspicious_armor", "malicious_armor", "leaders_crest", "scroll_of_darkness",
            "scroll_of_waters", "strawberry_sweet", "love_sweet", "berry_sweet",
            "clover_sweet", "flower_sweet", "star_sweet", "ribbon_sweet"
    );

    private static final Set<String> MEDICINE_WORDS = Set.of(
            "potion", "restore", "heal", "antidote", "awakening", "revive", "remedy",
            "ether", "elixir", "berry_juice", "heal_powder", "energy_powder", "energy_root"
    );

    private static final Set<String> BATTLE_WORDS = Set.of(
            "x_attack", "x_defence", "x_defense", "x_speed", "x_accuracy", "dire_hit", "guard_spec"
    );

    private CobblemonItemOrder() {
    }

    static SortKey key(ItemStack stack) {
        if (stack.isEmpty()) {
            return new SortKey(99, "", "");
        }

        Identifier id = Registries.ITEM.getId(stack.getItem());
        String namespace = id.getNamespace();
        String path = id.getPath().toLowerCase(Locale.ROOT);
        int category = category(namespace, path);
        return new SortKey(category, namespace, path);
    }

    private static int category(String namespace, String path) {
        if (!namespace.equals("cobblemon")) {
            return 90;
        }
        if (path.endsWith("_stone") || EVOLUTION_ITEMS.contains(path)) {
            return 0;
        }
        if (path.endsWith("_berry")) {
            return 10;
        }
        if (path.endsWith("_poke_ball") || path.endsWith("_ball")) {
            return 20;
        }
        if (containsAny(path, MEDICINE_WORDS)) {
            return 30;
        }
        if (path.startsWith("held_item/") || path.contains("choice_") || path.contains("leftovers")
                || path.contains("exp_share") || path.contains("lucky_egg")) {
            return 40;
        }
        if (containsAny(path, BATTLE_WORDS)) {
            return 50;
        }
        if (path.contains("candy") || path.contains("mint") || path.contains("stew")
                || path.contains("dip") || path.contains("leek")) {
            return 60;
        }
        if (path.contains("apricorn") || path.contains("ore") || path.contains("block")) {
            return 70;
        }
        return 80;
    }

    private static boolean containsAny(String path, Set<String> words) {
        return words.stream().anyMatch(path::contains);
    }

    record SortKey(int category, String namespace, String path) implements Comparable<SortKey> {
        @Override
        public int compareTo(SortKey other) {
            int result = Integer.compare(category, other.category);
            if (result == 0) result = namespace.compareTo(other.namespace);
            if (result == 0) result = path.compareTo(other.path);
            return result;
        }
    }
}
