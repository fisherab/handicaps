import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Swap {

	private Map<Integer, HashMap<Integer, Integer>> swaps = new HashMap<>();

	public Swap(String fileName) throws IOException {

		List<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(OldMain.class.getResourceAsStream(fileName)))) {
			String line;

			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		}
		Map<Integer, Integer> hcapForOffset = new HashMap<>();
		Map<Integer, Integer> offsetForHcap = new HashMap<>();
		int n = 0;
		for (String line : lines) {
			String[] bits = line.split("\\s");
			int winner = Integer.parseInt(bits[0]);
			offsetForHcap.put(winner, n);
			hcapForOffset.put(n++, winner);
		}

		for (String line : lines) {
			String[] bits = line.split("\\s");
			int winner = Integer.parseInt(bits[0]);
			HashMap<Integer, Integer> losers = new HashMap<>();
			swaps.put(winner, losers);

			losers.put(winner, 10);
			for (int pos = 1; pos < bits.length; pos++) {
				losers.put(hcapForOffset.get(offsetForHcap.get(winner) + pos), Integer.parseInt(bits[pos]));
			}
		}

	}

	public int get(int winner, int loser) {
		if (winner > loser) {
			return 20 - get(loser, winner);
		} else {
			Integer n = swaps.get(winner).get(loser);
			return n == null ? 1 : n;
		}
	}

}
