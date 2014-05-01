package me.johnnywoof.check;

import me.johnnywoof.CheckType;
import me.johnnywoof.NoHack;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockCheck {
	
	public boolean checkPlace(NoHack nh, Block b, Block pa, Player p){
		
		if(pa.isLiquid()){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.IMPOSSIBLE, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.IMPOSSIBLE, p);
			
			nh.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Impossible! Placed a block on a liquid. VL " + id);
					
				}
				return true;
			
			}
			
		}
		return false;
		
	}
	
	public boolean checkBreak(NoHack nh, long ls, Block b, Player p){
		
		XYZ c = nh.getCurrentBlock(p.getName());
		
		if(c != null){
			
			if(c.x != b.getX() || c.y != b.getY() || c.z != b.getZ()){
				
				int id = nh.raiseViolationLevel(p.getName(), CheckType.VISIBLE, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VISIBLE, p);
				
				nh.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
					
					if(id != 0){
						
						Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Visible! Tried to break another block than the one interacting with. VL " + id);					
					}
					return true;
				
				}
				
			}
			
		}
		
		if((System.currentTimeMillis() - ls) >= 2500){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.NOSWING, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.NOSWING, p);
			
			nh.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed NoSwing! Difference was " + (System.currentTimeMillis() - ls) + ". VL " + id);
					
				}
				return true;
			
			}
			
		}
		return false;
		
	}
	
	/*/**
	 * @param b = airblock
	 * 
	private boolean checkFullbright(Block b){
		
		return (b.getLightLevel() <= 0);
		
	}*/
	
}
