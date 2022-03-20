package tk.kqstone.dedd;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tk.kqstone.dedd.ProjDataHelper.DataCheckException;

public interface IController {
	ProjData readData() throws IOException, ParserConfigurationException, SAXException, DataCheckException;

	void saveData() throws IOException;

}
