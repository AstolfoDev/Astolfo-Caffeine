package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import org.bson.Document;
import org.bson.conversions.Bson;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.BasicDBObject;

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
        int cooldown = 300;
        if (App.cooldown.containsKey(msg.getAuthor().getIdLong())) {
          long time = (System.currentTimeMillis()-App.cooldown.get(msg.getAuthor().getIdLong()))/1000;
          if (time < cooldown) {
            msg.getChannel().sendMessage("oi! u gotta wait 4 da cooldown 2 expire before workin' again ;P\n*("+(cooldown-time)+" seconds leftzZzz...)*").queue();
            return;
          } else {
            App.cooldown.remove(msg.getAuthor().getIdLong());
          }
        }
        App.cooldown.put(msg.getAuthor().getIdLong(), System.currentTimeMillis());

        BasicDBObject filter1 = new BasicDBObject()
            .append("members", new BasicDBObject("$in",Arrays.asList(e.getMessage().getAuthor().getIdLong())));

        Document comp = App.company.find(filter1).first();

        if (comp != null) {
          Float num = Float.parseFloat(String.valueOf(0.01*comp.getInteger("xp")));
          Bson up = set("xp", comp.getInteger("xp")+1);
          App.company.updateOne(filter1, up);
          success(msg, num);
          return;
        }
        success(msg, null);
        return;
    }
    
    private void miningGame(Message msg) {

      waiter.waitForEvent(
      GuildMessageReactionAddEvent.class, 
      ev -> {
        return msg.getAuthor().getIdLong() == ev.getUser().getIdLong();
      }, 
      ev -> {
        if(ev.getReactionEmote().equals("minn:683035609014861870")) msg.editMessage("MINE HARDER");
        return;
      }
      );

    }

    private void success(Message msg, @Nullable Float extra) {
        new Database().create_account(msg.getAuthor().getIdLong());
        Document doc = new Database().get_account(msg.getAuthor().getIdLong());

        int rand = Math.round((float) Math.random()*5)+1;
        int tax = rand/4;

        int pay = rand-tax;

        if (extra != null) {
          pay = Math.round((float) rand+extra);
        }
      
        Bson filter = eq("userID", msg.getAuthor().getIdLong());
        Bson update = set("credits", doc.getDouble("credits")+pay);

        App.col.updateOne(filter, update);

        EmbedBuilder eb = App.embed(msg)
            .setAuthor("Work Complete!", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
            .setDescription("You earned "+pay+" <:credit:738537190652510299> from working!\nYou now have **"+(doc.getDouble("credits")+pay)+"** <:credit:738537190652510299>");
        if (tax > 0) {
            eb.setDescription("You earned "+pay+" <:credit:738537190652510299> *(taxed at 25%)* from working!\nYou now have **"+(doc.getDouble("credits")+pay)+"** <:credit:738537190652510299>");
        }
        if (extra != null) {
          eb.appendDescription("\nCompany Bonus: `"+extra+"`");
        }

        msg.getChannel().sendMessage(eb.build()).queue();
    }
}