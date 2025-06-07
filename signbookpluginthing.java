package com.example.signbook;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;

public class SignBookPlugin extends JavaPlugin implements CommandExecutor {

    // flags to control whether OP is required for signing/unsigning
    private final boolean requireOpToSign = true;
    private final boolean requireOpToUnsign = true;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("loring")) return false;
        
        // handles non-player senders
        if (!(sender instanceof Player)) {
            sender.sendMessage("you're a console you silly goose you don't have any books to lore");
            return true;
        }
        
        Player p = (Player) sender;
        
        // gets held item
        ItemStack item = p.getInventory().getItemInMainHand(); 

        // checks for empty hand
        if (item == null || item.getType() == Material.AIR) {
            p.sendMessage("You must be holding a book.");
            return true;
        }
        
        // handles subcommands
        if (args.length < 1) return false;
        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("author")) {
            return handleSign(p, item, args);
        } else if (subCommand.equals("unsign")) {
            return handleUnsign(p, item);
        }
        
        return false;
    }

    private boolean handleSign(Player p, ItemStack item, String[] args) {
        // argument check
        if (args.length < 2) {
            p.sendMessage("Usage: /loring author <name>");
            return true;
        }

        // OP check
        if (requireOpToSign && !p.isOp()) {
            p.sendMessage("You must be OP to sign books.");
            return true;
        }

        // book check!!!!!!
        if (item.getType() != Material.WRITABLE_BOOK) {
            p.sendMessage("You must be holding a writable book.");
            return true;
        }

        // gets and validates book meta
        BookMeta meta = (BookMeta) item.getItemMeta();
        if (meta == null || !meta.hasPages()) {
            p.sendMessage("This book has no content.");
            return true;
        }

        // signing process
        ItemStack signed = item.clone(); // preserves original item properties
        signed.setType(Material.WRITTEN_BOOK); // changes to signed book
        
        // sets title
        BookMeta signedMeta = (BookMeta) signed.getItemMeta();
        String author = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        signedMeta.setAuthor(author);
        signedMeta.setTitle(meta.hasTitle() ? meta.getTitle() : "Untitled");
        signed.setItemMeta(signedMeta);

        // updates player's inventory
        p.getInventory().setItemInMainHand(signed);
        p.sendMessage("Book signed.");
        
        // bookiewookie!!
        if (author.equalsIgnoreCase("worm")) {
            p.sendMessage("§4§lPOWER!!!!");
        }
        
        return true;
    }

    // OP chec
    private boolean handleUnsign(Player p, ItemStack item) {
        if (requireOpToUnsign && !p.isOp()) {
            p.sendMessage("You must be OP to unsign books.");
            return true;
        }

        // book check!!!!!!
        if (item.getType() != Material.WRITTEN_BOOK) {
            p.sendMessage("You must be holding a signed book.");
            return true;
        }

        // gets and validates book meta
        BookMeta signedMeta = (BookMeta) item.getItemMeta();
        if (signedMeta == null || !signedMeta.hasPages()) {
            p.sendMessage("This book has no content.");
            return true;
        }

        // unsigning process
        ItemStack editable = item.clone();
        editable.setType(Material.WRITABLE_BOOK);
        
        BookMeta editableMeta = (BookMeta) editable.getItemMeta();
        editable.setItemMeta(editableMeta);

        // updates player's inventory
        p.getInventory().setItemInMainHand(editable);
        p.sendMessage("Book unsigned.");
        
        return true;
    }
}