SCRIPT_DIR=`dirname $0`
java -Xms768M -Xmx1024M -Xss1M -XX:MaxPermSize=256M -Dfile.encoding=UTF-8 -XX:+CMSClassUnloadingEnabled -jar "$SCRIPT_DIR/project/sbt-launch-0.13.0.jar" $@
