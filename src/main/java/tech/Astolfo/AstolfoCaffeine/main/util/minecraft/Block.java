package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.Astolfo.AstolfoCaffeine.App;

import java.util.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Block {

    public enum State {
        ACTIVE,
        EXPIRED,
        BROKEN
    }

    public enum Material {
        STONE,
        WOOD,
        DIRT
    }

    private String emote_template;
    private Material mat;
    public State state;
    private int hits;
    public int stage;
    private int num_stages;
    private int hits_per_stage;
    public int value;
    private int max_time;
    private long start_time;
    private static JDA jda;
    public static String emote_server = "457645193202499586";

    //TODO: Make this work on Vim's PC
    /*

    public Map<Material, Map<Short, List<String>>> blockState = Map.ofEntries(
            entry(
                    Material.STONE,
                    Map.ofEntries(
                            entry(
                                    (short) 0,
                                    Arrays.asList(
                                            "738014582555279460", // 1
                                            "738014591988138045", // 2
                                            "738014594911698996", // 3
                                            "738014598862733422", // 4
                                            "738015059070156830", // 5
                                            "738015060949205083", // 6
                                            "738015063960584222", // 7
                                            "738015063025123360", // 8
                                            "738015061922021437", // 9
                                            "738015062274342939"  // 10
                                    )
                            )
                    )
            )
    );
    */


    public Block(String _emote_template, Material _material, int _num_stages, int _hits_per_stage, int _value, int _max_time) {
        emote_template = _emote_template;
        mat = _material;
        num_stages = _num_stages;
        hits_per_stage = _hits_per_stage;
        value = _value;
        max_time = _max_time;
        stage = 0;
        hits = 0;
        state = State.ACTIVE;
        startTimer();
    }

    public String hitWith(Tool tool) {
        int damage = tool.damageTo(mat);
        hits += damage;
        if (leftTime() < 1) {
            state = State.EXPIRED;
            return null;
        }
        if (hits >= hits_per_stage) {
            hits %= hits_per_stage;
            stage++;
            if (stage >= num_stages) {
                state = State.BROKEN;
                return null;
            }
            return render();
        }
        return null;
    }

    public int leftTime() {
        return (int) (max_time - (System.currentTimeMillis() / 1000 - start_time));
    }

    public void startTimer() {
        start_time = System.currentTimeMillis() / 1000;
    }

    public String render() {
        Emote emote = Objects.requireNonNull(jda.getGuildById(emote_server)).getEmotesByName(String.format(emote_template, stage), false).get(0);
        //Emote emote = Objects.requireNonNull(jda.getGuildById(emote_server)).getEmoteById(emote_map.get(mat).get(stage));
        return emote.getAsMention();
    }

    public static void setJda(JDA jda) {
        Block.jda = jda;
    }
}
