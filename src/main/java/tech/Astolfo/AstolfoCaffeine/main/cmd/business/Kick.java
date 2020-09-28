package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Updates.*;

public class Kick extends Command {

  public Kick() {
    super.name = "kick";
    super.aliases = new String[]{"boot","fire"};
    super.help = "fire an employee from the company!";
    super.category = new Category("business");
    super.arguments = "<@employee>";
  }

  @Override
  protected void execute(CommandEvent e) {
    Message msg = e.getMessage();
    User author = msg.getAuthor();

    BasicDBObject filter1 = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(author.getIdLong())));
    Document comp = App.company.find(filter1).first();

    if (comp == null) {
      e.reply(new Logging().error("**heY!!** ur not even in a company... u canz kick anyone from it..."));
      return;
    }
    List<Long> admins = (List<Long>) comp.get("admins");

    if (comp.getLong("owner") != author.getIdLong()) {
      if (admins == null) {
        noAuth(e, "oiiiii!! there are no other directors, ask the founder to kick the member");
        return;
      } else if (!admins.contains(msg.getAuthor().getIdLong())) {
        noAuth(e, "oiiii ur not a director!!");
        return;
      }
    }


    String args = e.getArgs();
    if (args.equals("")) {
      e.reply("nuuuuuuuuuuuuu u didnt tag anyonezzzzzz");
      return;
    }

    if (args.contains(" ")) {
      e.reply(new Logging().error("eeeeeeeeeeeeehhh u sorta errrm put too many arguments... just @mention them plzzz!!"));
      return;
    }

    String id = args.replaceAll("<", "").replaceAll("!", "").replaceAll("@", "").replaceAll(">", "");
    User target = e.getJDA().getUserById(id);

    assert target != null;
    if (target.isBot()) {
      e.reply("BEEEEP BOOOOOOOOOP!!!!!!!! no robo time today thxxxxxxxx~ <3");
      return;
    }

    if (target.getIdLong() == comp.getLong("owner")) {
      e.reply("no...");
      return;
    }

    if (target.getIdLong() == author.getIdLong()) {
      e.reply("NUUUUU!!! >;(");
      return;
    }

    BasicDBObject filter2 = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(target.getIdLong())))
            .append("name", comp.getString("name"));
    Document comp2 = App.company.find(filter2).first();

    if (comp2 == null) {
      noAuth(e, "whaaaa-!>/??? " + target.getAsMention() + " aint in ur companaaaaaaaay!!!!!");
      return;
    }

    List<Long> docs = (List<Long>) comp.get("members");

    List<Long> obj = new ArrayList<>(docs);

    obj.remove(target.getIdLong());
    Bson up = set("members", obj);

    App.company.updateOne(filter1, up);
    e.reply(
            App.embed()
                    .setAuthor("Employee fired!", "https://astolfo.tech", author.getAvatarUrl())
                    .setThumbnail(target.getAvatarUrl()).setDescription("i hazzz fired da employee "+target.getAsMention()+" from das Company 4 u!! ;3")
                    .build()
    );

    target
            .openPrivateChannel()
            .flatMap(ch -> ch.sendMessage(
                    App.embed()
                            .setThumbnail(comp.getString("logo"))
                            .setAuthor("Lé Pink Slip", "https://astolfo.tech", comp.getString("logo"))
                            .setDescription("hewWWo, u haz been fired from... `"+comp.getString("name")+"`\nby: "+author.getAsMention()+" ("+author.getAsTag()+")"+"\nvewwwy sad ;(")
                            .build())
            )
            .queue();
  }



  private void noAuth(CommandEvent e, String msg) {
    e.reply(new Logging().error(msg));
  }
}