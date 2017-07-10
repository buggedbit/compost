public class demo {
	
	public static void main(String[] args) {
		DateTime d1 = new DateTime("28-02-2012 14:00:59","-",":"," ");
		DateTime d2 = new DateTime("01-03-2012 16:30:59","-",":"," ");
		System.out.println(d1.formal12Representation());
		System.out.println(d1.formal12Representation());
		System.out.println(d2.formal12Representation());
		System.out.println(d2.dateTimeDifferenceFrom(d1).hourMinRepresentation());
	}

}