package ca.bsolomon.gw2events.newevents.util;

import org.joda.time.DateTime;

public class EventData {

	private final String eventId;
	private final String eventName;
	private final DateTime time;
	private final String mapName;
	
	private int fHashCode;
	
	public EventData(String eventId, String eventName, DateTime time,
			String mapName) {
		super();
		this.eventId = eventId;
		this.eventName = eventName;
		this.time = time;
		this.mapName = mapName;
	}

	public String getEventId() {
		return eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public DateTime getTime() {
		return time;
	}

	public String getMapName() {
		return mapName;
	} 
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof EventData))return false;
	    EventData otherObject = (EventData)other;
	    
	    if (otherObject.getEventId() == eventId)
	    	return true;
	    else 
	    	return false;
	}

	@Override
	public int hashCode() {
		if (fHashCode == 0) {
			int result = HashCodeUtil.SEED;
			result = HashCodeUtil.hash(result, eventId);
			fHashCode = result;
		}
		
		return fHashCode;
	}
}
