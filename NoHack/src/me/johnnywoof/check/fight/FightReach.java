package me.johnnywoof.check.fight;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FightReach extends Check{

	public FightReach(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.FIGHT);
	}
	
	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block b, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		double d = p.getEyeLocation().distanceSquared(e.getEyeLocation());
		
		//reach check
		
		if(d > ((p.getGameMode() == GameMode.CREATIVE) ? 27.5 : 14)){
			
			int id = this.vars.raiseViolationLevel(CheckType.ATTACK_REACH, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.ATTACK_REACH, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
				
				if(id != 0){
					
					String message = Setting.fightreach;
					
					message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
					message = message.replaceAll(".vl.", id + "");

					Utils.messageAdmins(message);
					
				}
				return 1;
			
			}
			
		}
		
		return 0;
		
	}

}