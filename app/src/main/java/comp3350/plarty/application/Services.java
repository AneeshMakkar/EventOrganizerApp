package comp3350.plarty.application;

import comp3350.plarty.persistence.DataAccess;
import comp3350.plarty.persistence.DataAccessObject;

public class Services {
	private static DataAccess dataAccessService = null;


	public static DataAccess createDataAccess(DataAccess alternateDataAccessService){
		if (dataAccessService == null) {
			dataAccessService = alternateDataAccessService;
			dataAccessService.open(Main.getDBPathName());
		}
		return dataAccessService;
	}

	public static DataAccess createDataAccess(){
		if (dataAccessService == null){
			dataAccessService = new DataAccessObject();
			dataAccessService.open(Main.getDBPathName());
		}
		return dataAccessService;
	}

	public static DataAccess getDataAccess() {
		if (dataAccessService == null){
			System.out.println("Connection to data access has not been established.");
			System.exit(1);
		}
		return dataAccessService;
	}

	public static void closeDataAccess(){
		if (dataAccessService != null){
			dataAccessService.close();
		}
		dataAccessService = null;
	}
}
