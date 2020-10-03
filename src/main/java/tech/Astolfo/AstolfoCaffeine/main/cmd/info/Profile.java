package tech.Astolfo.AstolfoCaffeine.main.cmd.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class Profile extends Command {

    public Profile() {
        super.name = "profile";
        super.aliases = new String[]{"user","vp"};
        super.help = "view the AstolfoBot profile of yourself or another user";
        super.arguments = "(@user)";
        super.category = new Category("info");
    }


    private String baseURL = "https://profile.astolfo.tech/";


    @Override
    protected void execute(CommandEvent e) {

        // Get arguments from user's message
        String[] args = e.getArgs().split("\\s+");
        List<User> mentions = e.getMessage().getMentionedUsers();

        // Check if no arguments were provided
        if (args.length < 1) {
            send(e, e.getAuthor().getId());
        }

        // Check if arguments were provided and the argument is equal to a mentioned user
        else if (args.length == 1 && mentions.size() == 1) {
            send(e, mentions.get(0).getId());
        }

        // If too many arguments are provided it defaults to the author
        else {
            send(e, e.getAuthor().getId());
        }

    }

    private void send(CommandEvent e, String id) {
        e.reply(baseURL+"user/"+id);
    }
}
