package mmt;

import java.io.Serializable;

public interface ServiceSelector {
	boolean ok(Service s);
}