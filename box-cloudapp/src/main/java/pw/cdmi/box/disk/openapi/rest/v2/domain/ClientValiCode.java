package pw.cdmi.box.disk.openapi.rest.v2.domain;

public class ClientValiCode
{
    public String getArithmetic()
    {
        return arithmetic;
    }

    public void setArithmetic(String arithmetic)
    {
        this.arithmetic = arithmetic;
    }

    public String getFeaturecode()
    {
        return featurecode;
    }

    public void setFeaturecode(String featurecode)
    {
        this.featurecode = featurecode;
    }

    private String arithmetic;
    
    private String featurecode;
}
