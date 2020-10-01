package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;

import net.dv8tion.jda.api.entities.Emote;

import java.util.*;

import static tech.Astolfo.AstolfoCaffeine.App.jda;

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

    //private final String emote_template;
    private final Material mat;
    private final Short data;
    public State state;
    private int hits;
    public int stage;
    private final int num_stages;
    private final int hits_per_stage;
    public int value;
    private final int max_time;
    private long start_time;
    public static String emote_server = "512594569263579147";

    /*

    [DEPRECATED] unusable until VimV– *COUGH* i mean, Zyra gets gud

    public Map <Material, Map<Short, List<String>>> blockState = Map.ofEntries(
            Map.entry(
                    Material.STONE,
                    Map.ofEntries(
                            Map.entry(
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

    public HashMap<Material, HashMap<Short, List<String>>> blockState = new HashMap<>() {
        {
            put(
                    Material.STONE,
                    new HashMap<>() {
                        {
                            put(
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
                            );
                        }
                    }
            );
        }
    };


    public Block(Material _material, Short _data, int _num_stages, int _hits_per_stage, int _value, int _max_time) {
        //emote_template = _emote_template;
        mat = _material;
        data = _data;
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
        //Emote emote = Objects.requireNonNull(jda.getGuildById(emote_server)).getEmotesByName(String.format(emote_template, stage), false).get(0);
        Emote emote = Objects.requireNonNull(jda.getGuildById(emote_server)).getEmoteById(blockState.get(mat).get(data).get(stage));
        assert emote != null;
        return emote.getAsMention();
    }

}
