#pragma once

#include "IFile.h"
#include "AsyncTaskCommon.h"
#include "ShareUserInfo.h"
#include "ShareNode.h"
#include "FileItem.h"
#include <boost/function.hpp>
#include <boost/bind.hpp>
#include "TeamSpacesNode.h"
#include "MsgInfo.h"
#include "PageParam.h"

namespace Onebox
{
	#define  MAX_DIR_NUM	20

	#define DUI_MSGTYPE_ITEMDRAGMOVE            (_T("itemDragMove"))
	#define DUI_MSGTYPE_ITEMDRAGCOPY            (_T("itemDragCopy"))
	#define DUI_MSGTYPE_ITEMDRAGFILE            (_T("itemDragFile"))
	#define DUI_MSGTYPE_SELECTITEMCHANGED 		(_T("selectItemChanged"))
	#define DUI_MSGTYPE_DELETE		 			(_T("itemdelete"))
	#define DUI_MSGTYPE_SELECTALL				(_T("itemselectall"))
	#define DUI_MSGTYPE_LISTSELECTALL			(_T("listselectall"))
	#define DUI_MSGTYPE_BACK					(_T("back"))

	#define SYSROLE_EDITOR				"editor"
	#define SYSROLE_VIEWER				"viewer"
	#define SYSROLE_LISTER				"lister"
	#define SYSROLE_PREVIEWER			"previewer"
	#define SYSROLE_UPLOADER			"uploader"
	#define SYSROLE_UPLOADERANDVIEWER	"uploadAndView"

	#define FOLDER_ICON_COMPUTER (L"computer")
	#define FOLDER_ICON_DISK (L"disk")

	enum EVENT_SENDTYPE
	{
		Event_Default,
		Event_FromParent,
		Event_FromChild,
		Event_ChildSend
	};

	struct UIFileNode
	{
		FILE_DIR_INFO basic; // the basic information of the file node
		UserContext* userContext; // the pointer of the userConetxt

		UIFileNode() : userContext(NULL)
		{}
	};	

	struct UITransTaskDetailNode : st_AsyncTransDetailNode
	{

	};

	struct UITransTaskRootNode : st_AsyncTransRootNode
	{
		int64_t completeTime;

		UITransTaskRootNode()
			:completeTime(0)
		{
			status = ATS_Waiting;
		}

		UITransTaskRootNode& operator =(const UITransTaskRootNode& right)
		{
			group = right.group;
			source = right.source;
			parent = right.parent;
			name = right.name;
			type = right.type;
			fileType = right.fileType;
			userId = right.userId;
			userType = right.userType;
			userName = right.userName;
			size = right.size;
			status = right.status;
			statusEx = right.statusEx;
			priority = right.priority;
			transedSize = right.transedSize;
			errorCode = right.errorCode;

			completeTime = right.completeTime;
			return *this;
		}
											  
		UITransTaskRootNode& operator =(const st_AsyncTransRootNode& right)
		{
			group = right.group;
			source = right.source;
			parent = right.parent;
			name = right.name;
			type = right.type;
			fileType = right.fileType;
			userId = right.userId;
			userType = right.userType;
			userName = right.userName;
			size = right.size;
			status = right.status;
			statusEx = right.statusEx;
			priority = right.priority;
			transedSize = right.transedSize;
			errorCode = right.errorCode;
			return *this;
		}

		UITransTaskRootNode& operator =(const st_AsyncTransCompleteNode& right)
		{
			group = right.group;
			source = right.source;
			parent = right.parent;
			name = right.name;
			type = right.type;
			fileType = right.fileType;
			userId = right.userId;
			userType = right.userType;
			userName = right.userName;
			size = right.size;

			completeTime = right.completeTime;

			return *this;
		}
	};

	struct UIGroupNode
	{
		int64_t id; //
		std::wstring name; //
		std::wstring description; //
		int64_t accountId; //
		int32_t maxMembers; //
		int64_t ownedBy; // 
		int64_t createdAt; // 外链创建时间
		int64_t modifiedAt; // 外链最后更新时间
		int64_t createdBy; // 外链创建者ID
		std::wstring status;	//
		std::wstring appId;
		std::wstring type;
		std::wstring roleName;

		UIGroupNode()
			:id(0L)
			,name(L"")
			,description(L"")
			,accountId(0L)
			,maxMembers(0)
			,createdAt(0L)
			,modifiedAt(0L)
			,createdBy(0L)
			,ownedBy(0L)
			,status(L"")
			,appId(L"")
			,type(L"")
			,roleName(L"viewer")
		{
		}
	};

	typedef std::vector<UIGroupNode> UIGroupNodeList;

	struct UIShareNode
	{
		ShareUserInfo shareUserInfo;
		ShareNode shareNode;
		UIGroupNode groupNode;
	};

	struct UIFileVersionNode
	{
		FILE_VERSION_INFO basic;
		//FileVersionItem basic; // the basic information of the fileVersion node
		UserContext* userContext; // the pointer of the userConetxt

		UIFileVersionNode() : userContext(NULL)
		{}
	};	

	struct UITeamSpaceNode
	{
		UserTeamSpaceNodeInfo basic; // the basic information of the TeamSpace node
		UserContext* userContext; // the pointer of the userConetxt

		UITeamSpaceNode() : userContext(NULL)
		{}
	};

	struct UITeamSpaceManageNode
	{
		UserTeamSpaceNodeInfo basic; // the basic information of the fileVersion node
		UserContext* userContext; // the pointer of the userConetxt

		UITeamSpaceManageNode() : userContext(NULL)
		{}
	};

	struct UITeamSpaceFileNode
	{
		//FILE_DIR_INFO basic; // the basic information of the TeamSpaceManage node
		UserTeamSpaceNodeInfo teamData; // the basic information of the TeamSpace node
		//UserContext* userContext; // the pointer of the userConetxt
	};

	struct UITeamSpaceManageUserNode
	{
		ShareUserInfo basic; // the basic information of the TeamSpaceManage list user node
		UIGroupNode groupNode;
		UserContext* userContext; // the pointer of the userConetxt
		std::wstring role;
		std::wstring teamRole;

		UITeamSpaceManageUserNode():role(L""),teamRole(L""),userContext(NULL)
		{}
	};

	typedef boost::function<void(DuiLib::TNotifyUI&)> call_func;

	enum FileNodeType
	{
		FileNode_Rest,
		FileNode_Local,
		FileNode_FileRoot,			//我的文件/团队空间/本地路径的根节点，虚节点，无对应的文件对象
		FileNode_TeamSpaceRoot		//团队空间列表的根节点
	};

	struct FileBaseInfo
	{
		UIFileNode fileData;
		std::wstring path;
		FileNodeType nodeType;
		FileBaseInfo():path(L""), nodeType(FileNode_Rest)
		{
		}
	};

	bool thumbEnabled(const std::wstring& name);

	bool dbClickEnabled(const std::wstring& name);
}