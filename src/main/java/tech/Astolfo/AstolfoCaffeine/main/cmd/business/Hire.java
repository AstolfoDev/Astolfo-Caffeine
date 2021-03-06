package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Updates.set;

public class Hire extends Command {
  public Hire() {
    super.name = "hire";
    super.aliases = new String[]{"inv"};
    super.help = "hire a new employee!";
    super.category = new Category("business");
    super.arguments = "<@user>";
  }
  @Override
  protected void execute(CommandEvent e) {
    MongoCollection<Document> company = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company);

    Message msg = e.getMessage();
    User author = msg.getAuthor();

    BasicDBObject filter1 = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(author.getIdLong())));
    Document comp = company.find(filter1).first();

    if (comp == null) {
      e.reply(new Logging().error("**heY!!** ur not even in a company... u canz invite anyone to it..."));
      return;
    }
    List<Long> admins = (List<Long>) comp.get("admins");

    if (comp.getLong("owner") != author.getIdLong()) {
      if (admins == null) {
        noAuth(e, "oiiiii!! there are no other directors, ask the founder to invite the new member");
        return;
      } else if (!admins.contains(msg.getAuthor().getIdLong())) {
        noAuth(e, "oiiii ur not a director!!"); return;
      }
    }
    
    String[] args = e.getArgs().split("\\s+");
    List<User> mentions = msg.getMentionedUsers();

    if (args.length < 1) {
      noAuth(e, "nuuuu u didnt @mention the person u wanna inviteeeeee!!!! >:(");
      return;
    } else if (mentions.size() < 1) {
      noAuth(e, "ugggghghghghgh u didnt mention anyone!!!!!!");
      return;
    } else if (mentions.size() > 1) {
      noAuth(e, "omgggggg u mentioned too many ppl!!!!!");
      return;
    } else if (!(args[0].replaceAll("<", "").replaceAll("!","").replaceAll("@","").replaceAll(">","").equals(mentions.get(0).getId()))) {
      noAuth(e, "huh!? i am confusion... can u like @mention the person u wanna invite pls??");
      return;
    }

    BasicDBObject filter2 = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(mentions.get(0).getIdLong())));

    Document comp2 = company.find(filter2).first();

    if (comp2 != null) {
      e.reply(new Logging().error("oh no...... that person is already in a companyyyyyy ahhhhhhhhhHHHhhhn u canz invite em rn"));
      return;
    }

    List<Long> docs = (List<Long>) comp.get("invites");

    if (docs.contains(mentions.get(0).getIdLong())) {
      noAuth(e, "sozz mate ur trynaaaaaaa invite some1 who's already been invitedddd!!!!!!!1!");
      return;
    } else if (mentions.get(0).isBot()) {
      noAuth(e, "gimme gimme gimme a bot after midnight");
      return;
    }

    List<Long> obj = new ArrayList<>();

    obj.addAll(docs);

    obj.add(mentions.get(0).getIdLong());
    Bson up = set("invites", obj);

    company.updateOne(filter1, up);

    MessageEmbed embed = new Logging().embed()
            .setAuthor("Invited " + mentions.get(0).getName(), "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
            .setThumbnail(mentions.get(0).getAvatarUrl())
            .setDescription("SuccessfOwOlly sent an invite to " + mentions.get(0).getName() + " :3\nAwaiiiiiiting reply!!! :D")
            .build();

    e.reply(embed);

    MessageEmbed emb = new Logging().embed()
            .setAuthor("wagwan mah g", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
            .setDescription("you've just been invited to join `" + comp.get("name") + "` (O꒳O)\nto accept the invite just do `" + System.getenv("PREFIX") + "join " + comp.get("name") + "`")
            .setThumbnail(comp.getString("logo"))
            .build();

    mentions.get(0)
      .openPrivateChannel()
      .flatMap(ch -> ch.sendMessage(emb))
      .queue();
  }

  private void noAuth(CommandEvent e, String msg) {
    e.reply(new Logging().error(msg));
  }
}