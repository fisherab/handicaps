import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Main {

	String personToFollow = "Pam";
	int year = 2017;

	boolean debug = false;

	public static void main(String[] args) throws IOException {
		new Main();
	}

	Main() throws IOException {

		Triggers triggers = new Triggers("triggers.txt");

		Swap swaps = new Swap("swap.txt");

		Map<String, Person> people = new HashMap<>();

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(Main.class.getResourceAsStream("players-" + year + ".txt")))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] bits = line.split("\\s+");
				String name = bits[0];
				int handicap = Integer.parseInt(bits[1]);
				int index = Integer.parseInt(bits[2]);
				Person p = new Person(name, handicap, index);
				people.put(name, p);
				if (debug) {
					System.out.println(p);
				}
			}
		}
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(Main.class.getResourceAsStream("results-" + year + ".txt")))) {
			String line;
			LocalDate oldDate = null;
			while ((line = br.readLine()) != null) {
				String[] bits = line.split("\\s");

				LocalDate date = LocalDate.parse(bits[0], DateTimeFormatter.ISO_LOCAL_DATE);
				String name1 = bits[1];
				Person person1 = people.get(name1);
				int handicap1 = person1 == null ? Integer.parseInt(name1) : person1.handicap;

				if (oldDate != null && date.isAfter(oldDate)) {
					for (Person person : people.values()) {
						checkHandicap(person, triggers);
					}
				}
				oldDate = date;

				boolean level;
				String hl = bits[2].toUpperCase();
				if (hl.equals("L")) {
					level = true;
				} else if (hl.equals("H")) {
					level = false;
				} else {
					System.out.println("Bad result " + line);
					continue;
				}
				String name2 = bits[3];
				Person person2 = people.get(name2);
				int handicap2 = person2 == null ? Integer.parseInt(name2) : person2.handicap;

				if (person1 != null) {
					updatePerson(person1, true, person2, handicap2, level, date, swaps);
					person1.addGame();
				}
				if (person2 != null) {
					updatePerson(person2, false, person1, handicap1, level, date, swaps);
					person2.addGame();
				}

			}

		}

		if (debug) {
			System.out.println("Final handicap check");
		}
		for (Person p : people.values()) {
			checkHandicap(p, triggers);
			System.out.println(p.name + " " + p.index + "/" + p.handicap + " " + (p.index - p.initialIndex) + "/"
					+ (p.initialHandicap - p.handicap) + " played " + p.getPlayed());
		}
	}

	private void checkHandicap(Person person, Triggers triggers) {
		Triggers.Value t = triggers.get(person.handicap);
		if (person.index >= t.getHighTrigger()) {
			person.handicap = t.getHighHcap();
			System.out.println("Improved handicap for " + person.name + " to " + person.handicap);
		} else if (person.index <= t.getLowTrigger()) {
			person.handicap = t.getLowHcap();
			System.out.println("Worsened handicap for " + person.name + " to " + person.handicap);
		}
	}

	private void updatePerson(Person person, boolean wins, Person opponent, int handicap, boolean level, LocalDate date,
			Swap swaps) {

		int points;
		if (level) {
			points = wins ? swaps.get(person.handicap, handicap) : -swaps.get(handicap, person.handicap);
		} else {
			points = wins ? 10 : -10;
		}
		person.date = date;
		if (debug || person.name.equals(personToFollow)) {
			System.out.println(person + " " + (wins ? "wins" : "loses") + " against " + handicap
					+ (opponent != null ? " (" + opponent.name + ") " : " ") + (level ? "level" : "handicap")
					+ (points > 0 ? " to gain " + points : " to lose " + (-points)) + " points");
		}
		person.index += points;
		if (person.index < 0) {
			person.index = 0;
		}
	}
}
