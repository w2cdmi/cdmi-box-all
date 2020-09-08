#ifndef _MD5_H_
#define _MD5_H_

#include <string>
#include <fstream>

#ifdef __cplusplus
extern "C"
{
#endif

/* ��Ϊ���а�ȫ���� */
extern errno_t memset_s(void* dest, size_t destMax, int c, size_t count);

#ifdef __cplusplus
}
#endif  /* __cplusplus */

/* Type define */
typedef unsigned char byte;
typedef unsigned int uint32;

using std::string;
using std::ifstream;

/* CMD5 declaration. */
class CMD5 {
public:
    CMD5();
    CMD5(const void* input, size_t length);
    CMD5(const string& str);
    CMD5(ifstream& in);
    void update(const void* input, size_t length);
    void update(const string& str);
    void update(ifstream& in);
    const byte* digest();
    string toString();
    void reset();

private:
    void update(const byte* input, size_t length);
    void final();
    void transform(const byte block[64]);
    void encode(const uint32* input, byte* output, size_t length);
    void decode(const byte* input, uint32* output, size_t length);
    string bytesToHexString(const byte* input, size_t length);

    /* class uncopyable */
    CMD5(const CMD5&);
    CMD5& operator=(const CMD5&);

private:
    uint32 _state[4];	/* state (ABCD) */
    uint32 _count[2];	/* number of bits, modulo 2^64 (low-order word first) */
    byte _buffer[64];	/* input buffer */
    byte _digest[16];	/* message digest */
    bool _finished;		/* calculate finished ? */

    static const byte PADDING[64];	/* padding for calculate */
    static const char HEX[16];
    enum { BUFFER_SIZE = 1024 };
};

#endif /*MD5_H*/
