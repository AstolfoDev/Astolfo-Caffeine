package tech.Astolfo.AstolfoCaffeine.main.cmd.ancap;

import org.bson.Document;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

public class Portfolio extends Command {

    public Portfolio() {
        super.name = "portfolio";
        super.aliases = new String[]{"shares"};
        super.help = "view another user's shares";
        super.category = new Category("ancap");
        super.arguments = "<@user>";
    }

    @Override
    protected void execute (CommandEvent e) {
        Message msg = e.getMessage();
        String[] args = e.getArgs().split("\\s+");

        try {
            long id = msg.getAuthor().getIdLong();
            User user = msg.getAuthor();

            if (args.length == 1 && args[0].startsWith("<")) {
                long temp = Long.parseLong(args[0].replace("<", "").replace("@", "").replace("!", "").replace(">", ""));
                if (msg.getMentionedUsers().get(0).getIdLong() == temp) {
                    id = temp;
                    user = msg.getMentionedUsers().get(0);
                }
            } else if (args.length > 1) {
              msg.getChannel().sendMessage("Too many arguments! Defaulting to command issuer...").queue();
            }

            new Database().create_stocks(id);
            Document doc = new Database().get_stocks(id);

            EmbedBuilder eb = App.embed()
                .setAuthor(user.getAsTag()+" AstolfoEx Portfolio", "https://astolfo.tech", user.getAvatarUrl());
            
            if (doc.getInteger("astf") >= 1) {
              eb.addField("Team Astolfo **(ASTF)**", doc.getInteger("astf")+" shares", true);
            }
            if (doc.getInteger("gudk") >= 1) {
              eb.addField("Gudako, Corp. **(GUDK)**", doc.getInteger("gudk")+" shares", true);
            }
            if (doc.getInteger("vimx") >= 1) {
              eb.addField("KAY&VIM Index **(^VIM)**", doc.getInteger("vimx")+" shares", true);
            }
            if (doc.getInteger("weeb") >= 1) {
              eb.addField("Ishtar Motors **(WEEB)**", doc.getInteger("weeb")+" shares", true);
            }
            if (doc.getInteger("wolf") >= 1) {
              eb.addField("Meliodaf, Inc. **(WOLF)**", doc.getInteger("wolf")+" shares", true);
            }
            if (doc.getInteger("emo") >= 1) {
              eb.addField("Emortal, Inc. **(EMO)**", doc.getInteger("emo")+" shares", true);
            }

            if (eb.getFields().size() < 1) {
              eb.setDescription(user.getName()+" does not currently hold any shares.");
            }

            MessageEmbed embed = eb.build();
            
            msg.getChannel().sendMessage(embed).queue();
        } catch (NumberFormatException err) {
            msg.getChannel().sendMessage(new Logging().error("OooooOoh no! u gave me and invalid numbwer ;(\n*(Hint: make sure it's a whole number!)*")).queue();
        }
    }
}