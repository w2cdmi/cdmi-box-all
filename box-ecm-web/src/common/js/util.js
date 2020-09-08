var SIGN_REGEXP = /([yMdhsm])(\1*)/g;
var DEFAULT_PATTERN = "yyyy-MM-dd";
var picArr = [
  { url: require("../../assets/images/NWE_71.png") },
  { url: require("../../assets/images/NWE_40.png") },
  { url: require("../../assets/images/NWE_42.png") },
  { url: require("../../assets/images/NWE_44.png") },
  { url: require("../../assets/images/NWE_47.png") },
  { url: require("../../assets/images/NWE_50.png") },
  { url: require("../../assets/images/NWE_52.png") },
  { url: require("../../assets/images/NWE_54.png") },
  { url: require("../../assets/images/NWE_56.png") },
  { url: require("../../assets/images/NWE_67.png") },
  { url: require("../../assets/images/NWE_69.png") }
  // {url:require('../assets/a1.png'),title:'你看我叼吗1',id:1},
];

function padding (s, len) {
  len = len - (s + "").length;
  for (var i = 0; i < len; i++) {
    s = "0" + s;
  }
  return s;
}

export default {
  // 数组转树形
  parseTree: function (ary, data) {
    //           obj = ary[j];
    //           obj.ID = ary[j].id;
    //           obj.pid = ary[j].pid;
    //           obj.checked = false;
    //           obj.command = 0;
    //           obj.label = ary[i].name;
    //           obj.value = ary[i].id;
    //           obj.characterName = "";
    //           obj.character = -1;
    //           data[i].children = data[i].children || [];
    //           data[i].children.push(obj);
    //           data[i].child = data[i].child || [];
    //           data[i].child.push(obj);
    function toTreeData (data, attributes) {
      let resData = data;
      let tree = [];
      for (let i = 0; i < resData.length; i++) {
        let item = resData[i];
        if (item[attributes.parentId] == attributes.rootId) {
          let obj = {};
          // obj = item;
          obj = {
            id: resData[i][attributes.id],
            title: resData[i][attributes.name],
            children: [],
            label: item[attributes.name],
            ID: resData[i][attributes.id],
            pId: resData[i].Pid,
            checked: false,
            character: -1,
            value: item[attributes.name],
            type: resData[i].type,
            subEmployees: resData[i].subEmployees,
            subDepts: resData[i].subDepts,
            name: item[attributes.name]
          };
          tree.push(obj);
          resData.splice(i, 1);
          i--;
        }
      }
      run(tree);

      function run (chiArr) {
        if (resData.length !== 0) {
          for (let i = 0; i < chiArr.length; i++) {
            for (let j = 0; j < resData.length; j++) {
              if (chiArr[i].id === resData[j][attributes.parentId]) {
                let obj = {};
                // obj = resData[j];
                obj = {
                  id: resData[j][attributes.id],
                  title: resData[j][attributes.name],
                  children: [],
                  label: resData[j][attributes.name],
                  ID: resData[j][attributes.id],
                  pId: resData[j].Pid,
                  checked: false,
                  character: -1,
                  type: resData[j].type,
                  subEmployees: resData[j].subEmployees,
                  subDepts: resData[j].subDepts,
                  name: resData[j][attributes.name]
                };
                chiArr[i].children.push(obj);
                resData.splice(j, 1);
                j--;
              }
            }
            run(chiArr[i].children);
          }
        }
      }

      return tree;
    }
    let attributes = {
      id: "id",
      parentId: "pId",
      name: "name",
      rootId: "0"
    };
    return toTreeData(ary, attributes);
  },
  delChildren: function (ary) {
    function del (ary) {
      for (let i = 0, len = ary.length; i < len; i++) {
        let item = ary[i];
        if (item.hasOwnProperty("children")) {
          if (item.children.length === 0) {
            delete item.children;
          } else {
            del(item.children);
          }
        }
      }
    }
    del(ary);
    return ary;
  },
  // 给部门树形添加公司
  addCompany: function (ary, com) {
    let obj = com;
    obj.id = "0";
    obj.PID = 0;
    obj.iscom = true;
    obj.label = com.label;
    obj.children = ary;
    obj.name = com.label;
    return [obj];
  },
  // 改变树节点的选中状态
  checkedTreeitem: function (ary, id, radio) {
    let checkobj = {};
    function checkedTreeitem (ary, checkId) {
      function deb (id, item) {
        if (id == item.id) {
          return true;
        }
        return false;
      }
      let cc = false;
      for (let i = 0, len = ary.length; i < len; i++) {
        let item = ary[i];
        let newItem = item;
        if (radio) newItem.checked = false;
        if (deb(checkId, item)) {
          cc = true;
          newItem.checked = !item.checked;
          ary.splice(i, 1, newItem);
          checkobj = {
            obj: newItem,
            id: checkId,
            checked: newItem.checked
          };
        }
      }
      if (!cc || radio) {
        for (let i = 0, len = ary.length; i < len; i++) {
          let item = ary[i];
          if (item.children && item.children.length > 0) {
            checkobj = checkedTreeitem(item.children, checkId, radio);
          }
        }
      }
      return checkobj;
    }
    checkedTreeitem(ary, id, radio);
    return checkobj;
  },
  // 格式化树形的数据
  fromatTree (ary) {
    function fromatTree (params) {
      if (
        Object.prototype.toString.call(ary) === "[object Array]" &&
        ary.length > 0
      ) {
        ary.map(item => {
          let obj = item;
          obj.label = item.name;
          obj.ID = item.id || "";
          obj.checked = false;
          obj.characterName = "";
          obj.character = -1;
          item = obj;
          if (item.children) {
            obj.children = fromatTree(item.children);
          }
        });
      }
      return ary;
    }
    return fromatTree(ary);
  },
  getQueryStringByName: function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    var context = "";
    if (r !== null) {
      context = r[2];
    }
    reg = null;
    r = null;
    return context === null || context === "" || context === "undefined"
      ? ""
      : context;
  },
  /* 将文件大小转化为带单位的表示 */
  formatFileSize: function (size) {
    if (size == undefined || size == "" || isNaN(size)) {
      return 0 + "B";
    }

    if (size < 1024) {
      return size + "B";
    } else if (size >= 1024 && size < 1024 * 1024) {
      return (size / 1024).toFixed(2) + "KB";
    } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
      return (size / 1024 / 1024).toFixed(2) + "MB";
    } else {
      return (size / 1024 / 1024 / 1024).toFixed(2) + "GB";
    }
  },
  /* 判断是否为图片 */
  isImg: function (imgName) {
    var index = imgName.lastIndexOf(".");
    if (index !== -1) {
      var fileType = imgName.substring(index + 1).toLowerCase();
      if (
        fileType === "png" ||
        fileType === "jpg" ||
        fileType === "jpeg" ||
        fileType === "bmp"
      ) {
        return true;
      }
    }
    return false;
  },
  /**
   *转换日期对象为日期字符串
   * @param l long值
   * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss
   * @return 符合要求的日期字符串
   */
  getFormatDate: function (date, pattern) {
    if (date == undefined) {
      date = new Date();
    }
    if (typeof date == "number") {
      date = new Date(date);
    }
    if (pattern == undefined) {
      pattern = "yyyy-MM-dd hh:mm:ss";
    }
    // 暂时使用
    if (date.length < 10) {
      return date;
    }
    if (date.length == 10) {
      return date.replace("/", "-");
    }

    var o = {
      "M+": date.getMonth() + 1,
      "d+": date.getDate(),
      "h+": date.getHours(),
      "m+": date.getMinutes(),
      "s+": date.getSeconds(),
      "q+": Math.floor((date.getMonth() + 3) / 3),
      S: date.getMilliseconds()
    };
    if (/(y+)/.test(pattern)) {
      pattern = pattern.replace(
        RegExp.$1,
        (date.getFullYear() + "").substr(4 - RegExp.$1.length)
      );
    }
    for (var k in o) {
      if (new RegExp("(" + k + ")").test(pattern)) {
        pattern = pattern.replace(
          RegExp.$1,
          RegExp.$1.length == 1
            ? o[k]
            : ("00" + o[k]).substr(("" + o[k]).length)
        );
      }
    }

    return pattern;
  },
  /* 判断file（包括文件和目录）显示的图片 */
  getImgSrc: function (file) {
    // 目录
    if (file.type < 1) {
      return picArr[0].url;
    }

    var index = file.name.lastIndexOf(".");
    if (index != -1) {
      var fileType = file.name.substring(index + 1).toLowerCase();
      if (fileType == "txt" || fileType == "pdf") {
        return picArr[5].url;
      } else if (
        fileType == "DVDRip" ||
        fileType == "mp4" ||
        fileType == "mkv" ||
        fileType == "avi" ||
        fileType == "rm" ||
        fileType == "rmvb" ||
        fileType == "wmv" ||
        fileType == "3gp"
      ) {
        return picArr[8].url;
      } else if (
        fileType == "jpg" ||
        fileType == "png" ||
        fileType == "gif" ||
        fileType == "bmp" ||
        fileType == "jpeg" ||
        fileType == "jpeg2000" ||
        fileType == "tiff" ||
        fileType == "psd"
      ) {
        return picArr[10].url;
      } else if (fileType == "docx" || fileType == "doc") {
        return picArr[4].url;
      } else if (fileType == "xlsx" || fileType == "xls") {
        return picArr[1].url;
      } else if (fileType == "pptx" || fileType == "ppt") {
        return picArr[3].url;
      } else if (
        fileType == "rar" ||
        fileType == "zip" ||
        fileType == "7z" ||
        fileType == "cab" ||
        fileType == "iso"
      ) {
        return picArr[6].url;
      } else if (
        fileType == "mp3" ||
        fileType == "wma" ||
        fileType == "wav" ||
        fileType == "mod" ||
        fileType == "ra" ||
        fileType == "cd" ||
        fileType == "md" ||
        fileType == "asf" ||
        fileType == "aac" ||
        fileType == "mp3pro"
      ) {
        return picArr[9].url;
      }
    }

    return picArr[7].url;
  },
  formatDate: {
    format: function (date, pattern) {
      pattern = pattern || DEFAULT_PATTERN;
      return pattern.replace(SIGN_REGEXP, function ($0) {
        switch ($0.charAt(0)) {
          case "y":
            return padding(date.getFullYear(), $0.length);
          case "M":
            return padding(date.getMonth() + 1, $0.length);
          case "d":
            return padding(date.getDate(), $0.length);
          case "w":
            return date.getDay() + 1;
          case "h":
            return padding(date.getHours(), $0.length);
          case "m":
            return padding(date.getMinutes(), $0.length);
          case "s":
            return padding(date.getSeconds(), $0.length);
        }
      });
    },
    parse: function (dateString, pattern) {
      var matchs1 = pattern.match(SIGN_REGEXP);
      var matchs2 = dateString.match(/(\d)+/g);
      if (matchs1.length === matchs2.length) {
        var _date = new Date(1970, 0, 1);
        for (var i = 0; i < matchs1.length; i++) {
          var _int = parseInt(matchs2[i]);
          var sign = matchs1[i];
          switch (sign.charAt(0)) {
            case "y":
              _date.setFullYear(_int);
              break;
            case "M":
              _date.setMonth(_int - 1);
              break;
            case "d":
              _date.setDate(_int);
              break;
            case "h":
              _date.setHours(_int);
              break;
            case "m":
              _date.setMinutes(_int);
              break;
            case "s":
              _date.setSeconds(_int);
              break;
          }
        }
        return _date;
      }
      return null;
    },
    variation: dateString => {
      return dateString.replace(
        /^(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})$/,
        "$1-$2-$3 $4:$5:$6"
      );
    }
  },
  MttoClone: function (value) {
    function mottoClone (obj) {
      if (obj === null || typeof obj !== "object") return obj;
      if (obj instanceof Boolean) return new Boolean(obj.valueOf());
      if (obj instanceof Number) return new Number(obj.valueOf());
      if (obj instanceof String) return new String(obj.valueOf());
      if (obj instanceof RegExp) return new RegExp(obj.valueOf());
      if (obj instanceof Date) return new Date(obj.valueOf());
      var cpObj = obj instanceof Array ? [] : {};
      for (var key in obj) cpObj[key] = mottoClone(obj[key]);
      return cpObj;
    }
    let newObj = mottoClone(value);
    return newObj;
  }
};
