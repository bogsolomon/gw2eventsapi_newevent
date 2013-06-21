package ca.bsolomon.gw2events.newevents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GJChronology;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2events.newevents.util.EventData;

@DisallowConcurrentExecution
public class NewEventsDataRetrieveJob implements Job {

	private GW2EventsAPI api = new GW2EventsAPI();
	
	private static ConcurrentHashMap<String, Boolean> events = new ConcurrentHashMap<>(16, 0.8f, 1);
	
	public static ConcurrentHashMap<DateTime, List<EventData>> eventTimes = new ConcurrentHashMap<>(16, 0.8f, 1);
	
	private DateTimeZone zone = DateTimeZone.forID("America/New_York");
	private Chronology gregorianJuian = GJChronology.getInstance(zone);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		if (GW2EventsAPI.eventIdToName.size() == 0) {
			GW2EventsAPI.generateEventIds();
		}

		JSONArray result = api.queryEventIds();
		
		DateTime time = new DateTime(gregorianJuian);
		
		for (int i=0;i< result.size();i++) {
			JSONObject obj = result.getJSONObject(i);
			
			String eventId = obj.getString("id");
			String name = obj.getString("name");
			
			if (!GW2EventsAPI.eventIdToName.containsKey(eventId)) {
				if (!events.containsKey(eventId)) {
					String mapName = api.getEventMap(eventId);
					
					events.put(eventId, true);
					
					if (!eventTimes.contains(time)) {
						eventTimes.put(time, new ArrayList<EventData>());
					}
					
					eventTimes.get(time).add(new EventData(eventId, name, time, mapName));
				}
			}
		}
	}

}
