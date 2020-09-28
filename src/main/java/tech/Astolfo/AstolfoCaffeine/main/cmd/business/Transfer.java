package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;
import tech.Astolfo.AstolfoCaffeine.main.util.ParamChecker;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import java.util.List;
import static com.mongodb.client.model.Updates.set;

public class Transfer extends Command  {

    public Transfer() {
        super.name = "transfer";
        super.aliases = new String[]{"t","trans"};
        super.help = "HELP HELP";
        super.arguments = "<@user> <item>";
        super.category = new Category("business");
    }

    @Override
    protected void execute(CommandEvent e) {

        Message msg = e.getMessage();
        String[] args = e.getArgs().split("\\s+");

        if  (!new ParamChecker()
                .addCheck(0, "Sowwy! You fOwOgot to mention the user you want to transfer capital tuuuuuuu...\n*(Ex: "+System.getenv("PREFIX")+"transfer <@682220266901733381> 10)*")
                .addCheck(1, "Sowwy! You fOwOgot to put the amount you want to send...\n*(Ex: "+System.getenv("PREFIX")+"transfer <@682220266901733381> 10)*")
                .addCheck(2, "VALID")
                .addCheck(3, "sozzzzz! You entered too many arguwuments...\n*(Ex: "+System.getenv("PREFIX")+"transfer <@682220266901733381> 10)*")
                .parse(e)) return;

        List<User> mentions = msg.getMentionedUsers();
        int amount = Integer.parseInt(args[1]);
        Logging err_channel = new Logging();

        if (mentions.size() < 1) {
            err_channel.error("Sowwy! You fOwOgot to mention the user you want to pay...\n*(Ex: "+System.getenv("PREFIX")+"pay <@682220266901733381> 10)*");
            return;
        } if (mentions.get(0).isBot()) {
            err_channel.error("beep boop! nuu robot overlords pls thx~ <3");
            return;
        } if (amount < 1) {
            err_channel.error("u canz send less than 1 credit!!!");
            return;
        }

        Bson company_filter = new BasicDBObject("owner", msg.getAuthor().getIdLong());
        Document company = App.company.find(company_filter).first();

        if (company == null) {
            err_channel.error("Noooooooo, chu dunt own any company :(");
            return;
        }

        if (company.get("bank") == null) {
          e.reply("nah bruv, dont be returning null like a wasteman");
          return;
        }
        int bank = company.getInteger("bank");

        if (amount > bank) {
            err_channel.error("OooooOwOh nuuuu! u dont hazzz enough cwedits 4 dat transfer!!!");
            return;
        }

        Bson user_filter = new BasicDBObject("userID", mentions.get(0).getIdLong());
        Document user = new Database().get_account(mentions.get(0).getIdLong());
        int user_bal = (int) Math.round(user.getDouble("credits"));

        Bson bank_up = set("bank", bank - amount);
        Bson user_up = set("credits", user_bal + amount);

        App.company.updateOne(company_filter, bank_up);
        App.col.updateOne(user_filter, user_up);


        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Transfer Successful!", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
                .setFooter(System.getenv("VERSION_ID"), msg.getJDA().getSelfUser().getAvatarUrl())
                .setColor(0xde1073)
                .setThumbnail("https://i.imgur.com/xxpd9tq.png")
                .addField("Bank", (bank - amount) +" <:credit:738537190652510299>", true)
                .addField(mentions.get(0).getName(), (user_bal + amount) +" <:credit:738537190652510299>", true)
                .build();
        msg.getChannel().sendMessage(embed).queue();
    }

}

        /*
        FindIterable<Document> companies = App.company.find();
        Document comp = null;
        for (Document company: companies) {
            if (company.getLong("owner") == msg.getAuthor().getIdLong()) {
                comp = company;
                break;
            }
        }
        */
