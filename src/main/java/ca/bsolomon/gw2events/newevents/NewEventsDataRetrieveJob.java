package ca.bsolomon.gw2events.newevents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GJChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2event.api.dao.Names;
import ca.bsolomon.gw2events.newevents.util.BuildVersion;
import ca.bsolomon.gw2events.newevents.util.EventData;

@DisallowConcurrentExecution
public class NewEventsDataRetrieveJob implements Job {

	private GW2EventsAPI api = new GW2EventsAPI();
	
	private static HashMap<String, Boolean> events = new HashMap<>();
	
	public static ConcurrentLinkedDeque<BuildVersion> buildEvents = new ConcurrentLinkedDeque<>();
	
	private DateTimeZone zone = DateTimeZone.forID("America/New_York");
	private Chronology gregorianJuian = GJChronology.getInstance(zone);
	
	private static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static DateTimeFormatter format = new DateTimeFormatterBuilder().
			appendDayOfMonth(2).appendLiteral("/").
			appendMonthOfYear(2).appendLiteral("/").
			appendYear(4, 4).appendLiteral("-").
			appendHourOfDay(2).appendLiteral(":").
			appendMinuteOfHour(2).toFormatter();
	
	private static DateTimeFormatter dateFileFormat = new DateTimeFormatterBuilder().
			appendDayOfMonth(2).appendLiteral("-").
			appendMonthOfYear(2).appendLiteral("-").
			appendYear(4, 4).appendLiteral("-").
			appendHourOfDay(2).appendLiteral("-").
			appendMinuteOfHour(2).toFormatter();
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		if (GW2EventsAPI.eventIdToName.size() == 0) {
			GW2EventsAPI.generateEventIds();
			GW2EventsAPI.generateMapIds();
			readVersionFiles();
		}

		List<Names> result = api.queryEventIds();
		
		DateTime time = new DateTime(gregorianJuian);
		
		BuildVersion version = new BuildVersion(api.queryBuildVersion(), time);
		
		for (int i=0;i< result.size();i++) {
			Names obj = result.get(i);
			
			String eventId = obj.getId();
			String name = obj.getName();
			
			if (!GW2EventsAPI.eventIdToName.containsKey(eventId)) {
				if (!events.containsKey(eventId)) {
					String mapName = api.getEventMap(eventId);
					
					events.put(eventId, true);
					
					version.getEvents().add(new EventData(eventId, name, mapName));
				}
			}
		}
		
		if (version.getEvents().size() > 0) {
			buildEvents.add(version);
			
			saveVersionFile(version);
		}
	}

	private void readVersionFiles() {
		File folder = new File("build_events");
		
		if (!folder.exists())
			folder.mkdir();
		
		File[] files = folder.listFiles();
		
		for(File f:files) {
			if (f.isFile()) {
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(f));
					
					String line = br.readLine();
					StringTokenizer st = new StringTokenizer(line, "|");
					
					BuildVersion version = new BuildVersion(st.nextToken(), format.parseDateTime(st.nextToken()));
					
					while ((line = br.readLine()) != null) {
						st = new StringTokenizer(line, "|");
						
						EventData event = new EventData(st.nextToken(), st.nextToken(), st.nextToken());
						version.getEvents().add(event);
						
						events.put(event.getEventId(), true);
					}
					
					buildEvents.add(version);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void saveVersionFile(BuildVersion version) {
		File f = new File("build_events/"+version.getBuildVersion()+"-"+dateFileFormat.print(version.getBuildDate())+".txt");
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(f));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bw.write(version.getBuildVersion()+"|"+format.print(version.getBuildDate())+LINE_SEPARATOR);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for (EventData event :version.getEvents()) {
			try {
				bw.write(event.getEventId()+"|"+event.getEventName()+"|"+event.getMapName()+LINE_SEPARATOR);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
