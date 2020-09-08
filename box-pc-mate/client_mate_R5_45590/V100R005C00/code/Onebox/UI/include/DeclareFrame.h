#pragma  once

namespace Onebox
{
	class  DeclareFrame;
	class Declare
	{
	public:
		Declare();
		~Declare();
	public:
		static Declare* create(HWND parent,UserContext* userContext);
		virtual void Run() = 0;
	};
}