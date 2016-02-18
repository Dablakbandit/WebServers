package fr.rhaz.webservers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.log.Log;

import fr.rhaz.webservers.WebServers.API;
import fr.rhaz.webservers.WebServers.API.WebEvent;
import fr.rhaz.webservers.WebServers.API.WebHandler;
import fr.rhaz.webservers.WebServers.API.WebPlugin;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class Bungee extends Plugin implements Listener, WebPlugin{
	private static Bungee plugin;
	
	public void onEnable() {
		plugin = this;
		API.addWebPlugin(this);
		Log.setLog(API.NoLogging());
	}
	
	public static Bungee instance(){
		return plugin;
	}

	public void callWebEvent(WebHandler handler, HttpServletRequest request, HttpServletResponse response, String target, Request baseReq) {
		getProxy().getPluginManager().callEvent(new BungeeWebEvent(handler, request, response, target, baseReq));
	}
	
	public class BungeeWebEvent extends Event implements WebEvent{
		private final WebHandler handler;
		private HttpServletRequest request;
		private HttpServletResponse response;
		private String target;
		private Request baseReq;
		
		public BungeeWebEvent(WebHandler handler, HttpServletRequest request, HttpServletResponse response, String target, Request baseReq) {
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

		public boolean isCancelled() {
			return !baseReq.isHandled();
		}
		
		public void setCancelled(boolean cancel){
			baseReq.setHandled(!cancel);
		}
	}
}