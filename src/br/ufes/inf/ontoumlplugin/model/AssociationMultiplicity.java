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
		}
		
	}
	
	public AssociationMultiplicity(int min, int max){
		this.mMinMultiplicity = min;
		this.mMaxMultiplicity = max;
		this.mMultiplicity = IAssociationEnd.MULTIPLICITY_MANY;
	}
	
	public int getMinMultiplicity(){
		return mMinMultiplicity;
	}
	
	public int getMaxMultiplicity(){
		return mMaxMultiplicity;
	}

}