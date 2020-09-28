package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class Join extends Command {
  public Join() {
    super.name = "join";
    super.aliases = new String[]{"j","acceptinvite"};
    super.help = "allows you to accept any employment offers!";
    super.category = new Category("business");
    super.arguments = "<company>";
  }

  @Override
  protected void execute(CommandEvent e) {

    
    Message msg = e.getMessage();
    User author = msg.getAuthor();

    BasicDBObject filter = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(e.getMessage().getAuthor().getIdLong())));
    Document comp1 = App.company.find(filter).first();

    if (comp1 != null) {
      e.reply(new Logging().error("EEEERMMMMM ur sorta in a company already... leave it first before joining a new one!!! ;P"));
      return;
    }

    String name = e.getArgs();
    if (name.equals("")) {
      e.reply(new Logging().error("nah nah nah u cant just put no args, mate. pls lmk which comp u wanna jooooin thx ;3"));
      return;
    }
    
    if (name.length() > 16) {
      e.reply("right... no, that's an invalid company name lawl");
    }

    BasicDBObject filter1 = new BasicDBObject("invites", new BasicDBObject("$in", Collections.singletonList(author.getIdLong())))
      .append("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE));
    
    Document doc = App.company.find(filter1).first();
    if (doc == null) {
      e.reply(new Logging().error("omgomogmogmgomgomg literally no one from `"+name+"` invited u... :expressionless:"));
      return;
    }

    List<Long> docs = (List<Long>) doc.get("invites");
    List<Long> members = (List<Long>) doc.get("members");

    List<Long> obj = new ArrayList<>(docs);

    List<Long> obj2 = new ArrayList<>(members);

    obj.remove(author.getIdLong());
    obj2.add(author.getIdLong());

    Bson up = set("invites", obj);
    Bson up2 = set("members", obj2);

    Bson filt = eq("name", doc.getString("name"));

    App.company.updateOne(filt, up2);
    App.company.updateOne(filt, up);

    MessageEmbed embed = App.embed()
        .setAuthor("SuccessfOwOlly joined!", "https://astolfo.tech", author.getAvatarUrl())
        .setDescription("You've joined the `"+doc.getString("name")+"` company!!1 ;3\n```\n"+doc.getString("description")+"\n```")
        .build();
        
    e.reply(embed);
  }
}

