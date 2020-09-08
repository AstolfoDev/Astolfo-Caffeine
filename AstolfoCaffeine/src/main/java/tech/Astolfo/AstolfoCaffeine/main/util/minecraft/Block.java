package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.Astolfo.AstolfoCaffeine.App;

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

    private String url_template;
    private String name;
    private Material mat;
    public State state;
    private int hits;
    public int stage;
    private int num_stages;
    private int hits_per_stage;
    public int value;
    private int max_time;
    private long start_time;

    public Block(String _name, String _url_template, Material _material, int _num_stages, int _hits_per_stage, int _value, int _max_time) {
        url_template = _url_template;
        name = _name;
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

    public MessageEmbed hitWith(Tool tool) {
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

    public MessageEmbed render() {
        return App.embed().setTitle(name + " - " + leftTime() + " seconds left").setImage(String.format(url_template, stage)).build();
    }
}
