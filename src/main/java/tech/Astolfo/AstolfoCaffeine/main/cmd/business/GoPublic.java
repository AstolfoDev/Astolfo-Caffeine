package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.bson.Document;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoPublic extends Command {

    private final EventWaiter waiter;

    public GoPublic(EventWaiter waiter) {
        super.name = "gopublic";
        super.aliases = new String[]{"tradepublic", "publiccompany", "pubcomp"};
        super.help = "make your company trade publically!";
        super.category = new Category("business");

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {

        /*

        NOTES:
        -- U.S Markets --
        To qualify for NYSE listing, a company must have at least 400 shareholders who own more than 100 shares of stock,
        have at least 1.1 million shares of publicly traded stock and have a market value of public shares of at least $40 million.
        The stock price must be at least $4 a share.


        Tickers/Symbology : https://www.nyse.com/publicdocs/nyse/listing/secapproval1108.pdf
                         :: https://www.nyse.com/publicdocs/nyse/listing/symbology_nms_plan.pdf
                        ::: https://www.nyse.com/publicdocs/nyse/listing/appendix1208.pdf

        https://www.nyse.com/listings-process

        Listing requirements vary by exchange but there are certain metrics which are almost always included.
        The two most important categories of requirements deal with the size of the firm (as defined by annual income or market capitalization)
        and the liquidity of the shares (a certain number of shares must already have been issued).

        For example, the NYSE requires firms to already have 1.1 million publicly-traded shares outstanding with a collective market value of
        at least $100 million; the Nasdaq requires firms to already have 1.25 million publicly-traded shares with a collective market value of $45 million.
        Both the NYSE and the Nasdaq require a minimum security listing price of $4 per share.

        There is generally a listing fee involved as well as yearly listing fees, which scale up depending on the number of shares
        being traded and can total hundreds of thousands of dollars. Nasdaq fees are considerably lower than those of the NYSE,
        which has historically made the Nasdaq a more popular choice for newer or smaller firms.

        https://www.investopedia.com/terms/l/listingrequirements.asp

        -- U.K Markets --
        PLCs must: have at least two shareholders. have issued shares to the public to a value of at least £50,000
        or the prescribed equivalent in euros before it can trade. be registered with Companies House.


        Market capitalisation
        LR 2.2.7 R 01/07/2005 RP
            (1)
            The expected aggregate market value of all securities (excluding treasury shares) to be listed must be at least:
                (a)
                    £700,000 for shares; and
                (b)
                    £200,000 for debt securities.

        https://www.handbook.fca.org.uk/handbook/LR/2/?view=chapter

         */

        User author = e.getAuthor();
        MessageChannel channel = e.getChannel();

        Document companyData = new CloudData().get_data(author.getIdLong(), CloudData.Database.Economy, CloudData.Collection.company);

        if (companyData == null) {
            e.getChannel().sendMessage(new Logging().error("You don't belong to a company!")).queue();
            return;
        }

        List<Long> members = companyData.getList("members", Long.class);
        int bank = companyData.getInteger("bank");

        // Requirements to go public? What's next, requirements to make toast in your own damn toaster?
        if (!companyData.getLong("owner").equals(author.getIdLong())) {
            e.getChannel().sendMessage(new Logging().error(String.format("You are not the owner of `%s`!", companyData.getString("name")))).queue();
            return;
        }

        MessageEmbed start = new Logging().send(null, "Alrighty, to start the process of taking your company public...\n\nPlease react to this message with " +
                "✅", "AstolfoEx Marketplace", companyData.getString("logo"));

        channel.sendMessage(start).queue(
                msg -> {
                    msg.addReaction("✅").queue();
                    start(e, author, channel, msg);
                }
        );
    }

    private void start(CommandEvent e, User author, MessageChannel channel, Message msg) {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,

                check -> author.getIdLong() == check.getUser().getIdLong() && msg.getIdLong() == check.getMessageIdLong(),

                action -> {
                    if (action.getReactionEmote().getName().equals("✅")) {
                        action.getReaction().removeReaction(author).queue();

                    } else {
                        action.getReaction().removeReaction(author).queue();
                        start(e, author, channel, msg);
                    }
                },

                60, TimeUnit.SECONDS,
                () -> {
                    channel.sendMessage("**Woah!** you ranz outta dat time 2 react, guess ur comp aint goin public ;P").queue();
                }

        );
    }
}