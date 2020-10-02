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
                                    // Emerald Ore
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

                            put(
                                    // Diamond Ore
                                    (short) 1,
                                    Arrays.asList(
                                            "761581882109853706", // 1
                                            "761581882206715934", // 2
                                            "761581882407911454", // 3
                                            "761581882818297856", // 4
                                            "761581882689060865", // 5
                                            "761581882718289941", // 6
                                            "761581883111899166", // 7
                                            "761581883196440596", // 8
                                            "761581882756038707", // 9
                                            "761581883301167134"  // 10
                                    )
                            );

                            put(
                                    // Gold Ore
                                    (short) 2,
                                    Arrays.asList(
                                            "761585001925967872", // 1
                                            "761585001862004776", // 2
                                            "761585002067918908", // 3
                                            "761585002332815360", // 4
                                            "761585002307649561", // 5
                                            "761585002281697293", // 6
                                            "761585002290217041", // 7
                                            "761585002361651250", // 8
                                            "761585002362175488", // 9
                                            "761585002374496266"  // 10
                                    )
                            );

                            put(
                                    // Iron Ore
                                    (short) 3,
                                    Arrays.asList(
                                            "761587111388250152", // 1
                                            "761586946241462353", // 2
                                            "761586946287206440", // 3
                                            "761586946241724436", // 4
                                            "761586945821376523", // 5
                                            "761586946170159125", // 6
                                            "761586946244870154", // 7
                                            "761586946186543165", // 8
                                            "761586945956118600", // 9
                                            "761586946308308992" // 10
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
