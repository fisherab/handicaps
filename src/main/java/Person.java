import java.time.LocalDate;

public class Person {

	String name;
	LocalDate date;
	int initialHandicap;
	int handicap;
	int initialIndex;
	int index;
	int played;

	public Person(String name, int handicap, int index) {
		this.name = name;
		this.initialHandicap = this.handicap = handicap;
		this.initialIndex = this.index = index;
	}

	public void addGame() {
		played++;
	}

	public int getPlayed() {
		return played;
	}

}
