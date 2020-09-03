package tech.Astolfo.AstolfoCaffeine.main.cmd.gambling;

import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import org.bson.Document;
import org.bson.conversions.Bson;

import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

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
        String[] args = e.getArgs().split("\\s+");
        List<String> heads = Arrays.asList(new String[]{"h","heads","head","he","hed","heds"});
        List<String> tails = Arrays.asList(new String[]{"t","tails","tail","ta","tal","tals"});
        if (!heads.contains(args[0]) && !tails.contains(args[0])) {
            e.reply(new Logging().error(e.getSelfUser(), "hEy HEyyYYYyy! wherezzzzz urrrrr coinflip choice hmmmmmmmmmm, i dont think dat loookoz leik heads OR TAils to me!!!!!"));
            return;
        }
        final String choice;
        if (heads.contains(args[0])) {
          choice = "heads";
        } else choice = "tails";

        int side = oreo();
        String face = new String();

        if (side == 1) face = "heads"; else face = "tails";
        if (choice.equals(face)) {
          e.reply("wooooaahahh u picked "+choice+"... AND IT LANDED ON "+face.toUpperCase()+"!!!!!!1!");
          return;
        } else {
          e.reply("hmmmmmzzz u picked "+choice+"... but uhhhhh it sorta landed on "+face+" instead.... errrrm ;-;");
          return;
        }
    }

    private void chocolate(CommandEvent e) {
        String[] args = e.getArgs().split("\\s+");
        List<String> heads = Arrays.asList(new String[]{"h","heads","head","he","hed","heds"});
        List<String> tails = Arrays.asList(new String[]{"t","tails","tail","ta","tal","tals"});
        if (!heads.contains(args[0]) && !tails.contains(args[0])) {
            e.reply("hEy HEyyYYYyy! wherezzzzz urrrrr coinflip choice hmmmmmmmmmm, i dont think dat loookoz leik heads OR TAils to me!!!!!");
            return;
        }

        try {
          int bet = Integer.parseInt(args[1]);
          Bson filter = eq("userID", e.getAuthor().getIdLong());
          Document doc = App.col.find(filter).first();
          if (doc == null) {
            e.reply("uh mate couldnt find ur wallet there... uhhh loooks like someone is **B R O K E**");
            return;
          }
          if (bet < 1) {
            e.reply("No.");
            return;
          } else if (bet > doc.getDouble("credits")) {
            e.reply("u uhhhh sorta only hazzz "+doc.getDouble("credits")+"  but ur trying to bet "+bet+" credits?!? confusion???");
            return;
          }
          final String choice;
          if (heads.contains(args[0])) {
            choice = "heads";
          } else choice = "tails";

          int side = oreo();
          String face = new String();

          Bson win = set("credits", doc.getDouble("credits")+bet);
          Bson lose = set("credits", doc.getDouble("credits")-bet);

          if (side == 1) face = "heads"; else face = "tails";
          if (choice.equals(face)) {
            App.col.updateOne(filter, win);
            e.reply("**WINNER!** u just wonzzzz "+bet+" cweditzzZz by landing on "+face+"!!!");
            return;
          } else {
            App.col.updateOne(filter, lose);
            e.reply("**LOSER!** u just wonn't "+bet+" credDDDdditz by landing on "+face+" instead of "+choice);
            return;
          }
        } catch (NumberFormatException err) {
          e.reply("HUAHAHH!?! dat aint no numberrrrr TF!?!??! HELP???1");
          return;
        }
    }

    private int oreo() {
      return Math.round((float) Math.random()*2);
    }
}