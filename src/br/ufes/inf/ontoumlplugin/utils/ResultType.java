package br.ufes.inf.ontoumlplugin.utils;

public class ResultType {

    public enum Result { SUCESS, WARNING, ERROR }

    private Object[] data;
    private Result resultType;
    private String description;

    public ResultType(Result resultType, String description, Object[] data){
        this.data = data;
        this.resultType = resultType;
        this.description = description;
    }

    public Object[] getData() { return data; }
    public void setData(Object[] data) { this.data = data; }
    public Result getResultType() { return resultType; }
    public void setResultType(Result resultType) { this.resultType = resultType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString(){
        String ans = "";
        if(resultType != Result.SUCESS) ans += resultType.name() + ": ";
        ans += description;
        return ans;
    }
}
