package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import tech.Astolfo.AstolfoCaffeine.main.util.maths.Rounding;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class Pay extends Command {
    public Pay() {
        super.name = "pay";
        super.aliases = new String[]{"send","give","sendmoney","givemoney"};
        super.help = "send credits to another user";
        super.arguments = "<@user> <item>";
        super.category = new Category("economy");
    }

    @Override
    protected void execute (CommandEvent e) {
        try {
            Message msg = e.getMessage();

            String[] args = e.getArgs().split("\\s+");
            List<User> mentions = msg.getMentionedUsers();

            if (args.length < 1) {
                msg.getChannel().sendMessage(new Logging().error("Sowwy! You fOwOgot to mention the user you want to pay...\n*(Ex: "+System.getenv("PREFIX")+"pay <@682220266901733381> 10)*")).queue();
                return;
            } else if (mentions.size() < 1) {
                msg.getChannel().sendMessage(new Logging().error("Sowwy! You fOwOgot to mention the user you want to pay...\n*(Ex: "+System.getenv("PREFIX")+"pay <@682220266901733381> 10)*")).queue();
                return;
            } else if (args.length > 2) {
                msg.getChannel().sendMessage(new Logging().error("sozzzzz! You entered too many arguwuments...\n*(Ex: "+System.getenv("PREFIX")+"pay <@682220266901733381> 10)*")).queue();
                return;
            }
            if (mentions.get(0).isBot()) {
                msg.getChannel().sendMessage("beep boop! nuu robot overlords pls thx~ <3").queue();
                return;
            } else if (args.length < 2) {
                msg.getChannel().sendMessage(new Logging().error("Sowwy! You fOwOgot to put the amount you want to send...\n*(Ex: " + System.getenv("PREFIX") + "pay <@682220266901733381> 10)*")).queue();
                return;
            }

            Document doc = new CloudData().get_data(e.getAuthor().getIdLong(), CloudData.Database.Economy, CloudData.Collection.wallets);
            Document doc2 = new CloudData().get_data(mentions.get(0).getIdLong(), CloudData.Database.Economy, CloudData.Collection.wallets);

            double amt = Double.parseDouble(args[1]);

            amt = Rounding.round(amt, 0);

            if (amt < 1) {
                msg.getChannel().sendMessage(new Logging().error("u canz send less than 1 credit!!!")).queue();
                return;
            }

           if (!(msg.getAuthor().getId().equals("508777382472187914") || msg.getAuthor().getId().equals("246357206973415425"))) {

                if (amt > Double.parseDouble(doc.get("credits").toString())) {
                    msg.getChannel().sendMessage(new Logging().error("OooooOwOh nuuuu! u dont hazzz enough cwedits 4 dat transfer!!!")).queue();
                    return;
                }

                Bson up1 = set("credits", Double.parseDouble(doc.get("credits").toString())-amt);
                Bson f1 = eq("userID", msg.getAuthor().getIdLong());
               new CloudData().update_data(f1, up1, CloudData.Database.Economy, CloudData.Collection.wallets);
            }

            Bson up2 = set("credits", Double.parseDouble(doc2.get("credits").toString())+amt);
            Bson f2 = eq("userID", mentions.get(0).getIdLong());
            new CloudData().update_data(f2, up2, CloudData.Database.Economy, CloudData.Collection.wallets);
            
            MessageEmbed embed = new EmbedBuilder()
                    .setAuthor("Transfer Successful!", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
                    .setFooter(System.getenv("VERSION_ID"), msg.getJDA().getSelfUser().getAvatarUrl())
                    .setColor(0xde1073)
                    .setThumbnail("https://i.imgur.com/xxpd9tq.png")
                    .addField(msg.getAuthor().getName(), new CloudData().get_data(msg.getAuthor().getIdLong(), CloudData.Database.Economy, CloudData.Collection.wallets).get("credits") + " <:credit:738537190652510299>", true)
                    .addField(mentions.get(0).getName(), new CloudData().get_data(mentions.get(0).getIdLong(), CloudData.Database.Economy, CloudData.Collection.wallets).get("credits") + " <:credit:738537190652510299>", true)
                .build();
            msg.getChannel().sendMessage(embed).queue();
  
        } catch (NumberFormatException err) {
          e.getChannel().sendMessage(new Logging().error("HeyyYYy! That's not a valid number ovvvv cwedits to send!!!")).queue();
        }
    }
}