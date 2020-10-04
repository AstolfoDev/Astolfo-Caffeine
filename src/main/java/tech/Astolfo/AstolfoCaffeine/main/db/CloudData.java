package tech.Astolfo.AstolfoCaffeine.main.db;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.App;

import static com.mongodb.client.model.Filters.eq;

public class CloudData {

    // Method for retrieving standard data from the MongoDB database
    public Document get_data(long id, String collection) {

        // Create query to find data in table
        Bson filter = eq("userID", id);

        // Use query to retrieve first document matching the query conditions
        Document query_data = App.db.getCollection(collection).find(filter).first();

        // Check if the query returns no results
        // Creates and insert a new default document into the collection then returns the value of the document
        if (query_data == null) query_data = create_template(id, collection);

        // The method returns the value of the document
        return query_data;
    }

    // Method for creating and inserting standard documents into the MongoDB database
    private Document create_template(long id, String selected_collection) {

        Document to_insert = null;

        // Check the value of the collection variable
        switch(selected_collection) {

            case "wallets":
                // Create a default wallet document
                to_insert = new Document("userID", id)
                        .append("credits", 0D)
                        .append("trapcoins", 0D)
                        .append("tokens", 0D);
                break;

            case "stocks":
                // Create a default stocks document
                to_insert = new Document("userID", id)
                        .append("astf", 0)
                        .append("gudk", 0)
                        .append("weeb", 0)
                        .append("wolf", 0)
                        .append("emo", 0)
                        .append("vimx", 0);
                break;

            case "tools":
                // Create a default tools document
                to_insert = new Document("userID", id)
                        .append("tools", 0);
                break;

        }

        if (to_insert == null) throw new RuntimeException();
        MongoCollection<Document> collection = App.db.getCollection(selected_collection);
        collection.insertOne(to_insert);
        return to_insert;
    }
}
