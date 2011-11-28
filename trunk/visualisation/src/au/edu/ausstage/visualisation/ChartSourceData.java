package au.edu.ausstage.visualisation;

import java.io.PrintWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChartSourceData {
	String chartName = null;
	String[]columnName = null;
	String[]template = null;
	String[][] dataArr = null ;
	
	public ChartSourceData(String chartName, String[]columnName, String[][] dataArr){
		this.chartName = chartName;
		this.columnName = columnName;
		this.dataArr = dataArr;		
	}

	//constructor that merge two arrays (dataArr_1 & dataArr_2) to template
	public ChartSourceData(String chartName, String[]columnName, String[] template, String[][] dataArr_1, String[][] dataArr_2){
		this.chartName = chartName;
		this.columnName = columnName;
		this.template = template;
		
		dataArr = new String[template.length][columnName.length];
		
		for(int i = 0; i < template.length; i ++){
			dataArr[i][0] = template[i];
			
			boolean found = false;
			for(int x = 0; x < dataArr_1.length; x++){
				if (dataArr_1[x][0].equals(template[i])){
					dataArr[i][1] = dataArr_1[x][1];
					found = true;
					break;
				}
			}
			if (found == false)
				dataArr[i][1] = "0";
			
			found = false;
			for(int y = 0; y <dataArr_2.length; y++){
				if (dataArr_2[y][0].equals(template[i])){
					dataArr[i][3] = dataArr_2[y][1];
					found = true;
					break;
				}
			}
			if (found == false)
				dataArr[i][3] = "0";
			
			//column 2 and 4 are accumulated.
			if (i >= 1){
				dataArr[i][2] = Integer.toString(Integer.parseInt(dataArr[i][1]) + Integer.parseInt(dataArr[i-1][2]));
				dataArr[i][4] = Integer.toString(Integer.parseInt(dataArr[i][3]) + Integer.parseInt(dataArr[i-1][4]));
			}else {
				dataArr[i][2] = dataArr[i][1];
				dataArr[i][4] = dataArr[i][3];
			}
		}
	}
	
	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public String[] getColumnName() {
		return columnName;
	}

	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}

	public String[][] getDataArr() {
		return dataArr;
	}

	public void setDataArr(String[][] dataArr) {
		this.dataArr = dataArr;
	}
	
	public void printDataArr(){
		for (int i = 0; i < dataArr.length; i++){ 
			
			for (int j = 0; j < dataArr[i].length; j++) {
					System.out.print(dataArr[i][j] + "\t");
			}
			System.out.println();
		
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public String toJSON(){
		if (columnName == null || dataArr == null)
			return null;
		
		JSONArray  json_record  = new JSONArray();
		
		for (int i = 0; i < dataArr.length; i++){ 
			
			JSONObject object = new JSONObject();
		
			for (int j = 0; j < columnName.length; j++) {
				object.put(columnName[j], dataArr[i][j]);
				//System.out.print(dataArr[i][j] + "\t");
			}
			//System.out.println();
			json_record.add(object);
		}
		
		return json_record.toString();
	}
	
	public void toCSV(PrintWriter writer){
		
		if (columnName == null || dataArr == null)
			return ;
		
		for (int i = 0; i < columnName.length; i++) {
			if (i != 0)
				writer.append('\t');  
			writer.append(columnName[i]);			    			   
		}
		writer.append('\n');
		
		for (int i = 0; i < dataArr.length; i++){
			for (int j = 0; j < dataArr[i].length; j++){
				if (j != 0)
					writer.append('\t');
				writer.append(dataArr[i][j]);					
			}	
			writer.append('\n');
		}
		writer.flush();
		//writer.close();
		
	}
	
}
