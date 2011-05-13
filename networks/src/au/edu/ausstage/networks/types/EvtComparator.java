package au.edu.ausstage.networks.types;

import java.util.Comparator;

public class EvtComparator implements Comparator{
	
	public int compare(Object obj1, Object obj2) {
		Event e1 = (Event) obj1;
		Event e2 = (Event) obj2;
		
		String date_1 = e1.getFirstDate();
		String date_2 = e2.getFirstDate();
		if (date_1 == null || date_1.isEmpty())
			System.out.println(e1.getName() + " ID:" + e1.getId() + " firstDate is null or empty." );
		if (date_2 == null || date_2.isEmpty())
			System.out.println(e2.getName() + " ID:" + e2.getId() + " firstDate is null or empty." );
		
		int dateCmp = date_1.compareTo(date_2);
		if (dateCmp != 0)
            return dateCmp;
							
		String name_1 = e1.getName();
		String name_2 = e2.getName();
		if (name_1 == null || name_1.isEmpty())
			System.out.println("Event ID:" + e1.getId() + " name is null or empty." );
		if (name_2 == null || name_2.isEmpty())
			System.out.println("Event ID:" + e2.getId() + " name is null or empty." );
		
		int nameCmp = name_1.compareTo(name_2);
		if (nameCmp != 0)
            return nameCmp;

		return e1.getVenue().compareTo(e2.getVenue());
	}

	
}
