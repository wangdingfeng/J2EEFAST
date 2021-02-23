#!/bin/bash
# 运行打WAR包
# /**
#  * All content copyright http://www.j2eefast.com, unless
#  * otherwise indicated. All rights reserved.
#  * No deletion without permission
#  *
#  */
echo ""
echo "    _____                 ____.    _     ____   _____"
echo "    |_. _|               | |--|   / \   /_/\_\ |_|_|_|"
echo "   _  | |  ___  __   __  | |__.  /___\  \_\_--|  | |"
echo "  ( |_| | /. / /__\ /__\ | |--| / |_| \ .___) |  | |"
echo "   \____| __/__|__. |__. |_|   /_/   \_\|_____|  |_|"
echo ">>> 打war包。"
echo ""
read -s -n1 -p "请按任意键继续 ... "
cd ..
echo ""
echo ""
mvn clean compile -Dmaven.test.skip=true -U
echo ""
echo ">>> 依赖下载完成，下面开始项目打war包。"
echo ""
read -s -n1 -p "请按任意键继续 ... "
echo ""
echo ""

MAVEN_OPTS="$MAVEN_OPTS -Xms512m -Xmx1024m"
mvn clean package -f pom-war.xml

cd bin