package pw.cdmi.box.uam.statistics.domain;

import java.io.Serializable;
import java.util.List;

public class HistUserStatisResponse implements Serializable
{
    private static final long serialVersionUID = 5616425234342590479L;
    
    private HistUserStatisDataset histUserStatisDataset;
    
    private List<String> urls;
    
    public HistUserStatisDataset getHistUserStatisDataset()
    {
        return histUserStatisDataset;
    }
    
    public List<String> getUrls()
    {
        return urls;
    }
    
    public void setHistUserStatisDataset(HistUserStatisDataset histUserStatisDataset)
    {
        this.histUserStatisDataset = histUserStatisDataset;
    }
    
    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }
}
