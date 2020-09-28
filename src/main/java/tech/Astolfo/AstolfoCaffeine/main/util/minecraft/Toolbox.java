package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.ArrayList;

public class Toolbox {

    private ArrayList<Tool> tools;

    public Toolbox() {
        tools = new ArrayList<>();
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
}
