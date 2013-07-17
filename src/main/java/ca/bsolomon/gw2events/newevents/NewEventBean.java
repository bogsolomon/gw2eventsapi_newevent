package ca.bsolomon.gw2events.newevents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GJChronology;
import org.primefaces.event.SelectEvent;

import ca.bsolomon.gw2events.newevents.util.BuildVersion;
import ca.bsolomon.gw2events.newevents.util.EventData;

@ManagedBean(name="newEventBean")
@ViewScoped
public class NewEventBean {

	private DateTimeZone zone = DateTimeZone.forID("America/New_York");
	private Chronology gregorianJuian = GJChronology.getInstance(zone);
	
	private Date date = new Date();  
    
    public Date getDate() {  
        return date;  
    }  
  
    public void setDate(Date date) {  
        this.date = date;  
    }  
	
    public void handleDateSelect(SelectEvent event) {  
          
    }  
    
    public List<BuildVersion> getEvents() {
    	List<BuildVersion> data = new ArrayList<>();
    		
    	DateTime cutOff = new DateTime(date.getTime(), gregorianJuian); 
    	
    	for (BuildVersion build:NewEventsDataRetrieveJob.buildEvents) {
    		if (build.getBuildDate().isAfter(cutOff)) {
    			data.add(build);
    		}
    	}
    	
    	return data;
    }
}
