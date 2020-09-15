package tech.Astolfo.AstolfoCaffeine.main.event.list;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Message
{
    public void run(MessageReceivedEvent e)
    {
        if (e.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
          if (e.getMessage().getContentRaw().equals("online")) {
            e.getChannel().sendMessage("online true").queue(m -> m.delete().queue());
          }
        }
    }
}