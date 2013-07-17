package ca.bsolomon.gw2events.newevents.util;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class BuildVersion {

	private final String buildVersion;
	private final DateTime buildDate;
	
	private List<EventData> events = new ArrayList<>();
	
	private int fHashCode = 0;
	
	private static DateTimeFormatter format = new DateTimeFormatterBuilder().
			appendDayOfMonth(2).appendLiteral("/").
			appendMonthOfYear(2).appendLiteral("/").
			appendYear(4, 4).appendLiteral("-").
			appendHourOfDay(2).appendLiteral(":").
			appendMinuteOfHour(2).toFormatter();
	
	public BuildVersion(String buildVersion, DateTime buildDate) {
		super();
		this.buildVersion = buildVersion;
		this.buildDate = buildDate;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public DateTime getBuildDate() {
		return buildDate;
	}
	
	public String getBuildTime() {
		return format.print(buildDate);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof BuildVersion))return false;
	    BuildVersion otherObject = (BuildVersion)other;
	    
	    if (otherObject.getBuildVersion() == buildVersion)
	    	return true;
	    else 
	    	return false;
	}

	@Override
	public int hashCode() {
		if (fHashCode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, buildVersion);
			fHashCode = result;
		}
		
		return fHashCode;
	}

	public List<EventData> getEvents() {
		return events;
	}

	public void setEvents(List<EventData> events) {
		this.events = events;
	}
}
