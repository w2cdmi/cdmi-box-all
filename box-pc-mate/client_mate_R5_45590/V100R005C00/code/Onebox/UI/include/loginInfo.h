#pragma  once

namespace Onebox
{
	class LoginInfo
	{
	public:
		static LoginInfo* getInstance(UserContext* userContext );

        virtual ~LoginInfo();

		virtual void Show() = 0;
	private:
		static std::auto_ptr<LoginInfo> instance_;
		UserContext* m_pUserContext;
	};
}