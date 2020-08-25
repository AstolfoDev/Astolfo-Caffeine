package tech.Astolfo.AstolfoCaffeine.main.cmd.gambling;

import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Coinflip extends Command {

    public Coinflip() {
        super.name = "coinflip";
        super.aliases = new String[]{"flip","cf"};
        super.help = "flip a coin and bet on it!";
        super.category = new Category("gambling");
        super.arguments = "(heads/tails) (bet)";
    }

    @Override
    protected void execute(CommandEvent e) {

        String[] args = e.getArgs().split("\\s+");

        if (args.length < 1 || args[0] == "") {
            vanilla(e);
        } else if (args.length == 1) {
            strawberry(e);
        } else if (args.length >= 2) {
            chocolate(e);
        }
    }

    private void vanilla(CommandEvent e) {
        int side = oreo();
        String face = new String();

        if (side == 1) face = "heads"; else face = "tails";

        e.reply("**"+face+"!** the cOwOin wanded onnnnnn.. "+face+"!!");
    }

    private void strawberry(CommandEvent e) {
        List<String> heads = Arrays.asList(new String[]{"a","b"});

    }

    private void chocolate(CommandEvent e) {
        e.reply("c");
    }

    private int oreo() {
      return Math.round((float) Math.random()*2);
    }
}