package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;

import net.dv8tion.jda.api.entities.Message;

public class ToolHandler {

    public Message host_msg;

    ToolHandler() {
        Toolbox.DefaultTools.addToMessage(host_msg);

    }

}
