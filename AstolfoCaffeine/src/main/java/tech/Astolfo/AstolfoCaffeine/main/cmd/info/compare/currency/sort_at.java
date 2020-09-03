package tech.Astolfo.AstolfoCaffeine.main.cmd.info.compare.currency;

import org.bson.Document;

import java.util.Comparator;

public class sort_at implements Comparator<Document>
    {
    @Override
    public int compare(Document v1, Document v2)
    {
        return (int) (v1.getDouble("tokens") - v2.getDouble("tokens"));
    }
}
