package tech.Astolfo.AstolfoCaffeine.main.db;

import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.App;

import static com.mongodb.client.model.Filters.eq;

public class CloudData {

    /**
     * Method for retrieving standard data from the MongoDB database
     *
     * @param id         The ID of the user who's data is being retrieved from the collection.
     * @param collection The name of the collection where the data required is being stored.
     * @return The method returns the value of the document
     */
    public Document get_data(long id, Collection collection) {

        // Create query to find data in table
        Bson filter = eq("userID", id);

        // Convert the enum value into a string
        String col_query = String.valueOf(collection);

        // Use query to retrieve first document matching the query conditions
        Document query_data = App.db.getCollection(col_query).find(filter).first();

        // Check if the query returns no results
        // Creates and insert a new default document into the collection then returns the value of the document
        if (query_data == null) query_data = create_template(id, collection);

        // The method returns the value of the document
        return query_data;
    }

    /**
     * Method for overwriting data in a selected collection in the MongoDB database
     *
     * @param query      The query being used to filter through data in the collection
     * @param update     The update query to specify how the data should be modified
     * @param collection The name of the collection being modified
     * @return The number of documents modified in the collection
     */
    public Long update_data(Bson query, Object update, Collection collection) {

        // Convert the enum value into a string
        String col_query = String.valueOf(collection);

        // Checks what class the object is and runs the appropriate operation based on the class type
        if (update instanceof Document) {
            // Replace the document and return the number of documents that were successfully replaced
            return App.db.getCollection(col_query).replaceOne(query, (Document) update).getModifiedCount();

        } else if (update instanceof Bson) {
            // Update the document and return the number of documents that were successfully updated
            return App.db.getCollection(col_query).updateOne(query, (Bson) update).getModifiedCount();

        } else {
            // Fallback if no required object was given as a parameter for the update statement
            return 0L;
        }

    }

    /**
     * Method for setting new data in a selected collection in the MongoDB database
     *
     * @param data       The document that is being inserted into the collection
     * @param collection The name of the collection where the document is being inserted into
     */
    public void set_data(Document data, Collection collection) {

        // Checks if the document for that user already exists and updates it instead of inserting a new document
        if (update_data(eq("userID", data.getLong("userID")), data, collection) >= 1) return;

        // Convert the enum value into a string
        String col_query = String.valueOf(collection);

        // Inserts a new document if there is not already one present for the user
        App.db.getCollection(col_query).insertOne(data);

    }

    /**
     * Method for creating and inserting standard documents into the MongoDB database
     *
     * @param id                  The ID of the user who's having their default data set for them
     * @param selected_collection The collection where the default data is being set
     * @return The document that was inserted into the selected collection
     */
    private Document create_template(long id, Collection selected_collection) {

        // Check and get the value of the collection variable
        Document to_insert = switch (selected_collection) {

            // Create a default wallet document
            case wallets -> new Document("userID", id)
                    .append("credits", 0D)
                    .append("trapcoins", 0D)
                    .append("tokens", 0D);

            // Create a default stocks document
            case stocks -> new Document("userID", id)
                    .append("astf", 0)
                    .append("gudk", 0)
                    .append("weeb", 0)
                    .append("wolf", 0)
                    .append("emo", 0)
                    .append("vimx", 0);

            // Create a default tools document
            case tools -> new Document("userID", id)
                    .append("tools", 0);

            default -> throw new RuntimeException();
        };

        // Inserts the new document into the collection specified
        set_data(to_insert, selected_collection);

        // The method returns the inserted document
        return to_insert;
    }

    // An enum storing the different valid collection types for the CloudData#get_data() method
    public enum Collection {
        wallets,
        stocks,
        tools
    }

}
