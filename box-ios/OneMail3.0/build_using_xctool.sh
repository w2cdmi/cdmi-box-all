#!/bin/bash

if [[ $# < 1 ]]; then
  echo "请输入更新日志"
  exit 2
fi

#计时
SECONDS=0

#假设脚本放置在与项目相同的路径下
project_path=$(pwd)
#取当前时间字符串添加到文件结尾
now=$(date +"%Y_%m_%d_%H_%M_%S")

#指定项目的scheme名称
scheme="OneBox"
#指定要打包的配置名
configuration="Release"
#指定打包所使用的provisioning profile名称
provisioning_profile='iPhone Distribution: Chinasoft Resource Corporation'

#指定项目地址
workspace_path="$project_path/OneMail.xcodeproj"
#指定输出路径
output_path="******"
#指定输出归档文件地址
archive_path="$output_path/******${now}.xcarchive"
#指定输出ipa地址
ipa_path="$output_path/******${now}.ipa"

#输出设定的变量值
echo "===workspace path: ${workspace_path}==="
echo "===archive path: ${archive_path}==="
echo "===ipa path: ${ipa_path}==="
echo "===profile: ${provisioning_profile}==="OneMail.xcodeproj

#先清空前一次build
xctool clean -workspace ${workspace_path} -scheme ${scheme} -configuration ${configuration}

#根据指定的项目、scheme、configuration与输出路径打包出archive文件
xctool build -workspace ${workspace_path} -scheme ${scheme} -configuration ${configuration} archive -archivePath ${archive_path}

#使用指定的provisioning profile导出ipa
#我暂时没找到xctool指定provisioning profile的方法，所以这里用了xcodebuild
xcodebuild -exportArchive -archivePath ${archive_path} -exportPath ${ipa_path} -exportFormat ipa -exportProvisioningProfile "${provisioning_profile}"

echo "===upload .ipa to PGYER==="
#上传.ipa到蒲公英，参数`updateDescription`是更新日志
#参数请查阅：https://www.pgyer.com/doc/api#uploadApp
curl -F "file=@${ipa_path}" -F "uKey=******" -F "_api_key=******" -F "updateDescription=$1" http://www.pgyer.com/apiv1/app/upload

echo ""

#输出总用时
echo "===Finished. Total time: ${SECONDS}s==="

#通知
osascript -e 'display notification "打包上传蒲公英成功！" with title "任务完成"'
