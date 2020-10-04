package tech.Astolfo.AstolfoCaffeine.main.cmd.ancap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import tech.Astolfo.AstolfoCaffeine.main.web.webAPI;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class SellAllStocks extends Command {

    public SellAllStocks() {
        super.name = "sellall";
        super.aliases = new String[]{"shares"};
        super.help = "sells all stocks";
        super.category = new Category("ancap");
        super.arguments = "<@user>";
    }

    @Override
    protected void execute(CommandEvent e) {
        Message msg = e.getMessage();
        String[] args = e.getArgs().split("\\s+");

        new Database().create_account(msg.getAuthor().getIdLong());
        Document wallet = new Database().get_account(msg.getAuthor().getIdLong());

        new Database().create_stocks(msg.getAuthor().getIdLong());
        Document portfolio = new Database().get_stocks(msg.getAuthor().getIdLong());



        Bson filter = eq("userID", msg.getAuthor().getIdLong());
        String cr = "<:credit:738537190652510299>";

        if (args.length < 1 || args[0].equals("")) {
            msg.getChannel().sendMessage(new Logging().error("nuuuuu! u fOwOgot to write which kind of stock u wanz to sell...\n*(Ex. "+System.getenv("PREFIX")+"sellall ASTF)*")).queue();
            return;
        }

        String stock = args[0].toLowerCase();
        String company;
        String ticker;

        switch (stock) {
            default:
                msg.getChannel().sendMessage(new Logging().error("invalid ticker! view tickers @ "+System.getenv("PREFIX")+"market")).queue();
                return;
            case "astf":
            case "astolfo":
            case "teamastolfo":
            case "team_astlfo":
                stock = "AMZN";
                company = "Team Astolfo";
                ticker = "astf";
                break;
            case "gudk":
            case "gudako":
            case "gudakocorp":
            case "gudakocorporation":
                stock = "AAPL";
                company = "Gudako, Corp.";
                ticker = "gudk";
                break;
            case "vim":
            case "kay&vim":
            case "^vim":
            case "kay":
            case "vimvim":
                stock = "VIM";
                company = "KAY&VIM Index";
                ticker = "vimx";
                break;
            case "clwn":
            case "ishtar":
            case "clown":
            case "ishtarmotors":
            case "weeb":
                stock = "TSLA";
                company = "Ishtar Motors.";
                ticker = "weeb";
                break;
            case "meliodaf":
            case "meliodas":
            case "wolf":
                stock = "GOOGL";
                company = "Meliodaf, Inc.";
                ticker = "wolf";
                break;
            case "emo":
            case "emortal":
            case "xeno":
            case "emortalinc":
                stock = "CIH";
                company = "Emortal, Inc.";
                ticker = "emo";
                break;
        }

        int amt = portfolio.getInteger(ticker);
        double price;
        Bson up1;
        Bson up2;


        switch (stock) {
            default:
                msg.getChannel().sendMessage(new Logging().error("huh!? there was an unexpected problem handling dissss reqwest.... uh... contact the devs @ https://discord.gg/RSEpxVJ for help...")).queue();
                return;

            case "AMZN":
                if (amt > portfolio.getInteger("astf")) {
                    msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                    return;
                }

                price = App.round((new webAPI().get_price("AMZN")*amt), 2);

                up1 = set("credits", App.round(wallet.getDouble("credits")+price, 2));
                up2 = set("astf", portfolio.getInteger("astf")-amt);

                App.col.updateOne(filter, up1);
                App.stocks.updateOne(filter, up2);

                new Logging().send(e.getMessage(), "Sell order successfully placed!", "AstolfoEx Market", null, new String[]{"Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", "false"});
                return;

            case "AAPL":
                if (amt > portfolio.getInteger("gudk")) {
                    msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                    return;
                }

                price = App.round((new webAPI().get_price("AAPL")*amt), 2);

                up1 = set("credits", App.round(wallet.getDouble("credits")+price, 2));
                up2 = set("gudk", portfolio.getInteger("gudk")-amt);

                App.col.updateOne(filter, up1);
                App.stocks.updateOne(filter, up2);

                new Logging().send(e.getMessage(), "Sell order successfully placed!", "AstolfoEx Market", null, new String[]{"Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", "false"});
                return;

            case "VIM":
                if (amt > portfolio.getInteger("vimx")) {
                    msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                    return;
                }

                price = App.round((new webAPI().get_price("AMZN"))+(new webAPI().get_price("AAPL"))+(new webAPI().get_price("GOOGL"))+(new webAPI().get_price("TSLA"))*amt, 2);

                up1 = set("credits", App.round(wallet.getDouble("credits")+price, 2));
                up2 = set("vimx", portfolio.getInteger("vimx")-amt);

                App.col.updateOne(filter, up1);
                App.stocks.updateOne(filter, up2);

                new Logging().send(e.getMessage(), "Sell order successfully placed!", "AstolfoEx Market", null, new String[]{"Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", "false"});
                return;

            case "TSLA":
                if (amt > portfolio.getInteger("weeb")) {
                    msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                    return;
                }
                price = App.round((new webAPI().get_price("TSLA")*amt), 2);

                up1 = set("credits", App.round(wallet.getDouble("credits")+price, 2));
                up2 = set("weeb", portfolio.getInteger("weeb")-amt);

                App.col.updateOne(filter, up1);
                App.stocks.updateOne(filter, up2);

                new Logging().send(e.getMessage(), "Sell order successfully placed!", "AstolfoEx Market", null, new String[]{"Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", "false"});
                return;

            case "GOOGL":
                if (amt > portfolio.getInteger("wolf")) {
                    msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                    return;
                }

                price = App.round((new webAPI().get_price("GOOGL")*amt), 2);

                up1 = set("credits", App.round(wallet.getDouble("credits")+price, 2));
                up2 = set("wolf", portfolio.getInteger("wolf")-amt);

                App.col.updateOne(filter, up1);
                App.stocks.updateOne(filter, up2);

                new Logging().send(e.getMessage(), "Sell order successfully placed!", "AstolfoEx Market", null, new String[]{"Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", "false"});
                return;

            case "CIH":
                if (amt > portfolio.getInteger("emo")) {
                    msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                    return;
                }

                price = App.round((new webAPI().get_price("CIH")*amt), 2);

                up1 = set("credits", App.round(wallet.getDouble("credits")+price, 2));
                up2 = set("emo", portfolio.getInteger("emo")-amt);

                App.col.updateOne(filter, up1);
                App.stocks.updateOne(filter, up2);

                new Logging().send(e.getMessage(), "Sell order successfully placed!", "AstolfoEx Market", null, new String[]{"Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", "false"});
                break;
        }

    }
}
