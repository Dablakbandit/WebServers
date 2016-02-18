package fr.rhaz.webservers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.log.Logger;

import fr.rhaz.webservers.WebServers.API.WebPlugin;

import java.io.IOException;
import java.util.ArrayList;

public class WebServers {
	private static ArrayList<WebPlugin> plugins = new ArrayList<WebPlugin>();
	
	public static Bungee bungee(){
		return Bungee.instance();
	}
	
	public static Bukkit bukkit(){
		return Bukkit.instance();
	}
	
	public static WebServers instance(){
		return new WebServers();
	}
	
	public static API API(){
		return new API();
	}
	
	public static class API {
		
		public static void addWebPlugin(WebPlugin plugin){
			plugins.add(plugin);
		}
		
		public static void remWebPlugin(WebPlugin plugin){
			plugins.remove(plugin);
		}
		
		public static interface WebPlugin{
			void callWebEvent(WebHandler handler, HttpServletRequest request, HttpServletResponse response, String target, Request baseReq);
		}
		
		public static WebServer createServer(int port, String name, String basePath){
			ContextHandler context = new ContextHandler("/"+basePath);
			SessionHandler sessions = new SessionHandler(new HashSessionManager());
			context.setHandler(sessions);
			final WebServer server = new WebServer(port, name);
			WebHandler handler = new WebHandler(context, server);
			server.setWebHandler(handler);
			sessions.setHandler(handler);
			server.setSessionIdManager(new HashSessionIdManager());
			server.setHandler(sessions);
			server.setStopAtShutdown(true);
			return server;
		}
		
		public static void deleteServer(WebServer server){
			server.destroy();
		}
		
		public static class WebServer extends Server{
			static int serverPort;
			static String serverName;
			WebHandler handler;
			
			public WebServer(int port, String name){
				super(port);
				serverPort = port;
				serverName = name;
			}
			
			public int getPort(){
				return serverPort;
			}
			
			public String getName(){
				return serverName;
			}
	
			public void setName(String name){
				serverName = name;
			}
			
			public WebHandler getWebHandler(){
				return handler;
			}
			
			public void setWebHandler(WebHandler handler){
				this.handler = handler;
			}
		}
		
		public static class WebHandler extends AbstractHandler{
			private ContextHandler handlerContext;
			private WebServer handlerServer;;
			
			public WebHandler(ContextHandler context, WebServer server){
				handlerContext = context;
				handlerServer = server;
			}
			
			public void handle(String target, Request baseReq, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.setStatus(200);
				baseReq.setHandled(true);
				for(WebPlugin plugin:plugins){
					plugin.callWebEvent(this, request, response, target, baseReq);
				}
			}
			
			public ContextHandler getContext(){
				return handlerContext;
			}
			
			public WebServer getServer(){
				return handlerServer;
			}
		}
		
		public static interface WebEvent{
			public WebHandler getHandler();
			public ContextHandler getContext();
			public int getPort();
			public HttpServletRequest getRequest();
			public HttpServletResponse getResponse();
			public String getTarget();
			public Request getBaseReq();
			public boolean isCancelled();
			public void setCancelled(boolean cancel);
		}
		
		public static NoLogging NoLogging(){
			return new NoLogging();
		}
		
		public static class NoLogging implements Logger {
		    @Override public String getName() { return "no"; }
		    @Override public void warn(String msg, Object... args) { }
		    @Override public void warn(Throwable thrown) { }
		    @Override public void warn(String msg, Throwable thrown) { }
		    @Override public void info(String msg, Object... args) { }
		    @Override public void info(Throwable thrown) { }
		    @Override public void info(String msg, Throwable thrown) { }
		    @Override public boolean isDebugEnabled() { return false; }
		    @Override public void setDebugEnabled(boolean enabled) { }
		    @Override public void debug(String msg, Object... args) { }
		    @Override public void debug(Throwable thrown) { }
		    @Override public void debug(String msg, Throwable thrown) { }
		    @Override public Logger getLogger(String name) { return this; }
		    @Override public void ignore(Throwable ignored) { }
		}
	}
}
