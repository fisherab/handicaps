import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Main {

	int[] trigger = { 1000, 800, 650, 500, 400, 350, 300, 250, 200, 150, 100, 50, 0 };

	int[][] table = { { 10, 6, 4, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 14, 10, 7, 4, 3, 3, 2, 2, 1, 1, 1, 1, 1 },
			{ 16, 13, 10, 7, 5, 4, 4, 3, 3, 2, 2, 1, 1 }, { 18, 16, 13, 10, 8, 7, 6, 5, 4, 4, 3, 3, 2 },
			{ 19, 17, 15, 12, 10, 9, 8, 7, 6, 5, 4, 4, 3 }, { 19, 17, 16, 13, 11, 10, 9, 8, 7, 6, 5, 4, 4 },
			{ 19, 18, 16, 14, 12, 11, 10, 9, 8, 7, 6, 5, 4 }, { 19, 18, 17, 15, 13, 12, 11, 10, 9, 8, 7, 6, 5 },
			{ 19, 19, 17, 16, 14, 13, 12, 11, 10, 9, 8, 7, 6 }, { 19, 19, 18, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7 },
			{ 19, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8 },
			{ 19, 19, 19, 17, 16, 16, 15, 14, 13, 12, 11, 10, 9 },
			{ 19, 19, 19, 18, 17, 16, 16, 15, 14, 13, 12, 11, 10 } };

	public static void main(String[] args) {
		new Main();
	}

	Main() {
		for (int i = 0; i < table.length; i++) {
			System.out.println(i + " " + table[i].length);
		}

		Map<String, Person> people = new HashMap<>();

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(Main.class.getResourceAsStream("players.txt")))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] bits = line.split("\\s");
				String name = bits[0];
				LocalDate date = LocalDate.parse(bits[1], DateTimeFormatter.ISO_LOCAL_DATE);
				int handicap = Integer.parseInt(bits[2]);
				int index = Integer.parseInt(bits[3]);
				Person p = new Person(name, date, handicap, index);
				people.put(name, p);
				System.out.println(p);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(Main.class.getResourceAsStream("results.txt")))) {
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
						checkHandicap(person);
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
					updatePerson(person1, true, handicap2, level, date);
				}
				if (person2 != null) {
					updatePerson(person2, false, handicap1, level, date);
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Final handicap check");
		for (Person p : people.values()) {
			checkHandicap(p);
			System.out.println(p.name + " " + p.index + "/" + p.handicap + " " + (p.index - p.initialIndex) + "/"
					+ (p.initialHandicap - p.handicap));
		}
	}

	private void checkHandicap(Person person) {
		if (person.index >= trigger[person.handicap - 1]) {
			person.handicap--;
			System.out.println("Improved handicap for " + person.name + " to " + person.handicap);
		} else if (person.index <= trigger[person.handicap + 1]) {
			person.handicap++;
			System.out.println("Worsened handicap for " + person.name + " to " + person.handicap);
		}
	}

	private void updatePerson(Person person, boolean wins, int handicap, boolean level, LocalDate date) {

		int points;
		if (level) {
			points = wins ? table[person.handicap][handicap] : -table[handicap][person.handicap];
		} else {
			points = wins ? 10 : -10;
		}
		person.date = date;
		System.out.println(person + " " + (wins ? "wins" : "loses") + " against " + handicap + " "
				+ (level ? "level" : "handicap") + " to gain " + points);
		person.index += points;
		if (person.index < 0) {
			person.index = 0;
		}
	}
}
