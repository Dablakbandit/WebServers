package fr.rhaz.webservers;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.util.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import fr.rhaz.webservers.WebServers.API;
import fr.rhaz.webservers.WebServers.API.WebEvent;
import fr.rhaz.webservers.WebServers.API.WebHandler;
import fr.rhaz.webservers.WebServers.API.WebPlugin;

public class Bukkit extends JavaPlugin implements Listener, WebPlugin{
	private static Bukkit plugin;
	
	public void onEnable() {
		plugin = this;
		API.addWebPlugin(this);
        Log.setLog(API.NoLogging());
	}
	
	public static Bukkit instance(){
		return plugin;
	}

	public void callWebEvent(WebHandler handler, HttpServletRequest request, HttpServletResponse response, String target, Request baseReq) {
		getServer().getPluginManager().callEvent(new BukkitWebEvent(handler, request, response, target, baseReq));
	}
	
	public static class BukkitWebEvent extends Event implements WebEvent{
		private static final HandlerList handlers = new HandlerList();
		private final WebHandler handler;
		private HttpServletRequest request;
		private HttpServletResponse response;
		private String target;
		private Request baseReq;
		
		public BukkitWebEvent(WebHandler handler, HttpServletRequest request, HttpServletResponse response, String target, Request baseReq) {
			this.handler = handler;
			this.request = request;
			this.response = response;
			this.target = target;
			this.baseReq = baseReq;
		}
		
		public WebHandler getHandler(){
			return handler;
		}
		
		public ContextHandler getContext(){
			return handler.getContext();
		}
		
		public int getPort(){
			return handler.getServer().getPort();
		}
		
		public HttpServletRequest getRequest(){
			return request;
		}
		
		public HttpServletResponse getResponse(){
			return response;
		}
		
		public String getTarget(){
			return target;
		}
		
		public Request getBaseReq(){
			return baseReq;
		}
		
		public HandlerList getHandlers() {
			return handlers;
		}
		
		public static HandlerList getHandlerList(){
			return handlers;
		}

		public boolean isCancelled() {
			return !baseReq.isHandled();
		}
		
		public void setCancelled(boolean cancel){
			baseReq.setHandled(!cancel);
		}
	}
}