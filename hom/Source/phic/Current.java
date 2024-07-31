package phic;

import phic.common.CommonThread;
/**
 * The current state of the phic classes are here for static reference by all
 * classes. The values here may only change when loading and saving the current
 * state.
 *
 * This class eventually to be replaced with a non-static instance object,
 * perhaps Person.
 */
public class Current{
	/**
	 * The resource path, used to find data files.
	 * Must contain
	 * Variables.txt    for {@link phic.gui.Variables the variable list}
	 * Fluids.txt       for {@link phic.common.Fluids available fluids}
	 * SVP.txt          for {@link phic.Environment environmental vapour}
	 * PCO2table.txt    for {@link phic.common.GasConc GasConc conversions}
	 * PO2table.txt     for {@link phic.common.GasConc GasConc conversions}
	 * Screens.txt      for {@link phic.gui.ScreensDialog the medical screens}
	 * Pathology.txt    for {@link phic.gui.PathologyAnalysisDialog pathology analysis}
	 * Pharmacy.txt     for {@link phic.drug.Pharmacy the pharmacy}
	 * GraphSetups.txt  for {@link phic.gui.SimplePhicFrame the display}
	 * VariableInfo.txt for {@link phic.gui.VariablePropertiesPanel Variable properties}
	 * Pathology.med    for {@link phic.gui.PathologyAnalysisDialog the medical browser}
	 * OrbitSetups.txt  for {@link phic.gui.SimplePhicFrame the display}
	 */
	private static String resourcePath=
			Resource.loader.getResourceURL("").toExternalForm();

	/**
	 * Get a string representing the path to the resource files.
	 * @deprecated Replace all file operations with InputStream operations
	 * where possible, except for file loading and saving. Instead of the path,
	 * use the Resource.loader.getResource() method. This method retrieves an
	 * inputstream to the resource specified in the classpath/phic/resources
	 * folder.
	 * The file capability will be lost when deployed over the web or as a JAR.
	 */
	public static String getResourcePath(){
		return resourcePath;
	}


        /**
         * The thread that calls the organs' tick methods in sequence.
         */
        public static CommonThread thread=new CommonThread();

	/**
	 * The environment associated with the current setup
	 */
	public static Environment environment=new Environment();

	/**
	 * Current person information
	 */
	public static Person person=new Person();

	/**
	 * The body of the current setup.
	 */
	public static Body body=new Body();


	/**
	 * Initialise the environment with the current body.
	 * This must be called as soon as the body is created and initialised. It
			 * connects the environment to the body! Needed for Infusions and BasicActions
	 * to influence the body.
	 *
	 * This is part of the move to eliminate the static references to Current.
	 */
	static{
                person.body = body;
                person.environment = environment;
		environment.setBody(body);
		body.setEnvironment(environment);
                person.organList = phic.common.Organ.getList();
                body.setupControllers();
	}
}
