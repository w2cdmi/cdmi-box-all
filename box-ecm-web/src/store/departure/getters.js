import util from "@/common/js/util";
export default {
  getTeamspacesList (state) {
    let teamspacesSpacesList = state.teamspacesSpacesList;
    if (teamspacesSpacesList) {
      let newlist = [];
      teamspacesSpacesList.forEach(item => {
        if (item.teamspace.type === 0) {
          item.teamspace.type = "协作空间";
        }
        if (item.teamspace.spaceQuota === -1) {
          item.teamspace.spaceQuota = "无限制";
        } else {
          item.teamspace.spaceQuota =
            util.formatFileSize(item.teamspace.spaceUsed) +
            "/" +
            util.formatFileSize(item.teamspace.spaceQuota);
        }
        if (item.teamspace.status === 1) {
          item.teamspace.status = "不可用";
        }
        if (item.teamspace.status === 0) {
          item.teamspace.status = "正常";
        }
        // if(item.teamspace.curNumbers) {
        item.teamspace.curNumbers = item.teamspace.curNumbers + "人";
        item.teamspace.createdAt = util.getFormatDate(
          item.teamspace.createdAt,
          "yyyy-MM-dd hh:mm:ss"
        );
        // }
        newlist.push(item.teamspace);
      });
      newlist.head = [
        {
          prop: "name",
          label: "空间名称"
        },
        {
          prop: "type",
          label: "空间类型"
        },
        {
          prop: "spaceQuota",
          label: "容量"
        },
        {
          prop: "curNumbers",
          label: "人数"
        },
        {
          prop: "createdAt",
          label: "创建时间"
        },
        {
          prop: "status",
          label: "状态"
        }
      ];
      //   this.TeamspacesList = newlist;
      return newlist;
    }
    return [];
  },
  getDocumentHandOver (state) {
    let documentHandOverInnerList = state.documentHandOverInnerList.data;
    if (documentHandOverInnerList) {
      let newlist = documentHandOverInnerList.folders.concat(
        documentHandOverInnerList.files
      );
      newlist.forEach(item => {
        if (item.size == undefined) {
          item.size = "--";
        } else {
          item.size = util.formatFileSize(item.size);
        }
      });

      newlist.head = [
        {
          prop: "name",
          label: "文件名称"
        },
        {
          prop: "size",
          label: "大小"
        },
        {
          prop: "department",
          label: "状态"
        }
      ];
      //   this.DocumentHandOver = newlist;
      return newlist;
    }
    return [];
  }
};
