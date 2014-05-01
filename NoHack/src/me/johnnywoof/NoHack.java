package me.johnnywoof;

import java.util.HashMap;

import me.johnnywoof.check.BlockCheck;
import me.johnnywoof.check.ChatCheck;
import me.johnnywoof.check.FightCheck;
import me.johnnywoof.check.InteractCheck;
import me.johnnywoof.check.MovingCheck;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.XYZ;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class NoHack extends JavaPlugin{

	public MovingCheck mc;
	public BlockCheck bc;
	public InteractCheck ic;
	public FightCheck fc;
	public ChatCheck cc;
	
	final private HashMap<String, Violation> viodata = new HashMap<String, Violation>();
	final private HashMap<String, Long> lastswong = new HashMap<String, Long>();
	final public HashMap<String, Long> deniedlogin = new HashMap<String, Long>();
	final private HashMap<String, MoveData> movedata = new HashMap<String, MoveData>();
	private final HashMap<String, XYZ> currentInteracting = new HashMap<String, XYZ>();
	
	public static int tps = 0;
	private long second = 0;

	public void onEnable(){
		mc = new MovingCheck();
		bc = new BlockCheck();
		ic = new InteractCheck();
		fc = new FightCheck();
		cc = new ChatCheck();
		this.getServer().getPluginManager().registerEvents(new NoHackListener(this), this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			long sec;
			int ticks;
			
			@Override
			public void run()
			{
				sec = (System.currentTimeMillis() / 1000);
				
				if(second == sec)
				{
					ticks++;
				}
				else
				{
					second = sec;
					tps = (tps == 0 ? ticks : ((tps + ticks) / 2));
					ticks = 0;
					
					tps = tps + 1;
					
					if(tps > 20){
						tps = 20;
					}
					
				}
			}
		}, 20, 1);
		this.reloadConfig();
		this.getLogger().info("[NoHack] NoHack has been enabled!");
	}
	
	public void onDisable(){
		this.getServer().getScheduler().cancelTasks(this);
		this.getLogger().info("[NoHack] NoHack has been disabled!");
	}
	
	public void reloadConfig(){
		
		
		
	}
	
	public XYZ getCurrentBlock(String n){
		if(this.currentInteracting.containsKey(n)){
			
			return this.currentInteracting.get(n);
			
		}else{
			
			return null;
			
		}
	}
	
	public void setCurrentBlock(String n, XYZ v){
		this.currentInteracting.put(n, v);
	}
	
	public MoveData getMoveData(String n){
		if(this.movedata.containsKey(n)){
			
			return this.movedata.get(n);
			
		}else{
			
			return new MoveData(0, 0);
			
		}
	}
	
	public void setMoveData(String n, MoveData v){
		this.movedata.put(n, v);
	}
	
	public void setViolation(String v, Violation vio){
		this.viodata.put(v, vio);
	}
	
	public Violation getViolation(String v){
		
		Violation vio = null;
		
		if(this.viodata.containsKey(v)){
			
			vio = this.viodata.get(v);
			
		}else{
			
			vio = new Violation();
			
		}
		
		return vio;
		
	}
	
	public void updateLastSwong(String v){
		this.lastswong.put(v, System.currentTimeMillis());
	}
	
	public int raiseViolationLevel(String v, CheckType ct, Player p){
		
		Violation vio = null;
		
		if(this.viodata.containsKey(v)){
			
			vio = this.viodata.get(v);
			
		}else{
			
			vio = new Violation();
			
		}
		
		boolean non = vio.shouldNotify();
		
		if(vio.raiseLevel(ct, p)){//Plugin canceled it, do not notify
			
			non = false;
			
		}
		
		if(non){
			
			vio.updateNotify();
			
		}
		
		this.viodata.put(v, vio);
		
		if(non){
			
			return vio.getLevel(ct);
			
		}else{
			
			return 0;
			
		}
		
	}
	
	public long getLastSwong(String v){
		if(this.lastswong.containsKey(v)){
			return this.lastswong.get(v);
		}else{
			return 0;
		}
	}
	
	private void displayHelp(CommandSender sender){
		
		sender.sendMessage(ChatColor.GREEN + "/nohack ak <name> " + ChatColor.GOLD + "- Checks for anti-knockback");
		sender.sendMessage(ChatColor.GREEN + "/nohack info <name> " + ChatColor.GOLD + "- Views violation data");
		sender.sendMessage(ChatColor.GREEN + "/nohack reload " + ChatColor.GOLD + "- Reloads the config file");
		sender.sendMessage(ChatColor.GREEN + "/nohack test " + ChatColor.GOLD + "- Developer left over");
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if(sender instanceof Player){
			
			Player p = (Player) sender;
			
			if(args.length <= 0){
				
				this.displayHelp(sender);
				
			}else{
				
				if(args[0].equalsIgnoreCase("reload")){
					
					this.reloadConfig();
					
					sender.sendMessage(ChatColor.GOLD + "Config file has been reloaded.");
					
				}else if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")){
					
					HandlerList.unregisterAll(this);
					
					if(args[0].equalsIgnoreCase("enable")){
						
						this.getServer().getPluginManager().registerEvents(new NoHackListener(this), this);
						sender.sendMessage(ChatColor.GREEN + "Listener is now active");
						
					}else{
						
						sender.sendMessage(ChatColor.GREEN + "Listener is no longer active");
						
					}
					
				}else if(args[0].equalsIgnoreCase("test")){
					
					p.setVelocity(p.getVelocity().setX(4));
					
				}else if(args[0].equalsIgnoreCase("info")){
					
					if(args.length <= 1){
						this.displayHelp(sender);
					}else{
					
						Player t = this.getServer().getPlayer(args[1]);
						
						if(t != null){
						
							//sender.sendMessage(ChatColor.GREEN + t.getName() + "" + ChatColor.GOLD + "'s ping is " + Utils.getPing(t) + " milliseconds.");
							//sender.sendMessage(ChatColor.GREEN + t.getName() + "" + ChatColor.GOLD + "'s ip is " + Utils.getIP(t) + ".");
							sender.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
							Violation vio = null;
							
							if(this.viodata.containsKey(t.getName())){
								
								vio = this.viodata.get(t.getName());
								
							}else{
								
								vio = new Violation();
								
							}
							
							for(CheckType ct : CheckType.values()){
								
								int level = vio.getLevel(ct);
								
								if(level > 0){
									
									sender.sendMessage(ChatColor.GREEN + "" + ct.toString().toLowerCase() + "" + ChatColor.RED + ": " + ChatColor.YELLOW + "" + level);
									
								}
								
							}
							
							sender.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
						
						}else{
							
							sender.sendMessage(ChatColor.RED + "" + args[1] + " is not online.");
							
						}
					
					}
					
				}else{
					
					this.displayHelp(sender);
					
				}
				
			}
			
			p = null;
			
		}else{
			
			sender.sendMessage(ChatColor.GREEN + "Console is not supported, sorry!");
			
		}
		
		return true;
		
	}
	
}
