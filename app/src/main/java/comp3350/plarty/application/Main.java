package comp3350.plarty.application;

public class Main
{
	public static String dbName = "Plarty";
	private static String dbPathName = "database/Plarty";

	public static void main(String[] args)
	{
		System.out.println("Starting up Plarty…");
		startUp();
		
		shutDown();
		System.out.println("All done");
	}

	public static void startUp() {
		Services.closeDataAccess();
		Services.createDataAccess();
	}

	public static void shutDown()
	{
		Services.closeDataAccess();
	}

	public static void setDBPathName(String pathName) {
		System.out.println("Setting DB path to: " + pathName);
		dbPathName = pathName;
	}
	public static String getDBPathName() {
		if (dbPathName == null)
			return dbName;
		else
			return dbPathName;
	}
}
