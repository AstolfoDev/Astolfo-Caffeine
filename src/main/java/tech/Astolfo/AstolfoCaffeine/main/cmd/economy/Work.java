package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import org.bson.Document;
import org.bson.conversions.Bson;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.BasicDBObject;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Block;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.MCgame;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Tool;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Toolbox;

public class Work extends Command {

  private EventWaiter waiter;



    public Work(EventWaiter waiter) {
        super.name = "work";
        super.aliases = new String[]{"w","job"};
        super.help = "earn a salary at your company";
        super.category = new Category("economy");
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) throws NumberFormatException {
        Message msg = e.getMessage();
        int cooldown = 5; // TODO: Set to 300
        if (App.cooldown.containsKey(msg.getAuthor().getIdLong())) {
          long time = (System.currentTimeMillis() - App.cooldown.get(msg.getAuthor().getIdLong()))/1000;
          if (time < cooldown) {
            msg.getChannel().sendMessage("oi! u gotta wait 4 da cooldown 2 expire before workin' again ;P\n*("+(cooldown-time)+" seconds leftzZzz...)*").queue();
            return;
          } else {
            App.cooldown.remove(msg.getAuthor().getIdLong());
          }
        }
        App.cooldown.put(msg.getAuthor().getIdLong(), System.currentTimeMillis());

        Toolbox toolbox = new Toolbox();
        toolbox.addTool(new Tool(2, 0, 0, msg.getGuild().getEmoteById("737855791171895308")));
        toolbox.addTool(new Tool(0, 0, 1, msg.getGuild().getEmotes().get(1)));
        Block block = new Block("Imo-Imo", "https://cloud.orz.cx/public/ImoImo/%d.jpg", Block.Material.STONE, (short) 0, 2, 3, 10, 10);
        msg.getChannel().sendMessage("Loading...").queue(
                (message) -> new MCgame(block, toolbox, message, waiter).runThen(
                        () -> {
                            if (block.state == Block.State.BROKEN) {
                                message.editMessage("Nice you broke it").queue();
                            } else if (block.state == Block.State.EXPIRED) {
                                message.editMessage("You are too slow").queue();
                            }
                        }
                )
        );
    }


    // TODO: Fix the spaghetti code in here
    private void success(Message msg) {

        // TODO: This might have a bug, where it always returns null, even when you're in a company
        BasicDBObject filter1 = new BasicDBObject().append("members", new BasicDBObject("$in", Arrays.asList(msg.getAuthor().getIdLong())));
        Document comp = App.company.find(filter1).first();

        float extra = 0;
        float cut = 1;


        if (comp != null) {
            extra = Float.parseFloat(String.valueOf(0.01 * comp.getInteger("xp")));
            cut = Float.parseFloat(String.valueOf(0.01 * comp.getInteger("cut")));

            Bson up = set("xp", comp.getInteger("xp") + 1);
            App.company.updateOne(filter1, up);

            return;
        }


        Document doc = new Database().get_account(msg.getAuthor().getIdLong());

        int rand = (int) Math.round(Math.random() * 5) + 1;
        int tax = rand/4;
        int pay = rand-tax;

        if (extra != 0) {
          pay = Math.round(rand + extra);
        }

        int user_cut = (int) (pay * cut);
        int comp_cut = pay - user_cut;

        if (comp != null) {
            App.company.updateOne(filter1, set("bank", comp.getInteger("bank") + comp_cut));
        }


        Bson filter2 = eq("userID", msg.getAuthor().getIdLong());
        Bson update = set("credits", doc.getDouble("credits") + user_cut);

        App.col.updateOne(filter2, update);


        EmbedBuilder eb = App.embed()
            .setAuthor("Work Complete!", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
            .setDescription("You earned "+user_cut+" <:credit:738537190652510299> from working!\nYou now have **"+(doc.getDouble("credits")+user_cut)+"** <:credit:738537190652510299>");


        if (tax > 0) {
            eb.setDescription("You earned "+user_cut+" <:credit:738537190652510299> *(taxed at 25%)* from working!\nYou now have **"+(doc.getDouble("credits")+user_cut)+"** <:credit:738537190652510299>");
        }

        if (comp != null) {
          eb.appendDescription("\nCompany Bonus: `"+extra+"`\nCompany Cut: `"+cut+"`");
        }


        msg.getChannel().sendMessage(eb.build()).queue();
    }
}
