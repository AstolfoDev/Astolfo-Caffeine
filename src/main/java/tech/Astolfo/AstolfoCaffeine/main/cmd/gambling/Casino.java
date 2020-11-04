package tech.Astolfo.AstolfoCaffeine.main.cmd.gambling;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

public class Casino extends Command {

    public Casino() {
        super.name = "casino";
        super.aliases = new String[]{"gambling","gamble"};
        super.help = "displays information regarding the casino operations";
        super.category = new Category("gambling");
        super.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent e) {
        Message msg = e.getMessage();
        new Logging().send(
                msg,
                "Currently under construction, check back later!",
                "Astolfo Casino",
                "https://cdn.discordapp.com/attachments/738514936338055178/747059545154650122/Screenshot_2020-08-23_at_12.46.40.png",
                (String[]) null
        );
    }
}