package tech.Astolfo.AstolfoCaffeine.main.db;

import com.mongodb.BasicDBObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;

public class Database {

    public void create_company(String name, String description, String logo, long id) {
        Document doc = new Document("owner", id)
                .append("name", name)
                .append("description", description)
                .append("logo", logo)
                .append("admins", new ArrayList<Long>())
                .append("members", Collections.singletonList(id))
               .append("invites", new ArrayList<Long>())
               .append("industry", "none")
               .append("xp", 0);
        new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.company).insertOne(doc);
    }

    public void clear_unused() {
        BasicDBObject filter = new BasicDBObject()
            .append("credits", 0D)
            .append("trapcoins", 0D)
            .append("tokens", 0D);
        new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.wallets).deleteMany(filter);
    }

    public void clear_stocks() {
        BasicDBObject filter = new BasicDBObject()
            .append("astf", 0)
            .append("gudk", 0)
            .append("clwn", 0)
            .append("weeb", 0)
            .append("emo", 0)
            .append("vimx", 0);
        new CloudData().get_collection(CloudData.Database.Economy, CloudData.Collection.stocks).deleteMany(filter);
    }
}
