package helldragger.RolePlaySpeciality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	public RPSPlugin RPS;
	static Map<String, String> playerSessions = new HashMap<String, String>(); //liste <Nom de joueur,Couleur>
	static Map<String, Boolean> playerRPenabled= new HashMap<String, Boolean>(); //liste <Nom de joueur,Couleur>
	static Map<Player, Boolean> allowedToBypassVoiceDistance= new HashMap<Player, Boolean>();//liste <Nom de joueur,permission ou pas>
	static Map<Player, Boolean> BypassVoiceDistanceMode= new HashMap<Player, Boolean>(); //liste <Nom de joueur,Mode activ� ou pas>
	
	
	
	static
	{
		playerSessions = new HashMap<String, String>();
		playerSessions.put("CONSOLE", Config.DEFAULT_COLOR);
	}

	static
	{
		playerRPenabled = new HashMap<String, Boolean>();
	}

	

	//La o� on gere les commandes tap�es.

	public Commands(RPSPlugin plugin){
		this.RPS = plugin;
		return;
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if (sender.getName() == "CONSOLE"){
			RPSPlugin.log.warning("You must be in game to use this plugin.");
			
		}
		else
		{
			boolean access = true;
			Player player = Bukkit.getServer().getPlayer(sender.getName());

			/*
			 * Commande RP
			 * hierarchie de la commande
			 * 
			 */


			if(!RPS.events.zoneRP((Player) sender))
			{
				return false;
			}
			
			
			

			if(commandLabel.toLowerCase().equals("rp")) // Si la commande est bien rp, on continue
			{   
				
				/*
				 * VERIFICATION SI LE MONDE OU SE TROUVE LE PERSO EST AUTORIS�
				 * 
				 * 
				 */
				
				
				
				
				if(Config.RP_ENABLED)
				{
					if(Config.USE_PERMS){
						if(Permissions.hasPermission(player, "rp"))
						{
							access = true;
						}
						else{
							access = false;
						}
					}
					if(access)
					{
					
					
					/*
					 * Commande RP seule
					 * switche la session utilisateur
					 * 
					 */

						if(args.length == 0){
							switchPlayerSession(player.getName());
						}
	
						/*
						 * Commande RP + 1 argument
						 * hi�rarchie
						 * 
						 */
	
						if(args.length == 1) // Commande � 1 argument
						{
							String cmd = args[0]; // Si il y en a un seul, on le met dans une variable.
							cmd=cmd.toUpperCase();
							cmd=cmd.replace(" ", "_");
	
							/*
							 * commande RP list
							 * affiche la liste des couleurs autoris�es a l'utilisateur.
							 * 
							 */
	
							if(cmd.toLowerCase().equals("list")  | cmd.toLowerCase().equals("l")) { //si c'est la commande list, on affiche les couleurs autoris�es
								String listeAutorisees = "";
								for (int index=0; index != (Config.AUTORISED_COLORS.size()); index++)
								{
									if (index == 0){
										listeAutorisees = ChatColor.valueOf( Config.AUTORISED_COLORS.get( index ) )
												+Config.AUTORISED_COLORS.get( index );
									}
									else{
										listeAutorisees = listeAutorisees 
												+ChatColor.WHITE
												+", "
														+ChatColor.valueOf(Config.AUTORISED_COLORS.get(index))
														+Config.AUTORISED_COLORS.get(index);
									}
								}
	
								sender.sendMessage("Liste des couleurs autoris�es: \n "+listeAutorisees);
							}
	
							/*
							 * Commande RP <couleur>
							 * Tentative de changer la couleur RP par celle choisie
							 * si autoris�e:
							 * 
							 */
	
							else if(Config.AUTORISED_COLORS.toString().contains(cmd)) // Commande couleur? verification si celle ci appartient aux couleurs autoris�es ou non.
							{
	
								UpdatePlayerColor(sender.getName(),cmd);
								sender.sendMessage(ChatColor.valueOf(cmd)+"Couleur choisie d�finie."); // On envoie un message au joueur qui a tap�e la commande, pour vous prouver que ca marche!
								return true; // Tout a bien march�, on retourne vrai
							}
	
							/*
							 * sinon:
							 */
	
							else{
	
								RPSPlugin.log.warning(sender.getName()+" a tent� de selectionner la couleur interdite ou inconnue "+cmd);
								sender.sendMessage(ChatColor.DARK_RED+"Couleur choisie interdite."); // On envoie un message au joueur qui a tap�e la commande, pour vous prouver que ca marche!
	
							}
	
						}

					}
				}
			}


			/*
			 * Commande DO
			 * Envoie un message seul, distance de voix normale.
			 * 
			 */


			else if(commandLabel.toLowerCase().equals("do") ){
				if(Config.DO_ENABLED)
				{
					if(Config.USE_PERMS){
						if(Permissions.hasPermission(player, "do"))
						{
							access = true;
						}
						else{
							access = false;
						}
					}
					if(access)
					{
						boolean messagecree = false;
						String messageargs = "";


						while (args.length != 0 & messagecree == false){ //recuperation du message pass� en arguments
	
							for (int indexargs = 0;indexargs != args.length; indexargs++){
								messageargs = messageargs + args[indexargs]+" ";
							}
							messagecree = true;
						}
	
						if (Config.DO_RANGE_ENABLED){
	
							ArrayList<Player> listeJoueurs = messageRP(player.getName(),false);//recuperation de la liste de joueurs proches
	
							RPSPlugin.log.info(player.getName()+" envoie un message aux joueurs alentours("+listeJoueurs);
	
							
							
							for (int index= 0;index != listeJoueurs.size();index++){
								if (args.length != 0 & messagecree == false){ //recuperation du message pass� en arguments
	
									for (int indexargs = 0;indexargs != args.length; indexargs++){
										messageargs = messageargs + args[indexargs]+" ";
									}
									messagecree = true;
								}
	
								//envoi du message a chaque joueur.
	
								listeJoueurs.get(index).sendMessage( 
										ChatColor.DARK_GREEN
										+"*"
										+ChatColor.WHITE+
										player.getName()
										+ChatColor.DARK_GREEN
										+" dis: "
										+messageargs
										+"*");
							}
						}
						else{
							boolean switcher = false;
							if (playerRPenabled.get(Bukkit.getServer().getPlayer(player.getName())) == true){
								switchPlayerSession(player.getName());
								switcher = true;
							}
	
	
							Bukkit.getServer().getPlayer(player.getName()).chat(
									ChatColor.DARK_GREEN
									+"*"
									+ChatColor.WHITE
									+player.getName()
									+ChatColor.DARK_GREEN
									+" dis: "
									+messageargs
									+"*");
	
							if (switcher){
								switchPlayerSession(player.getName());
							}
						}
					}
				}
			}




			/*
			 * Commande SHOUT
			 * Crie un message, distance de voix multipli�e par deux.
			 * 
			 */


			else if (commandLabel.toLowerCase().equals("shout") || commandLabel.toLowerCase().equals("sh")){
				if (Config.SHOUT_ENABLED)
				{

						if(Config.USE_PERMS){
							if(Permissions.hasPermission(player, "shout"))
							{
								access = true;
							}
							else{
								access = false;
							}
						}
						if(access)
						{
							boolean messagecree = false;
							String messageargs = "";
		
		
							while (args.length != 0 & messagecree == false){ //recuperation du message pass� en arguments
		
								for (int indexargs = 0;indexargs != args.length; indexargs++){
									messageargs = messageargs + args[indexargs]+" ";
								}
								messagecree = true;
							}
		
							if (Config.SHOUT_RANGE_ENABLED){
		
								ArrayList<Player> listeJoueurs = messageRP(player.getName(),false);//recuperation de la liste de joueurs proches
		
								RPSPlugin.log.info(player.getName()+" envoie un message aux joueurs alentours("+listeJoueurs);
		
								
								for (int index= 0;index != listeJoueurs.size();index++){
									if (args.length != 0 & messagecree == false){ //recuperation du message pass� en arguments
		
										for (int indexargs = 0;indexargs != args.length; indexargs++){
											messageargs = messageargs + args[indexargs]+" ";
										}
										messagecree = true;
									}
		
									//envoi du message a chaque joueur.
		
									listeJoueurs.get(index).sendMessage( 
											ChatColor.RED
											+"*"
											+ChatColor.WHITE+
											player.getName()
											+ChatColor.RED
											+" crie: "
											+messageargs
											+"*");
								}
							}
							else{
								boolean switcher = false;
								if (playerRPenabled.get(Bukkit.getServer().getPlayer(player.getName())) == true){
									switchPlayerSession(player.getName());
									switcher = true;
								}
		
		
								Bukkit.getServer().getPlayer(player.getName()).chat(
										ChatColor.RED
										+"*"
										+ChatColor.WHITE
										+player.getName()
										+ChatColor.RED
										+" crie: "
										+messageargs
										+"*");
		
								if (switcher){
									switchPlayerSession(player.getName());
								}
							}
						}	
					}
				
			}

			/*
			 * Commande THINK / TH
			 * 
			 * pens�es en italique de couleur grise
			 * visible a distance de voix ET permission
			 *  
			 * 		    
			 */

			else if (commandLabel.toLowerCase().equals("think") || commandLabel.toLowerCase().equals("th")){
				if(Config.THINK_ENABLED)
				{
						if(Config.USE_PERMS){
							if(Permissions.hasPermission(player, "think"))
							{
								access = true;
							}
							else{
								access = false;
							}
						}
						if(access)
						{
							boolean messagecree = false;
							String messageargs = "";
		
		
							while (args.length != 0 & messagecree == false){ //recuperation du message pass� en arguments
		
								for (int indexargs = 0;indexargs != args.length; indexargs++){
									messageargs = messageargs + args[indexargs]+" ";
								}
								messagecree = true;
							}
		
							if (Config.THINK_RANGE_ENABLED){
		
								ArrayList<Player> listeJoueurs = messageRP(player.getName(),false);//recuperation de la liste de joueurs proches
		
								RPSPlugin.log.info(player.getName()+" envoie un message aux joueurs alentours("+listeJoueurs);
		
								
								
								for (int index= 0;index != listeJoueurs.size();index++){
									if (args.length != 0 & messagecree == false){ //recuperation du message pass� en arguments
		
										for (int indexargs = 0;indexargs != args.length; indexargs++){
											messageargs = messageargs + args[indexargs]+" ";
										}
										messagecree = true;
									}
		
									//envoi du message a chaque joueur.
		
									listeJoueurs.get(index).sendMessage( 
											ChatColor.GRAY
											+"*"
											+ChatColor.WHITE+
											player.getName()
											+ChatColor.GRAY
											+" pense: "
											+messageargs
											+"*");
								}
							}
							else{
								boolean switcher = false;
								if (playerRPenabled.get(Bukkit.getServer().getPlayer(player.getName())) == true){
									switchPlayerSession(player.getName());
									switcher = true;
								}
		
		
								Bukkit.getServer().getPlayer(player.getName()).chat(
										ChatColor.GRAY
										+"*"
										+ChatColor.WHITE
										+player.getName()
										+ChatColor.GRAY
										+" pense: "
										+messageargs
										+"*");
		
								if (switcher){
									switchPlayerSession(player.getName());
								}
							}
						}
					}
			}

			/*
			 * Commande DESTIN / DS
			 * Lance les d�s du destin pour un joueur.
			 * 
			 */

			else if(commandLabel.toLowerCase().equals("destin") | commandLabel.toLowerCase().equals("ds")){
				if(Config.DESTIN_ENABLED){
					if(Config.USE_PERMS){
						if(Permissions.hasPermission(player, "destin"))
						{
							access = true;
						}
						else{
							access = false;
						}
					}
					if(access)
					{
						DestinAction(sender.getName());
					}
				}
			}
 
			
			else if(commandLabel.toLowerCase().equals("spy")){
				if(Config.SPY_ENABLED){
					if(Config.USE_PERMS){
						if(Permissions.hasPermission(player, "bypass"))
						{
							access = true;
							if (allowedToBypassVoiceDistance.get(player) == null)
							{
								allowedToBypassVoiceDistance.put(player,true);
								if (BypassVoiceDistanceMode.get(player) == null)
									BypassVoiceDistanceMode.put(player, true);
							}
						}
						else
						{
							if (allowedToBypassVoiceDistance.get(player) == null)
							{
								allowedToBypassVoiceDistance.put(player,false);
							}
							access = false;
						}
					}
					if(access)
					{
						BypassVoiceDistanceMode.put(player, !BypassVoiceDistanceMode.get(player));
						if (BypassVoiceDistanceMode.get(player))
							player.sendMessage(ChatColor.GOLD+"[RolePlaySpeciality] SPY mode "+ChatColor.GREEN+"ENABLED");
						else
							player.sendMessage(ChatColor.GOLD+"[RolePlaySpeciality] SPY mode "+ChatColor.RED+"DISABLED");
						
						}
				}
			}
 

			else if(commandLabel.toLowerCase().equals("loot") | commandLabel.toLowerCase().equals("lt")){
				if (Config.LOOT_ENABLED){
					if(Config.USE_PERMS){
						if(Permissions.hasPermission(player, "loot"))
						{
							access = true;
						}
						else{
							access = false;
						}
					}
					if(access)
					{
						DestinLoot(sender.getName());
					}
				}
			}
			/*
			 * Commande RPRELOAD
			 * recharge le plug in sans arreter le serveur
			 * 
			 * 
			 */

			else if(commandLabel.toLowerCase().equals("rpreload")){
				if (Config.RP_RELOAD_ENABLED){

					if(Config.USE_PERMS){
						if(Permissions.hasPermission(player, "rpreload"))
						{
							access = true;
						}
						else{
							access = false;
						}
					}
					if(access)
					{
						RPS.onReload();
					}
				}
			}




			//			    
			//			    /*
			//			     * Commande PSessions
			//			     * montre le contenu de playerSession
			//			     * 
			//			     */
			//			    else if(commandLabel.toLowerCase().equals("playsess") || commandLabel.toLowerCase().equals("pls")){
			//			    	showPlayerSessions(Bukkit.getPlayer(sender.getName()));
			//			    }
			//			    
			//			    /*
			//			     * Commande RPenabled
			//			     * montre le contenu de playerSession
			//			     * 
			//			     */
			//			    else if(commandLabel.toLowerCase().equals("rpenabled") || commandLabel.toLowerCase().equals("rpena") ){
			//			    	showSessionsEnabled(Bukkit.getPlayer(sender.getName()));
			//			    }
			//			    
			/*
			 * FIN COMMANDES
			 * 
			 */


		}
		return false;
	}

	public static void DestinAction(String player) {
		Random rand = new Random();
		int nombreAleatoire = rand.nextInt(100) + 1;
		ArrayList<Player> joueursProches = messageRP(player);
		
		if(nombreAleatoire > Config.WIN_CHANCE){
			if (!Config.DESTIN_RANGE_ENABLED) 
			{Bukkit.getServer( ).broadcastMessage(ChatColor.BLUE+"Les d�s du destin ont �t� lanc�s par "+ChatColor.WHITE+player+ChatColor.BLUE+", son action a "+ChatColor.RED+"rat�!");}
			
			else{
				
				
				for (Player players : joueursProches){
					players.sendMessage(ChatColor.BLUE+"Les d�s du destin ont �t� lanc�s par "+ChatColor.WHITE+player+ChatColor.BLUE+", son action a "+ChatColor.RED+"rat�!");
				}
			}
			
		
		}
		else{
			if (!Config.DESTIN_RANGE_ENABLED) 
			{Bukkit.getServer( ).broadcastMessage(ChatColor.BLUE+"Les d�s du destin ont �t� lanc�s par "+ChatColor.WHITE+player+ChatColor.BLUE+", son action a "+ChatColor.GREEN+"r�ussi!");}
			else{
				
				
				for (Player players : joueursProches){
					players.sendMessage(ChatColor.BLUE+"Les d�s du destin ont �t� lanc�s par "+ChatColor.WHITE+player+ChatColor.BLUE+", son action a "+ChatColor.GREEN+"r�ussi!");
				}
			}
		}
	}

	public static void DestinLoot(String player) {
		Random rand = new Random();
		int nombreAleatoire = rand.nextInt(100) + 1;
		ArrayList<Player> joueursProches = messageRP(player);
		
		
		if (!Config.LOOT_RANGE_ENABLED) 
		{
			Bukkit.getServer().broadcastMessage(ChatColor.BLUE+"Les d�s du destin ont �t� lanc�s par "+ChatColor.WHITE+player+ChatColor.BLUE+", son num�ro de loot est le "+ChatColor.BOLD+ nombreAleatoire + " !");
		}
		else{
			
			
			
			for (Player players : joueursProches){
				players.sendMessage(ChatColor.BLUE+"Les d�s du destin ont �t� lanc�s par "+ChatColor.WHITE+player+ChatColor.BLUE+", son num�ro de loot est le "+ChatColor.BOLD+ nombreAleatoire + " !");
			}
		}
	}

	public static ArrayList<Player> messageRP(String sender){//recuperation des joueurs proches.
		
		int Distancedevoixlocal = Config.VOICE_RANGE;
		
		
		
		ArrayList<Player> listeJoueursProches = new ArrayList<Player>();
		
		Location centre = Bukkit.getPlayer(sender).getLocation();//localisation du joueur lancant le message.
		World mondeJoueur = centre.getWorld();
		
		
		Player[] listeJoueurs = Bukkit.getServer().getOnlinePlayers();//liste des joueurs connect�s
		ArrayList<Player> listeJoueursMemePlanete = new ArrayList<Player>() ;
	
		
		
		
		//liste des joueurs sur le meme monde que l'envoyeur du message.
		for (int index = 0; index != listeJoueurs.length; index++){
			if (listeJoueurs[index].getLocation().getWorld() == mondeJoueur){
				listeJoueursMemePlanete.add(listeJoueurs[index]);
			}
		}
		//verifier la distance des coordonn�es
		
		for (int index = 0; index != listeJoueursMemePlanete.size();index++){
			if ( Bukkit.getPlayer( listeJoueursMemePlanete.get( index ).getName( ) ).getLocation( ).distance( centre ) <= Distancedevoixlocal & !listeJoueursProches.contains( Bukkit.getPlayer( listeJoueurs[index].getName() ) ) ){
				listeJoueursProches.add(Bukkit.getPlayer(listeJoueursMemePlanete.get(index).getName()));
			}
		}
		
		/*
		 * Mise en commun des joueurs online a distance acceptable (Distancedevoix)
		 * de l'envoyeur du message.
		 * Verifier s'ils sont sur le meme monde que le joueur en premier lieu. fait.
		 * 
		 * 
		 */
		
		for(Player bypassingplayer : BypassVoiceDistanceMode.keySet())
		{
			if (BypassVoiceDistanceMode.get(bypassingplayer))
				if (!listeJoueursProches.contains(bypassingplayer))
					listeJoueursProches.add(bypassingplayer);
		}
		
		return listeJoueursProches;
	}

	public static ArrayList<Player> messageRP(String sender, boolean voicerangeoffset ){//recuperation des joueurs proches.
		
		int Distancedevoixlocal = Config.VOICE_RANGE;
		
		if (voicerangeoffset){
			Distancedevoixlocal = (Config.VOICE_RANGE * 2);
		}
		
		ArrayList<Player> listeJoueursProches = new ArrayList<Player>();
		
		Location centre = Bukkit.getPlayer(sender).getLocation();//localisation du joueur lancant le message.
		World mondeJoueur = centre.getWorld();
		Player[] listeJoueurs = Bukkit.getServer().getOnlinePlayers();//liste des joueurs connect�s
		ArrayList<Player> listeJoueursMemePlanete = new ArrayList<Player>() ;
	
		
		
		
		//liste des joueurs sur le meme monde que l'envoyeur du message.
		for (int index = 0; index != listeJoueurs.length; index++){
			if (listeJoueurs[index].getLocation().getWorld() == mondeJoueur){
				listeJoueursMemePlanete.add(listeJoueurs[index]);
			}
		}
		//verifier la distance des coordonn�es
		
		for (int index = 0; index != listeJoueursMemePlanete.size();index++){
			if ( Bukkit.getPlayer( listeJoueursMemePlanete.get( index ).getName( ) ).getLocation( ).distance( centre ) <= Distancedevoixlocal & !listeJoueursProches.contains( Bukkit.getPlayer( listeJoueurs[index].getName() ) ) ){
				listeJoueursProches.add(Bukkit.getPlayer(listeJoueursMemePlanete.get(index).getName()));
			}
		}
		
		/*
		 * Mise en commun des joueurs online a distance acceptable (Distancedevoix)
		 * de l'envoyeur du message.
		 * Verifier s'ils sont sur le meme monde que le joueur en premier lieu. fait.
		 * 
		 * 
		 */
		for(Player bypassingplayer : BypassVoiceDistanceMode.keySet())
		{
			if (BypassVoiceDistanceMode.get(bypassingplayer))
				if (!listeJoueursProches.contains(bypassingplayer))
					listeJoueursProches.add(bypassingplayer);
		}
		
		return listeJoueursProches;
	}

	static void UpdatePlayerColor(String playeradded,String color){
		
		RPSPlugin.log.info("Changement de la couleur utilis�e par "+playeradded+" en "+color+".");
		playerSessions.put( playeradded, color) ;
		Bukkit.getPlayer(playeradded).sendMessage(ChatColor.GOLD+"[Couleur RP] chang�e en "+ChatColor.valueOf(color)+color);
		
		return;
	}

	static void switchPlayerSession(String playerSession){ // desactive/active la couleur RP (par defaut) pour un joueur
		if(playerRPenabled.get(playerSession) == true){ //si le gars est en couleur activ�e (par defaut ou non) on desactive.
			
			playerRPenabled.put(playerSession, false);
			
			Bukkit.getPlayer(playerSession).sendMessage(ChatColor.GOLD+"[Couleur RP] d�sactivee");
		}
		else{//Si le gars n'as rien d'activ� on active.
			playerRPenabled.put(playerSession, true);
			Bukkit.getPlayer(playerSession).sendMessage(ChatColor.GOLD+"[Couleur RP] activee");
	
		}
		return;
	}

}
