#include <fstream>
#include "SyncRules.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncRules")
#endif

class SyncRulesImpl : public SyncRules
{
public:
	SyncRulesImpl(void)
	{
		init();
	}

	virtual ~SyncRulesImpl(void)
	{
	}

	//获取冲突处理方式
	bool getActions(const SyncRuleKey& op, ExecuteActions& actions)
	{
		SyncRulesInfo::const_iterator it = syncRules_.find(op);

		if(syncRules_.end() == it)
		{
			SERVICE_ERROR(MODULE_NAME, RT_DIFF_NOSYNCRULE, "no sync rules, operation:%d", (int)op); 
			return false;
		}

		actions = it->second;
		return true;
	}

private:
	/*
	加载冲突规则
	xml内不可注释可用规则
	冲突规则示例：
	<BoxRule id="1i">
	<BDChangeStatus ItemMapType="Local" Created="0" Deleted="0" Moved="0" Renamed="0" Edited="0"/>
	<BDChangeStatus ItemMapType="Box" Created="0" Deleted="0" Moved="0" Renamed="0" Edited="0"/>
	<BDConflictStatus ConflictType="No" Resolution=""/>
	<!--NoActionAdd command is added here so that the item can be added to the last Sync tree if not there-->
	<BDExecuteAction ActionType="Local" ActionCommand="NoActionAdd"/>
	</BoxRule>
	*/
	void loadSyncRules(void)
	{
		std::ifstream infile(SYNC_RULES_PATH,std::ios::in);
		std::string temp;

		SyncRuleKey key;
		ExecuteActions actions;
		bool rightRule = false;
		while(getline(infile,temp))
		{
			if(-1 != temp.find(SYNCRULE_RULE_TAG))
			{
				key = SRK_NONE;
				actions.clear();
				rightRule = true;
				continue;
			}
			if(!rightRule)
			{
				continue;
			}

			if(-1 != temp.find(SYNCRULE_CHANGE_TAG))
			{
				std::string itemMapType = getValue(temp, SYNCRULE_ITEM_MAPTYPE);

				if(SYNCRULE_ITEM_MAPTYPE_LOCAL==itemMapType)
				{
					for(SyncRuleKeyMap::const_iterator it = lSyncRuleKeyMap_.begin();
						it!= lSyncRuleKeyMap_.end(); ++it)
					{
						if(!setSyncRuleKey(getValue(temp, it->first), it->second, key))
						{
							rightRule = false;
							break;
						}
					}
				}
				else if(SYNCRULE_ITEM_MAPTYPE_REMOTE==itemMapType)
				{
					for(SyncRuleKeyMap::const_iterator it = rSyncRuleKeyMap_.begin();
						it!= rSyncRuleKeyMap_.end(); ++it)
					{
						if(!setSyncRuleKey(getValue(temp, it->first), it->second, key))
						{
							rightRule = false;
							break;
						}
					}
				}
				continue;
			}

			if(-1 != temp.find(SYNCRULE_EXEC_TAG))
			{
				//<BDExecuteAction ActionType="Local" ActionCommand="NoActionAdd"/>
				std::string actionType = getValue(temp, SYNCRULE_ACTION_TYPE);
				ExecuteAction executeAction(new st_ExecuteAction);
				if(SYNCRULE_ITEM_MAPTYPE_LOCAL==actionType)
				{
					executeAction->actionType = ActionType_Local;
				}
				else if(SYNCRULE_ITEM_MAPTYPE_REMOTE==actionType)
				{
					executeAction->actionType = ActionType_Remote;
				}
				std::string actionCommand = getValue(temp, SYNCRULE_ACTION_COMMAND);
				ActionCommandMap::const_iterator it = actionCommandMap_.find(actionCommand);
				if(actionCommandMap_.end()!=it)
				{
					executeAction->actionCommand = it->second;
				}
				actions.push_back(executeAction);
				continue;
			}
			if(-1 != temp.find(SYNCRULE_RULE_ENDTAG))
			{
				syncRules_.insert(std::make_pair(key, actions));
			}
		}
	}

	std::string getValue(const std::string& str, const std::string& attrKey)
	{
		std::string value = "0";
		int pos = str.find(attrKey);
		if(-1!=pos)
		{
			int posStart = str.find("\"", pos) + 1;
			int posEnd = str.find("\"", posStart);
			if(posEnd>posStart)
			{
				value = str.substr(posStart, posEnd-posStart);
			}
		}
		return value;
	}

	bool setSyncRuleKey(const std::string& attrValue, 
				SyncRuleKey add_value, 
				SyncRuleKey& key,
				const std::string& trueValue="1", 
				const std::string& falseValue="0")
	{
		bool isVirtual = true;
		if(trueValue==attrValue)
		{
			key = SyncRuleKey(key|add_value);
		}
		else if(falseValue==attrValue||attrValue.empty())
		{
			//初始值为0，为falseValue时可默认什么都不做
			//key = SyncRuleKey(key&(~add_value));
		}
		else
		{
			isVirtual = false;
		}
		return isVirtual;
	}

	void init()
	{
		actionCommandMap_.clear();
		actionCommandMap_.insert(std::make_pair("Rename", CMD_Rename));
		actionCommandMap_.insert(std::make_pair("Move", CMD_Move));
		actionCommandMap_.insert(std::make_pair("Create", CMD_Create));
		actionCommandMap_.insert(std::make_pair("Delete", CMD_Delete));

		lSyncRuleKeyMap_.clear();
		lSyncRuleKeyMap_.insert(std::make_pair("Created", SRK_Local_Created));
		lSyncRuleKeyMap_.insert(std::make_pair("Deleted", SRK_Local_Deleted));
		lSyncRuleKeyMap_.insert(std::make_pair("Moved", SRK_Local_Moved));
		lSyncRuleKeyMap_.insert(std::make_pair("Renamed", SRK_Local_Renamed));
		lSyncRuleKeyMap_.insert(std::make_pair("Edited", SRK_Local_Edited));

		rSyncRuleKeyMap_.clear();
		rSyncRuleKeyMap_.insert(std::make_pair("Created", SRK_Remote_Created));
		rSyncRuleKeyMap_.insert(std::make_pair("Deleted", SRK_Remote_Deleted));
		rSyncRuleKeyMap_.insert(std::make_pair("Moved", SRK_Remote_Moved));
		rSyncRuleKeyMap_.insert(std::make_pair("Renamed", SRK_Remote_Renamed));
		rSyncRuleKeyMap_.insert(std::make_pair("Edited", SRK_Remote_Edited));

		//加载冲突处理规则到内存
		loadSyncRules();
	}

private:
	ActionCommandMap actionCommandMap_;
	SyncRuleKeyMap lSyncRuleKeyMap_;
	SyncRuleKeyMap rSyncRuleKeyMap_;
	SyncRulesInfo syncRules_;
};

SyncRules::~SyncRules(void)
{
}

std::auto_ptr<SyncRules> SyncRules::create()
{
	return std::auto_ptr<SyncRules>(new SyncRulesImpl());
}