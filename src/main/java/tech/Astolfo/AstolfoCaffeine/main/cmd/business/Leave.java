package tech.Astolfo.AstolfoCaffeine.main.cmd.business;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;

import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Updates.set;

public class Leave extends Command {

    public Leave() {
        super.name = "leave";
        super.aliases = new String[]{"resign"};
        super.help = "resign from the company you work for";
        super.category = new Category("business");
    }

    @Override
    protected void execute(CommandEvent e) {

        MongoCollection<Document> company = new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company);

        Message msg = e.getMessage();
        User author = msg.getAuthor();

        BasicDBObject filter1 = new BasicDBObject("members", new BasicDBObject("$in", Collections.singletonList(author.getIdLong())));
        Document comp = company.find(filter1).first();

        if (comp == null) {
            e.reply(new Logging().error("u no workz 4 a company tho!?"));
            return;
        }

        List<Long> members = (List<Long>) comp.get("members");
        List<Long> admins = (List<Long>) comp.get("admins");
        members.remove(author.getIdLong());

        if (comp.getLong("owner").equals(author.getIdLong())) {
            long newOwner = members.get(Math.round((float) Math.random()*members.size()));
            Bson update = set("owner", newOwner);
            company.updateOne(filter1, update);
        }

        if (admins.contains(author.getIdLong())) {
            admins.remove(author.getIdLong());
            Bson update2 = set("admins", admins);
            company.updateOne(filter1, update2);
        }

        Bson update3 = set("members", members);
        company.updateOne(filter1, update3);

        e.reply("u left! owo");
    }
}