package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Toolbox {


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
        addTool(new Tool(1, 1, 0, "703305007264694394", 15)); //Sword
        addTool(new Tool(1, 1, 0, "703305007264694394", 20)); //Sword
        addTool(new Tool(1, 1, 0, "703305007264694394", 25)); //Sword
        addTool(new Tool(1, 1, 0, "703305007264694394", 30)); //Sword
        addTool(new Tool(1, 1, 0, "703305007264694394", 35)); //Sword
        addTool(new Tool(1, 1, 0, "703305007264694394", 40)); //Sword
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
