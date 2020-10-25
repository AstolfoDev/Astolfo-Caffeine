package tech.Astolfo.AstolfoCaffeine.main.db;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.App;

import java.util.ArrayList;
import java.util.Collections;

import static com.mongodb.client.model.Filters.eq;

public class Database {
    public Document get_account(long id) {
        Bson filter = eq("userID", id);
        Document info = App.col.find(filter).first();
        assert info != null;
        return info;
    }

    public Document get_stocks(long id) {
        Bson filter = eq("userID", id);
        Document info = App.stocks.find(filter).first();
        assert info != null;
        return info;
    }

    public Document get_tools(long id) {
        Bson filter = eq("userID", id);
        Document info = App.db.getCollection("tools").find(filter).first();
        assert info != null;
        return info;
    }

    public void create_account(long id) {
        if (get_account(id) != null) return;
        Document doc = new Document("userID", id)
                .append("credits", 0D)
                .append("trapcoins", 0D)
                .append("tokens", 0D);
        App.col.insertOne(doc);
    }

    public void create_stocks(long id) {
        if (get_stocks(id) != null) return;
        Document doc = new Document("userID", id)
                .append("astf", 0)
                .append("gudk", 0)
                .append("weeb", 0)
                .append("wolf", 0)
                .append("emo", 0)
                .append("vimx", 0);
        App.stocks.insertOne(doc);
    }

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
        App.company.insertOne(doc);
    }

    public void create_tools(long id) {
        if (get_tools(id) != null) return;
        Document doc = new Document("userID", id)
                .append("tools", 0);
        App.db.getCollection("tools").insertOne(doc);
    }

    public void clear_unused() {
        BasicDBObject filter = new BasicDBObject()
            .append("credits", 0D)
            .append("trapcoins", 0D)
            .append("tokens", 0D);
        App.col.deleteMany(filter);
    }

    public void clear_stocks() {
        BasicDBObject filter = new BasicDBObject()
            .append("astf", 0)
            .append("gudk", 0)
            .append("clwn", 0)
            .append("weeb", 0)
            .append("emo", 0)
            .append("vimx", 0);
        App.stocks.deleteMany(filter);
    }
}
