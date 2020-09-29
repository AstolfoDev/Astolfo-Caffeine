package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;

import java.util.HashMap;

public class Tool {

    private HashMap<Block.Material, Integer> specs;
    public Emote emote;
    static private JDA jda;

    public Tool(int stone_dmg, int wood_dmg, int dirt_dmg, String emote_str) {
        emote = jda.getEmoteById(emote_str);
        specs = new HashMap<>();
        specs.put(Block.Material.STONE, stone_dmg);
        specs.put(Block.Material.WOOD, wood_dmg);
        specs.put(Block.Material.DIRT, dirt_dmg);
    }

    public int damageTo(Block.Material type) {
        return specs.get(type);
    }

    public static void setJda(JDA jda) {
        Tool.jda = jda;
    }

}
