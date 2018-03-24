package mmt;

import java.io.Serializable;

public class DepartureStationSelector implements ServiceSelector, Serializable {
	private String _stationName;

	
	public DepartureStationSelector(String stationName) {
		_stationName = stationName;
	}

	public boolean ok(Service s) {
		String station = s.getDepartureStation();
		return station.equals(_stationName);
	}
}