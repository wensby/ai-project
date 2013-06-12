import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import libsvm.svm_model;

/**
 * Holds multiple SVM's which you can iterate through.
 * @author Lukas J. Wensby
 * @version 2013-06-11
 */
public class SvmSet extends SvmInterface {
	/**
	 * One single SVM that contains its filepath, its feature structure string and its weight.
	 * @author Lukas J. Wensby
	 * @version 2013-06-11
	 */
	public class Svm {
		private String filepath;
		private String featureStructure;
		private int weight;
		
		public Svm(String filepath, String featureStructure, int weight) {
			this.filepath = filepath;
			this.featureStructure = featureStructure;
			this.weight = weight;
		}
		
		public String getFilepath() {
			return filepath;
		}
		
		public String getFeatureStructure() {
			return featureStructure;
		}
		
		public int getWeight() {
			return weight;
		}
		
		public void incrementWeight() {
			weight++;
		}

        public void setWeight(int i) {
            weight = i;
        }
	}
	
	private LinkedList<Svm> svms;
	private String structureFilepath;
	private int numSvms;
	
	public SvmSet(String filepath) {
		structureFilepath = filepath;
		svms = new LinkedList<Svm>();
	}
	
	/**
	 * @param filepath has to be the filepath were the model is stored.
	 * @param featureStructure
	 */
	public void add(String filepath, String featureStructure) {
		Svm svm = new Svm(filepath, featureStructure, 1);
		svms.add(svm);
		numSvms++;
        resetWeights();
	}
	
	private void add(String filepath, String featureStucture, int weight) {
		Svm svm = new Svm(filepath, featureStucture, weight);
		svms.add(svm);
		numSvms++;
	}
	
	public void save(){
        File file = new File(structureFilepath);
        try{
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        
        for (Svm svm : svms) {
        	// When saving, I'm basically separating the variables by a '@' symbol
        	bw.write(svm.getFilepath() + "@" + svm.getFeatureStructure() + "@" + svm.getWeight() + "\n");
        }
        
        bw.flush();
        bw.close();
        } catch (Exception e){e.printStackTrace();}
    }
	
	public int size() {
		return numSvms;
	}

    public double ClassifySample(Database db, int userID, int itemID){
        int sum = 0;
        try {
            for(Svm svm : svms) {
                Feature featureSet = new Feature(new User(userID,db), new Item(itemID,db), svm.getFeatureStructure());
                Svm_model opened = Svm_model.LoadModel(svm.getFilepath(), svm.getFeatureStructure().length());
                sum += svm.getWeight() * PredictSingleDataPoint(opened, featureSet.getFeatureVector());
            }
        } catch (Exception e){e.printStackTrace();}
        if (sum < 0) return -1.0;
        else return 1.0;
    }

    public void TrainFromSample(double outcome, Database db, int userID, int itemID){
        double res = 0.0;
        try {
            for(Svm svm : svms) {
                Feature featureSet = new Feature(new User(userID,db), new Item(itemID,db), svm.getFeatureStructure());
                Svm_model opened = Svm_model.LoadModel(svm.getFilepath(), svm.getFeatureStructure().length());
                res = PredictSingleDataPoint(opened, featureSet.getFeatureVector());
                if ((int)res == (int)outcome) svm.incrementWeight();
            }
        } catch (Exception e){e.printStackTrace();}
    }
    
    public static SvmSet load(String filepath){

    	File file = new File(filepath);
    	if (!file.exists()) throw new IllegalArgumentException("tried to load non-existing svms structure... its not very effective");

    	SvmSet returner = new SvmSet(filepath);

        try{
    	BufferedReader br = new BufferedReader(new FileReader(file));
    	String line;
    	while ((line = br.readLine()) != null) {
    		String split[] = line.split("@");
    		returner.add(split[0], split[1], Integer.parseInt(split[2]));
    	}
        } catch (Exception e){e.printStackTrace();}
    	
    	return returner;
    }

    public void resetWeights(){
        for(Svm svm : svms){
            svm.setWeight(1);
        }
    }
}
