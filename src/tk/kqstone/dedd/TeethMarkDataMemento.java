package tk.kqstone.dedd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeethMarkDataMemento {
	private List<TeethMarkData> listTeethMarkData;
	private int index;
	private static TeethMarkDataMemento instance = new TeethMarkDataMemento();
	
	
	private TeethMarkDataMemento() {
		listTeethMarkData = new ArrayList<>();
		index = -1;
	}
	
	public static TeethMarkDataMemento getInstance() {
		return instance;
	}
	
	public void add(TeethMarkData teethMarkData) {		
		try {
			index++;
			TeethMarkData td = Utils.deepCloneObject(teethMarkData);
			if (index < listTeethMarkData.size())
				listTeethMarkData = listTeethMarkData.subList(0, index);
			listTeethMarkData.add(td);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clear() {
		listTeethMarkData.clear();
	}
	
	public TeethMarkData getPrevious() throws IndexOutOfBoundsException{
		
		if (index <= 0)
			throw new IndexOutOfBoundsException();
		TeethMarkData tmd = listTeethMarkData.get(index - 1);
//		listTeethMarkData.remove(index);
		index--;
		return tmd;
	}
	
	public TeethMarkData getNext() throws IndexOutOfBoundsException{
		index++;
		if (index >= listTeethMarkData.size())
			throw new IndexOutOfBoundsException();
		return listTeethMarkData.get(index);
	}

}
