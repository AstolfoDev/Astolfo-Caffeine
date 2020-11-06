package tech.Astolfo.AstolfoCaffeine.main.cmd.ancap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.Astolfo.AstolfoCaffeine.main.util.maths.Maths;
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
        double astf = Maths.Rounding.round(new webAPI().get_price("AMZN"), 2);
        double gudk = Maths.Rounding.round(new webAPI().get_price("AAPL"), 2);
        double clwn = Maths.Rounding.round(new webAPI().get_price("TSLA"), 2);
        double weeb = Maths.Rounding.round(new webAPI().get_price("GOOGL"), 2);
        double emo = Maths.Rounding.round(new webAPI().get_price("CIH"), 2);
        double vimx = Maths.Rounding.round(astf + gudk + clwn + weeb, 2);
        String cr = "<:credit:738537190652510299>";

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("AstolfoEx Market", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
                .setFooter(System.getenv("VERSION_ID"), msg.getJDA().getSelfUser().getAvatarUrl())
                .setColor(0xde1073)
                .addField("Team Astolfo. **(ASTF)**", astf + " " + cr, true)
                .addField("Gudako, Corp. **(GUDK)**", gudk + " " + cr, true)
                .addField("KAY&VIM Index **(^VIM)**", vimx + " " + cr, true)
                .addField("Ishtar Motors. **(WEEB)**", clwn + " " + cr, true)
            .addField("Meliodaf, Inc. **(WOLF)**", weeb+" "+cr, true)
            .addField("Emortal, Inc. **(EMO)**", emo+" "+cr, true)
            .build();
        msg.getChannel().sendMessage(embed).queue();
    }
}