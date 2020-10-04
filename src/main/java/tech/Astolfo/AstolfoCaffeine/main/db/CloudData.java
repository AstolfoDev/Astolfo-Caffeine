package tech.Astolfo.AstolfoCaffeine.main.db;

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
        if (query_data == null) {
            // Creates and insert a new default document into the collection then returns the value of the document
            query_data = create_template(id, collection);
        }

        // The method returns the value of the document
        return query_data;
    }

    // Method for creating and inserting standard documents into the MongoDB database
    private Document create_template(long id, String collection) {

        // Check the value of the collection variable
        switch(collection) {

            default:
                return null;

            case "wallets":
                // Create a default wallet document
                Document wallet = new Document("userID", id)
                        .append("credits", 0D)
                        .append("trapcoins", 0D)
                        .append("tokens", 0D);

                // Insert the wallet document into the database
                App.db.getCollection(collection).insertOne(wallet);

                // Return the default wallet document
                return wallet;

            case "stocks":
                // Create a default stocks document
                Document stocks = new Document("userID", id)
                        .append("astf", 0)
                        .append("gudk", 0)
                        .append("weeb", 0)
                        .append("wolf", 0)
                        .append("emo", 0)
                        .append("vimx", 0);

                // Insert the stocks document into the database
                App.db.getCollection(collection).insertOne(stocks);

                // Return the stocks document
                return stocks;

            case "tools":
                // Create a default tools document
                Document tools = new Document("userID", id)
                        .append("tools", 0);

                // Insert the tools document into the database
                App.db.getCollection(collection).insertOne(tools);

                // Return the tools document
                return tools;
        }
    }
}
