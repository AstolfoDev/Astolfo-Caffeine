package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

public class Balance extends Command {

    public Balance() {
        super.name = "balance";
        super.aliases = new String[]{"bal","money","wallet","cash"};
        super.help = "view your eco balance";
        super.category = new Category("economy");
    }

    @Override
    protected void execute(CommandEvent e) {
        Message msg = e.getMessage();
        User u = msg.getAuthor();
        if (msg.getMentionedUsers().size() >= 1) {
          u = msg.getMentionedUsers().get(0);
        }

        if (u.isBot()) {
          msg.getChannel().sendMessage("beep boop! nuu robot overlords pls thx~ <3").queue();
            return;
        }

        Document doc = new CloudData().get_data(u.getIdLong(), CloudData.Database.Economy, CloudData.Collection.wallets);

        if (doc == null) {
            msg.getChannel().sendMessage(new Logging().error("Balance for the selected user was not found!")).queue();
            return;
        }

        new Logging().send(
                msg,
                null,
                u.getAsTag(),
                null,
                new String[]{"Credits", doc.get("credits").toString() + " <:credit:738537190652510299>", "true"},
                new String[]{"Trap Coins", doc.get("trapcoins").toString() + " <:trapcoin:738537189884821606>", "true"},
                new String[]{"Tokens", doc.get("tokens").toString() + " <:token:738537180003172354>", "true"}
        );
    }
}