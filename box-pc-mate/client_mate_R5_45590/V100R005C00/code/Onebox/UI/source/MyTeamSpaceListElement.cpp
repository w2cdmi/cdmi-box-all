#include "stdafxOnebox.h"
#include "Common.h"
#include "MyTeamSpaceListElement.h"
#include "TileLayoutListUI.h"
#include "SkinConfMgr.h"
#include "Utility.h"

namespace Onebox
{

	UserContext* CTeamSpaceElementUI::getTeamSPaceContext(UserContext* context)
	{
		userContext= UserContextMgr::getInstance()->createUserContext(context);
		userContext->id.type = UserContext_Teamspace;
		userContext->id.id = teamspaceInfo.teamInfo_.id();
		userContext->id.name = SD::Utility::String::utf8_to_wstring(teamspaceInfo.teamInfo_.name()).c_str();

		return userContext;
	}
    void CTeamSpaceElementUI::fillData(const UserTeamSpaceNodeInfo& fillNode,const wstring iconpath)
    {

		teamspaceInfo=fillNode;
		iconPath_=iconpath;
  
    }

    void TeamSpaceTileLayoutListContainerElement::initUI()
    {
        CLabelUI* teamsicon = static_cast<CLabelUI*>(this->FindSubControl(L"teamSpace_nameIcon"));
        if (teamsicon != NULL)
        {
			teamsicon->SetBkImage(iconPath_.c_str());
        }

        CLabelUI* teamsname = static_cast<CLabelUI*>(this->FindSubControl( L"teamSpace_name"));
        if (NULL != teamsname)
        {
         
			teamsname->SetText(SD::Utility::String::utf8_to_wstring(teamspaceInfo.member_.name()).c_str());
        }

        CLabelUI* teamsDes = static_cast<CLabelUI*>(this->FindSubControl(L"teamSpace_description"));
        if (teamsDes != NULL)
        {
            
			teamsDes->SetText(SD::Utility::String::utf8_to_wstring(teamspaceInfo.member_.description()).c_str());
        }
	   CLabelUI* teamsowner = static_cast<CLabelUI*>(this->FindSubControl(L"teamSpace_owner"));
        if (teamsowner != NULL)
        {
			teamsowner->SetText(SD::Utility::String::utf8_to_wstring(teamspaceInfo.member_.ownerByUserName()).c_str());

        }
	  CLabelUI* teamsNum = static_cast<CLabelUI*>(this->FindSubControl(L"teamSpace_memNum"));
        if (teamsNum != NULL)
        {
           
			teamsNum->SetText(SD::Utility::String::type_to_string<wstring,int64_t>(teamspaceInfo.member_.curNumbers()).c_str());
        }
    }
}