package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Block;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Tool;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Toolbox;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Shop extends Command {

    private final EventWaiter waiter;

    public Shop(EventWaiter waiter) {
        super.name = "shop";
        super.aliases = new String[]{"shp"};
        super.help = "buy stuff";
        super.arguments = "";
        super.category = new Category("economy");
        this.waiter = waiter;
    }


    private Message lp_HostMsg;
    private Message lp_cmdMsg;
    private Toolbox lp_ownedTools;
    private AtomicInteger lp_userBal;

    @Override
    protected void execute(CommandEvent e) {

        lp_cmdMsg = e.getMessage();
        User u = lp_cmdMsg.getAuthor();
        Toolbox defaultTools = Toolbox.DefaultTools;

        // DB magic to get users bal -> e.g. 10
        lp_userBal = new AtomicInteger(9999);

        // DB magic here to get users tools -> e.g. 0 (no tools)
        lp_ownedTools = Toolbox.fromBits(0);

        String[][] fields = new String[defaultTools.tools.size()][4];
        fields[0] = new String[]{"Stats", "Stone:\nWood:\nDirt:\nCost:", "true"};
        for (int i = 1; i < fields.length; i++)
        {
            Tool tool = defaultTools.tools.get(i);
            fields[i] = new String[]{tool.emote.getAsMention(), String.format("**%s**\n**%s**\n**%s**\n**%s**", tool.specs.get(Block.Material.STONE), tool.specs.get(Block.Material.WOOD), tool.specs.get(Block.Material.DIRT), tool.cost), "true"};
        }
        MessageEmbed embed = new Logging().send(null, "SHOP HERE", u.getAsTag(), "https://i.imgur.com/emm8mqS.gif", fields);
        lp_cmdMsg.getChannel().sendMessage(embed).queue(message -> {
            defaultTools.addToMessage(message);
            lp_HostMsg = message;
            mainLoop();
        });
    }

    private void mainLoop() {
        Toolbox defaultTools = Toolbox.DefaultTools;
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                ev -> lp_HostMsg.getId().equals(ev.getMessageId()) && ev.getUser().equals(lp_cmdMsg.getAuthor()) && defaultTools.getTool(ev.getReaction()) != null,
                ev -> {
                    ev.getReaction().removeReaction(ev.getUser()).queue();
                    Tool selectedTool = defaultTools.getTool(ev.getReaction());
                    if (lp_ownedTools.tools.contains(selectedTool)) {
                        lp_cmdMsg.getChannel().sendMessage("Noooo, you already have that tool").queue();
                        mainLoop();
                    } else if (selectedTool.cost > lp_userBal.get()) {
                        lp_cmdMsg.getChannel().sendMessage("Oof, is tooooo expensive for u").queue();
                        mainLoop();
                    } else {
                        lp_ownedTools.addTool(selectedTool);
                        lp_cmdMsg.getChannel().sendMessage("Yayyyy, here special for you onlyyyyyy").queue(THIS_VARIABLE_DOESNT_DO_ANYTHING_DO_WE_REALLY_NEED_TO_DEFINE_IT_QUESTION_MARK -> lp_cmdMsg.getChannel().sendMessage(selectedTool.emote.getAsMention()).queue());
                        lp_userBal.addAndGet(-selectedTool.cost);
                        mainLoop();
                    }
                },
                30, TimeUnit.SECONDS,
                () -> {
                    // Put back bal and tool
                    lp_ownedTools.asBits();
                    lp_userBal.get();
                }
        );
    }

}

