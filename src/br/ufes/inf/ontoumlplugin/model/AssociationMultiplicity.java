package br.ufes.inf.ontoumlplugin.model;

import com.vp.plugin.model.IAssociationEnd;

public class AssociationMultiplicity {
	
	private int mMinMultiplicity;
	private int mMaxMultiplicity;
	private String mMultiplicity;
	
	public AssociationMultiplicity(String multiplicity){
		this.mMultiplicity = multiplicity;
		
		switch(mMultiplicity){
			case IAssociationEnd.MULTIPLICITY_ONE:
			case IAssociationEnd.MULTIPLICITY_UNSPECIFIED:
				this.mMinMultiplicity = 1;
				this.mMaxMultiplicity = 1;
				break;
			case IAssociationEnd.MULTIPLICITY_ONE_TO_MANY:
				this.mMinMultiplicity = 1;
				this.mMaxMultiplicity = -1;
				break;
			case IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY:
				this.mMinMultiplicity = 0;
				this.mMaxMultiplicity = -1;
				break;
			case IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE:
				this.mMinMultiplicity = 0;
				this.mMaxMultiplicity = 1;
				break;
			case IAssociationEnd.MULTIPLICITY_MANY:
				this.mMinMultiplicity = -1;
				this.mMaxMultiplicity = -1;
			default:
				String[] multStr = multiplicity.split("[.]+");
				this.mMinMultiplicity = multStr[0].equals("*") ? 
											-1 : 
											Integer.valueOf(multStr[0]);
				this.mMaxMultiplicity = multStr.length == 2 ?
										(multStr[1].equals("*") ? 
											-1 : 
											Integer.valueOf(multStr[1])) :
										(multStr[0].equals("*") ? 
											-1 : 
											Integer.valueOf(multStr[0]));		
				
		}
		
	}
	
	public AssociationMultiplicity(int min, int max){
		this.mMinMultiplicity = min;
		this.mMaxMultiplicity = max;
		this.mMultiplicity = buildMultiplicityString(min, max);
	}
	
	private static String buildMultiplicityString(int min, int max){
		if(min != max){
			return min + ".." + (max == -1 ? "*" : max);
		}
		return min == -1 ? "*" : Integer.toString(min);
	}
	
	public int getMinMultiplicity(){
		return mMinMultiplicity;
	}
	
	public int getMaxMultiplicity(){
		return mMaxMultiplicity;
	}
	
	public String getMultiplicityString(){
		return mMultiplicity;
	}

}