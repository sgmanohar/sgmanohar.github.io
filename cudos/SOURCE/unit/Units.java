package unit;

import java.util.*;

public class Units{
	static Vector units;
	static{
		units=new Vector();

		//Base units

		units.addElement(new Unit( "Kilogram",	"kg","Mass"));
		units.addElement(new Unit( "Metre",	"m", "Length"));
		units.addElement(new Unit( "Second",	"s", "Time"));
		units.addElement(new Unit( "Coulomb",	"C", "Charge"));
		units.addElement(new Unit( "Mole",	"mol", "Quantity"));


		//derived units

		units.addElement(new Unit( "kg m/s^2",	"Newton",	"N", "Force"));
		units.addElement(new Unit( "mol/m^3", 1E-3,	"Molar",	"M", "Concentration"));
		units.addElement(new Unit("N/m^2",	"Pascal",	"Pa","Pressure"));
		units.addElement(new Unit("C/s",	"Ampère",	"A", "Current"));
		units.addElement(new Unit("N m",	"Joule",	"J", "Energy"));
		units.addElement(new Unit("J/C",	"Volt",		"V", "Voltage"));
		units.addElement(new Unit("A/V",	"Ohm",		"O", "Resistance"));
		units.addElement(new Unit("C/V",	"Farad",	"F", "Capacitance"));
		units.addElement(new Unit("N/A/m",	"Tesla",	"T", "Magnetic Flux Density"));

		units.addElement(new Unit( "m^3", 1E-3,	"Litre",	"l", "Volume"));
		units.addElement(new Unit( "J/s",	"Watt",		"W", "Power"));

		//Alternate measures

		units.addElement(new Unit( "m", 0.3048 ,"feet", "ft", "Length", false));
		units.addElement(new Unit( "m", 1609.344 ,"mile", "mi", "Length", false));

		units.addElement(new Unit( "l", 0.473176473 ,"pint", "pt", "Volume", false));
		units.addElement(new Unit( "l", 4.54609, "gallon", "gal", "Volume", false));

		units.addElement(new Unit( "s", 60, "minute", "min", "Time", false));
		units.addElement(new Unit( "s", 3600, "hour", "hr", "Time", false));
		units.addElement(new Unit( "s", 86400, "day", "d", "Time", false));
		units.addElement(new Unit( "s", 31556925.9747, "year", "yr", "Time", false));

		units.addElement(new Unit( "Pa", 133322.368421 ,"metre mercury", "mHg", "Pressure"));
		units.addElement(new Unit( "Pa", 101325, "atmosphere", "atm", "Pressure"));
		units.addElement(new Unit( "Pa", 9806.65, "metre water", "mH2O", "Pressure"));

		units.addElement(new Unit( "kg", 0.45359237, "Pound", "lb", "Mass", false));

		units.addElement(new Unit( "J", 4.1868, "Calorie", "Cal", "Energy"));
		units.addElement(new Unit( "J", 4.1868, "Calorie", "Cal", "Energy"));

		units.addElement(new Unit( "W", 745.699871582, "Horsepower", "hp", "Power"));

	}
	public static Enumeration elements(){return units.elements();}
}