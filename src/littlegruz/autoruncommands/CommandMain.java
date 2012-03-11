package littlegruz.autoruncommands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import littlegruz.autoruncommands.listeners.CommandBlockListener;
import littlegruz.autoruncommands.listeners.CommandEntityListener;
import littlegruz.autoruncommands.listeners.CommandPlayerListener;
import littlegruz.autoruncommands.listeners.CommandServerListener;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandMain extends JavaPlugin{
   Logger log = Logger.getLogger("This is MINECRAFT!");
   private HashMap<String, String> playerCommandMap;
   private HashMap<Location, String> blockCommandMap;
   private HashMap<String, String> commandMap;
   private HashMap<String, Location> playerPosMap;
   private HashMap<String, String> deathCommandMap;
   private Stack<Location> buttons;
   private File playerFile;
   private File commandFile;
   private File blockFile;
   private File deathFile;
   private File startupFile;
   private boolean placeBlock;
   private boolean startupDone;
   private String blockCommand;
   private String startupCommands;

   public void onDisable(){
      //Save player data
      try{
         BufferedWriter bw = new BufferedWriter(new FileWriter(playerFile));
         Iterator<Map.Entry<String, String>> it = playerCommandMap.entrySet().iterator();
         
         //Save the players and corresponding commands
         bw.write("<Player> <Command>\n");
         while(it.hasNext()){
            Entry<String, String> mp = it.next();
            bw.write(mp.getKey() + " " + mp.getValue() + "\n");
         }
         bw.close();
      }catch(IOException e){
         log.info("Error saving player command file");
      }
      
      //Save block data
      try{
          BufferedWriter bw = new BufferedWriter(new FileWriter(blockFile));
          Iterator<Map.Entry<Location, String>> it = blockCommandMap.entrySet().iterator();
          
          //Save the blocks and corresponding commands
          bw.write("<Block Location> <Command>\n");
          while(it.hasNext()){
             Entry<Location, String> mp = it.next();
             bw.write(mp.getKey().getWorld().getUID().toString() + " "
                     + Double.toString(mp.getKey().getX()) + " "
                     + Double.toString(mp.getKey().getY()) + " "
                     + Double.toString(mp.getKey().getZ()) + " "
                     + mp.getValue() + "\n");
          }
          bw.close();
       }catch(IOException e){
          log.info("Error saving block command file");
       }
      
    //Save player death data
      try{
         BufferedWriter bw = new BufferedWriter(new FileWriter(deathFile));
         Iterator<Map.Entry<String, String>> it = deathCommandMap.entrySet().iterator();
         
         //Save the players and corresponding commands
         bw.write("<Player> <Command>\n");
         while(it.hasNext()){
            Entry<String, String> mp = it.next();
            bw.write(mp.getKey() + " " + mp.getValue() + "\n");
         }
         bw.close();
      }catch(IOException e){
         log.info("Error saving player death command file");
      }
      
      //Save server start up data
      try{
         BufferedWriter bw = new BufferedWriter(new FileWriter(startupFile));
         StringTokenizer st = new StringTokenizer(startupCommands, ":");
         
         while(st.countTokens() > 0){
            bw.write(st.nextToken() + "\n");
         }
         bw.close();
      }catch(IOException e){
         log.info("Error saving server start up command file");
      }
      
      //Save command data
      try{
          BufferedWriter bw = new BufferedWriter(new FileWriter(commandFile));
          Iterator<Map.Entry<String, String>> it = commandMap.entrySet().iterator();
          
          //Save the blocks and corresponding commands
          bw.write("<Identifing name> <Command>\n");
          while(it.hasNext()){
             Entry<String, String> mp = it.next();
             bw.write(mp.getKey() + " " + mp.getValue() + "\n");
          }
          bw.close();
       }catch(IOException e){
          log.info("Error saving command file");
       }
      log.info("Autorun Commands v2.5 is melting! MELTING!");
   }

   public void onEnable(){
      //Create the directory and files if needed
      new File(getDataFolder().toString()).mkdir();
      playerFile = new File(getDataFolder().toString() + "/playerList.txt");
      commandFile = new File(getDataFolder().toString() + "/commands.txt");
      blockFile = new File(getDataFolder().toString() + "/blockList.txt");
      deathFile = new File(getDataFolder().toString() + "/deathList.txt");
      startupFile = new File(getDataFolder().toString() + "/startupCommands.txt");
      
      //Load the player file data
      playerCommandMap = new HashMap<String, String>();
      try{
         BufferedReader br = new BufferedReader(new FileReader(playerFile));
         StringTokenizer st;
         String input;
         String name;
         String command;
         while((input = br.readLine()) != null){
            if(input.compareToIgnoreCase("<Player> <Command>") == 0){
               continue;
            }
            st = new StringTokenizer(input, " ");
            name = st.nextToken();
            command = st.nextToken();
            playerCommandMap.put(name, command);
         }
         
      }catch(FileNotFoundException e){
         log.info("No original player command file, creating new one.");
      }catch(IOException e){
         log.info("Error reading player command file");
      }catch(Exception e){
         log.info("Incorrectly formatted player command file");
      }
      
      //Load the block file data
      blockCommandMap = new HashMap<Location, String>();
      try{
         BufferedReader br = new BufferedReader(new FileReader(blockFile));
         StringTokenizer st;
         String input;
         String command;
         Location loc = null;
         
         while((input = br.readLine()) != null){
            if(input.compareToIgnoreCase("<Block Location> <Command>") == 0){
               continue;
            }
            st = new StringTokenizer(input, " ");
            loc = new Location(getServer().getWorld(UUID.fromString(st.nextToken())), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
            command = st.nextToken();
            blockCommandMap.put(loc, command);
         }
         
      }catch(FileNotFoundException e){
         log.info("No original block command file, creating new one.");
      }catch(IOException e){
         log.info("Error reading block command file");
      }catch(Exception e){
         log.info("Incorrectly formatted block command file");
      }
      
      //Load the player death file data
      deathCommandMap = new HashMap<String, String>();
      try{
         BufferedReader br = new BufferedReader(new FileReader(deathFile));
         StringTokenizer st;
         String input;
         String name;
         String command;
         while((input = br.readLine()) != null){
            if(input.compareToIgnoreCase("<Player> <Command>") == 0){
               continue;
            }
            st = new StringTokenizer(input, " ");
            name = st.nextToken();
            command = st.nextToken();
            deathCommandMap.put(name, command);
         }
         
      }catch(FileNotFoundException e){
         log.info("No original player death command file, creating new one.");
      }catch(IOException e){
         log.info("Error reading player death command file");
      }catch(Exception e){
         log.info("Incorrectly formatted player death command file");
      }
      
      //Load the start up data
      startupCommands = "";
      try{
         BufferedReader br = new BufferedReader(new FileReader(startupFile));
         String input;
         while((input = br.readLine()) != null){
            if(input.compareToIgnoreCase("<Command>") == 0){
               continue;
            }
            startupCommands += ":" + input;
         }
         
      }catch(FileNotFoundException e){
         log.info("No original start up command file, creating new one.");
      }catch(IOException e){
         log.info("Error reading start up command file");
      }catch(Exception e){
         log.info("Incorrectly formatted start up command file");
      }
      
      //Load the command file data
      commandMap = new HashMap<String, String>();
      try{
         BufferedReader br = new BufferedReader(new FileReader(commandFile));
         StringTokenizer st;
         String input;
         String args;
         String name;
         
         //Assumes that the name is only one token long
         while((input = br.readLine()) != null){
            if(input.compareToIgnoreCase("<Identifing name> <Command>") == 0){
               continue;
            }
            st = new StringTokenizer(input, " ");
            name = st.nextToken();
            args = st.nextToken();
            while(st.hasMoreTokens()){
               args += " " + st.nextToken();
            }
            commandMap.put(name, args);
         }
         
      }catch(FileNotFoundException e){
         log.info("No original command file, creating new one.");
      }catch(IOException e){
         log.info("Error reading command file");
      }catch(Exception e){
         log.info("Incorrectly formatted command file");
      }
      
      placeBlock = false;
      startupDone = false;
      blockCommand = "";
      playerPosMap = new HashMap<String, Location>();
      buttons = new Stack<Location>();
      
      //Set up the listeners
      getServer().getPluginManager().registerEvents(new CommandPlayerListener(this), this);
      getServer().getPluginManager().registerEvents(new CommandBlockListener(this), this);
      getServer().getPluginManager().registerEvents(new CommandEntityListener(this), this);
      getServer().getPluginManager().registerEvents(new CommandServerListener(this), this);
      
      log.info("Autorun Commands v2.5 is enabled");
   }
   
   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
      if(commandLabel.compareToIgnoreCase("setclickcommand") == 0){
         if(sender.hasPermission("autoruncommands.setclick")){
            if(args.length != 0){
               String command = args[0];
               String associate;
               
               if(args.length == 2)
                  associate = args[1];
               else
                  associate = sender.getName();
               
               if(commandMap.get(command) != null){
                  if(playerCommandMap.get(associate) != null){
                     playerCommandMap.remove(associate);
                  }
                  playerCommandMap.put(associate, command);
                  sender.sendMessage("Command association successful");
               }
               else if(commandMap.get(command + "[op]") != null){
                  if(playerCommandMap.get(associate) != null){
                     playerCommandMap.remove(associate);
                  }
                  playerCommandMap.put(associate, command + "[op]");
                  sender.sendMessage("OP command association successful");
               }
               else{
                  sender.sendMessage("No command found with that identifier");
                  sender.sendMessage("Try \'/addacommand <identifier> <command> [args]\' first");
               }
            }
            else
               sender.sendMessage("Not enough arguments");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("removeclickcommand") == 0){
         if(sender.hasPermission("autoruncommands.removeclick")){
            String associate;
            
            if(args.length == 1)
               associate = args[0];
            else
               associate = sender.getName();
            
            if(playerCommandMap.get(associate) != null){
               playerCommandMap.remove(associate);
               sender.sendMessage("Command removed");
            }
            else
               sender.sendMessage(associate + " has no associated command");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("removedeathcommand") == 0){
         if(sender.hasPermission("autoruncommands.removedeath")){
            String associate;
            
            if(args.length == 1)
               associate = args[0];
            else
               associate = sender.getName();
            
            if(deathCommandMap.get(associate) != null){
               deathCommandMap.remove(associate);
               sender.sendMessage("Command removed");
            }
            else
               sender.sendMessage(associate + " has no associated death command");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("displayclickcommand") == 0){
         if(sender.hasPermission("autoruncommands.displayclick")){
            if(playerCommandMap.get("GLOBAL") != null)
               sender.sendMessage("Your command in use is: /" + playerCommandMap.get("GLOBAL").replace("[op]", ""));
            else if(playerCommandMap.get(sender.getName()) != null)
               sender.sendMessage("Your command in use is: /" + playerCommandMap.get(sender.getName()).replace("[op]", ""));
            else
               sender.sendMessage("You have no associated command");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("displaydeathcommand") == 0){
         if(sender.hasPermission("autoruncommands.displaydeath")){
            if(deathCommandMap.get("GLOBAL") != null)
               sender.sendMessage("Your death command in use is: /" + deathCommandMap.get("GLOBAL").replace("[op]", ""));
            else if(deathCommandMap.get(sender.getName()) != null)
               sender.sendMessage("Your death command in use is: /" + deathCommandMap.get(sender.getName().replace("[op]", "")));
            else
               sender.sendMessage("You have no associated death command");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("displaystartupcommands") == 0){
         if(sender.hasPermission("autoruncommands.displaydeath")){
            if(!startupCommands.isEmpty()){
               sender.sendMessage("The commands that run on start up are:");
               StringTokenizer st = new StringTokenizer(startupCommands, ":");
               while(st.countTokens() > 0)
                  sender.sendMessage(st.nextToken().replace("[op]", ""));
            }
            else
               sender.sendMessage("You have no commands set to run at start up");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("setcommandblock") == 0){
         if(sender.hasPermission("autoruncommands.setblock")){
            if(args.length != 0){
               if(commandMap.get(args[0]) != null){
                  blockCommand = args[0];
                  placeBlock = true;
                  sender.sendMessage("Right click with your fist to apply \'"
                  + commandMap.get(args[0]) + "\'");
               }
               else if(commandMap.get(args[0] + "[op]") != null){
                  blockCommand = args[0] + "[op]";
                  placeBlock = true;
                  sender.sendMessage("Right click with your fist to apply the OP command \'"
                  + commandMap.get(args[0] + "[op]").replace("[op]", "") + "\'");
               }
               else{
                  sender.sendMessage("No command found with that identifier");
                  sender.sendMessage("Try \'/addacommand <identifier> <command> [args]\' first");
               }
            }
            else
               sender.sendMessage("No autorun command given");
         }
      }
      else if(commandLabel.compareToIgnoreCase("addacommand") == 0){
         if(sender.hasPermission("autoruncommands.addcommand")){
            if(args.length > 1){
               String id;
               String command;
               if(commandMap.get(args[0] + "[op]") != null){
                  sender.sendMessage("An op command with that name already exists");
                  return true;
               }
               id = args[0];
               command = args[1];
               for(int i = 2; i < args.length; i++){
                  command += " " + args[i];
               }
               if(commandMap.put(id, command) != null)
                  sender.sendMessage("Overwrote old command");
               else
                  sender.sendMessage("Command added");
            }
            else
               sender.sendMessage("An identifier and command must be given");
         }
      }
      else if(commandLabel.compareToIgnoreCase("addopcommand") == 0){
         if(sender.hasPermission("autoruncommands.addopcommand")){
            if(args.length > 1){
               String id;
               String command;
               if(commandMap.get(args[0]) != null){
                  sender.sendMessage("A non-op command with that name already exists");
                  return true;
               }
               id = args[0] + "[op]";
               command = args[1];
               for(int i = 2; i < args.length; i++){
                  command += " " + args[i];
               }
               if(commandMap.put(id, command) != null)
                  sender.sendMessage("Overwrote old op command");
               else
                  sender.sendMessage("Op command added");
            }
            else
               sender.sendMessage("An identifier and command must be given");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("removeacommand") == 0){
         if(sender.hasPermission("autoruncommands.removecommand")){
            if(args.length == 1){
               boolean remove = false;
               String rmCommand = args[0];
               
               //Check if the command to be removed exists or is op
               if(commandMap.remove(rmCommand) != null)
                  remove = true;
               else if(commandMap.remove(rmCommand + "[op]") != null){
                  rmCommand += "[op]";
                  remove = true;
               }
                  
               if(remove){
                  ArrayList<String> names = new ArrayList<String>();
                  // Remove the command where it is associated with players
                  Iterator<Map.Entry<String, String>> it1 = playerCommandMap.entrySet().iterator();
                  while(it1.hasNext()){
                     Entry<String, String> mp1 = it1.next();
                     if(mp1.getValue().compareTo(rmCommand) == 0)
                        names.add(mp1.getKey());
                  }
                  for(int i = 0; i < names.size(); i++)
                     playerCommandMap.remove(names.get(i));
                  names.clear();

                  // Remove the command where it is associated with blocks
                  ArrayList<Location> places = new ArrayList<Location>();
                  Iterator<Map.Entry<Location, String>> it2 = blockCommandMap.entrySet().iterator();
                  while(it2.hasNext()){
                     Entry<Location, String> mp2 = it2.next();
                     if(mp2.getValue().compareTo(rmCommand) == 0)
                        places.add(mp2.getKey());
                  }
                  for(int i = 0; i < places.size(); i++)
                     blockCommandMap.remove(places.get(i));
                  places.clear();
                  
                  // Remove the command where it is associated with the death of players
                  it1 = deathCommandMap.entrySet().iterator();
                  while(it1.hasNext()){
                     Entry<String, String> mp1 = it1.next();
                     if(mp1.getValue().compareTo(rmCommand) == 0)
                        names.add(mp1.getKey());
                  }
                  for(int i = 0; i < names.size(); i++)
                     deathCommandMap.remove(names.get(i));
                  names.clear();
                  names.trimToSize();
                  
                  startupCommands = startupCommands.replace(":" + rmCommand, "");
                  sender.sendMessage("Command removed");
               }
               else
                  sender.sendMessage("No command was found with that identifer");
            }
            else
               sender.sendMessage("No command identifier given");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }if(commandLabel.compareToIgnoreCase("setdeathcommand") == 0){
         if(sender.hasPermission("autoruncommands.setdeath")){
            if(args.length != 0){
               String command = args[0];
               String associate;
               
               if(args.length == 2)
                  associate = args[1];
               else
                  associate = sender.getName();
               
               if(commandMap.get(command) != null){
                  if(deathCommandMap.get(associate) != null){
                     deathCommandMap.remove(associate);
                  }
                  deathCommandMap.put(associate, command);
                  sender.sendMessage("Command association successful");
               }
               else if(commandMap.get(command + "[op]") != null){
                  if(deathCommandMap.get(associate) != null){
                     deathCommandMap.remove(associate);
                  }
                  deathCommandMap.put(associate, command + "[op]");
                  sender.sendMessage("OP command association successful");
               }
               else{
                  sender.sendMessage("No command found with that identifier");
                  sender.sendMessage("Try \'/addacommand <identifier> <command> [args]\' first");
               }
            }
            else
               sender.sendMessage("Not enough arguments");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("addstartupcommand") == 0){
         if(sender.hasPermission("autoruncommands.addstartup")){
            if(args.length != 0){
               String command = args[0];
               
               if(commandMap.get(command) != null)
                  addStartupCommand(sender, command);
               else if(commandMap.get(command + "[op]") != null)
                  addStartupCommand(sender, command);
               else{
                  sender.sendMessage("No command found with that identifier");
                  sender.sendMessage("Try \'/addacommand <identifier> <command> [args]\' first");
               }
            }
            else
               sender.sendMessage("Not enough arguments");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      else if(commandLabel.compareToIgnoreCase("removestartupcommand") == 0){
         if(sender.hasPermission("autoruncommands.removestartup")){
            if(args.length != 0){
               String command = args[0];
               
               if(commandMap.get(command) != null)
                  removeStartupCommand(sender, command);
               else if(commandMap.get(command + "[op]") != null)
                  removeStartupCommand(sender, command + "[op]");
               else{
                  sender.sendMessage("No command found with that identifier");
                  sender.sendMessage("Try \'/addacommand <identifier> <command> [args]\' first");
               }
            }
            else
               sender.sendMessage("Not enough arguments");
         }
         else
            sender.sendMessage("You don't have sufficient permissions");
      }
      
      return true;
   }
   
   public void removeStartupCommand(CommandSender sender, String command){
      if(startupCommands.contains(":" + command)){
         startupCommands = startupCommands.replace(":" + command, "");
         sender.sendMessage("Command removal successful");
      }
      else{
         sender.sendMessage("No command set with that identifier");
         sender.sendMessage("Check with \'/displaystartupcommands' first");
      }
   }
   
   public void addStartupCommand(CommandSender sender, String command){
      if(startupCommands.contains(command))
         sender.sendMessage("That command is already present");
      else{
         startupCommands += ":" + command;
         sender.sendMessage("Command association successful");
      }
   }

   public HashMap<String, String> getPlayerClickMap(){
      return playerCommandMap;
   }

   public HashMap<String, String> getPlayerDeathMap(){
      return deathCommandMap;
   }

   public HashMap<Location, String> getBlockCommandMap(){
      return blockCommandMap;
   }

   public HashMap<String, String> getCommandMap(){
      return commandMap;
   }
   
   public boolean isPlaceBlock(){
      return placeBlock;
   }
   
   public void setPlaceBlock(boolean placeBlock){
      this.placeBlock = placeBlock;
   }
   
   public String getBlockCommand(){
      return blockCommand;
   }

   public HashMap<String, Location> getPlayerPosMap() {
      return playerPosMap;
   }

   public String getStartupCommands() {
      return startupCommands;
   }

   public boolean isStartupDone() {
      return startupDone;
   }

   public void setStartupDone(boolean startupDone) {
      this.startupDone = startupDone;
   }

   public Stack<Location> getButtons(){
      return buttons;
   }
}
