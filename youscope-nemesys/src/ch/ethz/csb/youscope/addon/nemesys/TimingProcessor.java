/**
 * 
 */
package ch.ethz.csb.youscope.addon.nemesys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Moritz Lang
 *
 */
class TimingProcessor
{
	public static String getProtocol(SyringeTableRow[] timings)
	{
		StringBuilder protocol = new StringBuilder(timings.length * 30);
		for(SyringeTableRow timing : timings)
		{
			protocol.append(timing.time);
			for(double flowRate: timing.flowRates)
			{
				protocol.append(" ");
				protocol.append(flowRate);
			}
			protocol.append("\n");
		}
		return protocol.toString();
	}
	public static SyringeTableRow[] getTimings(String protocol) throws NemesysException
	{
		ArrayList<SyringeTableRow> timings = new ArrayList<SyringeTableRow>();
		BufferedReader reader = new BufferedReader(new StringReader(protocol));
		int numDosingUnits = -1;
		try
		{
			for(String line = reader.readLine(); line != null; line = reader.readLine())
			{
				String[] elements = line.split(" ");
				if(elements.length == 0)
					continue;
				else if(numDosingUnits == -1)
					numDosingUnits = elements.length -1;
				else if(numDosingUnits != elements.length - 1)
					throw new NemesysException("Number of dosing units different at different times in the Nemesys protocol.");
				SyringeTableRow timing = new SyringeTableRow(Long.parseLong(elements[0]), numDosingUnits);
				for(int i=0; i<numDosingUnits; i++)
				{
					timing.flowRates[i] = Double.parseDouble(elements[i+1]);
				}
				timings.add(timing);
			}
		}
		catch(IOException e)
		{
			throw new NemesysException("I/O error while parsing the Nemesys protocol.", e);
		}
		catch(NumberFormatException e)
		{
			throw new NemesysException("Could not parse time or flow rate in Nemesys script.", e);
		}
		return timings.toArray(new SyringeTableRow[timings.size()]);
	}
	
}
