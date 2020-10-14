package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.*;

public class Toolbox {

    enum type {
        SWORD,
        PICK
    }

    public Map tool_id = new HashMap() {{
        put(
                type.SWORD,
                new HashMap() {{
                    put(0, "762750435902881875"); // Wood
                    put(1, "762750401807122433"); // Stone
                    put(2, "762748274435162112"); // Iron
                    put(3, "762751771813609513"); // Iron (Enchanted)
                    put(4, "762748240599842816"); // Gold
                    put(5, "762751733419474974"); // Gold (Enchanted)
                    put(6, "762746914823209010"); // Diamond
                    put(7, "762750036160413697"); // Diamond (Enchanted)
                    put(8, "762745406353833995"); // Netherite
                    put(9, "762745409885175828"); // Netherite (Enchanted)
                }}
        );
        put(
                type.PICK,
                new HashMap() {{
                    put(0, "762750435873390622"); // Wood
                    put(1, "762750401840939018"); // Stone
                    put(2, "762748274246942740"); // Iron
                    put(3, "762751771244232774"); // Iron (Enchanted)
                    put(4, "762748240315547689"); // Gold
                    put(5, "762751733066891314"); // Gold (Enchanted)
                    put(6, "737855791171895308"); // Diamond
                    put(7, "762750341376245811"); // Diamond (Enchanted)
                    put(8, "762745405746053181"); // Netherite
                    put(9, "762745408966361171"); // Netherite (Enchanted)
                }}
        );
    }};

    public List<Tool> tools;

    public Toolbox() {
        tools = new ArrayList<Tool>();
    }

    public Toolbox addTool(Tool tool) {
        tools.add(tool);
        return this;
    }

    public void addToMessage(Message msg) {
        for (Tool tool: tools) {
            msg.addReaction(tool.emote).queue();
        }
    }

    public Tool getTool(MessageReaction reaction) {
        for (Tool tool: tools) {
            if (tool.emote.getIdLong() == reaction.getReactionEmote().getIdLong()) {
                return tool;
            }
        }
        return null;
    }

    public static final Toolbox DefaultTools = new Toolbox() {{
        addTool(new Tool(2, 0, 0, "737855791171895308", 1));
        addTool(new Tool(1, 1, 0, "703305007264694394", 10)); //Sword
        //TODO: Add more
    }};


    public static Toolbox fromBits(int bits) {
        Toolbox myTools = new Toolbox();
        for (Tool tool: DefaultTools.tools) {
            if ((bits & 1) == 1) {
                myTools.addTool(tool);
            }
            bits >>= 1;
        }
        return myTools;
    }

    public int asBits() {
        int bits = 0;
        List<Tool> revTools = new ArrayList<>(DefaultTools.tools);
        Collections.reverse(revTools);
        for (Tool tool: revTools) {
            bits <<= 1;
            if (this.tools.contains(tool)) {
                bits += 1;
            }
        }
        return bits;
    }
}
