#ifndef _ONEBOX_TOKEN_H_
#define _ONEBOX_TOKEN_H_

#include <string>

struct TOKEN 
{
	std::string token;
	//std::string type;
	uint32_t period;
	uint32_t start;

	TOKEN ()
	{
		token = "";
		//type = "";
		period = 0;
		start = 0;
	}

	TOKEN (const TOKEN& rhs)
	{
		token = rhs.token;
		//type = rhs.type;
		period = rhs.period;
		start = rhs.start;
	}

	TOKEN & operator=(const TOKEN& rhs)
	{
		token = rhs.token;
		//type = rhs.type;
		period = rhs.period;
		start = rhs.start;

		return *this;
	}
};

#endif
