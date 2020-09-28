package tech.Astolfo.AstolfoCaffeine.main.cmd.gambling;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.Astolfo.AstolfoCaffeine.App;

public class Casino extends Command {

    public Casino() {
        super.name = "casino";
        super.aliases = new String[]{"gambling","gamble"};
        super.help = "displays information regarding the casino operations";
        super.category = new Category("gambling");
    }

    @Override
    protected void execute(CommandEvent e) {
        Message msg = e.getMessage();
        User author = msg.getAuthor();

        e.reply(
            App.embed()
                .setAuthor("Astolfo Casino", "https://astolfo.tech", author.getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/747059545154650122/Screenshot_2020-08-23_at_12.46.40.png")
                .setDescription("Currently under construction, check back later!")
                .build()
        );
    }
}