/**
 * 
 */
package ch.ethz.csb.youscope.addon.nemesys;

/**
 * @author Moritz Lang
 *
 */
class ScriptTemplates
{
	public static String generateJavaScriptTemplate(int numDosingUnits)
	{
		String returnValue =
			"/**\n" +
			" ** Nemesys syringe control algorithm.\n" +
			" ** @param evaluationTime time in ms since the measurement was started.\n" +
			" ** @param evaluationNumber Number of times the algorithm has been evaluated, starting at zero.\n" +
			" ** @param nemesys Nemesys device, allowing to set the flow rate of the syringes.\n" +
			" **/\n" +
			"function myAlgorithm(evaluationTime, evaluationNumber, nemesys)\n" + 
			"{\n" +
			"	// Load a state variable. This command sets it to the value it was in the last evaluation\n" +
			"	// set to, or to 1 if it did not yet exist (initialization).\n" +
			"	x = nemesys.getStateAsInteger(\"myVarName\", 1); \n" +
			"\n"+
			"	// Change state variable.\n" +
			"	// Enter your program logic here.\n" +
			"	x += 2; \n";
		returnValue += 
			"\n" +
			"	// Save states\n" +
			"	nemesys.setState(\"myVarName\", x);\n" +
			"\n" +
			"	// Set flow rates\n" +
			"	if(x <= 350)\n" + 
			"	{\n";
		for(int i=0; i<numDosingUnits; i++)
		{
			returnValue += "		nemesys.setFlowRate(" + Integer.toString(i) + ", " + Double.toString(Math.random() * 100) + ");\n";
		}
		returnValue +=
			"	}\n" + 
			"	else\n" +
			"	{\n";
		for(int i=0; i<numDosingUnits; i++)
		{
			returnValue += "		nemesys.setFlowRate(" + Integer.toString(i) + ", 0);\n";
		}
		returnValue +=
			"	}\n" +
			"}\n" +
			"\n" +
			"// Call algorithm\n" +
			"myAlgorithm(evaluationTime, evaluationNumber, nemesys);\n";
		
		return returnValue;
	}
	public static String generateMatlabTemplate(int numDosingUnits)
	{
		String returnValue =
			"% Nemesys syringe control algorithm.\n" +
			"% Three variables are available by default:\n"+
			"% - evaluationNumber: Number of times the algorithm has been evaluated, starting at zero.\n" +
			"% - evaluationTime: time in ms since the measurement was started.\n" +
			"% - nemesys: Nemesys device, allowing to set the flow rate of the syringes.\n" +
			"\n" +
			"%% Load a state variable.\n" +
			"% This command sets it to the value it was in the last evaluation\n" +
			"% set to, or to 1 if it did not yet exist (initialization).\n" +
			"x = nemesys.getStateAsInteger('myVarName', 1); \n" +
			"\n"+ 
			"%% Change state variable.\n" +
			"% Enter here your control logic.\n" +
			"x = x + 10; % [seconds]\n";
		returnValue += 
			"\n" +
			"%% Save states\n" +
			"nemesys.setState('myVarName', x);\n" +
			"\n" +
			"%% Set flow rates\n" +
			"if x <= 350\n"; 
		
		for(int i=0; i<numDosingUnits; i++)
		{
			returnValue += "	nemesys.setFlowRate(" + Integer.toString(i) + ", " + Double.toString(Math.random() * 100) + ");\n";
		}
		returnValue +=
			"else\n";
		for(int i=0; i<numDosingUnits; i++)
		{
			returnValue += "	nemesys.setFlowRate(" + Integer.toString(i) + ", 0);\n";
		}
		returnValue +=
			"end\n";
		return returnValue;
	}
}
