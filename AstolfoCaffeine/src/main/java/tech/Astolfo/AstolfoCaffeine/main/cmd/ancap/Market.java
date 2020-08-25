package tech.Astolfo.AstolfoCaffeine.main.cmd.ancap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.web.webAPI;

public class Market extends Command {

    public Market() {
        super.name = "market";
        super.aliases = new String[]{"markets","stocks","stockmarket","aex","astolfoex"};
        super.help = "view the AstolfoEx market";
        super.category = new Category("ancap");
    }

    @Override
    protected void execute(CommandEvent e) {
        Message msg = e.getMessage();
        double astf = new webAPI().get_price("AMZN");
        double gudk = new webAPI().get_price("AAPL");
        double clwn = new webAPI().get_price("TSLA");
        double weeb = new webAPI().get_price("GOOGL");
        double emo = new webAPI().get_price("CIH");
        double vimx = App.round(astf+gudk+clwn+weeb, 2);
        String cr = "<:credit:738537190652510299>";

        MessageEmbed embed = new EmbedBuilder()
            .setAuthor("AstolfoEx Market", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
            .setFooter(System.getenv("VERSION_ID"), msg.getJDA().getSelfUser().getAvatarUrl())
            .setColor(0xde1073)
            .addField("Team Astolfo. **(ASTF)**", astf+" "+cr, true)
            .addField("Gudako, Corp. **(GUDK)**", gudk+" "+cr, true)
            .addField("KAY&VIM Index **(^VIM)**", vimx+" "+cr, true)
            .addField("Ishtar Motors. **(WEEB)**", clwn+" "+cr, true)
            .addField("Meliodaf, Inc. **(WOLF)**", weeb+" "+cr, true)
            .addField("Emortal, Inc. **(EMO)**", emo+" "+cr, true)
            .build();
        msg.getChannel().sendMessage(embed).queue();
    }
}