import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Triggers {

	public class Value {

		private Integer lowHcap;

		public Integer getLowHcap() {
			return lowHcap;
		}

		public Integer getLowTrigger() {
			return lowTrigger;
		}

		public Integer getHighHcap() {
			return highHcap;
		}

		public Integer getHighTrigger() {
			return highTrigger;
		}

		private Integer lowTrigger;
		private Integer highHcap;
		private Integer highTrigger;

		public Value(Integer highHcap, Integer highTrigger, Integer lowHcap, Integer lowTrigger) {
			this.lowHcap = lowHcap;
			this.lowTrigger = lowTrigger;
			this.highHcap = highHcap;
			this.highTrigger = highTrigger;
		}

		@Override
		public String toString() {
			return lowHcap + ":" + lowTrigger + " - " + highHcap + ":" + highTrigger;
		}

	}

	private Map<Integer, Value> values = new HashMap<>();

	public Triggers(String name) throws IOException {
		List<Integer> hcaps = new ArrayList<>();
		List<Integer> triggers = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(OldMain.class.getResourceAsStream("triggers.txt")))) {
			String line;

			while ((line = br.readLine()) != null) {
				String[] bits = line.split("\\s");
				hcaps.add(Integer.parseInt(bits[0]));
				triggers.add(Integer.parseInt(bits[1]));
			}
		}
		values.put(hcaps.get(0), new Value(null, null, hcaps.get(1), triggers.get(1)));
		for (int i = 1; i < hcaps.size() - 1; i++) {
			values.put(hcaps.get(i),
					new Value(hcaps.get(i - 1), triggers.get(i - 1), hcaps.get(i + 1), triggers.get(i + 1)));
		}
		values.put(hcaps.get(hcaps.size() - 1),
				new Value(hcaps.get(hcaps.size() - 2), triggers.get(hcaps.size() - 2), null, null));
	}

	public Value get(int handicap) {
		return values.get(handicap);
	}

}
