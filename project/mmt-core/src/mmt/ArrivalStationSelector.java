package mmt;

import java.io.Serializable;

public class ArrivalStationSelector implements ServiceSelector, Serializable {
	private String _stationName;

	
	public ArrivalStationSelector(String stationName) {
		_stationName = stationName;
	}

	public boolean ok(Service s) {
		String station = s.getArrivalStation();
		return station.equals(_stationName);
	}
}