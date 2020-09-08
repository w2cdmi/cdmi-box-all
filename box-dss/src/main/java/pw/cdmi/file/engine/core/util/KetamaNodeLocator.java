package pw.cdmi.file.engine.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.exception.InnerException;

public final class KetamaNodeLocator
{
    private NavigableMap<Long, String> ketamaNodes = new TreeMap<Long, String>();
    
    private int numReps = 160;

    /**
     * 计算接入码对象的表
     * 
     * @param nodes
     * @param alg
     * @param nodeCopies
     */
    public KetamaNodeLocator(String[] nodeNames)
    {
        byte[] digest = null;
        long m = 0;
        for (String nodeName : nodeNames)
        {
            for (int i = 0; i < numReps / 4; i++)
            {
                digest = computeMd5(nodeName + i);
                for (int h = 0; h < 4; h++)
                {
                    m = hash(digest, h);
                    ketamaNodes.put(m, nodeName);
                }
            }
        }
    }
    
    /**
     * 计算接入码对象的表
     * 
     * @param nodes
     * @param alg
     * @param nodeCopies
     */
    public KetamaNodeLocator(String[] nodeNames, int nodeCopies)
    {
        numReps = nodeCopies;
        byte[] digest = null;
        long m = 0;
        for (String nodeName : nodeNames)
        {
            for (int i = 0; i < numReps / 4; i++)
            {
                digest = computeMd5(nodeName + i);
                for (int h = 0; h < 4; h++)
                {
                    m = hash(digest, h);
                    ketamaNodes.put(m, nodeName);
                }
            }
        }
    }
    
    // 兼容老的list接口，做适配
    public KetamaNodeLocator(List<String> nodeNames)
    {
        this(nodeNames.toArray(new String[nodeNames.size()]));
    }
    
    // 兼容老的list接口，做适配
    public KetamaNodeLocator(List<String> nodeNames, int nodeCopies)
    {
        this(nodeNames.toArray(new String[nodeNames.size()]), nodeCopies);
    }
    
    public String getPrimary(final String k)
    {
        if (StringUtils.isBlank(k))
        {
            return null;
        }
        byte[] digest = computeMd5(k);
        String rv = getNodeNameForKey(hash(digest, 0));
        return rv;
    }
    
    /**
     * 获取HASH对应的节点名称
     * 
     * @param hash
     * @return
     */
    private String getNodeNameForKey(long hash)
    {
        String rv = null;
        Long key = hash;
        if (!ketamaNodes.containsKey(key))
        {
            key = ketamaNodes.ceilingKey(key);
            if (key == null)
            {
                key = ketamaNodes.firstKey();
            }
        }
        rv = ketamaNodes.get(key);
        return rv;
    }
    
    private long hash(byte[] digest, int nTime)
    {
        long rv = ((digest[3 + nTime * 4] & 0xFF) << 24)
            | ((digest[2 + nTime * 4] & 0xFF) << 16) | ((digest[1 + nTime * 4] & 0xFF) << 8)
            | (digest[0 + nTime * 4] & 0xFF);
        
        return rv & 0xffffffffL;
    }
    
    /**
     * Get the md5 of the given key.
     */
    private byte[] computeMd5(String k)
    {
        MessageDigest md5;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new InnerException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        try
        {
            keyBytes = k.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new InnerException("UnsupportedEncoding : UTF-8", e);
        }
        md5.update(keyBytes);
        return md5.digest();
    }
}
