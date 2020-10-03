package tech.Astolfo.AstolfoCaffeine.main.cmd.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.client.FindIterable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.bson.Document;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.cmd.info.compare.currency.sort_at;
import tech.Astolfo.AstolfoCaffeine.main.cmd.info.compare.currency.sort_cr;
import tech.Astolfo.AstolfoCaffeine.main.cmd.info.compare.currency.sort_tc;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Leaderboard extends Command
{
    private final EventWaiter waiter;
    private List<Document> wallet_list;
    public Leaderboard(EventWaiter waiter)
    {
        super.name = "leaderboard";
        super.aliases = new String[]{"lb","top","best","ranking"};
        super.help = "view the official Astolfo rankings!";
        super.category = new Category("info");
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e)
    {
        //poly1305
        FindIterable<Document> wallets = App.col.find();
        wallet_list = new ArrayList<>();

        wallets
                .forEach(
                        (Consumer<? super Document>) doc -> wallet_list.add(doc)
                );

        e.getChannel().sendMessage(menu(e)).queue(
                m -> {
                    m.addReaction("👥").queue();
                    m.addReaction("🏢").queue();
                    menuOption(m, e);
                }
        );
    }

    private void menuOption(Message msg, CommandEvent e) {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                check -> e.getMessage().getAuthor().getIdLong() == check.getUser().getIdLong() && msg.getIdLong() == check.getMessageIdLong(),
                action -> {
                    if(action.getReactionEmote().getName().equals("👥")) {
                        msg.editMessage(userMenu(e)).queue(
                                m -> {
                                    m.clearReactions().queue();
                                    m.addReaction("⏪").queue();
                                    m.addReaction(":credit:738537190652510299").queue();
                                    m.addReaction(":trapcoin:738537189884821606").queue();
                                    m.addReaction(":token:738537180003172354").queue();
                                    userOption(m, e);
                                }
                        );
                    } else if (action.getReactionEmote().getName().equals("🏢")) {
                        msg.clearReactions().queue();
                        msg.editMessage(underConstruction(e)).queue();
                    } else {
                        action.getReaction().removeReaction(action.getUser()).queue();
                        menuOption(msg, e);
                    }
                },
                60, TimeUnit.SECONDS,
                () -> {}
        );
    }

    private void userOption(Message msg, CommandEvent e) {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                check -> e.getMessage().getAuthor().getIdLong() == check.getUser().getIdLong() && msg.getIdLong() == check.getMessageIdLong(),
                action -> {
                    if(action.getReactionEmote().getName().equals("credit")) {
                        msg.editMessage(cr(wallet_list, e, 1)).queue(
                                m -> {
                                    m.clearReactions().queue();
                                    m.addReaction("⏪").queue();
                                    if (((int) Math.ceil((double) wallet_list.size()/5D)) > 1) {
                                        m.addReaction("◀️").queue();
                                        m.addReaction("▶️").queue();
                                    }
                                    currencyOption(m, e, "credits", 1);
                                }
                        );
                    } else if (action.getReactionEmote().getName().equals("trapcoin")) {
                        msg.editMessage(tc(wallet_list, e, 1)).queue(
                                m-> {
                                    m.clearReactions().queue();
                                    m.addReaction("⏪").queue();
                                    if (((int) Math.ceil((double) wallet_list.size()/5D)) > 1) {
                                        m.addReaction("◀️").queue();
                                        m.addReaction("▶️").queue();
                                    }
                                    currencyOption(m, e, "trapcoins", 1);
                                }
                        );
                    } else if (action.getReactionEmote().getName().equals("token")) {
                        msg.editMessage(at(wallet_list, e, 1)).queue(
                                m-> {
                                    m.clearReactions().queue();
                                    m.addReaction("⏪").queue();
                                    if (((int) Math.ceil((double) wallet_list.size()/5D)) > 1) {
                                        m.addReaction("◀️").queue();
                                        m.addReaction("▶️").queue();
                                    }
                                    currencyOption(m, e, "tokens", 1);
                                }
                        );
                    } else if (action.getReactionEmote().getName().equals("⏪")) {
                        msg.editMessage(menu(e)).queue(
                                m -> {
                                    m.clearReactions().queue();
                                    m.addReaction("👥").queue();
                                    m.addReaction("🏢").queue();
                                    menuOption(m, e);
                                }
                        );
                    } else {
                        action.getReaction().removeReaction(action.getUser()).queue();
                        userOption(msg, e);
                    }
                },
                60, TimeUnit.SECONDS,
                () -> {}
        );
    }

    private void currencyOption(Message msg, CommandEvent e, String type, int page) {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                check -> e.getMessage().getAuthor().getIdLong() == check.getUser().getIdLong() && msg.getIdLong() == check.getMessageIdLong(),
                action -> {
                    if (action.getReactionEmote().getName().equals("⏪")) {
                        msg.editMessage(userMenu(e)).queue(
                                m -> {
                                    m.clearReactions().queue();
                                    m.addReaction("⏪").queue();
                                    m.addReaction(":credit:738537190652510299").queue();
                                    m.addReaction(":trapcoin:738537189884821606").queue();
                                    m.addReaction(":token:738537180003172354").queue();
                                    userOption(m, e);
                                }
                        );
                    } else if (action.getReactionEmote().getName().equals("▶️")) {
                        if (page == ((int) Math.ceil((double) wallet_list.size()/5D))) {
                            currencyOption(msg, e, type, page);
                            return;
                        }
                        action.getReaction().removeReaction(action.getUser()).queue();
                        currencyOption(msg, e, type, page+1);
                        switch (type) {
                            case "credits":
                                msg.editMessage(cr(wallet_list, e, page+1)).queue();
                                break;
                            case "trapcoins":
                                msg.editMessage(tc(wallet_list, e, page+1)).queue();
                                break;
                            case "tokens":
                                msg.editMessage(tc(wallet_list, e, page+1)).queue();
                                break;
                        }
                    } else if (action.getReactionEmote().getName().equals("◀️")) {
                        if (page == 1) {
                            currencyOption(msg, e, type, page);
                            return;
                        }
                        action.getReaction().removeReaction(action.getUser()).queue();
                        currencyOption(msg, e, type, page-1);
                        switch (type) {
                            case "credits":
                                msg.editMessage(cr(wallet_list, e, page-1)).queue();
                                break;
                            case "trapcoins":
                                msg.editMessage(tc(wallet_list, e, page-1)).queue();
                                break;
                            case "tokens":
                                msg.editMessage(tc(wallet_list, e, page-1)).queue();
                                break;
                        }
                    } else {
                        action.getReaction().removeReaction(action.getUser()).queue();
                        currencyOption(msg, e, type, page);
                    }
                },
                60, TimeUnit.SECONDS,
                () -> {}
        );
    }

    private MessageEmbed menu(CommandEvent e) {
        return App.embed()
                .setAuthor("Super Duper LeaderboardzZz", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/750055125757722694/xui4vc3dipsz.png")
                .addField(":busts_in_silhouette: Users", "check out da top playerz on da bot!", false)
                .addField(":office: Companies", "woahhh companies have competition too1/!?", false)
                .build();
    }

    private MessageEmbed userMenu(CommandEvent e) {
        return App.embed()
                .setAuthor("Da User LeaderboardzZz", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/750055125757722694/xui4vc3dipsz.png")
                .addField("<:credit:738537190652510299>  Credits", "", true)
                .addField("<:trapcoin:738537189884821606> Trap Coins", "", true)
                .addField("<:token:738537180003172354> Tokens", "", true)
                .build();
    }

    private MessageEmbed underConstruction(CommandEvent e) {
        return App.embed()
                .setAuthor("Under constwuctuwution 🏗️", "https://astolfo.tech", e.getAuthor().getAvatarUrl())
                .setThumbnail("https://cdn.discordapp.com/attachments/738514936338055178/755885044748517466/325-3256608_new-wip-haunted-astolfo-bean-plushie.png")
                .setDescription("heY funny story this isn't done\ncheck back laterrrrzZz")
                .build();
    }

    private MessageEmbed cr(List<Document> wallet_list, CommandEvent e, int page)
    {
        Comparator<Document> descCR = Collections.reverseOrder(new sort_cr());
        wallet_list.sort(descCR);
        int pages = (int) Math.ceil((double) wallet_list.size()/5D);
        EmbedBuilder cr = App.embed().setAuthor("Credits Leaderboard (Page "+page+"/"+pages+")");

        int[] p = {page*5-5, page*5-4, page*5-3, page*5-2, page*5-1, page*5};



        List<User> user = new ArrayList<>();

        e.getJDA().retrieveUserById(wallet_list.get(p[0]).getLong("userID").toString()).queue(user::add);


        if (wallet_list.size() >= p[0])
        {
            cr.addField(p[0]+1+". "+user.get(0).getAsTag(), wallet_list.get(p[0]).getDouble("credits").toString()+" <:credit:738537190652510299>", true);
            cr.setThumbnail(user.get(0).getAvatarUrl());
        }
        if (wallet_list.size()-1 > p[1]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[1]).getLong("userID").toString()).queue(user::add);
            cr.addField(p[1]+1+". "+user.get(1).getAsTag(), wallet_list.get(p[1]).getDouble("credits").toString()+" <:credit:738537190652510299>", true);
        }
        if (wallet_list.size()-1 > p[2]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[2]).getLong("userID").toString()).queue(user::add);
            cr.addField(p[2]+1+". "+user.get(2).getAsTag(), wallet_list.get(p[2]).getDouble("credits").toString()+" <:credit:738537190652510299>", true);
        }
        if (wallet_list.size()-1 > p[3]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[3]).getLong("userID").toString()).queue(user::add);
            cr.addField(p[3]+1+". "+user.get(3).getAsTag(), wallet_list.get(p[3]).getDouble("credits").toString()+" <:credit:738537190652510299>", true);
        }
        if (wallet_list.size()-1 > p[4]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[4]).getLong("userID").toString()).queue(user::add);
            cr.addField(p[4]+1+". "+user.get(4).getAsTag(), wallet_list.get(p[4]).getDouble("credits").toString()+" <:credit:738537190652510299>", true);
        }

        return cr.build();
    }

    private MessageEmbed tc(List<Document> wallet_list, CommandEvent e, int page)
    {
        Comparator<Document> descTC = Collections.reverseOrder(new sort_tc());
        wallet_list.sort(descTC);
        int pages = (int) Math.ceil((double) wallet_list.size()/5D);
        EmbedBuilder embed = App.embed().setAuthor("Trap Coins Leaderboard (Page "+page+"/"+pages+")");

        int[] p = {page*5-5, page*5-4, page*5-3, page*5-2, page*5-1, page*5};

        List<User> user = new ArrayList<>();
        e.getJDA().retrieveUserById(wallet_list.get(p[0]).getLong("userID").toString()).queue(user::add);

        if (wallet_list.size() > p[0])
        {
            embed.addField(p[0]+1+". "+user.get(0).getAsTag(), wallet_list.get(p[0]).getDouble("trapcoins").toString()+" <:trapcoin:738537189884821606>", true);
            embed.setThumbnail(user.get(0).getAvatarUrl());
        }
        if (wallet_list.size()-1 > p[1]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[1]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[1]+1+". "+user.get(1).getAsTag(), wallet_list.get(p[1]).getDouble("trapcoins").toString()+" <:trapcoin:738537189884821606>", true);
        }
        if (wallet_list.size()-1 > p[2]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[2]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[2]+1+". "+user.get(2).getAsTag(), wallet_list.get(p[2]).getDouble("trapcoins").toString()+" <:trapcoin:738537189884821606>", true);
        }
        if (wallet_list.size()-1 > p[3]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[3]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[3]+1+". "+user.get(3).getAsTag(), wallet_list.get(p[3]).getDouble("trapcoins").toString()+" <:trapcoin:738537189884821606>", true);
        }
        if (wallet_list.size()-1 > p[4]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[4]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[4]+1+". "+user.get(4).getAsTag(), wallet_list.get(p[4]).getDouble("trapcoins").toString()+" <:trapcoin:738537189884821606>", true);
        }

        return embed.build();
    }

    private MessageEmbed at(List<Document> wallet_list, CommandEvent e, int page)
    {
        Comparator<Document> descAT = Collections.reverseOrder(new sort_at());
        wallet_list.sort(descAT);
        int pages = (int) Math.ceil((double) wallet_list.size()/5D);
        EmbedBuilder embed = App.embed().setAuthor("Apocrypha Tokens Leaderboard (Page "+page+"/"+pages+")");

        int[] p = {page*5-5, page*5-4, page*5-3, page*5-2, page*5-1, page*5};

        List<User> user = new ArrayList<>();

        if (wallet_list.size() > p[0])
        {
            e.getJDA().retrieveUserById(wallet_list.get(p[0]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[0]+1+". "+user.get(0).getAsTag(), wallet_list.get(p[0]).getDouble("tokens").toString()+" <:token:738537180003172354>", true);
            embed.setThumbnail(user.get(0).getAvatarUrl());
        }
        if (wallet_list.size()-1 > p[1]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[1]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[1]+1+". "+user.get(1).getAsTag(), wallet_list.get(p[1]).getDouble("tokens").toString()+" <:token:738537180003172354>", true);
        }
        if (wallet_list.size()-1 > p[2]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[2]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[2]+1+". "+user.get(2).getAsTag(), wallet_list.get(p[2]).getDouble("tokens").toString()+" <:token:738537180003172354>", true);
        }
        if (wallet_list.size()-1 > p[3]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[3]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[3]+1+". "+user.get(3).getAsTag(), wallet_list.get(p[3]).getDouble("tokens").toString()+" <:token:738537180003172354>", true);
        }
        if (wallet_list.size()-1 > p[4]) {
            e.getJDA().retrieveUserById(wallet_list.get(p[4]).getLong("userID").toString()).queue(user::add);
            embed.addField(p[4]+1+". "+user.get(4).getAsTag(), wallet_list.get(p[4]).getDouble("tokens").toString()+" <:token:738537180003172354>", true);
        }

        return embed.build();
    }
}
