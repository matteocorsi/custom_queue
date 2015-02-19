package diario.alimentare.coda.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int data1 = 100;
	private String data2 = "hello";
	private List<String> list = new ArrayList<String>();
	
	public DataObject(String data){
		data2=data;
		list.add("String 1");
		list.add("String 2");
		list.add("String 3");
	}

	// getter and setter methods

	@Override
	public String toString() {
		return "DataObject [data1=" + data1 + ", data2=" + data2 + ", list="+ list + "]";
	}

}
