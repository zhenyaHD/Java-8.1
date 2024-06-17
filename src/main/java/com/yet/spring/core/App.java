package com.yet.spring.core;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yet.spring.core.beans.Client;
import com.yet.spring.core.beans.Event;
import com.yet.spring.core.beans.EventType;
import com.yet.spring.core.loggers.EventLogger;

public class App {

    private Client client;

    private EventLogger defaultLogger;

    private Map<EventType, EventLogger> loggers;
    
    private String startupMessage;
    
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(
                "spring.xml", "loggers.xml", "aspects.xml", "db.xml");
        App app = (App) ctx.getBean("app");
        
        System.out.println(app.startupMessage);
        
        Client client = ctx.getBean(Client.class);
        System.out.println("Client says: " + client.getGreeting());
        
        app.logEvents(ctx);
        
        ctx.close();
    }
    
    public void logEvents(ApplicationContext ctx) {
        Event event = ctx.getBean(Event.class);
        logEvent(EventType.INFO, event, "One more INFO event for 1");
        
        event = ctx.getBean(Event.class);
        logEvent(EventType.INFO, event, "And one more INFO event for 1");
        
        event = ctx.getBean(Event.class);
        logEvent(EventType.ERROR, event, "Some ERROR event for 2.2");

        event = ctx.getBean(Event.class);
        logEvent(EventType.ERROR, event, "THIRD ERROR");

        event = ctx.getBean(Event.class);
        logEvent(null, event, "Some null event for 3");

        event = ctx.getBean(Event.class);
        logEvent(EventType.DbINFO, event, "Some event for database, but this is unique");

        event = ctx.getBean(Event.class);
        logEvent(EventType.FileINFO, event, "Some event for file, but he is preccious");
    }
    
    public App() {}

    public App(Client client, EventLogger eventLogger, Map<EventType, EventLogger> loggers) {
        super();
        this.client = client;
        this.defaultLogger = eventLogger;
        this.loggers = loggers;
    }

    private void logEvent(EventType eventType, Event event, String msg) {
        String message = msg.replaceAll(client.getId(), String.format("%s, from %s", client.getFullName(), client.getCity()));
        event.setMsg(message);
        
        EventLogger logger = loggers.get(eventType);
        if (logger == null) {
            logger = defaultLogger;
        }
        
        logger.logEvent(event);
    }
    
    public void setStartupMessage(String startupMessage) {
        this.startupMessage = startupMessage;
    }
    
    public EventLogger getDefaultLogger() {
        return defaultLogger;
    }

}
