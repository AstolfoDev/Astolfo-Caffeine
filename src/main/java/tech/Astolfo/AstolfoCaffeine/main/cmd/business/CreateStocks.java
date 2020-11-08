package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

public class CreateStocks extends Command {

    private final EventWaiter waiter;

    public CreateStocks(EventWaiter waiter) {
        super.name = "createstocks";
        super.aliases = new String[]{"createstock", "goprivate"};
        super.help = "create shares for your company to trade!";
        super.category = new Category("business");

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {
        User author = e.getAuthor();
        MessageChannel channel = e.getChannel();

        Document companyData = new CloudData().get_data(author.getIdLong(), CloudData.Database.Economy, CloudData.Collection.company);

        if (companyData == null) {
            e.getChannel().sendMessage(new Logging().error("You don't belong to a company!")).queue();
            return;

        } else if (companyData.containsKey("ticker")) {
            e.getChannel()
                    .sendMessage(
                            new Logging().error(
                                    String.format(
                                            "`%s` already has a ticker and registered shares!",
                                            companyData.getString("name")
                                    )
                            )
                    )
                    .queue();
            return;

        } else if (!companyData.getLong("owner").equals(author.getIdLong())) {
            e.getChannel()
                    .sendMessage(
                            new Logging().error(
                                    String.format(
                                            "You are not the owner of `%s`!",
                                            companyData.getString("name")
                                    )
                            )
                    ).queue();
            return;

        }

        EmbedBuilder embed = new Logging().embed()
                .setAuthor("How many shares would you like to start with?");

        Message message = e.getChannel()
                .sendMessage(embed.build())
                .complete();

    }
}
