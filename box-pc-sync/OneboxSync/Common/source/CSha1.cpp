#include "CSha1.h"
#include <sstream>

#pragma warning(disable : 4996) // ����vs c4996 ����

CSha1::CSha1()
{
    memset(&m_sha, 0, sizeof(SHA1Context));
    memset(m_digest, 0, SHA1HashSize);
}

CSha1::~CSha1()
{
}

bool CSha1::Reset()
{
    memset(&m_sha, 0, sizeof(SHA1Context));
    memset(m_digest, 0, SHA1HashSize);
    if (SHA1Reset(&m_sha))
    {
        return false;
    }
    return true;
}

bool CSha1::Update(unsigned char ch)
{
    if (SHA1Input(&m_sha, &ch, 1))
    {
        return false;
    }
    return true;
}

bool CSha1::Update(unsigned char* buf, uint32_t len)
{
    if (SHA1Input(&m_sha, buf, len))
    {
        return false;
    }
    return true;
}

bool CSha1::Update(const char* str)
{
    if (SHA1Input(&m_sha, (const unsigned char*)str, (uint32_t)strlen(str)))
    {
        return false;
    }
    return true;
}

bool CSha1::Update(std::fstream& fs)
{
    if (fs.fail())
    {
        return false;
    }

    char buffer[MAX_BUF_SIZE];
    std::streamsize length = 0;

    while (fs.good() && !fs.eof())
    {
        memset(buffer, 0, MAX_BUF_SIZE);
        fs.read(buffer, MAX_BUF_SIZE);
        length = fs.gcount();
        if (0 < length)
        {
            if (SHA1Input(&m_sha, (const unsigned char *) buffer, (uint32_t)length))
            {
                fs.close();
                return false;
            }
        }
    }
    fs.close();

    return true;
}

bool CSha1::Result()
{
    if (SHA1Result(&m_sha, m_digest))
    {
        return false;
    }
    return true;
}

std::string CSha1::ToString()
{
    char buf[3];
    std::stringstream str;
    for (int32_t i = 0; i < SHA1HashSize; i++)
    {
        memset(buf, 0, 3);
        sprintf(buf, "%02x", m_digest[i]);
        str << buf;
    }
    return str.str();
}