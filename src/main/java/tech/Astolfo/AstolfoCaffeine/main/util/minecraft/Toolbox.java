package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.bson.Document;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;

import java.util.*;

public class Toolbox {

    enum type {
        SWORD,
        PICK
    }

    public static Map<type, Map<Integer, Tool>> tool_id = new HashMap<type, Map<Integer, Tool>>() {{
        put(
                type.PICK,
                new HashMap<Integer, Tool>() {{
                    put(0, new Tool(1, 0, 0, "762750435873390622", Integer.MIN_VALUE)); // Wood
                    put(1, new Tool(2, 0, 0, "762750401840939018", Integer.MIN_VALUE)); // Stone
                    put(2, new Tool(3, 0, 1, "762748274246942740", Integer.MIN_VALUE)); // Iron
                    put(3, new Tool(4, 1, 1, "762751771244232774", Integer.MIN_VALUE)); // Iron (Enchanted)
                    put(4, new Tool(5, 1, 2, "762748240315547689", Integer.MIN_VALUE)); // Gold
                    put(5, new Tool(6, 1, 2, "762751733066891314", Integer.MIN_VALUE)); // Gold (Enchanted)
                    put(6, new Tool(7, 2, 3, "737855791171895308", Integer.MIN_VALUE)); // Diamond
                    put(7, new Tool(8, 2, 3, "762750341376245811", Integer.MIN_VALUE)); // Diamond (Enchanted)
                    put(8, new Tool(9, 3, 4, "762745405746053181", Integer.MIN_VALUE)); // Netherite
                    put(9, new Tool(10, 4, 5, "762745408966361171", Integer.MIN_VALUE)); // Netherite (Enchanted)
                }}
        );

        put(
                type.SWORD,
                new HashMap<Integer, Tool>() {{
                    put(0, new Tool(0, 0, 0, "762750435902881875", Integer.MIN_VALUE)); // Wood
                    put(1, new Tool(0, 0, 0, "762750401807122433", Integer.MIN_VALUE)); // Stone
                    put(2, new Tool(0, 0, 0, "762748274435162112", Integer.MIN_VALUE)); // Iron
                    put(3, new Tool(0, 0, 0, "762751771813609513", Integer.MIN_VALUE)); // Iron (Enchanted)
                    put(4, new Tool(0, 0, 0, "762748240599842816", Integer.MIN_VALUE)); // Gold
                    put(5, new Tool(0, 0, 0, "762751733419474974", Integer.MIN_VALUE)); // Gold (Enchanted)
                    put(6, new Tool(0, 0, 0, "762746914823209010", Integer.MIN_VALUE)); // Diamond
                    put(7, new Tool(0, 0, 0, "762750036160413697", Integer.MIN_VALUE)); // Diamond (Enchanted)
                    put(8, new Tool(0, 0, 0, "762745406353833995", Integer.MIN_VALUE)); // Netherite
                    put(9, new Tool(0, 0, 0, "762745409885175828", Integer.MIN_VALUE)); // Netherite (Enchanted)
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
        addTool(tool_id.get(type.PICK).get(0)); // Wooden Pickaxe
    }};

    public static Toolbox fromUserID(long id) {
        Document toolsDoc = new CloudData().get_data(id, CloudData.Database.Economy, CloudData.Collection.tools);
        Toolbox myTools = new Toolbox();

        int pick = toolsDoc.getInteger("pick");
        myTools.addTool(tool_id.get(type.PICK).get(pick));

        return myTools;
    }

}