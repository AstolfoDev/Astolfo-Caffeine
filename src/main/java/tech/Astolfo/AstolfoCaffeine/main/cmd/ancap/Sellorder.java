package tech.Astolfo.AstolfoCaffeine.main.cmd.ancap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import tech.Astolfo.AstolfoCaffeine.main.util.maths.Maths;
import tech.Astolfo.AstolfoCaffeine.main.web.webAPI;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class Sellorder extends Command {
    public Sellorder() {
        super.name = "sellorder";
        super.aliases = new String[]{"so","sell"};
        super.help = "sell shares on the market";
        super.category = new Category("ancap");
        super.arguments = "<stock> <amt>";
    }

    @Override
    protected void execute (CommandEvent e) {
        Message msg = e.getMessage();
        String[] args = e.getArgs().split("\\s+");
        try {
            Document doc = new CloudData().get_data(e.getAuthor().getIdLong(), CloudData.Database.Economy, CloudData.Collection.wallets);
            Document doc2 = new CloudData().get_data(e.getAuthor().getIdLong(), CloudData.Database.Economy, CloudData.Collection.stocks);

            Bson filter = eq("userID", msg.getAuthor().getIdLong());
            String cr = "<:credit:738537190652510299>";

            if (args.length < 1 || args[0].equals("")) {
                msg.getChannel().sendMessage(new Logging().error("nuuuuu! u fOwOgot to write which stock u wanz to sell...\n*(Ex. " + System.getenv("PREFIX") + "selloroder ASTF 100)*")).queue();
                return;
            } else if (args.length < 2) {
                msg.getChannel().sendMessage(new Logging().error("ahhhhH!!! u forgot to specifyyyy how many shares u wanna sellLLlz...\n*(Ex. " + System.getenv("PREFIX") + "sellorder ASTF 100)*")).queue();
                return;
            }

            String stock = args[0].toLowerCase();
            String company;

            EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("AstolfoEx Market", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
                .setFooter(System.getenv("VERSION_ID"), msg.getJDA().getSelfUser().getAvatarUrl())
                .setColor(0xde1073);

            switch (stock) {
                default -> {
                    msg.getChannel().sendMessage(new Logging().error("invalid ticker! view tickers @ " + System.getenv("PREFIX") + "market")).queue();
                    return;
                }
                case "astf", "astolfo", "teamastolfo", "team_astlfo" -> {
                    stock = "AMZN";
                    company = "Team Astolfo";
                }
                case "gudk", "gudako", "gudakocorp", "gudakocorporation" -> {
                    stock = "AAPL";
                    company = "Gudako, Corp.";
                }
                case "vim", "kay&vim", "^vim", "kay", "vimvim" -> {
                    stock = "VIM";
                    company = "KAY&VIM Index";
                }
                case "clwn", "ishtar", "clown", "ishtarmotors", "weeb" -> {
                    stock = "TSLA";
                    company = "Ishtar Motors.";
                }
                case "meliodaf", "meliodas", "wolf" -> {
                    stock = "GOOGL";
                    company = "Meliodaf, Inc.";
                }
                case "emo", "emortal", "xeno", "emortalinc" -> {
                    stock = "CIH";
                    company = "Emortal, Inc.";
                }
            }

            int amt = (int) Float.parseFloat(args[1]);
            
            if (amt < 1) {
                msg.getChannel().sendMessage(new Logging().error("nO! u cant jusT make an order for less thAn 1 sharee...")).queue();
                return;
            }

            double price;
            MessageEmbed embed;
            Bson up1;
            Bson up2;


            switch (stock) {
                default -> msg.getChannel().sendMessage(new Logging().error("huh!? there was an unexpected problem handling dissss reqwest.... uh... contact the devs @ https://discord.gg/RSEpxVJ for help...")).queue();
                case "AMZN" -> {
                    if (amt > doc2.getInteger("astf")) {
                        msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                        return;
                    }
                    price = Maths.Rounding.round((new webAPI().get_price("AMZN") * amt), 2);
                    up1 = set("credits", Maths.Rounding.round(doc.getDouble("credits") + price, 2));
                    up2 = set("astf", doc2.getInteger("astf") - amt);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.wallets).updateOne(filter, up1);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.stocks).updateOne(filter, up2);
                    embed = eb
                            .setDescription("Sell order successfully placed!")
                            .addField("Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", false)
                            .build();
                    msg.getChannel().sendMessage(embed).queue();
                }
                case "AAPL" -> {
                    if (amt > doc2.getInteger("gudk")) {
                        msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                        return;
                    }
                    price = Maths.Rounding.round((new webAPI().get_price("AAPL") * amt), 2);
                    up1 = set("credits", Maths.Rounding.round(doc.getDouble("credits") + price, 2));
                    up2 = set("gudk", doc2.getInteger("gudk") - amt);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.wallets).updateOne(filter, up1);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.stocks).updateOne(filter, up2);
                    embed = eb
                            .setDescription("Sell order successfully placed!")
                            .addField("Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", false)
                            .build();
                    msg.getChannel().sendMessage(embed).queue();
                }
                case "VIM" -> {
                    if (amt > doc2.getInteger("vimx")) {
                        msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                        return;
                    }
                    price = Maths.Rounding.round((new webAPI().get_price("AMZN")) + (new webAPI().get_price("AAPL")) + (new webAPI().get_price("GOOGL")) + (new webAPI().get_price("TSLA")) * amt, 2);
                    up1 = set("credits", Maths.Rounding.round(doc.getDouble("credits") + price, 2));
                    up2 = set("vimx", doc2.getInteger("vimx") - amt);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.wallets).updateOne(filter, up1);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.stocks).updateOne(filter, up2);
                    embed = eb
                            .setDescription("Sell order successfully placed!")
                            .addField("Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", false)
                            .build();
                    msg.getChannel().sendMessage(embed).queue();
                }
                case "TSLA" -> {
                    if (amt > doc2.getInteger("weeb")) {
                        msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                        return;
                    }
                    price = Maths.Rounding.round((new webAPI().get_price("TSLA") * amt), 2);
                    up1 = set("credits", Maths.Rounding.round(doc.getDouble("credits") + price, 2));
                    up2 = set("weeb", doc2.getInteger("weeb") - amt);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.wallets).updateOne(filter, up1);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.stocks).updateOne(filter, up2);
                    embed = eb
                            .setDescription("Sell order successfully placed!")
                            .addField("Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", false)
                            .build();
                    msg.getChannel().sendMessage(embed).queue();
                }
                case "GOOGL" -> {
                    if (amt > doc2.getInteger("wolf")) {
                        msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                        return;
                    }
                    price = Maths.Rounding.round((new webAPI().get_price("GOOGL") * amt), 2);
                    up1 = set("credits", Maths.Rounding.round(doc.getDouble("credits") + price, 2));
                    up2 = set("wolf", doc2.getInteger("wolf") - amt);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.wallets).updateOne(filter, up1);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.stocks).updateOne(filter, up2);
                    embed = eb
                            .setDescription("Sell order successfully placed!")
                            .addField("Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", false)
                            .build();
                    msg.getChannel().sendMessage(embed).queue();
                }
                case "CIH" -> {
                    if (amt > doc2.getInteger("emo")) {
                        msg.getChannel().sendMessage(new Logging().error("ahHHHHHHhhhh u no haz that many shares dat u can sell....")).queue();
                        return;
                    }
                    price = Maths.Rounding.round((new webAPI().get_price("CIH") * amt), 2);
                    up1 = set("credits", Maths.Rounding.round(doc.getDouble("credits") + price, 2));
                    up2 = set("emo", doc2.getInteger("emo") - amt);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.wallets).updateOne(filter, up1);
                    new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.stocks).updateOne(filter, up2);
                    embed = eb
                            .setDescription("Sell order successfully placed!")
                            .addField("Order Summary", "**x" + amt + "** shares (" + company + ") @ " + price / amt + " " + cr + "/share", false)
                            .build();
                    msg.getChannel().sendMessage(embed).queue();
                }
            }


        } catch (NumberFormatException ignored) {
            msg.getChannel().sendMessage(new Logging().error("OooooOoh no! u gave me and invalid numbwer ;(\n*(Hint: make sure it's a whole number!)*")).queue();
        }
    }
}