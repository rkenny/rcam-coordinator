package tk.bad_rabbit.rcam.app;

import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletRegistration;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.WebappContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.DefaultCoordinatorSpringConfig;



public class Coordinator {
  public static void main(String[] args) {    
    final WebappContext context;
    final HttpServer httpServer;
    
    IConfigurationProvider configurationProvider = new ConfigurationProvider();
    
    httpServer = prepareHttpServer((String) configurationProvider.getServerVariable("httpServerAddress"), (Integer) configurationProvider.getServerVariable("httpServerPort"));
    context = prepareWebappContext((String) configurationProvider.getServerVariable("httpServerBaseUrl"));
    
    runHttpSubsystem(httpServer, context);
    waitForExitSignal();
    
    httpServer.shutdownNow();
    System.exit(0);
  }
  
  
  private static HttpServer prepareHttpServer(String hostname, int port) {
    HttpServer httpServer = new HttpServer();
    httpServer.addListener(new NetworkListener("grizzly2", hostname, port));
    
    return httpServer;
  }
  
  private static WebappContext prepareWebappContext(String baseUrl) {
    WebappContext context = new WebappContext("context", baseUrl);
    
    AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();  
    context.addListener(new ContextLoaderListener(rootContext));
    
    AnnotationConfigWebApplicationContext dispatcherServlet = new AnnotationConfigWebApplicationContext();
    dispatcherServlet.register(DefaultCoordinatorSpringConfig.class);
    
    
    ServletRegistration.Dynamic dispatcher = context.addServlet("dispatcher", new DispatcherServlet(dispatcherServlet));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
    
    return context;
  }
  

  
  private static void runHttpSubsystem(HttpServer httpServer, WebappContext context) {
    context.deploy(httpServer);
    try {
      httpServer.start();
      System.out.println("Coordinator running");
    } catch (IOException e) {
      System.out.println("Terminal error. Cannot start http server");
      System.exit(-1);
    }
  }
  
  private static void waitForExitSignal() {
    System.out.println("Waiting for exit signal");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
  }
  
}
