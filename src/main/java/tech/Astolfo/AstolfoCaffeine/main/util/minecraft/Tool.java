package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;
import net.dv8tion.jda.api.entities.Emote;
import tech.Astolfo.AstolfoCaffeine.main.util.caching.Cache;

import java.util.HashMap;

public class Tool {

    public final HashMap<Block.Material, Integer> specs;
    public Emote emote;
    public int cost;

    public Tool(int stone_dmg, int wood_dmg, int dirt_dmg, String emote_str, int cost) {
        emote = Cache.jda.getEmoteById(emote_str);
        specs = new HashMap<>();
        specs.put(Block.Material.STONE, stone_dmg);
        specs.put(Block.Material.WOOD, wood_dmg);
        specs.put(Block.Material.DIRT, dirt_dmg);
        this.cost = cost;
    }

    public int damageTo(Block.Material type) {
        return specs.get(type);
    }

}
