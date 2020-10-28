package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Block;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Tool;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Toolbox;

import java.util.concurrent.TimeUnit;

public class Shop extends Command {

    private final EventWaiter waiter;

    public Shop(EventWaiter waiter) {
        super.name = "shop";
        super.aliases = new String[]{"shp", "upgrade"};
        super.help = "buy stuff";
        super.arguments = "";
        super.category = new Category("economy");
        this.waiter = waiter;
    }


    private Message lp_HostMsg;
    private Message lp_cmdMsg;
    private Toolbox lp_ownedTools;
    private Double lp_userBal;

    @Override
    protected void execute(CommandEvent e) {

        lp_cmdMsg = e.getMessage();
        User u = lp_cmdMsg.getAuthor();
        Toolbox defaultTools = Toolbox.DefaultTools;

        lp_userBal = new CloudData().get_data(u.getIdLong(), CloudData.Database.Economy, CloudData.Collection.wallets).getDouble("credits");

        // DB magic here to get users tools -> e.g. 0 (no tools)
        lp_ownedTools = Toolbox.fromUserID(u.getIdLong());

        String[][] fields = new String[defaultTools.tools.size() + 1][4];
        fields[0] = new String[]{"Stats", "Stone:\nWood:\nDirt:\nCost:", "true"};
        for (int i = 0; i < fields.length - 1; i++)
        {
            Tool tool = defaultTools.tools.get(i);
            fields[i + 1] = new String[]{tool.emote.getAsMention(), String.format("**%s**\n**%s**\n**%s**\n**%s**", tool.specs.get(Block.Material.STONE), tool.specs.get(Block.Material.WOOD), tool.specs.get(Block.Material.DIRT), tool.cost), "true"};
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
                    } else if (selectedTool.cost > lp_userBal) {
                        lp_cmdMsg.getChannel().sendMessage("Oof, is tooooo expensive for u").queue();
                        mainLoop();
                    } else {
                        lp_ownedTools.addTool(selectedTool);
                        lp_cmdMsg.getChannel().sendMessage("Yayyyy, here special for you onlyyyyyy").queue(THIS_VARIABLE_DOESNT_DO_ANYTHING_DO_WE_REALLY_NEED_TO_DEFINE_IT_QUESTION_MARK -> lp_cmdMsg.getChannel().sendMessage(selectedTool.emote.getAsMention()).queue());
                        lp_userBal -= selectedTool.cost;
                        mainLoop();
                    }
                },
                60, TimeUnit.SECONDS,
                () -> {
                    lp_HostMsg.editMessage("Shop expired!").queue();

                    // Put back bal and tool


                    /*TODO: Fix this

                    /System.out.println("Owned: " + lp_ownedTools.asBits() + "\nBal: " + lp_userBal);

                    Bson user_filter = eq("userID", lp_cmdMsg.getAuthor().getIdLong());

                    Bson balUpdate = set("credits", lp_userBal);
                    App.col.updateOne(user_filter, balUpdate);

                    Bson toolsUpdate = set("tools", lp_ownedTools.asBits());
                    App.db.getCollection("tools").updateOne(user_filter, toolsUpdate);

                     */
                }
        );
    }

}