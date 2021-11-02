package test;

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MCA_test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		List<List<String>> tmp = new ArrayList<>();
		List<Double>IBIlist = new ArrayList<>() ;
		
		calValues calculation = new calValues();
		READCSV readfile = new READCSV();
		
		String path="C:\\Users\\Dongwook\\Desktop\\IBI.csv";
		tmp = readfile.readCSV(path);
		
		for(int i=1;i<tmp.size();i++) {
			IBIlist.add(Double.parseDouble(tmp.get(i).get(1))); //cast string to integer(csv 파일 읽을때 string으로 읽음)
		}
		
		List<Double>HR = new ArrayList<>() ;
		List<Double>RMSSD = new ArrayList<>() ;
		List<Double>PNN50 = new ArrayList<>() ;
		
		
		HR = calculation.getValues(IBIlist).get(0);
		PNN50 = calculation.getValues(IBIlist).get(1);
		RMSSD = calculation.getValues(IBIlist).get(2);
		
		
		
		//바탕화면 txt파일에 작성해보기
		File file = new File("C:\\Users\\Dongwook\\Desktop\\text.txt");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for(int k=0;k<RMSSD.size();k++) {
			writer.write(Double.toString(RMSSD.get(k))+"\n");
			
			}
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	

	

}

class calValues {
	
	//list합을 구하는 function
	public double Sumlist(List<Double> listname,int index_start ,int index_finish) {
		double sum=0;
		for(int i =index_start ; i<index_finish; i++) 
			sum=sum+listname.get(i);
		return sum;
		
	}
	
	public List<List<Double>> getValues(List<Double> listname) {
		
		List<List<Double>> total = new ArrayList<>();
		
		List<Double> HRResult = new ArrayList<>();
		List<Double> pnn50Result = new ArrayList<>();
		List<Double> RMSSDResult = new ArrayList<>();
		
		
		boolean goOut=false;
		List<Double> temp;
		for(int i =0; i<listname.size();i++) {
			
			temp= new ArrayList<>(); //initialize
			
			int t=i+1;
			while(Sumlist(listname, i, t)<120.0) { //120초 = window size, stride =1
				
				temp.add(listname.get(t-1)); // put IBI value in tempList for 120sec
				t++;
				
				if(t>listname.size()-2) { //끝에 도달했을때 종료(overflow방지용)
					goOut=true;
					break;
				}
			}
			
			if(goOut) break;

			
			
		    //temp = 120초동안의 window로 잘라진 RR(=NN) interval들의 list
			//pnn50구하기
			int count =0;
			int aa= temp.size();
			for(int j=0;j<temp.size()-1;j++) {
				if(Math.abs(temp.get(j)-temp.get(j+1))>=0.05) // difference between adjacent value >= 50ms
					count++;
			}
			double pnn50 = (double)count/(double)temp.size();
			pnn50Result.add(pnn50);
			
			
			
			//RMSSD 구하기
			double sum=0;
			for(int j=0;j<temp.size()-1;j++)
				sum=sum+Math.pow(temp.get(j)*1000-temp.get(j+1)*1000,2);
			
			double RMSSD = Math.sqrt(sum)/(temp.size()-1);
			RMSSDResult.add(RMSSD);
		
			
			
			//HR구하기
			double HR=(double)temp.size();
			HRResult.add(HR);
			
			
			//total list 에 집어넣기(순서유의)
			total.add(HRResult);
			total.add(pnn50Result);
			total.add(RMSSDResult);
		}
		return total;
	}
	
	public List<Double> RMSSD(List<Double> listname) {
		List<Double> temp;
		List<Double> result = new ArrayList<>();
	
	
	
		return result;
	}
	
	
}

////////////////////////
//CSV파일을 읽는 class//
////////////////////////
class READCSV{
	public List<List<String>> readCSV(String path) {
        List<List<String>> csvList = new ArrayList<List<String>>();
        File csv = new File(path); 
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) { // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.
                List<String> aLine = new ArrayList<String>();
                String[] lineArr = line.split(","); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
                aLine = Arrays.asList(lineArr);
                csvList.add(aLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) { 
                    br.close(); // 사용 후 BufferedReader를 닫아준다.
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return csvList;
    }
}

